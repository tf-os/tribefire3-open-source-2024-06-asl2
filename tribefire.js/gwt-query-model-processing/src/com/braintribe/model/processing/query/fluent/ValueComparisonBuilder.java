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
package com.braintribe.model.processing.query.fluent;

import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.value.EntityReference;
import com.braintribe.model.query.Operator;
import com.braintribe.model.query.conditions.ValueComparison;

public class ValueComparisonBuilder<T> {
	private final T backLink;
	private final SourceRegistry sourceRegistry;
	private final Consumer<? super ValueComparison> receiver;
	private Object leftOperand;
	
	public ValueComparisonBuilder(SourceRegistry sourceRegistry, Object leftOperand, T backLink, Consumer<? super ValueComparison> receiver) {
		this.receiver = receiver;
		this.sourceRegistry = sourceRegistry;
		this.backLink = backLink;
		this.leftOperand = leftOperand;
	}
	
	public void setLeftOperand(Object leftOperand) {
		this.leftOperand = leftOperand;
	}
	
	public T comparsion(Operator operator, Object value) {
		try {
			ValueComparison comparison = ValueComparison.T.create();
			comparison.setOperator(operator);
			comparison.setLeftOperand(leftOperand);
			
			comparison.setRightOperand(value);
			
			receiver.accept(comparison);
			return backLink;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public T eq(Object value) {
		if (value instanceof Enum)
			return eq().enumConstant((Enum<?>) value);
		else
			return comparsion(Operator.equal, value);
	}
	
	public T ne(Object value) {
		return comparsion(Operator.notEqual, value);
	}
	
	public T gt(Object value) {
		return comparsion(Operator.greater, value);
	}
	
	public T ge(Object value) {
		return comparsion(Operator.greaterOrEqual, value);
	}
	
	public T lt(Object value) {
		return comparsion(Operator.less, value);
	}
	
	public T le(Object value) {
		return comparsion(Operator.lessOrEqual, value);
	}
	
	/**
	 * Note that the GM query framework does not use the SQL special characters (%, _), but 'command line' ones (*, ?).
	 * To escape a character use a backslash. So, if you want to match everything, use <code>"*"</code>, if you want to
	 * match a string containing exactly one asterisk, use: <code>"\\*"</code>.
	 */
	public T like(String value) {
		return comparsion(Operator.like, value);
	}
	
	/**
	 * @see #like(String)
	 */
	public T ilike(String value) {
		return comparsion(Operator.ilike, value);
	}
	
	public T in(Set<?> set) {
		return comparsion(Operator.in, set);
	}
	
	public T inEntities(Set<? extends GenericEntity> set) {
		Set<EntityReference> referenceSet = set.stream() //
				.<EntityReference> map(GenericEntity::reference) //
				.collect(Collectors.toSet());
			
		return in(referenceSet);
	}
	
	public OperandBuilder<T> eq() {
		return comparison(Operator.equal);
	}
	
	public OperandBuilder<T> ne() {
		return comparison(Operator.notEqual);
	}
	
	public OperandBuilder<T> gt() {
		return comparison(Operator.greater);
	}
	
	public OperandBuilder<T> ge() {
		return comparison(Operator.greaterOrEqual);
	}
	
	public OperandBuilder<T> lt() {
		return comparison(Operator.less);
	}
	
	public OperandBuilder<T> le() {
		return comparison(Operator.lessOrEqual);
	}
	
	public OperandBuilder<T> in() {
		return comparison(Operator.in);
	}
	
	public OperandBuilder<T> contains() {
		return comparison(Operator.contains);
	}

	public OperandBuilder<T> comparison(Operator operator) {
		return new OperandBuilder<T>(sourceRegistry, backLink, value -> {
			ValueComparison comparison = ValueComparison.T.create();
			comparison.setOperator(operator);
			comparison.setLeftOperand(leftOperand);
			comparison.setRightOperand(value);
			
			receiver.accept(comparison);
		});
	}

}
