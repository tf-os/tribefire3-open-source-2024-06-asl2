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
package com.braintribe.tribefire.jdbc.statement;

import java.util.function.Function;

import com.braintribe.model.processing.query.fluent.ConditionBuilder;
import com.braintribe.model.processing.query.fluent.OperandBuilder;
import com.braintribe.model.processing.query.fluent.ValueComparisonBuilder;
import com.braintribe.tribefire.jdbc.statement.TargetExpression.Type;

/**
 * The Class PlaceholderOperandBuilder.
 *
 */
public class PlaceholderOperandBuilder {
	
	private ConditionBuilder<?> whereBuilder;
	private TargetExpression leftExpression;
	private TargetExpression rightExpression;
	private Function<ValueComparisonBuilder<?>, OperandBuilder<?>> operandSupplier;
	
	private Object leftObject;
	private Object rightObject;
	private int valueSetCount = 0;

	/**
	 * Instantiates a new placeholder operand builder.
	 *
	 * @param whereBuilder
	 *            the where builder
	 * @param leftExpression
	 *            the left expression
	 * @param rightExpression
	 *            the right expression
	 * @param operandSupplier
	 *            the operand supplier
	 */
	public PlaceholderOperandBuilder(ConditionBuilder<?> whereBuilder, TargetExpression leftExpression, TargetExpression rightExpression, java.util.function.Function<ValueComparisonBuilder<?>, OperandBuilder<?>> operandSupplier) {
		this.whereBuilder = whereBuilder;
		this.leftExpression = leftExpression;
		this.rightExpression = rightExpression;
		this.operandSupplier = operandSupplier;
	}
	
	/**
	 * Checks for parameter name.
	 *
	 * @param name
	 *            the name
	 * @return true, if successful
	 */
	public boolean hasParameterName(String name) {
		if (leftExpression.getType() == Type.namedparameter && leftExpression.getName().equals(name)) {
			return true;
		}
		if (rightExpression.getType() == Type.namedparameter && rightExpression.getName().equals(name)) {
			return true;
		}
		return false;
	}
	
	/**
	 * Sets the value.
	 *
	 * @param index
	 *            the index
	 * @param value
	 *            the value
	 */
	public void setValue(int index, Object value) {
		if (leftExpression.getType() == Type.indexedparameter && leftExpression.getIndex() == index) {
			leftObject = value;
		} else if (rightExpression.getType() == Type.indexedparameter && rightExpression.getIndex() == index) {
			rightObject = value;
		} else {
			throw new RuntimeException("Unknown parameter index "+index);
		}
		valueSetCount++;
		if (valueSetCount == getNumberOfParameters()) {
			done();
		}
	}
	
	/**
	 * Sets the value.
	 *
	 * @param name
	 *            the name
	 * @param value
	 *            the value
	 */
	public void setValue(String name, Object value) {
		if (leftExpression.getType() == Type.namedparameter && leftExpression.getName().equals(name)) {
			leftObject = value;
		} else if (rightExpression.getType() == Type.namedparameter && rightExpression.getName().equals(name)) {
			rightObject = value;
		} else {
			throw new RuntimeException("Unknown parameter name "+name);
		}
		valueSetCount++;
		if (valueSetCount == getNumberOfParameters()) {
			done();
		}
	}
	
	public int getNumberOfParameters() {
		int count = 0;
		if (leftExpression.getType() == Type.indexedparameter || leftExpression.getType() == Type.namedparameter) {
			count++;
		}
		if (rightExpression.getType() == Type.indexedparameter || rightExpression.getType() == Type.namedparameter) {
			count++;
		}
		return count;
	}
	
	/**
	 * Done.
	 */
	private void done() {
		doLeftSide();
	}
	
	/**
	 * Do right side.
	 *
	 * @param vcb
	 *            the vcb
	 */
	private void doRightSide(ValueComparisonBuilder<?> vcb) {
		switch(rightExpression.getType()) {
			case indexedparameter:
			case namedparameter:
				OperandBuilder<?> paramValueOp = operandSupplier.apply(vcb);
				paramValueOp.value(rightObject);
				break;
			case property:
				if (rightExpression.getAlias() == null) {
					OperandBuilder<?> namedOp = operandSupplier.apply(vcb);
					namedOp.property(rightExpression.getName());
				} else {
					OperandBuilder<?> aliasedOp = operandSupplier.apply(vcb);
					aliasedOp.property(rightExpression.getAlias(), rightExpression.getName());
				}
				break;
			case value:
				OperandBuilder<?> valueOp = operandSupplier.apply(vcb);
				valueOp.value(rightExpression.getValue());
				break;
			default:
				throw new RuntimeException("Unsupported expression type "+rightExpression.getType()+" ("+rightExpression+")");
		}
	}
	
	/**
	 * Do left side.
	 */
	private void doLeftSide() {
		switch(leftExpression.getType()) {
			case indexedparameter:
			case namedparameter:
				ValueComparisonBuilder<?> paramVcb = whereBuilder.value(leftObject);
				doRightSide(paramVcb);
				break;
			case property:
				ValueComparisonBuilder<?> property;
				if (leftExpression.getAlias() == null) {
					property = whereBuilder.property(leftExpression.getName());
				} else {
					property = whereBuilder.property(leftExpression.getAlias(), leftExpression.getName());
				}
				doRightSide(property);
				break;
			case value:
				ValueComparisonBuilder<?> value = whereBuilder.value(leftExpression.getValue());
				doRightSide(value);
				break;
			default:
				break;
			
		}
	}
}
