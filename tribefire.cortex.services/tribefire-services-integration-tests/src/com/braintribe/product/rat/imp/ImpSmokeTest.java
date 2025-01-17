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
package com.braintribe.product.rat.imp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.braintribe.model.accessdeployment.smood.CollaborativeSmoodAccess;
import com.braintribe.model.deployment.DeploymentStatus;
import com.braintribe.model.meta.GmMetaModel;
import com.braintribe.model.meta.data.MetaData;
import com.braintribe.model.processing.session.api.persistence.PersistenceGmSession;
import com.braintribe.model.processing.session.api.persistence.PersistenceGmSessionFactory;
import com.braintribe.qa.tribefire.qatests.deployables.access.AbstractPersistenceTest;
import com.braintribe.qa.tribefire.test.Child;
import com.braintribe.qa.tribefire.test.Parent;
import com.braintribe.testing.internal.suite.crud.AccessTester;

public class ImpSmokeTest extends AbstractPersistenceTest {

	@Test
	public void test1() {
		ImpApi imp = apiFactory().build();

		// This tests creating a model, creating an access, deploying an access / service calls
		CollaborativeSmoodAccess familyAccess = createAndDeployFamilyAccess(imp);
		GmMetaModel familyModel = familyAccess.getMetaModel();

		assertThat(familyAccess.getDeploymentStatus()).as("Family access was not deployed").isEqualTo(DeploymentStatus.deployed);

		// This tests if model an access were created correctly
		AccessTester accessTester = new AccessTester(familyAccess.getExternalId(), apiFactory().buildSessionFactory(), familyModel);
		accessTester.executeTests();

		// This tests deletion of the model and the access
		eraseTestEntities();

		assertThatThrownBy(() -> imp.model(familyModel.getName())).as("Expected error when instantiating imp with non-present model")
				.isInstanceOf(ImpException.class);
		assertThat(imp.deployable().find(familyAccess.getExternalId())).as("family access should be deleted but was found").isNotPresent();

		// This checks if deletion was persisted to the server
		final ImpApi imp2 = apiFactory().build();

		assertThatThrownBy(() -> imp2.model(familyModel.getName())).as("Expected error when instantiating imp with non-present model")
				.isInstanceOf(ImpException.class);
		assertThat(imp2.deployable().find(familyAccess.getExternalId())).as("family access should be deleted but was found").isNotPresent();

		// This checks the querying capability of the entityType imp
		assertThat(imp2.model().entityType(MetaData.T).getProperty("important").getDeclaringType().getTypeSignature())
				.isEqualTo(MetaData.T.getTypeSignature());

		// This will fail if the family model was not deleted properly by the imp before
		CollaborativeSmoodAccess newFamilyAccess = createAndDeployFamilyAccess(imp2);

		// Check all three ways to create a session in a custom access
		ImpApi imp3 = apiFactory().build();
		PersistenceGmSession session1 = imp3.switchToAccess(newFamilyAccess.getExternalId()).session();
		Child child = session1.create(Child.T);
		session1.commit();

		PersistenceGmSessionFactory factory = apiFactory().buildSessionFactory();
		PersistenceGmSession session2 = factory.newSession(newFamilyAccess.getExternalId());
		Parent parent = session2.create(Parent.T);
		session2.commit();

		PersistenceGmSession session3 = apiFactory().newSessionForAccess(newFamilyAccess.getExternalId());
		assertThat(session3.query().entity(child).refresh().getName()).isNull();
		assertThat(session3.query().entity(parent).refresh().getName()).isNull();
	}

	@Before
	@After
	public void tearDown() {
		eraseTestEntities();
	}

}
