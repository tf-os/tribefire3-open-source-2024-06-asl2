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
package com.braintribe.model.processing.query.planner.context;

import static com.braintribe.utils.lcd.CollectionTools2.newList;

import java.util.List;

import com.braintribe.model.processing.query.planner.RuntimeQueryPlannerException;
import com.braintribe.model.processing.query.planner.builder.ConditionBuilder;
import com.braintribe.model.query.conditions.AbstractJunction;
import com.braintribe.model.query.conditions.Condition;
import com.braintribe.model.query.conditions.FulltextComparison;
import com.braintribe.model.query.conditions.Negation;
import com.braintribe.model.query.conditions.ValueComparison;
import com.braintribe.model.queryplan.filter.Junction;
import com.braintribe.model.queryplan.value.Value;

/**
 * @author peter.gazdik
 */
class ConditionConverter {

	private final QueryPlannerContext context;

	public ConditionConverter(QueryPlannerContext context) {
		this.context = context;
	}

	public com.braintribe.model.queryplan.filter.Condition convert(Condition condition) {
		switch (condition.conditionType()) {
			case conjunction:
				return convertJunction(ConditionBuilder.newConjunction(), (AbstractJunction) condition);

			case disjunction:
				return convertJunction(ConditionBuilder.newDisjunction(), (AbstractJunction) condition);

			case fulltextComparison:
				return convertFulltext((FulltextComparison) condition);

			case negation:
				return convertNegation((Negation) condition);

			case valueComparison:
				return convertValueComparison((ValueComparison) condition);
		}

		throw new RuntimeQueryPlannerException("Unsupported condition: " + condition + " of type: " + condition.conditionType());
	}

	private com.braintribe.model.queryplan.filter.Condition convertJunction(Junction junction, AbstractJunction condition) {
		List<com.braintribe.model.queryplan.filter.Condition> operands = newList();

		for (Condition operand: condition.getOperands())
			operands.add(convert(operand));

		junction.setOperands(operands);

		return junction;
	}

	private com.braintribe.model.queryplan.filter.Condition convertNegation(Negation condition) {
		return ConditionBuilder.newNegation(convert(condition.getOperand()));
	}

	private com.braintribe.model.queryplan.filter.Condition convertFulltext(FulltextComparison condition) {
		int entityIndex = context.sourceManager().indexForSource(condition.getSource());
		String text = condition.getText();

		return ConditionBuilder.fullText(entityIndex, text);
	}

	private com.braintribe.model.queryplan.filter.Condition convertValueComparison(ValueComparison condition) {
		Value left = context.convertOperand(condition.getLeftOperand());
		Value right = context.convertOperand(condition.getRightOperand());

		switch (condition.getOperator()) {
			case contains:
				return ConditionBuilder.newContains(left, right);
			case equal:
				return ConditionBuilder.newEquality(left, right);
			case greater:
				return ConditionBuilder.newGreaterThan(left, right);
			case greaterOrEqual:
				return ConditionBuilder.newGreaterThanOrEqual(left, right);
			case ilike:
				return ConditionBuilder.newILike(left, right);
			case in:
				return ConditionBuilder.newIn(left, right);
			case less:
				return ConditionBuilder.newLessThan(left, right);
			case lessOrEqual:
				return ConditionBuilder.newLessThanOrEqual(left, right);
			case like:
				return ConditionBuilder.newLike(left, right);
			case notEqual:
				return ConditionBuilder.newUnequality(left, right);
		}

		throw new RuntimeQueryPlannerException("Unsupported comparison operator: " + condition.getOperator());
	}

}
