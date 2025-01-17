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
package com.braintribe.model.generic.reflection;

import static com.braintribe.utils.lcd.CollectionTools2.newLinkedSet;
import static com.braintribe.utils.lcd.CollectionTools2.newList;
import static com.braintribe.utils.lcd.CollectionTools2.newMap;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.braintribe.common.lcd.UnknownEnumException;
import com.braintribe.model.bvd.time.Now;
import com.braintribe.model.generic.GMF;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.value.EnumReference;

/**
 * @author peter.gazdik
 */
public abstract class EntityInitializerImpl implements EntityInitializer {

	protected Property property;

	@Override
	public abstract void initialize(GenericEntity entity);

	public static EntityInitializer newInstance(Property property) {
		return newInstance(property, property.getInitializer());
	}

	public static EntityInitializer newInstance(Property property, Object initializer) {
		EntityInitializerImpl result = newInitializerFor(property, initializer);
		result.property = property;

		return result;
	}

	private static EntityInitializerImpl newInitializerFor(Property property, Object initializer) {
		if (initializer instanceof Now)
			return new CurrentDateInitializer();

		Object resolvedInitializer = resolveInitializer(property, initializer);

		return new StaticInitializer(resolvedInitializer);
	}

	private static Object resolveInitializer(Property p, Object initializer) {
		switch (p.getType().getTypeCode()) {
			case booleanType:
			case dateType:
			case decimalType:
			case doubleType:
			case floatType:
			case integerType:
			case longType:
			case stringType:
				return initializer;

			case enumType:
				if (initializer instanceof EnumReference)
					initializer = resolveEnumConstant((EnumReference) initializer);
				return initializer;

			case listType:
				return resolveCollection(p, newList(), (Collection<?>) initializer);
			case setType:
				return resolveCollection(p, newLinkedSet(), (Collection<?>) initializer);
			case mapType:
				return resolveMap(p, (Map<?, ?>) initializer);

			case objectType:
				return resolveObject(p, initializer);

			case entityType:
				throw new IllegalArgumentException("Cannot create initializer for an entity. " + p + ", initializer: " + initializer);

			default:
				throw new UnknownEnumException(p.getType().getTypeCode());
		}
	}

	private static Object resolveObject(Property p, Object o) {
		if (o instanceof Collection)
			if (o instanceof List)
				return resolveCollection(p, newList(), (Collection<?>) o);
			else
				return resolveCollection(p, newLinkedSet(), (Collection<?>) o);

		if (o instanceof Map)
			return resolveMap(p, (Map<?, ?>) o);

		return resolveScalar(p, o);

	}

	private static Collection<?> resolveCollection(Property p, Collection<Object> result, Collection<?> original) {
		for (Object o : original)
			result.add(resolveScalar(p, o));

		return result;
	}

	private static Map<?, ?> resolveMap(Property p, Map<?, ?> original) {
		Map<Object, Object> result = newMap();

		for (Entry<?, ?> e : original.entrySet())
			result.put(resolveScalar(p, e.getKey()), resolveScalar(p, e.getValue()));

		return result;
	}

	private static Object resolveScalar(Property p, Object o) {
		if (o instanceof GenericEntity) {
			if (o instanceof EnumReference)
				return resolveEnumConstant((EnumReference) o);

			throw new RuntimeException("No initializer implementation exists for initializer: " + o + ", " + p);
		}

		return o;
	}

	private static Object resolveEnumConstant(EnumReference enumReference) {
		EnumType et = GMF.getTypeReflection().getType(enumReference.getTypeSignature());
		return et.getEnumValue(enumReference.getConstant());
	}

	static class StaticInitializer extends EntityInitializerImpl {
		private final Object value;

		public StaticInitializer(Object value) {
			this.value = value;
		}

		@Override
		public void initialize(GenericEntity entity) {
			property.set(entity, value);
		}
	}

	static class CurrentDateInitializer extends EntityInitializerImpl {
		@Override
		public void initialize(GenericEntity entity) {
			property.set(entity, new Date());
		}
	}
}
