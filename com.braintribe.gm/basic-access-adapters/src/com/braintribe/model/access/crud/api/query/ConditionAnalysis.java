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
package com.braintribe.model.access.crud.api.query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.braintribe.model.generic.reflection.BaseType;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.value.EntityReference;
import com.braintribe.model.generic.value.EnumReference;
import com.braintribe.model.query.Operator;
import com.braintribe.model.query.conditions.FulltextComparison;
import com.braintribe.model.query.conditions.ValueComparison;

/**
 * @author gunther.schenk
 */
public interface ConditionAnalysis {
	
	/**
	 * @return all comparisons of analyzed condition referencing an id property.
	 */
	List<ValueComparison> getIdComparisons();
	
	/**
	 * @return all comparisons of analyzed condition not referencing an id property.
	 */
	Map<String,List<ValueComparison>> getPropertyComparisons();
	
	/**
	 * @return all comparisons of analyzed condition not referencing an id property.
	 */
	List<FulltextComparison> getFulltextComparisons();
	
	default boolean hasComparisons() {
		return hasIdComparisons() || hasFulltextComparisons() || hasPropertyComparisons();
	}
	
	default boolean hasIdComparisons() {
		return !getIdComparisons().isEmpty();
	}

	default boolean hasNonIdComparisons() {
		return hasFulltextComparisons() || hasPropertyComparisons();
	}

	default boolean hasFulltextComparisons() {
		return !getFulltextComparisons().isEmpty();
	}
	
	default boolean hasNonFulltextComparisons() {
		return hasIdComparisons() || hasPropertyComparisons();
	}

	default boolean hasPropertyComparisons() {
		return !getPropertyComparisons().isEmpty();
	}
	
	default boolean hasNonPropertyComparisons() {
		return hasIdComparisons() || hasFulltextComparisons();
	}
	
	
	
	/**
	 * Returns true if (and only if) this analysis contains id comparisons but no non id comparisons.
	 * Equal to <code>return hasIdComparisons() && !hasNonIdComparisons()</code>
	 */
	default boolean hasIdComparisonsExclusively() {
		return hasIdComparisons() && !hasNonIdComparisons();
	}
	
	default boolean hasPropertyComparisonsExclusively() {
		return hasPropertyComparisons() && !hasNonPropertyComparisons();
	}
	
	default List<ValueComparison> getComparisonsForProperty(String propertyName) {
		List<ValueComparison> comparisons = getPropertyComparisons().get(propertyName);
		return (comparisons == null) ? Collections.emptyList() : comparisons;
	}
	
	default boolean hasComparisonsForProperty(String propertyName) {
		return !getComparisonsForProperty(propertyName).isEmpty();
	}

	default boolean hasComparisonsForPropertyExclusively(String propertyName) {
		List<ValueComparison> comparisonsForProperty = getComparisonsForProperty(propertyName);
		return comparisonsForProperty.size() == 1 && !hasIdComparisons() && !hasFulltextComparisons();
	}

	default List<Object> getComparedIds() {
		return getComparedIds(Operator.equal, Operator.in, Operator.contains);
	}
	
	default List<Object> getComparedIds(Operator... includedOperators) {
		List<Operator> includedOperatorsList = Arrays.asList(includedOperators);
		
		return getIdComparisons()
			.stream()
			.filter((c) -> includedOperatorsList.contains(c.getOperator()))
			.map((c) -> {
				Object id = extractSimpleValueFromOperand(c.getLeftOperand());
				if (id != null) {
					return id;
				}
				return extractSimpleValueFromOperand(c.getRightOperand());
			})
			.collect(Collectors.toList());
	}
	
	default List<Object> getComparedPropertyValues(String propertyName) {
		return getComparedPropertyValues(propertyName, Operator.equal, Operator.in, Operator.contains);
	}
	
	default List<Object> getComparedPropertyValues(String propertyName, Operator... includedOperators) {
		List<Operator> includedOperatorsList = Arrays.asList(includedOperators);
		
		List<ValueComparison> comparisons = getPropertyComparisons().get(propertyName);
		if (comparisons == null) {
			return Collections.emptyList();
		}
		
		//@formatter:off
		List<Object> extractedValues =
			comparisons
			.stream()
			.filter((c) -> includedOperatorsList.contains(c.getOperator()))
			.map((c) -> {
				
				Object value = extractSimpleValueFromOperand(c.getLeftOperand());
				if (value != null) {
					return value;
				}
				return extractSimpleValueFromOperand(c.getRightOperand());
			})
			.collect(Collectors.toList());
		//@formatter:on
		
		List<Object> result = new ArrayList<Object>();
		extractedValues
			.stream()
			.forEach(extractedValue -> {
				if (extractedValue instanceof Collection<?>) {
					Collection<?> collection = (Collection<?>) extractedValue;
					result.addAll(collection);
				} else {
					result.add(extractedValue);
				}
			});
		
		return result;
		
	}
	
	default Object extractSimpleValueFromOperand(Object operand) {
		if (operand == null) {
			return null;
		}
		
		if (operand instanceof Collection<?>) {
			Collection<?> collection = (Collection<?>) operand;
			return collection
				.stream()
				.map(this::resolveSimpleOperand)
				.collect(Collectors.toList());
		} 	
		return resolveSimpleOperand(operand);
	}

	default Object resolveSimpleOperand(Object operand) {
		operand = resolveReferences(operand);

		GenericModelType actualType = BaseType.INSTANCE.getActualType(operand);
		switch (actualType.getTypeCode()) {
		case booleanType:
		case dateType:
		case decimalType:
		case doubleType:
		case floatType:
		case integerType:
		case longType:
		case stringType:
		case enumType:
			return operand;
		default:
			return null;
		}
	}

	default Object resolveReferences(Object operand) {
		if (operand instanceof EntityReference) {
			return ((EntityReference) operand).getRefId();
		}
		if (operand instanceof EnumReference) {
			return ((EnumReference) operand).constant();
		}
		return operand;
	}
}
