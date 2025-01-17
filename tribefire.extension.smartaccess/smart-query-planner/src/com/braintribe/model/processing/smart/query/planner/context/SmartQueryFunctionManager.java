// ============================================================================
// Copyright BRAINTRIBE TECHNOLOGY GMBH, Austria, 2002-2022
// 
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
// 
//     http://www.apache.org/licenses/LICENSE-2.0
// 
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
// ============================================================================
package com.braintribe.model.processing.smart.query.planner.context;

import static com.braintribe.utils.lcd.CollectionTools2.newList;
import static com.braintribe.utils.lcd.CollectionTools2.newMap;
import static com.braintribe.utils.lcd.CollectionTools2.newSet;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.processing.query.eval.api.RuntimeQueryEvaluationException;
import com.braintribe.model.processing.query.eval.api.function.QueryFunctionExpert;
import com.braintribe.model.processing.query.tools.QueryFunctionAnalyzer;
import com.braintribe.model.processing.smart.query.planner.graph.EntitySourceNode;
import com.braintribe.model.processing.smartquery.eval.api.function.SignatureSelectionOperand;
import com.braintribe.model.processing.smartquery.eval.api.function.SmartQueryFunctionExpert;
import com.braintribe.model.query.Operand;
import com.braintribe.model.query.functions.QueryFunction;
import com.braintribe.model.queryplan.value.StaticValue;
import com.braintribe.model.queryplan.value.Value;

/**
 * 
 */
public class SmartQueryFunctionManager {

	private final SmartQueryPlannerContext context;
	private final Map<EntityType<? extends QueryFunction>, QueryFunctionExpert<?>> functionExperts;

	private final Map<QueryFunction, List<Operand>> functionOperands = newMap();
	private final Map<QueryFunction, List<Operand>> functionOperandsToSelect = newMap();
	private final Map<QueryFunction, Map<Object, Value>> queryModelOperandValues = newMap();
	private final Set<QueryFunction> noticedFunctions = newSet();

	public SmartQueryFunctionManager(SmartQueryPlannerContext context,
			Map<EntityType<? extends QueryFunction>, QueryFunctionExpert<?>> functionExperts) {

		this.context = context;
		this.functionExperts = functionExperts;
	}

	public void noticeQueryFunction(QueryFunction function, PlanStructureInitializer psi) {
		if (!noticedFunctions.contains(function)) {
			noticedFunctions.add(function);
			markOperands(function, psi);
		}
	}

	private void markOperands(QueryFunction function, PlanStructureInitializer psi) {
		for (Operand operand : listOperandsToSelect(function)) {
			if (operand instanceof QueryFunction) {
				noticeQueryFunction((QueryFunction) operand, psi);

			} else if (operand instanceof SignatureSelectionOperand) {
				SignatureSelectionOperand sso = (SignatureSelectionOperand) operand;

				EntitySourceNode sourceNode = context.planStructure().acquireSourceNode(sso.getSource());
				sourceNode.markSignatureForSelection();

			} else {
				psi.analyzeOperand(operand);
			}
		}
	}

	public Map<Object, Value> getFunctionOperandMappings(QueryFunction function) {
		Map<Object, Value> operandMappings = queryModelOperandValues.get(function);

		if (operandMappings == null) {
			operandMappings = resolveOperandMappings(function);
			queryModelOperandValues.put(function, operandMappings);
		}

		return operandMappings;
	}

	private Map<Object, Value> resolveOperandMappings(QueryFunction function) {
		Map<Object, Value> result = newMap();

		for (Operand operand : listOperandsToSelect(function)) {
			Value operandValue = convert(operand);

			if (!(operandValue instanceof StaticValue))
				result.put(operand, operandValue);
		}

		return result;
	}

	private Value convert(Operand operand) {
		if (operand instanceof SignatureSelectionOperand) {
			SignatureSelectionOperand sso = (SignatureSelectionOperand) operand;
			EntitySourceNode sourceNode = context.planStructure().acquireSourceNode(sso.getSource());

			return SmartEntitySignatureTools.smartEntitySignatureFor(sourceNode);
		}

		return context.convertOperand(operand);
	}

	// TODO is this different than listOperandsToSelect?
	public List<Operand> listOperands(QueryFunction function) {
		List<Operand> result = functionOperands.get(function);

		if (result == null) {
			result = newList();
			functionOperands.put(function, result);

			for (Object operand : listAllOperands(function, false))
				if (operand instanceof Operand)
					result.add((Operand) operand);
		}

		return result;
	}

	private List<Operand> listOperandsToSelect(QueryFunction function) {
		List<Operand> result = functionOperandsToSelect.get(function);

		if (result == null) {
			result = newList();
			functionOperandsToSelect.put(function, result);

			for (Object operand : listAllOperands(function, true))
				if (operand instanceof Operand)
					result.add((Operand) operand);
		}

		return result;
	}

	private Collection<?> listAllOperands(QueryFunction function, boolean forSelection) {
		QueryFunctionExpert<QueryFunction> expert = getExpertFor(function);

		if (forSelection && expert instanceof SmartQueryFunctionExpert)
			return ((SmartQueryFunctionExpert<QueryFunction>) expert).listOperandsToSelect(function);
		else
			return QueryFunctionAnalyzer.findOperands(function);
	}

	private QueryFunctionExpert<QueryFunction> getExpertFor(QueryFunction function) {
		EntityType<?> functionType = function.entityType();
		QueryFunctionExpert<QueryFunction> result = (QueryFunctionExpert<QueryFunction>) functionExperts.get(functionType);

		if (result == null)
			throw new RuntimeQueryEvaluationException("No expert found for function:" + functionType.getTypeSignature());

		return result;
	}

}
