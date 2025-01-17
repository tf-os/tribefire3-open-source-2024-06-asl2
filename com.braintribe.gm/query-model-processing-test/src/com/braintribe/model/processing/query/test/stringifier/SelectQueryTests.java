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
package com.braintribe.model.processing.query.test.stringifier;

import static com.braintribe.utils.lcd.CollectionTools2.asSet;

import java.util.Calendar;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

import com.braintribe.model.processing.query.stringifier.experts.basic.DateStringifier;
import com.braintribe.model.processing.query.test.model.Color;
import com.braintribe.model.processing.query.test.model.Company;
import com.braintribe.model.processing.query.test.model.Owner;
import com.braintribe.model.processing.query.test.model.Person;
import com.braintribe.model.query.JoinType;
import com.braintribe.model.query.SelectQuery;
import com.braintribe.testing.junit.assertions.assertj.core.api.Assertions;

public class SelectQueryTests extends AbstractSelectQueryTests {
	@Test
	public void singleSourceNoCondition() {
		// @formatter:off
		 SelectQuery selectQuery = query()
				.from(Person.class, "_Person")
				.done();
		// @formatter:on

		String queryString = stringify(selectQuery);
		Assertions.assertThat(queryString).isEqualToIgnoringCase("select * from com.braintribe.model.processing.query.test.model.Person _Person");
	}

	@Test
	public void sourceType_() {
		// @formatter:off
		 SelectQuery selectQuery = query()
				.from(Owner.class, "_Owner")
				.select().entitySignature().entity("_Owner")
				.done();
		// @formatter:on

		String queryString = stringify(selectQuery);
		Assertions.assertThat(queryString)
				.isEqualToIgnoringCase("select typeSignature(_Owner) from com.braintribe.model.processing.query.test.model.Owner _Owner");
	}

	@Test
	public void sourceType_Hierarchy() {
		// @formatter:off
		 SelectQuery selectQuery = query()
				.from(Person.class, "_Person")
				.select().entitySignature().entity("_Person")
				.done();
		// @formatter:on

		String queryString = stringify(selectQuery);
		Assertions.assertThat(queryString)
				.isEqualToIgnoringCase("select typeSignature(_Person) from com.braintribe.model.processing.query.test.model.Person _Person");
	}

	@Test
	public void conditionOnSourceType() {
		// @formatter:off
		 SelectQuery selectQuery = query()
				.from(Person.class, "_Person")
				.select().entity("_Person")
				.where()
				.entitySignature("_Person").eq(Owner.class.getName())
				.done();
		// @formatter:on

		String queryString = stringify(selectQuery);
		Assert.assertTrue(queryString.equalsIgnoreCase(
				"select _Person from com.braintribe.model.processing.query.test.model.Person _Person where typeSignature(_Person) = 'com.braintribe.model.processing.query.test.model.Owner'"));
	}

	@Test
	public void sameSourceTwiceNoCondition() {
		// @formatter:off
		 SelectQuery selectQuery = query()
				.from(Person.class, "_Person")
				.from(Person.class, "_Person2")
				.done();
		// @formatter:on

		String queryString = stringify(selectQuery);
		Assertions.assertThat(queryString).isEqualToIgnoringCase(
				"select * from com.braintribe.model.processing.query.test.model.Person _Person, com.braintribe.model.processing.query.test.model.Person _Person2");
	}

	@Test
	public void singleSourceNonIndexCondition() {
		// @formatter:off
		 SelectQuery selectQuery = query()
				.from(Person.class, "_Person")
				.where()
				.property("_Person", "name").eq("John")
				.done();
		// @formatter:on

		String queryString = stringify(selectQuery);
		Assertions.assertThat(queryString)
				.isEqualToIgnoringCase("select * from com.braintribe.model.processing.query.test.model.Person _Person where _Person.name = 'John'");
	}

	@Test
	public void singleSourceConditionOnEntityProperty() {
		Company c = getCompany();

		// @formatter:off
		 SelectQuery selectQuery = query()
				.from(Person.class, "_Person")
				.where()
				.property("_Person", "company").eq().entity(c)
				.done();
		// @formatter:on

		String queryString = stringify(selectQuery);
		Assert.assertTrue(queryString.equalsIgnoreCase(
				"select * from com.braintribe.model.processing.query.test.model.Person _Person where _Person.company = reference(com.braintribe.model.processing.query.test.model.Company, 1l)"));
	}

	@Test
	public void singleSourceConditionOnEntityProperty2() {
		Company c = getCompany();

		// @formatter:off
		 SelectQuery selectQuery = query()
				.from(Person.class, "_Person")
				.where()
				.property("_Person", "company").eq().value(c)
				.done();
		// @formatter:on

		String queryString = stringify(selectQuery);
		Assert.assertTrue(queryString.equalsIgnoreCase(
				"select * from com.braintribe.model.processing.query.test.model.Person _Person where _Person.company = reference(com.braintribe.model.processing.query.test.model.Company, 1l)"));
	}

	@Test
	public void singleSourceInConditionOnEntityProperty() {
		Company c = getCompany();

		// @formatter:off
		 SelectQuery selectQuery = query()
				.from(Person.class, "_Person")
				.where()
				.property("_Person", "company").in(asSet(c))
				.done();
		// @formatter:on

		String queryString = stringify(selectQuery);
		Assert.assertTrue(queryString.equalsIgnoreCase(
				"select * from com.braintribe.model.processing.query.test.model.Person _Person where _Person.company in (reference(com.braintribe.model.processing.query.test.model.Company, 1l))"));
	}

	@Test
	public void singleSourceNonIndexConditionOnJoined() {
		// @formatter:off
		 SelectQuery selectQuery = query()
				.from(Owner.class, "_Owner")
				.join("_Owner", "company", "_Company")
				.where()
				.property("_Company", "name").eq("HP")
				.done();
		// @formatter:on

		String queryString = stringify(selectQuery);
		Assertions.assertThat(queryString).isEqualToIgnoringCase(
				"select * from com.braintribe.model.processing.query.test.model.Owner _Owner join _Owner.company _Company where _Company.name = 'HP'");
	}

	@Test
	public void singleSourceNonIndexConditionOnFrom() {
		// @formatter:off
		 SelectQuery selectQuery = query()
				.from(Owner.class, "_Owner")
				.join("_Owner", "company", "_Company")
				.where()
				.property("_Owner", "name").eq("Bill Hewlett")
				.done();
		// @formatter:on

		String queryString = stringify(selectQuery);
		Assertions.assertThat(queryString).isEqualToIgnoringCase(
				"select * from com.braintribe.model.processing.query.test.model.Owner _Owner join _Owner.company _Company where _Owner.name = 'Bill Hewlett'");
	}

	@Test
	public void singleSourceNonIndexConjunctionOfConditions() {
		// @formatter:off
		 SelectQuery selectQuery = query()
				.from(Person.class, "_Person")
				.where()
				.conjunction()
				.property("_Person", "name").eq("John")
				.property("_Person", "companyName").ilike("S*")
				.property("_Person", "phoneNumber").like("555-*")
				.close()
				.done();
		// @formatter:on

		String queryString = stringify(selectQuery);
		Assert.assertTrue(queryString.equalsIgnoreCase(
				"select * from com.braintribe.model.processing.query.test.model.Person _Person where (_Person.name = 'John' and _Person.companyName ilike 'S*' and _Person.phoneNumber like '555-*')"));
	}

	@Test
	public void singleSourceNonIndexConjunctionOfConditionsWithJoin() {
		// @formatter:off
		 SelectQuery selectQuery = query()
				.from(Owner.class, "_Owner")
				.join("_Owner", "company", "_Company")
				.where()
				.conjunction()
				.property("_Owner", "name").eq("John")
				.property("_Owner", "phoneNumber").like("555-*")
				.property("_Company", "name").eq("HP")
				.close()
				.done();
		// @formatter:on

		String queryString = stringify(selectQuery);
		Assert.assertTrue(queryString.equalsIgnoreCase(
				"select * from com.braintribe.model.processing.query.test.model.Owner _Owner join _Owner.company _Company where (_Owner.name = 'John' and _Owner.phoneNumber like '555-*' and _Company.name = 'HP')"));
	}

	@Test
	public void simpleNoWhereInnerJoin() {
		// @formatter:off
		 SelectQuery selectQuery = query()
				.from(Person.class, "_Person")
				.join("_Person", "company", "_Company", JoinType.inner)
				.done();
		// @formatter:on

		String queryString = stringify(selectQuery);
		Assertions.assertThat(queryString)
				.isEqualToIgnoringCase("select * from com.braintribe.model.processing.query.test.model.Person _Person join _Person.company _Company");
	}

	@Test
	public void simpleNoWhereLeftJoin() {
		// @formatter:off
		 SelectQuery selectQuery = query()
				.from(Person.class, "_Person")
				.join("_Person", "company", "_Company", JoinType.left)
				.done();
		// @formatter:on

		String queryString = stringify(selectQuery);
		Assertions.assertThat(queryString).isEqualToIgnoringCase(
				"select * from com.braintribe.model.processing.query.test.model.Person _Person left join _Person.company _Company");
	}

	@Test
	public void simpleNoWhereRightJoin() {
		// @formatter:off
		 SelectQuery selectQuery = query()
				.from(Person.class, "_Person")
				.join("_Person", "company", "_Company", JoinType.right)
				.done();
		// @formatter:on

		String queryString = stringify(selectQuery);
		Assertions.assertThat(queryString).isEqualToIgnoringCase(
				"select * from com.braintribe.model.processing.query.test.model.Person _Person right join _Person.company _Company");
	}

	@Test
	public void simpleNoWhereFullJoin() {
		// @formatter:off
		 SelectQuery selectQuery = query()
				.from(Person.class, "_Person")
				.join("_Person", "company", "_Company", JoinType.full)
				.done();
		// @formatter:on

		String queryString = stringify(selectQuery);
		Assertions.assertThat(queryString).isEqualToIgnoringCase(
				"select * from com.braintribe.model.processing.query.test.model.Person _Person full join _Person.company _Company");
	}

	@Test
	public void implicitJoin() {
		// @formatter:off
		 SelectQuery selectQuery = query()
				.from(Company.class, "_Company")
				.select("_Company","persons")
				.done();
		// @formatter:on

		String queryString = stringify(selectQuery);
		Assertions.assertThat(queryString)
				.isEqualToIgnoringCase("select _Company.persons from com.braintribe.model.processing.query.test.model.Company _Company");
	}

	@Test
	public void singleSourceNonIndexDisjunctionOfConditions() {
		// @formatter:off
		 SelectQuery selectQuery = query()
				.from(Person.class, "_Person")
				.where()
				.disjunction()
				.property("_Person", "name").eq("John")
				.property("_Person", "companyName").ilike("S*")
				.property("_Person", "phoneNumber").like("555-*")
				.close()
				.done();
		// @formatter:on

		String queryString = stringify(selectQuery);
		Assert.assertTrue(queryString.equalsIgnoreCase(
				"select * from com.braintribe.model.processing.query.test.model.Person _Person where (_Person.name = 'John' or _Person.companyName ilike 'S*' or _Person.phoneNumber like '555-*')"));
	}

	@Test
	public void dateSourceConditionOnEntityProperty() {
		// Create birthDate in GMT+1 TimeZone and clear value
		Calendar setBirthDate = Calendar.getInstance();
		setBirthDate.clear();

		// Set specific date as birthDate
		setBirthDate.set(1975, 2, 20, 12, 50, 33);
		setBirthDate.setTimeInMillis(setBirthDate.getTimeInMillis() + 582);

		// @formatter:off
		 Date birthDate = setBirthDate.getTime();
		 SelectQuery selectQuery = query()
				.from(Person.class, "_Person")
				.where()
					.disjunction()
						.property("_Person", "birthDate").eq(birthDate)
					.close()
				.done();
		// @formatter:on

		String birthDateString = new DateStringifier().stringify(birthDate, null);

		String queryString = stringify(selectQuery);
		Assertions.assertThat(queryString).isEqualToIgnoringCase(
				"select * from com.braintribe.model.processing.query.test.model.Person _Person where (_Person.birthDate = " + birthDateString + ")");
	}

	@Test
	public void mixedSourceConditionOnEntityProperty() {
		// @formatter:off
		 SelectQuery selectQuery = query()
				.from(Person.class, "_Person")
				.where()
					.disjunction()
						.property("_Person", "color").eq(Color.GREEN)
						.property("_Person", "indexedFriend").ne(this.getPerson())
						.conjunction()
							.property("_Person", "age").ge(40)
							.property("_Person", "age").le(60)
						.close()
						.property("_Person", "company").ne(this.getCompany())
					.close()
				.done();
		// @formatter:on

		String queryString = stringify(selectQuery);
		Assert.assertTrue(queryString.equalsIgnoreCase(
				"select * from com.braintribe.model.processing.query.test.model.Person _Person where (_Person.color = enum(com.braintribe.model.processing.query.test.model.Color, GREEN) or _Person.indexedFriend != reference(com.braintribe.model.processing.query.test.model.Person, 1l) or (_Person.age >= 40 and _Person.age <= 60) or _Person.company != reference(com.braintribe.model.processing.query.test.model.Company, 1l))"));
	}

	// ####################################
	// ## . . . . collection joins . . . ##
	// ####################################

	@Test
	public void joinWithSet() {
		// @formatter:off
		 SelectQuery selectQuery = query()
				.from(Owner.class, "_Owner")
				.join("_Owner", "companySet", "_Company")
				.done();
		// @formatter:on

		String queryString = stringify(selectQuery);
		Assertions.assertThat(queryString)
				.isEqualToIgnoringCase("select * from com.braintribe.model.processing.query.test.model.Owner _Owner join _Owner.companySet _Company");
	}

	@Test
	public void joinWithListAndCondition() {
		// @formatter:off
		 SelectQuery selectQuery = query()
				.from(Owner.class, "_Owner")
				.join("_Owner", "companyList", "_Company")
				.where()
				.listIndex("_Company").le(1)
				.done();
		// @formatter:on

		String queryString = stringify(selectQuery);
		Assertions.assertThat(queryString).isEqualToIgnoringCase(
				"select * from com.braintribe.model.processing.query.test.model.Owner _Owner join _Owner.companyList _Company where listIndex(_Company) <= 1");
	}

	@Test
	public void joinWithMapAndCondition() {
		// @formatter:off
		 SelectQuery selectQuery = query()
				.from(Owner.class, "_Owner")
				.join("_Owner", "companyMap", "_Company")
				.where()
				.mapKey("_Company").eq("c2")
				.done();
		// @formatter:on

		String queryString = stringify(selectQuery);
		Assertions.assertThat(queryString).isEqualToIgnoringCase(
				"select * from com.braintribe.model.processing.query.test.model.Owner _Owner join _Owner.companyMap _Company where mapKey(_Company) = 'c2'");
	}

	@Test
	public void listIndexWithJoinAndCondition() {
		// @formatter:off
		 SelectQuery selectQuery = query()
				.from(Person.class, "_Person")
				.join("_Person", "company", "_Company")
				.select().listIndex("_Company")
				.where()
				.listIndex("_Company").eq().value(5)
				.done();
		// @formatter:on

		String expectedString = "select listIndex(_Company) from com.braintribe.model.processing.query.test.model.Person _Person join _Person.company _Company where listIndex(_Company) = 5";
		String queryString = stringify(selectQuery);
		Assert.assertTrue(queryString.equalsIgnoreCase(expectedString));
	}
}
