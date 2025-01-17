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

import java.util.List;

import com.braintribe.logging.Logger;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.processing.session.api.persistence.PersistenceGmSession;
import com.braintribe.model.processing.session.api.persistence.PersistenceGmSessionFactory;
import com.braintribe.product.rat.imp.impl.utils.QueryHelper;

public class StandardTraversingCriteriaTest extends AbstractAccessCRUDTest {
	private static Logger logger = Logger.getLogger(StandardTraversingCriteriaTest.class);

	public StandardTraversingCriteriaTest(String accessId, PersistenceGmSessionFactory factory) {
		super(accessId, factory);
	}

	@Override
	protected List<GenericEntity> run(PersistenceGmSession session) {
		QueryHelper queryHelper = new QueryHelper(session);
		return queryHelper.allPersistedEntities();
	}

	@Override
	protected void verifyResult(Verificator verificator, List<GenericEntity> testResult) {
		if (testResult.isEmpty())
			logger.warn("Could not find any entities - this makes the result of this test meaningless");
		
		testResult.forEach(entity -> assertAbsenceInformationSetOnAllComplexProperties(entity, verificator));
	}
	
	private void assertAbsenceInformationSetOnAllComplexProperties(GenericEntity entity, Verificator verificator) {
		for (Property property : verificator.nonFilteredPropertiesOf(entity)) {
			if (property.getType().isScalar() || property.isIdentifying())
				assertThat(property.isAbsent(entity))
					.as("Expected property to be not absent but it was: " + entity.type().getTypeName() + "." + property.getName() + ": " + property)
					.isFalse();
			else
				assertThat(property.isAbsent(entity))
					.as("Expected property to be absent but it wasn't: " + entity.type().getTypeName() + "." + property.getName() + ": " + property)
					.isTrue();
		}
	}
}
