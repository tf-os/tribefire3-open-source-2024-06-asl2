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
package com.braintribe.model.processing.query.eval.context;

import static com.braintribe.utils.lcd.CollectionTools2.newMap;
import static com.braintribe.utils.lcd.CollectionTools2.newSet;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.processing.query.eval.api.EvalTupleSet;
import com.braintribe.model.processing.query.eval.api.QueryEvaluationContext;
import com.braintribe.model.processing.query.eval.api.RuntimeQueryEvaluationException;
import com.braintribe.model.processing.query.eval.api.Tuple;
import com.braintribe.model.processing.query.eval.api.function.QueryFunctionExpert;
import com.braintribe.model.processing.query.eval.tools.PropertyPathResolver;
import com.braintribe.model.query.functions.QueryFunction;
import com.braintribe.model.queryplan.value.HashSetProjection;
import com.braintribe.model.queryplan.value.IndexValue;
import com.braintribe.model.queryplan.value.QueryFunctionValue;
import com.braintribe.model.queryplan.value.StaticValue;
import com.braintribe.model.queryplan.value.TupleComponent;
import com.braintribe.model.queryplan.value.Value;
import com.braintribe.model.queryplan.value.ValueProperty;

/**
 * 
 */
class ValueResolver {

	private final QueryEvaluationContext context;
	private final Map<EntityType<? extends QueryFunction>, QueryFunctionExpert<?>> experts;
	private final Map<QueryFunction, QueryFunctionExpert<QueryFunction>> expertsForFunctions;
	private final Map<StaticValue, Object> staticValueCache;

	public ValueResolver(QueryEvaluationContext context, Map<EntityType<? extends QueryFunction>, QueryFunctionExpert<?>> experts) {
		this.context = context;
		this.experts = experts;
		this.expertsForFunctions = newMap();
		this.staticValueCache = newMap();
	}

	public <T> T resolve(Tuple tuple, Value value) {
		return (T) resolve(value, tuple);
	}

	private Object resolve(Value value, Tuple tuple) {
		switch (value.valueType()) {
			case aggregateFunction:
				throw new RuntimeQueryEvaluationException(
						"Aggregate function cannot be evaluated like this. Probably an illegal query plan.");
			case queryFunction:
				return resolveValue((QueryFunctionValue) value, tuple);
			case hashSetProjection:
				return resolveValue((HashSetProjection) value);
			case indexValue:
				return resolveValue((IndexValue) value);
			case staticValue:
				return resolveStaticValue((StaticValue) value);
			case tupleComponent:
				return resolveValue((TupleComponent) value, tuple);
			case valueProperty:
				return resolveValue((ValueProperty) value, tuple);
			default:
				throw new RuntimeQueryEvaluationException("Unsupported Value: " + value + " of type: " + value.valueType());
		}
	}

	private Object resolveValue(QueryFunctionValue value, Tuple tuple) {
		QueryFunctionExpert<QueryFunction> expert = findExpertFor(value.getQueryFunction());

		return expert.evaluate(tuple, value.getQueryFunction(), value.getOperandMappings(), context);
	}

	private QueryFunctionExpert<QueryFunction> findExpertFor(QueryFunction queryFunction) {
		QueryFunctionExpert<QueryFunction> result = expertsForFunctions.get(queryFunction);

		if (result == null) {
			EntityType<?> functionType = queryFunction.entityType();
			result = (QueryFunctionExpert<QueryFunction>) experts.get(functionType);

			if (result == null)
				throw new RuntimeQueryEvaluationException("No expert found for function:" + functionType.getTypeSignature());

			expertsForFunctions.put(queryFunction, result);
		}

		return result;
	}

	private Object resolveValue(HashSetProjection value) {
		Set<Object> result = newSet();

		EvalTupleSet resolveTupleSet = context.resolveTupleSet(value.getTupleSet());
		Value tupleValue = value.getValue();
		for (Tuple tuple: resolveTupleSet)
			result.add(resolve(tupleValue, tuple));

		return result;
	}

	private Object resolveValue(IndexValue value) {
		Object resolvedKey = resolve(value.getKeys(), null);

		if (resolvedKey instanceof Collection<?>)
			return context.getAllValuesForIndicesDirectly(value.getIndexId(), (Collection<?>) resolvedKey);
		else
			return context.getAllValuesForIndexDirectly(value.getIndexId(), resolvedKey);
	}

	private Object resolveStaticValue(StaticValue value) {
		Object resolvedValue = staticValueCache.get(value);

		if (resolvedValue == null) {
			resolvedValue = context.resolveStaticValue(value.getValue());
			staticValueCache.put(value, resolvedValue);
		}

		return resolvedValue;
	}

	private Object resolveValue(TupleComponent value, Tuple tuple) {
		return tuple.getValue(value.getTupleComponentIndex());
	}

	private Object resolveValue(ValueProperty value, Tuple tuple) {
		GenericEntity entity = resolve(tuple, value.getValue());

		return PropertyPathResolver.resolvePropertyPath(entity, value.getPropertyPath());
	}

}
