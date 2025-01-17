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
package com.braintribe.testing.internal.suite.crud.tests;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.braintribe.logging.Logger;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.meta.data.constraint.Mandatory;
import com.braintribe.model.processing.meta.cmd.builders.PropertyMdResolver;
import com.braintribe.model.processing.session.api.persistence.PersistenceGmSession;
import com.braintribe.model.processing.session.api.persistence.PersistenceGmSessionFactory;

/**
 * See {@link #run(PersistenceGmSession)}
 */
public class UpdateToNullTest extends AbstractAccessCRUDTest {
	private static Logger logger = Logger.getLogger(UpdateToNullTest.class);

	private Collection<GenericEntity> entitiesToTest;

	public UpdateToNullTest(String accessId, PersistenceGmSessionFactory factory) {
		super(accessId, factory);
	}

	public Collection<GenericEntity> getEntitiesToTest() {
		return entitiesToTest;
	}

	public void setEntitiesToTest(Collection<GenericEntity> entitiesToTest) {
		this.entitiesToTest = entitiesToTest;
	}

	/**
	 * generically updating all nullable properties of every entity<br>
	 * {@link #setEntitiesToTest(Collection)} must be called first!<br>
	 * Verification step: check if all Simple properties were set to null
	 *
	 * @return list off all updated entities
	 */
	@Override
	public List<GenericEntity> run(PersistenceGmSession session) {
		List<GenericEntity> updatedEntities = new ArrayList<>();

		for (GenericEntity entity : entitiesToTest) {
			GenericEntity updatedEntity = session.query().entity(entity).refresh();
			testSetEntityToNull(updatedEntity, session);
			updatedEntities.add(updatedEntity);
		}

		return updatedEntities;
	}

	private void testSetEntityToNull(GenericEntity entity, PersistenceGmSession session) {
		EntityType<?> entityType = entity.entityType();

		Set<Property> nullablePropertySet = new HashSet<>();

		for (Property property : entityType.getProperties()) {
			PropertyMdResolver propertyMeta = session.getModelAccessory().getMetaData().entity(entity).property(property);

			if (!propertyMeta.is(Mandatory.T) && propertyIsNotFiltered(property, entity, session)) {
				property.set(entity, property.getDefaultRawValue());
				nullablePropertySet.add(property);
			}
		}

		session.commit();

		// check if touched collections are now empty
		GenericEntity updatedEntity = session.query().entity(entity).refresh();

		for (Property property : nullablePropertySet) {
			if (propertyIsNotFiltered(property, updatedEntity, session)) {
				String errorMessage = "Property of " + entityType.getTypeSignature() + " was not updated properly: " + property.getName();
				Object propertyValue = property.get(updatedEntity);

				if (property.getType().isCollection()) {
					if (property.getType().isCollection()) {
						if (propertyValue instanceof Map) {
							assertThat((Map<?, ?>) propertyValue).as(errorMessage).isEmpty();
						} else {
							assertThat((Collection<?>) propertyValue).as(errorMessage).isEmpty();
						}

					} else {
						assertThat(propertyValue).as(errorMessage).isNull();
					}
				}
			}
		}
	}

	@Override
	protected void verifyResult(Verificator verificator, List<GenericEntity> testResult) {
		verificator.assertEntitiesArePersisted(testResult);

	}
}
