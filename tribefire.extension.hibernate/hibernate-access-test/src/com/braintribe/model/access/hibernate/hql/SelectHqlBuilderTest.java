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
package com.braintribe.model.access.hibernate.hql;

import static com.braintribe.testing.junit.assertions.assertj.core.api.Assertions.assertThat;
import static com.braintribe.utils.SysPrint.spOut;

import org.junit.Test;

import com.braintribe.model.access.hibernate.base.model.acl.AclHaTestEntity;
import com.braintribe.model.access.hibernate.tests.Acl_HbmTest;
import com.braintribe.model.processing.query.fluent.SelectQueryBuilder;
import com.braintribe.model.query.SelectQuery;

/**
 * Tests for {@link SelectHqlBuilder}
 */
public class SelectHqlBuilderTest {

	/**
	 * Complementary test to {@link Acl_HbmTest}s.
	 * <p>
	 * Checks that the ACL condition is encoded in an optimized way using an explicit nested query, rather than a disjunction.
	 * 
	 * @see DisjunctedInOptimizer
	 */
	@Test
	public void aclTest() throws Exception {
		SelectHqlBuilder hqlBuilder = new SelectHqlBuilder(aclQuery());
		hqlBuilder.buildHql();

		String hql = hqlBuilder.builder.toString();
		spOut(hql);

		// Asserting "exists (" substring checks the optimization kicked in
		// Actual correctness is tested in the Acl_HbmTest
		assertThat(hql).contains(" exists (");
	}

	private SelectQuery aclQuery() {
		String ACC = "acl.accessibility";

		// @formatter:off
		return new SelectQueryBuilder().from(AclHaTestEntity.T, "e") //
				.where()
					.conjunction()
						.disjunction()
						.property("e", "name").eq("WHATEVER") // irrelevant condition to make the query less trivial
							.value("G1").in().property("e", ACC)
							.value("G2").in().property("e", ACC)
						.close()
						.negation()
							.disjunction()
								.value("!D1").in().property("e",ACC)
								.value("!D2").in().property("e",ACC)
							.close()
					.close()
					.done();
		// @formatter:on
	}

}
