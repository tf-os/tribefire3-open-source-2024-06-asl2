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
package com.braintribe.model.access.security.query;

import static com.braintribe.model.access.security.query.PasswordPropertyTools.HIDDEN_PASSWORD;

import java.util.List;

import org.junit.Rule;
import org.junit.Test;

import com.braintribe.model.access.security.testdata.query.EntityWithProps;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.processing.query.fluent.EntityQueryBuilder;
import com.braintribe.model.processing.query.fluent.PropertyQueryBuilder;
import com.braintribe.model.processing.query.fluent.SelectQueryBuilder;
import com.braintribe.model.query.EntityQuery;
import com.braintribe.model.query.PropertyQuery;
import com.braintribe.model.query.SelectQuery;
import com.braintribe.model.record.ListRecord;
import com.braintribe.utils.junit.assertions.BtAssertions;
import com.braintribe.utils.junit.core.rules.ThrowableChain;
import com.braintribe.utils.junit.core.rules.ThrowableChainRule;

/**
 * 
 */
public class PasswordQueryingTests extends AbstractQueryingTest {

	private EntityWithProps entity;

	@Rule
	public ThrowableChainRule exceptionChainRule = new ThrowableChainRule();

	@Override
	protected void prepareData() {
		entity = delegateSession.create(EntityWithProps.T);
		entity.setPassword("doesn't matter, this will not be retrieved");
	}

	// ########################################################
	// ## . . . . . Password condition not allowed . . . . . ##
	// ########################################################

	@Test
	@ThrowableChain({ IllegalArgumentException.class })
	public void entityQuery_PasswordCondition() throws Exception {
		EntityQuery query = EntityQueryBuilder.from(EntityWithProps.T).where().property("password").eq("pwd").done();
		queryEntities(query); // Exception expected
	}

	@Test
	@ThrowableChain({ IllegalArgumentException.class })
	public void entityQuery_PasswordCondition_PropertyPath() throws Exception {
		EntityQuery query = EntityQueryBuilder.from(EntityWithProps.T).where().property("parent.password").eq("pwd").done();
		queryEntities(query); // Exception expected
	}

	@Test
	@ThrowableChain({ IllegalArgumentException.class })
	public void selectQuery_PasswordCondition() throws Exception {
		SelectQuery query = new SelectQueryBuilder().from(EntityWithProps.T, "e").where().property("e", "password").eq("pwd").done();
		query(query); // Exception expected
	}

	// ########################################################
	// ## . . . . . Password replaced with asterisks . . . . ##
	// ########################################################

	@Test
	public void selectQuery_passwordOnly() {
		SelectQuery query = new SelectQueryBuilder().from(EntityWithProps.class, "e").select("e", "password").done();
		List<?> queryResult = query(query);

		BtAssertions.assertThat(queryResult).hasSize(1).containsOnly(HIDDEN_PASSWORD);
	}

	@Test
	public void selectQuery_passwordSelectionAmongOthers() throws Exception {
		SelectQuery query = new SelectQueryBuilder() //
				.select("e", "password") //
				.select("e", "id") //
				.select("e", "password") //
				.from(EntityWithProps.T, "e") //
				.done();
		List<?> queryResult = query(query);

		BtAssertions.assertThat(queryResult).hasSize(1);

		ListRecord lr = (ListRecord) queryResult.get(0);

		BtAssertions.assertThat(lr.getValues().get(0)).isEqualTo(HIDDEN_PASSWORD);
		BtAssertions.assertThat(lr.getValues().get(2)).isEqualTo(HIDDEN_PASSWORD);
	}

	@Test
	public void selectQuery_entityWithPassword() {
		SelectQuery query = new SelectQueryBuilder().from(EntityWithProps.T, "e").select("e").done();
		List<?> queryResult = query(query);

		BtAssertions.assertThat(queryResult).hasSize(1);

		EntityWithProps entity = (EntityWithProps) queryResult.get(0);
		BtAssertions.assertThat(entity.getPassword()).isEqualTo(HIDDEN_PASSWORD);
	}

	@Test
	public void entityQuery_entityWithPassword() {
		EntityQuery query = EntityQueryBuilder.from(EntityWithProps.T).done();
		List<GenericEntity> queryResult = queryEntities(query);

		BtAssertions.assertThat(queryResult).hasSize(1);

		EntityWithProps entity = (EntityWithProps) queryResult.get(0);
		BtAssertions.assertThat(entity.getPassword()).isEqualTo(HIDDEN_PASSWORD);
	}

	@Test
	public void propertyQuery_password() {
		PropertyQuery query = PropertyQueryBuilder.forProperty(EntityWithProps.T, entity.getId(), "password").done();
		Object password = queryProperty(query);

		BtAssertions.assertThat(password).isEqualTo(HIDDEN_PASSWORD);
	}
}
