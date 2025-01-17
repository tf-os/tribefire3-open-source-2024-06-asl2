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
package com.braintribe.model.access.smart.test.query;

import org.junit.Test;

import com.braintribe.model.processing.query.smart.test.model.accessA.CompanyA;
import com.braintribe.model.processing.query.smart.test.model.accessA.PersonA;
import com.braintribe.model.processing.query.smart.test.model.smart.SmartAddress;
import com.braintribe.model.processing.query.smart.test.model.smart.SmartPersonA;
import com.braintribe.model.processing.smart.query.planner.SingleSource_DelegateCondition_PlannerTests;
import com.braintribe.model.query.SelectQuery;

/**
 * 
 */
public class SingleSource_DelegateCondition_Tests extends AbstractSmartQueryTests {

	/** @see SingleSource_DelegateCondition_PlannerTests#simpleCondition() */
	@Test
	public void simpleCondition() {
		bA.address("st1").number(10).create();
		bA.address("st2").number(20).create();

		// @formatter:off
		SelectQuery selectQuery = query()		
				.from(SmartAddress.class, "a")
					.select("a", "street")
					.where()
						.property("a", "number").eq().value(10)
				.done();
		// @formatter:on

		evaluate(selectQuery);

		assertResultContains("st1");
		assertNoMoreResults();
	}

	/** @see SingleSource_DelegateCondition_PlannerTests#simplePropertyPathCondition() */
	@Test
	public void simplePropertyPathCondition() {
		PersonA bill = bA.personA("Bill").create();
		PersonA brad = bA.personA("Brad").create();
		PersonA doug = bA.personA("Doug").create();

		CompanyA bCompany = bA.company("Bill's company").owner(bill).create();
		CompanyA dCompany = bA.company("Doug's company").owner(doug).create();

		bill.setCompanyA(bCompany);
		brad.setCompanyA(bCompany);
		doug.setCompanyA(dCompany);

		bA.commitAllChanges();

		// @formatter:off
		SelectQuery selectQuery = query()		
				.from(SmartPersonA.class, "p")
				.join("p", "companyA", "c")
				.join("c", "ownerA", "o")
				.select("p", "nameA")
					.where()
						.property("o", "nameA").eq("Bill")
				.done();
		// @formatter:on

		evaluate(selectQuery);

		assertResultContains("Bill");
		assertResultContains("Brad");
		assertNoMoreResults();
	}

	@Test
	public void simpleFulltextCondition() {
		bA.personA("Yes").companyNameA("foobar llc").create();
		bA.personA("No").companyNameA("xxx").create();

		// @formatter:off
		SelectQuery selectQuery = query()		
				.from(SmartPersonA.class, "p")
					.select("p", "nameA")
					.where()
						.fullText("p", "foobar")
				.done();
		// @formatter:on

		evaluate(selectQuery);

		assertResultContains("Yes");
		assertNoMoreResults();
	}

	/** @see SingleSource_DelegateCondition_PlannerTests#disjunctionFulltextCondition() */
	@Test
	public void disjunctionFulltextCondition() {
		bA.personA("YesA").parentB("parentYesA").companyNameA("foobar llc").create();
		bA.personA("YesB").parentB("parentYesB").companyNameA("xxx").create();
		bA.personA("NoNo").parentB("parentNoNo").companyNameA("xxx").create();

		bB.personB("parentYesA").companyNameB("xxx").create();
		bB.personB("parentYesB").companyNameB("foobar llc").create();
		bB.personB("parentNoNo").companyNameB("xxx").create();

		// @formatter:off
		SelectQuery selectQuery = query()		
				.from(SmartPersonA.class, "pA")
				.join("pA", "smartParentB", "pB")
					.select("pA", "nameA")
					.where()
						.disjunction()
							.fullText("pA", "foobar")
							.fullText("pB", "foobar")
						.close()
				.done();
		// @formatter:on

		evaluate(selectQuery);

		assertResultContains("YesA");
		assertResultContains("YesB");
		assertNoMoreResults();
	}

	/** @see SingleSource_DelegateCondition_PlannerTests#simpleConjunction() */
	@Test
	public void simpleConjunction() {
		bA.address("st1").number(15).create();
		bA.address("st2").number(25).create();

		// @formatter:off
		SelectQuery selectQuery = query()		
				.from(SmartAddress.class, "a")
				.select("a", "street")
				.where()
					/* We wrap the conjunction  */
					.disjunction()
						.conjunction()
							.property("a", "number").ge().value(10)
							.property("a", "number").lt().value(20)
						.close()
						.property("a", "number").ge().value(1000)
					.close()
				.done();
		// @formatter:on

		evaluate(selectQuery);

		assertResultContains("st1");
		assertNoMoreResults();
	}

	/** @see SingleSource_DelegateCondition_PlannerTests#simpleDisjunction() */
	@Test
	public void simpleDisjunction() {
		bA.address("st1").number(9).create();
		bA.address("st2").number(10).create();
		bA.address("st3").number(20).create();

		// @formatter:off
		SelectQuery selectQuery = query()		
				.from(SmartAddress.class, "a")
				.select("a", "street")
				.where()
					.disjunction()
						.property("a", "number").lt().value(10)
						.property("a", "number").ge().value(20)
					.close()
				.done();
		// @formatter:on

		evaluate(selectQuery);

		assertResultContains("st1");
		assertResultContains("st3");
		assertNoMoreResults();
	}

	/** @see SingleSource_DelegateCondition_PlannerTests#simpleNegation() */
	@Test
	public void simpleNegation() {
		bA.address("st1").zipCode("z55b").create();
		bA.address("st2").zipCode("z555b").create();

		// @formatter:off
		SelectQuery selectQuery = query()		
				.from(SmartAddress.class, "a")
				.select("a", "street")
				.where()
					.negation()
						.property("a", "zipCode").like("*555*")
				.done();
		// @formatter:on

		evaluate(selectQuery);

		assertResultContains("st1");
		assertNoMoreResults();
	}
}
