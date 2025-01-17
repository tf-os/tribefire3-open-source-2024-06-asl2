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
package com.braintribe.model.processing.findrefs;

import java.util.ArrayList;
import java.util.List;

import com.braintribe.model.generic.value.EntityReference;
import com.braintribe.model.processing.findrefs.MapCandidateProperty.MapRefereeType;
import com.braintribe.model.query.From;
import com.braintribe.model.query.Join;
import com.braintribe.model.query.Operator;
import com.braintribe.model.query.PropertyOperand;
import com.braintribe.model.query.conditions.Condition;
import com.braintribe.model.query.conditions.Disjunction;
import com.braintribe.model.query.conditions.ValueComparison;
import com.braintribe.model.query.functions.MapKey;

/**
 * The {@link PropertyConditionCreator} creates a {@link Condition} to check whether a property references a specific
 * entity.
 * 
 * 
 */
public class PropertyConditionCreator {

	/**
	 * Creates a condition to check whether the property specified by <code>candidateProperty</code> references the
	 * entity specified by <code>reference</code>
	 */
	public Condition createConditionForProperty(CandidateProperty candidateProperty, EntityReference reference) {

		if (candidateProperty instanceof MapCandidateProperty) {
			MapCandidateProperty mapCandidateProp = (MapCandidateProperty) candidateProperty;
			return createConditionForMapCandidate(reference, mapCandidateProp);
		} else if (candidateProperty instanceof ListCandidateProperty) {
			return createPropertyComparison(candidateProperty, Operator.contains, reference);
		} else {
			return createPropertyComparison(candidateProperty, Operator.equal, reference);
		}
	}

	private Condition createConditionForMapCandidate(EntityReference reference,
			MapCandidateProperty mapCandidateProp) {

		MapRefereeType refereeType = mapCandidateProp.getRefereeType();

		switch (refereeType) {
		case KEY_REF:
			return createMapKeysContainComparison(mapCandidateProp, reference);
		case VALUE_REF:
			return createPropertyContainsValueComparison(mapCandidateProp, reference);
		case BOTH:
			return createMapKeysOrValuesContainDisjunction(mapCandidateProp, reference);
		default:
			/*
			 * This should never be reached since candidates are only added when there are potential references in key,
			 * value or both
			 */
			return null;
		}
	}

	private Condition createMapKeysOrValuesContainDisjunction(MapCandidateProperty mapCandidateProp,
			EntityReference reference) {
		Disjunction disjunction = Disjunction.T.create();
		List<Condition> operands = new ArrayList<Condition>(2);

		ValueComparison keyComparison = createMapKeysContainComparison(mapCandidateProp, reference);
		operands.add(keyComparison);

		ValueComparison valueComparison = createPropertyContainsValueComparison(mapCandidateProp, reference);
		operands.add(valueComparison);

		disjunction.setOperands(operands);
		return disjunction;
	}

	private ValueComparison createMapKeysContainComparison(MapCandidateProperty mapCandidateProp,
			EntityReference reference) {
		String propertyName = mapCandidateProp.getPropertyName();
		String entityTypeSignature = mapCandidateProp.getEntityTypeSignature();
		Join join = createJoinWithFrom(propertyName, entityTypeSignature);
		
		MapKey mapKeyFunction = MapKey.T.create();
		mapKeyFunction.setJoin(join);
		return createContainsComparison(mapKeyFunction, reference);
	}

	private Join createJoinWithFrom(String propertyName, String entityTypeSignature) {
		Join join = Join.T.create();
		join.setProperty(propertyName);
		From from = From.T.create();
		from.setEntityTypeSignature(entityTypeSignature);
		join.setSource(from);
		return join;
	}

	private ValueComparison createPropertyContainsValueComparison(MapCandidateProperty mapCandidateProp,
			EntityReference reference) {
		PropertyOperand propertyOperand = createOperandForProperty(mapCandidateProp);
		return createContainsComparison(propertyOperand, reference);
	}

	private ValueComparison createContainsComparison(Object leftOperand, EntityReference reference) {
		return createComparison(leftOperand, Operator.contains, reference);
	}

	private ValueComparison createPropertyComparison(CandidateProperty candidateProperty, Operator operator,
			EntityReference reference) {
		PropertyOperand propertyOperand = createOperandForProperty(candidateProperty);
		return createComparison(propertyOperand, operator, reference);
	}

	private ValueComparison createComparison(Object leftOperand, Operator operator, EntityReference reference) {
		ValueComparison comparison = ValueComparison.T.create();
		comparison.setLeftOperand(leftOperand);
		comparison.setOperator(operator);
		comparison.setRightOperand(reference);
		return comparison;
	}

	private PropertyOperand createOperandForProperty(CandidateProperty candidateProperty) {
		String propertyName = candidateProperty.getPropertyName();
		return propertyOperand(propertyName);
	}
	
	private static PropertyOperand propertyOperand(String path) {
		PropertyOperand result = PropertyOperand.T.create();
		result.setPropertyName(path);

		return result;
	}
}
