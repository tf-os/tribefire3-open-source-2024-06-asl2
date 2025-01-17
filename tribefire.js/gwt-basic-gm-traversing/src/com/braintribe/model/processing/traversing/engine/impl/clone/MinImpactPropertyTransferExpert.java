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
package com.braintribe.model.processing.traversing.engine.impl.clone;

import static com.braintribe.utils.lcd.CollectionTools2.newList;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.braintribe.model.generic.GMF;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.CollectionType;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.reflection.ListType;
import com.braintribe.model.generic.reflection.MapType;
import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.generic.reflection.PropertyAccessInterceptor;
import com.braintribe.model.generic.reflection.SetType;
import com.braintribe.model.processing.manipulation.basic.mindelta.ChangeMapWithMinDelta;
import com.braintribe.model.processing.manipulation.basic.mindelta.ChangeSetWithMinDelta;
import com.braintribe.model.processing.traversing.engine.api.customize.PropertyTransferContext;
import com.braintribe.model.processing.traversing.engine.api.customize.PropertyTransferExpert;

/**
 * TODO it makes much more sense to put this logic inside a manipulation tracking {@link PropertyAccessInterceptor}, as that can be applied in
 * situations other than cloning.
 * 
 * @author peter.gazdik
 */
public class MinImpactPropertyTransferExpert implements PropertyTransferExpert {

	private final List<CollectionHandler<?, ?>> collectionHandlers = newList();

	@Override
	public void transferProperty(GenericEntity clonedEntity, Property property, Object clonedValue, PropertyTransferContext context) {
		if (clonedValue == null) {
			justSetIt(clonedEntity, property, clonedValue);
			return;
		}

		GenericModelType type = property.getType();
		if (type.isBase())
			type = GMF.getTypeReflection().getType(clonedValue);

		if (!type.isCollection()) {
			justSetIt(clonedEntity, property, clonedValue);
			return;
		}

		CollectionType collectionType = type.cast();

		CollectionHandler<CollectionType, Object> handler = (CollectionHandler<CollectionType, Object>) newHandlerFor(collectionType);
		handler.property = property;
		handler.clonedEntity = clonedEntity;
		handler.valueType = collectionType;
		handler.clonedValue = clonedValue;

		collectionHandlers.add(handler);
	}

	public void onCloningFinished() {
		for (CollectionHandler<?, ?> handler : collectionHandlers)
			handler.run();
	}

	protected CollectionHandler<?, ?> newHandlerFor(CollectionType type) {
		switch (type.getCollectionKind()) {
			case list:
				return new ListHandler();
			case set:
				return new SetHandler();
			case map:
				return new MapHandler();
		}

		throw new IllegalStateException("This is unreachable.");
	}

	static void justSetIt(GenericEntity clonedEntity, Property property, Object clonedValue) {
		property.set(clonedEntity, clonedValue);
	}

	public static abstract class CollectionHandler<T extends CollectionType, V> {
		// none of the properties can be null
		public GenericEntity clonedEntity;
		public Property property;
		public T valueType;
		public V clonedValue;

		public final void run() {
			// TODO what if the property was absent?
			Object currentPropertyValue = property.get(clonedEntity);

			if (currentPropertyValue != null && valueType.isInstance(currentPropertyValue)) {
				/* in case our property is of type Object, the oldPropertyValue could be a different collection than then new one, thus we also have
				 * to set directly */
				if (!currentPropertyValue.equals(clonedValue))
					findMinimalWayToModifyCollection((V) currentPropertyValue);
			} else {
				if (currentPropertyValue != clonedValue)
					property.set(clonedEntity, clonedValue);
			}
		}

		protected abstract void findMinimalWayToModifyCollection(V oldValue);

	}

	static class ListHandler extends CollectionHandler<ListType, List<Object>> {
		@Override
		protected void findMinimalWayToModifyCollection(List<Object> oldValue) {
			// let's leave the List for later
			justSetIt(clonedEntity, property, clonedValue);
		}
	}

	static class SetHandler extends CollectionHandler<SetType, Set<Object>> {
		@Override
		protected void findMinimalWayToModifyCollection(Set<Object> currentPropertyValue) {
			ChangeSetWithMinDelta.apply(clonedEntity, property, currentPropertyValue, clonedValue);
		}
	}

	static class MapHandler extends CollectionHandler<MapType, Map<Object, Object>> {
		@Override
		protected void findMinimalWayToModifyCollection(Map<Object, Object> currentPropertyValue) {
			ChangeMapWithMinDelta.apply(clonedEntity, property, currentPropertyValue, clonedValue);
		}
	}

}
