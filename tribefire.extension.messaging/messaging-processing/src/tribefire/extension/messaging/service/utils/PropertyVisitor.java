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
package tribefire.extension.messaging.service.utils;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.braintribe.model.generic.GMF;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.reflection.Property;
import tribefire.extension.messaging.model.comparison.CollectionType;
import tribefire.extension.messaging.model.comparison.PropertyModel;

/**
 * This utility is meant for extraction of properties by their path from complex objects or collections of such.
 * Index and key extraction is supported for collections
 */
public class PropertyVisitor {
	public Object visit(Object object, String propertyPath) {
		ArrayDeque<PropertyModel> deque = populateDeque(propertyPath);
		return dequeCycle(deque, object);
	}

	Object dequeCycle(ArrayDeque<PropertyModel> deque, Object object) {
		Object result = object;
		while (!deque.isEmpty()) {
			GenericModelType type = GMF.getTypeReflection().getType(result);
			if (result instanceof GenericEntity e) {
				PropertyModel property = deque.pop();
				result = extractFromGenericEntity(e, property.getProperty());
			} else if (type.isCollection()) {
				result = visitCollection(deque, result);
				deque.pop();
			} else {
				throw new IllegalArgumentException(
						"Impossible to continue extraction as current element is a primitive and nothing can be extracted out of it!");
			}
		}
		return result;
	}

	private Object visitCollection(ArrayDeque<PropertyModel> deque, Object c) {
		if (deque.element().getIsIndex()) {
			PropertyModel property = deque.pop();
			Object o = extractIndexed(c, property);
			return dequeCycle(deque.clone(), o);
		} else {
			if (c instanceof Collection<?> l) {
				return l.stream().map(o -> new PropertyVisitor().dequeCycle(deque.clone(), o)).toList();
			} else if (c instanceof Map<?, ?> m) {
				return m.values().stream().map(o -> new PropertyVisitor().dequeCycle(deque.clone(), o)).toList();
			} else {
				throw new IllegalArgumentException("Unsupported collection type !!!");
			}
		}
	}

	private Object extractIndexed(Object collection, PropertyModel property) {
		String index = property.getIndex();
		if (collection instanceof List<?> l) {
			return l.get(Integer.parseInt(index));
		} else if (collection instanceof Map<?, ?> m) {
			if (m.containsKey(index)) {
				return m.get(index);
			} else {
				throw new IllegalArgumentException(String.format("Key: %s is not contained in current Map!", index));
			}
		} else {
			throw new IllegalArgumentException("Unsupported collection type for extraction by index!");
		}
	}

	private Object extractFromGenericEntity(GenericEntity e, String path) {
		for (Property p : e.entityType().getProperties()) {
			if (p.getName().equals(path)) {
				return p.get(e);
			}
		}
		throw new IllegalArgumentException("Could not extract property! No Such found!");
	}

	private ArrayDeque<PropertyModel> populateDeque(String propertyPath) {
		List<String> listing = Arrays.stream(propertyPath.split("\\.")).toList();
		ArrayDeque<PropertyModel> deque = new ArrayDeque<>();
		for (String element : listing) {
			if (element.endsWith("]")) {
				addIndexProperty(element, CollectionType.LIST, deque);
			} else if (element.endsWith(")")) {
				addIndexProperty(element, CollectionType.MAP, deque);
			} else {
				deque.add(PropertyModel.regularProperty(element));
			}
		}
		return deque;
	}

	private void addIndexProperty(String element, CollectionType indexType, ArrayDeque<PropertyModel> deque) {
		int index = element.indexOf(indexType == CollectionType.LIST ? '[' : '(');
		deque.add(PropertyModel.regularProperty(element.substring(0, index)));
		deque.add(PropertyModel.indexProperty(element.substring(index), indexType));
	}

	public PropertyVisitor() {
		// Empty constructor
	}
}
