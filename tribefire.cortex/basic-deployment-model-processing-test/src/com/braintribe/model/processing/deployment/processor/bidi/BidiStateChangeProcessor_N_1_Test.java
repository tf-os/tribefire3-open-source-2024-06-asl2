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
package com.braintribe.model.processing.deployment.processor.bidi;

import static com.braintribe.utils.lcd.CollectionTools2.newList;
import static com.braintribe.utils.lcd.CollectionTools2.newSet;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.junit.Test;

import com.braintribe.model.processing.deployment.processor.bidi.data.Company;
import com.braintribe.model.processing.deployment.processor.bidi.data.Person;
import com.braintribe.utils.junit.assertions.BtAssertions;

/**
 * 
 */
public class BidiStateChangeProcessor_N_1_Test extends AbstractBidiScpTests {

	// ######################################
	// ## . . . adding to collection . . . ##
	// ######################################

	@Test
	public void worksForNewEntities() throws Exception {
		apply(session -> {
			Company company = newCompany(session);

			addEmployee(company, newPerson(session));
			addEmployee(company, newPerson(session));
		});

		assertCompanyWithTwoPersons();
	}

	@Test
	public void worksWithBothExisting() throws Exception {
		prepare(session -> {
			newPerson(session);
			newCompany(session);
		});

		apply(session -> {
			Company company = queryFirst(Company.class, session);
			Person person = queryFirst(Person.class, session);

			addEmployee(company, newPerson(session));
			addEmployee(company, person);
		});

		assertCompanyWithTwoPersons();
	}

	private void assertCompanyWithTwoPersons() {
		assertLinked(queryAll(Person.class), queryFirst(Company.class));
	}

	// ######################################
	// ## . . . clearing collection . . . .##
	// ######################################

	@Test
	public void clearingCollectionWorks() throws Exception {
		prepareCompanyWithTwoEmployees();

		apply(session -> {
			Company company = queryFirst(Company.class, session);
			company.getEmployeeSet().clear();
			company.getEmployeeList().clear();
		});

		for (Person person : queryAll(Person.class)) {
			assertNull(person.getEmployerCompany());
			assertNull(person.getEmployerCompanyList());
		}
	}

	@Test
	public void settingEmptyCompanyWorks() throws Exception {
		prepareCompanyWithTwoEmployees();

		apply(session -> {
			Company company = queryFirst(Company.class, session);
			company.setEmployeeSet(new HashSet<Person>());
			company.setEmployeeList(new ArrayList<Person>());
		});

		for (Person person : queryAll(Person.class)) {
			assertNull(person.getEmployerCompany());
			assertNull(person.getEmployerCompanyList());
		}

	}

	// ######################################
	// ## . . removing from collection . . ##
	// ######################################

	@Test
	public void removingFromCollection() throws Exception {
		prepareCompanyWithTwoEmployees();

		apply(session -> {
			Company company = queryFirst(Company.class, session);
			Person person = personByName("p1", session);

			assertTrue(company.getEmployeeSet().remove(person));
			assertTrue(company.getEmployeeList().remove(person));
		});

		Person p1 = personByName("p1");
		Person p2 = personByName("p2");
		Company c = queryFirst(Company.class);

		assertLinked(c, p2);
		assertNull(p1.getEmployerCompany());
		assertNull(p1.getEmployerCompanyList());
	}

	@Test
	public void removingFromListWhenItsThereTwice() throws Exception {
		prepareCompanyWithTwoEmployees();

		// adding it second time
		apply(session -> {
			Company company = queryFirst(Company.class, session);
			Person p1 = personByName("p1", session);
			assertTrue(company.getEmployeeList().add(p1));
		});

		// removing the second entry
		apply(session -> {
			Company company = queryFirst(Company.class, session);
			assertNotNull(company.getEmployeeList().remove(2));
		});

		Person p1 = personByName("p1");
		Person p2 = personByName("p2");
		Company c = queryFirst(Company.class);

		assertLinked(c, p1, p2);
	}

	// ######################################
	// ## . . replacing in collection . . .##
	// ######################################

	@Test
	public void replacingInList() throws Exception {
		prepareCompanyWithTwoEmployees();

		apply(session -> {
			Company company = queryFirst(Company.class, session);
			Person p2 = personByName("p2", session);
			Person p3 = newPerson(session, "p3");

			company.getEmployeeList().set(1, p3);
			company.getEmployeeSet().remove(p2);
			company.getEmployeeSet().add(p3);
		});

		Person p1 = personByName("p1");
		Person p2 = personByName("p2");
		Person p3 = personByName("p3");
		Company c = queryFirst(Company.class);

		assertLinked(c, p1, p3);
		assertNull(p2.getEmployerCompany());
	}

	// ######################################
	// ## . . . moving in collection . . . ##
	// ######################################

	@Test
	public void movingInCollection() throws Exception {
		prepareCompanyWithTwoEmployees();

		apply(session -> {
			Company company = queryFirst(Company.class, session);

			Person p = company.getEmployeeList().remove(1);
			company.getEmployeeList().add(0, p);
		});

		Person p1 = personByName("p1");
		Person p2 = personByName("p2");
		Company c = queryFirst(Company.class);

		assertLinked(c, p1, p2);
		BtAssertions.assertThat(c.getEmployeeList()).containsExactly(p2, p1);
	}

	// ######################################
	// ## . . . . . . helpers . . . . . . .##
	// ######################################

	private void prepareCompanyWithTwoEmployees() {
		prepare(session -> {
			Person p1 = newPerson(session, "p1");
			Person p2 = newPerson(session, "p2");
			Company c = newCompany(session);

			p1.setEmployerCompany(c);
			p2.setEmployerCompany(c);
			p1.setEmployerCompanyList(c);
			p2.setEmployerCompanyList(c);

			addEmployee(c, p1);
			addEmployee(c, p2);
		});
	}

	private void addEmployee(Company c, Person p) {
		// Set
		if (c.getEmployeeSet() == null)
			c.setEmployeeSet(newSet());

		c.getEmployeeSet().add(p);

		// List
		if (c.getEmployeeList() == null)
			c.setEmployeeList(newList());

		c.getEmployeeList().add(p);
	}

	private void assertLinked(Company company, Person... employees) {
		assertLinked(Arrays.asList(employees), company);
	}

	private void assertLinked(List<Person> employees, Company company) {
		assertEquals(employees.size(), company.getEmployeeSet().size());
		assertEquals(employees.size(), company.getEmployeeList().size());

		assertTrue(company.getEmployeeSet().containsAll(employees));
		assertTrue(company.getEmployeeList().containsAll(employees));

		for (Person p : employees) {
			assertEquals(company, p.getEmployerCompany());
			assertEquals(company, p.getEmployerCompanyList());
		}
	}
}
