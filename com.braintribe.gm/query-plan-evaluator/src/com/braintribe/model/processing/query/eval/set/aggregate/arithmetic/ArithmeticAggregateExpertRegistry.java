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
package com.braintribe.model.processing.query.eval.set.aggregate.arithmetic;

import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.processing.query.eval.api.RuntimeQueryEvaluationException;
import com.braintribe.model.processing.query.eval.set.aggregate.ArithmeticExpert;
import com.braintribe.model.queryplan.value.AggregationFunctionType;

/**
 * 
 */
public class ArithmeticAggregateExpertRegistry {

	public static ArithmeticExpert<Object> getExpertIfPossible(AggregationFunctionType type) {
		return (ArithmeticExpert<Object>) getExpertHelper(type);
	}

	private static ArithmeticExpert<?> getExpertHelper(AggregationFunctionType type) {
		switch (type) {
			case count:
				return IntegerAggregateExpert.INSTANCE;

			case countDistinct:
				return new CountDistinctAggregateExpert();

			default:
				return null;
		}
	}

	public static ArithmeticExpert<Object> getExpertFor(GenericModelType gmType) {
		return (ArithmeticExpert<Object>) getExpertHelper(gmType);
	}

	private static ArithmeticExpert<?> getExpertHelper(GenericModelType gmType) {
		switch (gmType.getTypeCode()) {
			case dateType:
				return DateAggregateExpert.INSTANCE;
			case decimalType:
				return DecimalAggregateExpert.INSTANCE;
			case doubleType:
				return DoubleAggregateExpert.INSTANCE;
			case floatType:
				return FloatAggregateExpert.INSTANCE;
			case integerType:
				return IntegerAggregateExpert.INSTANCE;
			case longType:
				return LongAggregateExpert.INSTANCE;
			case stringType:
				return StringAggregateExpert.INSTANCE;

			case booleanType:
			case entityType:
			case enumType:
			case listType:
			case mapType:
			case objectType:
			case setType:
				throw new RuntimeQueryEvaluationException("Aggregation is not supported for type: " + gmType);
		}

		throw new RuntimeQueryEvaluationException("Unknown GenericModelType: " + gmType + " typeCode: " + gmType.getTypeCode());

	}

}
