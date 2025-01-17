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
package com.braintribe.model.access.hibernate.tools;

import static com.braintribe.utils.lcd.CollectionTools2.newMap;
import static com.braintribe.utils.lcd.CollectionTools2.newSet;
import static java.util.stream.Collectors.toCollection;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.EntityManagerFactory;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.Metamodel;

import com.braintribe.model.access.hibernate.gm.CompositeIdValues;
import com.braintribe.model.generic.GMF;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.meta.GmEntityType;

/**
 * @author peter.gazdik
 */
public class HibernateMappingInfoProvider {

	private final Map<String, Set<String>> mappedPropertiesByEntity = newMap();
	private final Set<Property> mappedProperties = newSet();
	private final Set<String> compositeIdEntityTypes = newSet();

	public HibernateMappingInfoProvider(EntityManagerFactory emFactory) {
		Metamodel metamodel = emFactory.getMetamodel();

		for (javax.persistence.metamodel.EntityType<?> javaxEntityType : metamodel.getEntities())
			index(javaxEntityType);
	}

	private void index(javax.persistence.metamodel.EntityType<?> javaxEntityType) {
		String entityName = javaxEntityType.getJavaType().getName();

		if (hasCompositeId(javaxEntityType))
			compositeIdEntityTypes.add(entityName);

		Set<? extends Attribute<?, ?>> attributes = javaxEntityType.getAttributes();
		Set<String> propertyNamesSet = attributes.stream().map(Attribute::getName).collect(Collectors.toSet());

		mappedPropertiesByEntity.put(entityName, propertyNamesSet);

		EntityType<GenericEntity> entityType = GMF.getTypeReflection().getEntityType(entityName);

		propertyNamesSet.stream().map(this::ensureGmPropertyName) //
				.map(entityType::getProperty) //
				.collect(toCollection(() -> mappedProperties));
	}

	private boolean hasCompositeId(javax.persistence.metamodel.EntityType<?> javaxEntityType) {
		return javaxEntityType.getIdType().getJavaType() == CompositeIdValues.class;
	}

	public boolean isEntityMapped(GmEntityType gmEntityType) {
		return mappedPropertiesByEntity.containsKey(gmEntityType.getTypeSignature());
	}

	public boolean isEntityMapped(EntityType<?> entityType) {
		return mappedPropertiesByEntity.containsKey(entityType.getTypeSignature());
	}

	public boolean isPropertyMapped(String ownerTypeSignature, String propertyName) {
		Set<String> props = mappedPropertiesByEntity.get(ownerTypeSignature);
		return props != null && props.contains(propertyName);
	}

	public boolean isPropertyMapped(Property property) {
		return mappedProperties.contains(property);
	}

	public boolean hasCompositeId(String typeSignature) {
		return compositeIdEntityTypes.contains(typeSignature);
	}

	/* Reverts ReflectionTools.ensureValidJavaBeansName */
	private String ensureGmPropertyName(String propertyName) {
		if (propertyName.length() > 0 && Character.isUpperCase(propertyName.charAt(0))) {
			return propertyName.substring(0, 1).toLowerCase() + propertyName.substring(1);
		}
		return propertyName;
	}

}
