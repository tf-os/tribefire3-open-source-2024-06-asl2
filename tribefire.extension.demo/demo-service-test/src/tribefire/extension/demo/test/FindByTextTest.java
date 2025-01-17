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
package tribefire.extension.demo.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.manipulation.DeleteMode;
import com.braintribe.model.processing.query.fluent.EntityQueryBuilder;

import tribefire.extension.demo.model.api.FindByText;
import tribefire.extension.demo.model.data.Person;

public class FindByTextTest extends DemoTestBase {
	
	@Before
	public void prepare() {
		Person person1 = testAccessSession.create(Person.T);
		person1.setFirstName("John");
		person1.setLastName("Doe");
		
		Person person2 = testAccessSession.create(Person.T);
		person2.setFirstName("Jane");
		person2.setLastName("Doe");
		
		Person person3 = testAccessSession.create(Person.T);
		person3.setFirstName("Sue");
		person3.setLastName("St. Doe");
		
		Person person4 = testAccessSession.create(Person.T);
		person4.setFirstName("James");
		person4.setLastName("Doeman");
		
		testAccessSession.commit();
	}
	
	@After
	public void cleanup() {
		List<GenericEntity> genericEntities = testAccessSession.query().entities(EntityQueryBuilder.from(GenericEntity.T).done()).list();

		for (GenericEntity genericEntity : genericEntities) {
			testAccessSession.deleteEntity(genericEntity, DeleteMode.dropReferences);
		}
		testAccessSession.commit();
	}
	
	@Test
	public void testFindingPersonByText() {
		FindByText request = FindByText.T.create();
		request.setDomainId(testAccessSession.getAccessId());
		request.setType(Person.T.getTypeSignature());
		request.setText("Jane");
		
		List<GenericEntity> response = request.eval(testAccessSession).get();
		
		assertNotNull(response);
		assertEquals(1, response.size());
		assertEquals(Person.T.getTypeSignature(), response.get(0).entityType().getTypeSignature());
		assertTrue(((Person) response.get(0)).getFirstName().toLowerCase().contains("jane") || ((Person) response.get(0)).getLastName().toLowerCase().contains("jane"));
		
		request.setText("Doe");
		
		response = request.eval(testAccessSession).get();
		
		assertNotNull(response);
		assertEquals(3, response.size());
		assertEquals(Person.T.getTypeSignature(), response.get(0).entityType().getTypeSignature());
		assertEquals(Person.T.getTypeSignature(), response.get(1).entityType().getTypeSignature());
		assertEquals(Person.T.getTypeSignature(), response.get(2).entityType().getTypeSignature());
		assertTrue(((Person) response.get(0)).getFirstName().toLowerCase().contains("doe") || ((Person) response.get(0)).getLastName().toLowerCase().contains("doe"));
		assertTrue(((Person) response.get(1)).getFirstName().toLowerCase().contains("doe") || ((Person) response.get(1)).getLastName().toLowerCase().contains("doe"));
		assertTrue(((Person) response.get(2)).getFirstName().toLowerCase().contains("doe") || ((Person) response.get(2)).getLastName().toLowerCase().contains("doe"));
	}
	
}
