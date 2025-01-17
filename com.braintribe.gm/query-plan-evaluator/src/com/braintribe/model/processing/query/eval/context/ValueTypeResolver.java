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

import com.braintribe.model.generic.GMF;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.reflection.GenericModelTypeReflection;
import com.braintribe.model.processing.query.eval.api.QueryEvaluationContext;
import com.braintribe.model.processing.query.eval.api.RuntimeQueryEvaluationException;
import com.braintribe.model.processing.query.eval.tools.PropertyPathResolver;
import com.braintribe.model.queryplan.TupleComponentPosition;
import com.braintribe.model.queryplan.set.IndexSet;
import com.braintribe.model.queryplan.set.QuerySourceSet;
import com.braintribe.model.queryplan.set.ReferenceableTupleSet;
import com.braintribe.model.queryplan.set.SourceSet;
import com.braintribe.model.queryplan.set.join.PropertyJoin;
import com.braintribe.model.queryplan.value.AggregateFunction;
import com.braintribe.model.queryplan.value.HashSetProjection;
import com.braintribe.model.queryplan.value.StaticValue;
import com.braintribe.model.queryplan.value.TupleComponent;
import com.braintribe.model.queryplan.value.Value;
import com.braintribe.model.queryplan.value.ValueProperty;

/**
 * 
 */
class ValueTypeResolver {

	private static final GenericModelTypeReflection typeReflection = GMF.getTypeReflection();

	private final QueryEvaluationContext context;

	public ValueTypeResolver(QueryEvaluationContext context) {
		this.context = context;
	}

	public <T extends GenericModelType> T resolveType(Value value) {
		return (T) resolveTypeHelper(value);
	}

	private GenericModelType resolveTypeHelper(Value value) {
		switch (value.valueType()) {
			case aggregateFunction:
				return resolveTypeHelper(((AggregateFunction) value).getOperand());
			case queryFunction:
				throw new RuntimeQueryEvaluationException("Resolving type for query-function is not supported. Function: " + value);
			case hashSetProjection:
				return resolveTypeHelper(((HashSetProjection) value).getValue());
			case staticValue:
				return resolveTypeHelper((StaticValue) value);
			case tupleComponent:
				return resolveTypeHelper((TupleComponent) value);
			case valueProperty:
				return resolveTypeHelper((ValueProperty) value);
			default:
				throw new RuntimeQueryEvaluationException("Unsupported Value: " + value + " of type: " + value.valueType());
		}
	}

	private GenericModelType resolveTypeHelper(StaticValue staticValue) {
		Object value = staticValue.getValue();

		return typeReflection.getType(value);
	}

	private GenericModelType resolveTypeHelper(TupleComponent tupleComponent) {
		TupleComponentPosition tupleComponentPosition = context.findTupleComponentPosition(tupleComponent.getTupleComponentIndex());

		if (!(tupleComponentPosition instanceof ReferenceableTupleSet))
			/* As we start by resolving a ValueProperty, we can never have one of the "JoinedCollectionKey"s here, only
			 * a ReferenceableTupleSet */
			throw new RuntimeQueryEvaluationException("Unsupported component type: " + tupleComponent);

		ReferenceableTupleSet tupleSet = (ReferenceableTupleSet) tupleComponentPosition;

		switch (tupleSet.tupleSetType()) {
			case indexRange:
			case indexSubSet:
				return resolveTypeFor((IndexSet) tupleSet);

			case querySourceSet:
				return resolveTypeFor((QuerySourceSet) tupleSet);
			case sourceSet:
				return resolveTypeFor((SourceSet) tupleSet);

			case entityJoin:
			case listJoin:
			case mapJoin:
			case setJoin:
				return resolveTypeFor((PropertyJoin) tupleSet);
			default:
				throw new RuntimeQueryEvaluationException("Unsupported TupleStye: " + tupleSet + " of type: " + tupleSet.tupleSetType());
		}
	}

	private GenericModelType resolveTypeFor(IndexSet tupleSet) {
		return resolveTypeForSignature(tupleSet.getTypeSignature());
	}

	private static GenericModelType resolveTypeFor(QuerySourceSet tupleSet) {
		return resolveTypeForSignature(tupleSet.getEntityTypeSignature());
	}
	
	private static GenericModelType resolveTypeFor(SourceSet tupleSet) {
		return resolveTypeForSignature(tupleSet.getTypeSignature());
	}

	private static GenericModelType resolveTypeForSignature(String typeSignature) {
		return typeReflection.getType(typeSignature);
	}

	private GenericModelType resolveTypeFor(PropertyJoin tupleSet) {
		return resolveTypeHelper(tupleSet.getValueProperty());
	}

	private GenericModelType resolveTypeHelper(ValueProperty valueProperty) {
		GenericModelType valueType = resolveType(valueProperty.getValue());

		return PropertyPathResolver.resolvePropertyPathType(valueType, valueProperty.getPropertyPath());
	}

}
