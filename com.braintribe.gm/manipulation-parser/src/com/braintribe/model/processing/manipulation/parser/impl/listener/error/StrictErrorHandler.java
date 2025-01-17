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
package com.braintribe.model.processing.manipulation.parser.impl.listener.error;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.CollectionType;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EnumType;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.reflection.MapType;
import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.processing.manipulation.parser.api.GmmlManipulatorErrorHandler;
import com.braintribe.model.processing.manipulation.parser.api.GmmlParserException;

/**
 * {@link GmmlManipulatorErrorHandler} for the non-lenient approach, throwing exception on any error.
 * 
 * @author peter.gazdik
 */
public class StrictErrorHandler implements GmmlManipulatorErrorHandler {

	public static final StrictErrorHandler INSTANCE = new StrictErrorHandler();

	protected StrictErrorHandler() {
	}

	@Override
	public void typeNotFound(String typeSignature) {
		throw new IllegalStateException("Type not found: " + typeSignature);
	}

	@Override
	public void propertyNotFound(GenericEntity entity, String propertyName) {
		throw new IllegalStateException(
				"Property not found: " + propertyName + " for entity '" + entity + "' of type: " + entity.entityType().getTypeSignature());
	}

	@Override
	public void enumConstantNotFound(EnumType enumType, String enumConstantName) {
		throw new IllegalStateException("Could not resolve enum constant " + enumType.getTypeSignature() + "::" + enumConstantName);
	}

	@Override
	public void entityNotFound(String globalId) {
		throw new IllegalStateException("Entity not found for globalId: " + globalId);
	}

	@Override
	public void problematicEntityReferenced(String globalId) {
		throw new IllegalStateException(
				"Problematic entity (which is configured for the manipulator by the client code) is being referenced: " + globalId);
	}

	@Override
	public void variableNotEntityType(String variableName) {
		throw new IllegalStateException("Value of variable " + variableName + " did not have the expected type " + EntityType.class.getName());
	}

	@Override
	public void propertyNotCollection(GenericEntity entity, Property property) {
		Object value = property.getDirectUnsafe(entity);
		throw new IllegalStateException("Property " + property + " is not a collection. Current value: '" + value + "', entity:" + entity);
	}

	@Override
	public void propertyValueNotCollectionCannotClear(GenericEntity entity, Property property, Object value) {
		throw new IllegalStateException(
				"Cannot clear property '" + property + "' of entity '" + entity + "' as it is not a collection. Property value: " + value);
	}

	@Override
	public void propertySettingFailed(GenericEntity entity, Property property, Object value, RuntimeException e) {
		throw new IllegalStateException("Value '" + value + "' for property '" + property + "' cannot be set. Entity:" + entity, e);
	}

	@Override
	public void wrongValueTypeToAddToCollection(Object value, CollectionType type) {
		throw new GmmlParserException("Invalid value type. Cannot add '" + value + "' to:" + type.getTypeSignature());
	}

	@Override
	public void wrongTypeForListAdd(Object index, boolean indexOk, Object value, boolean valueOk, GenericModelType valueType) {
		throw new GmmlParserException("Invalid value type. Cannot add element to list. Index: '" + index + "' , value: '" + value + "', value type:"
				+ valueType.getTypeSignature());
	}

	@Override
	public void wrongTypeForMapPut(Object key, boolean keyOk, Object value, boolean valueOk, MapType type) {
		throw new GmmlParserException("Cannot put entry ('" + key + "', '" + value + "') to: " + type);
	}

	@Override
	public void cannotResolvePropertyOfNonEntity(Object nonEntity, String propertyName) {
		throw new IllegalStateException("Cannot resolve property '" + propertyName + ". Owner is not an entity, but: " + nonEntity);
	}
	@Override
	public void typeNotGenericModelType(Object type) {
		throw new IllegalStateException("Value of the stack was not a GenericModelType. Value: " + type);
	}

	// globalId

	@Override
	public void globalIdSettingFailed(GenericEntity entity, Object globalId, RuntimeException e) {
		throw e;
	}

	@Override
	public void globalIdAdjusted(GenericEntity entity, Object globalId, String newGlobalId, RuntimeException e) {
		// should be unreachable, as globalIdSettingError throws exception
	}

}
