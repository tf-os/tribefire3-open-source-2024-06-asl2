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
package com.braintribe.model.processing.query.planner.condition;

import static com.braintribe.utils.lcd.CollectionTools2.asList;

import java.util.List;

import com.braintribe.model.query.Operator;
import com.braintribe.model.query.conditions.AbstractJunction;
import com.braintribe.model.query.conditions.Condition;
import com.braintribe.model.query.conditions.Conjunction;
import com.braintribe.model.query.conditions.Disjunction;
import com.braintribe.model.query.conditions.Negation;
import com.braintribe.model.query.conditions.ValueComparison;

/**
 * @author peter.gazdik
 */
public class QueryConditionBuilder {

	public static ValueComparison valueComparison(Object leftOperand, Object rightOperand, Operator operator) {
		ValueComparison result = ValueComparison.T.create();

		result.setLeftOperand(leftOperand);
		result.setRightOperand(rightOperand);
		result.setOperator(operator);

		return result;
	}

	public static Negation negation(Condition operand) {
		Negation result = Negation.T.create();
		result.setOperand(operand);

		return result;
	}

	public static Conjunction conjunction(Condition... operands) {
		return junction(Conjunction.T.createPlain(), asList(operands));
	}

	public static Conjunction conjunction(List<Condition> operands) {
		return junction(Conjunction.T.createPlain(), operands);
	}

	public static Disjunction disjunction(Condition... operands) {
		return junction(Disjunction.T.createPlain(), asList(operands));
	}

	public static Disjunction disjunction(List<Condition> operands) {
		return junction(Disjunction.T.createPlain(), operands);
	}

	private static <T extends AbstractJunction> T junction(T junction, List<Condition> operands) {
		junction.setOperands(operands);
		return junction;
	}
}
