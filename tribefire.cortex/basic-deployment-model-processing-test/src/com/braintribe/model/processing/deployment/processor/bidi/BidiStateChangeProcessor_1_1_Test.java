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

import org.junit.Assert;
import org.junit.Test;

import com.braintribe.model.processing.deployment.processor.Box;
import com.braintribe.model.processing.deployment.processor.bidi.data.Company;
import com.braintribe.model.processing.deployment.processor.bidi.data.Person;

/**
 * 
 */
public class BidiStateChangeProcessor_1_1_Test extends AbstractBidiScpTests {

	// ####################################
	// ## . . . . Simple tests . . . . . ##
	// ####################################

	@Test
	public void worksForNewEntities() throws Exception {
		apply(session -> {
			Person person = newPerson(session);
			Company company = newCompany(session);
			person.setOwnCompany(company);
		});

		assertSinglePersonAndCompanyAreLinked();
	}

	@Test
	public void worksWhenSettingOfNew() throws Exception {
		final Box<Company> companyBox = new Box<Company>();

		prepare(session -> {
			companyBox.value = newCompany(session);
		});

		apply(session -> {
			Person person = newPerson(session);
			person.setOwnCompany(companyBox.value);
		});

		assertSinglePersonAndCompanyAreLinked();
	}

	@Test
	public void worksWhenSettingValueThatIsNew() throws Exception {
		prepare(session -> {
			newPerson(session);
		});

		apply(session -> {
			Company company = newCompany(session);
			Person person = queryFirst(Person.class, session);
			person.setOwnCompany(company);
		});

		assertSinglePersonAndCompanyAreLinked();
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
			person.setOwnCompany(company);
		});

		assertSinglePersonAndCompanyAreLinked();
	}

	private void assertSinglePersonAndCompanyAreLinked() {
		assertLinked(queryFirst(Person.class), queryFirst(Company.class));
	}

	// ####################################
	// ## . . . . Advanced tests . . . . ##
	// ####################################

	/**
	 * We create person and company and link them together. Then we create another company and and set it as the original person's company.
	 * This means, that the first company has to be unlinked and the second one linked.
	 */
	@Test
	public void unlinksPreviousValue() throws Exception {
		prepare(session -> {
			Person person = newPerson(session);
			Company company = newCompany(session, "first");

			person.setOwnCompany(company);
			company.setOwner(person);
		});

		apply(session -> {
			Company company = newCompany(session, "other");
			Person person = queryFirst(Person.class, session);
			person.setOwnCompany(company);
		});

		Person person = queryFirst(Person.class);
		Company first = companyByName("first");
		Company other = companyByName("other");

		assertLinked(person, other);
		Assert.assertNull(first.getOwner());
	}

	@Test
	public void unlinksPreviousValueAndValueOverwrittenByLinking() throws Exception {
		prepare(session -> {
			Person p1 = newPerson(session, "p1");
			Person p2 = newPerson(session, "p2");
			Company c1 = newCompany(session, "c1");
			Company c2 = newCompany(session, "c2");

			p1.setOwnCompany(c1);
			p2.setOwnCompany(c2);
			c1.setOwner(p1);
			c2.setOwner(p2);
		});

		apply(session -> {
			Company c1 = companyByName("c1", session);
			Person p2 = personByName("p2", session);

			p2.setOwnCompany(c1);
		});

		Company c1 = companyByName("c1");
		Company c2 = companyByName("c2");
		Person p1 = personByName("p1");
		Person p2 = personByName("p2");

		assertLinked(p2, c1);
		Assert.assertNull(p1.getOwnCompany());
		Assert.assertNull(c2.getOwner());
	}

	@Test
	public void unlinksIfSetToNull() throws Exception {
		prepare(session -> {
			Person p1 = newPerson(session, "p1");
			Company c1 = newCompany(session, "c1");

			p1.setOwnCompany(c1);
			c1.setOwner(p1);
		});

		apply(session -> {
			Person p1 = personByName("p1", session);
			p1.setOwnCompany(null);
		});

		Company c1 = companyByName("c1");
		Person p1 = personByName("p1");

		Assert.assertNull(c1.getOwner());
		Assert.assertNull(p1.getOwnCompany());
	}

	@Test
	public void reassignExistingValue() throws Exception {
		prepare(session -> {
			Person p1 = newPerson(session, "p1");
			Company c1 = newCompany(session, "c1");

			p1.setOwnCompany(c1);
			c1.setOwner(p1);
		});

		apply(session -> {
			Person p1 = personByName("p1", session);
			p1.setOwnCompany(p1.getOwnCompany());
		});

		assertSinglePersonAndCompanyAreLinked();
	}

	private void assertLinked(Person person, Company company) {
		Assert.assertEquals(person, company.getOwner());
		Assert.assertEquals(company, person.getOwnCompany());
	}
}
