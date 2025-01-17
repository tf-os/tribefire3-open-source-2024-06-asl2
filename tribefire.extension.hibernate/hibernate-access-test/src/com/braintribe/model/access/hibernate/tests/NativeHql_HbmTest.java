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
package com.braintribe.model.access.hibernate.tests;

import static com.braintribe.utils.lcd.CollectionTools2.asMap;
import static com.braintribe.utils.lcd.CollectionTools2.asSet;

import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Test;

import com.braintribe.model.access.hibernate.base.HibernateAccessRecyclingTestBase;
import com.braintribe.model.access.hibernate.base.HibernateNativeModelTestBase;
import com.braintribe.model.access.hibernate.base.model.n8ive.Player;
import com.braintribe.model.persistence.ExecuteNativeQuery;
import com.braintribe.model.persistence.NativeQueryParameter;
import com.braintribe.model.processing.service.api.ServiceRequestContext;

/**
 * Basic tests for entity with scalar properties only.
 * 
 * @see HibernateAccessRecyclingTestBase
 * 
 * @author peter.gazdik
 */
public class NativeHql_HbmTest extends HibernateNativeModelTestBase {

	static final String _string = "string";
	static final int _integer = 123;
	static final long _long = 9_876_543_210L;
	static final float _float = 0.123F;
	static final double _double = 1.012_345_678_9D;
	static final Date date = new Date(10_000_000L);

	protected final ServiceRequestContext src = internalUserServiceRequestContext();

	@Test
	public void simpleQuery() throws Exception {
		player("Wilt", "Chamberlain");
		player("Shaq", (String) null);
		session.commit();

		executeNativeQuery(queryRequest("select e.name, coalesce(e.lastName, 'default') from Player e"));

		qra.assertContains("Wilt", "Chamberlain");
		qra.assertContains("Shaq", "default");
	}

	/** Exactly the same as {@link #simpleQuery()} but uses a fully-qualified entity name in the HQL */
	@Test
	public void simpleQuery_WorksFullyQualified() throws Exception {
		player("Wilt", "Chamberlain");
		player("Shaq", (String) null);
		session.commit();

		executeNativeQuery(queryRequest( //
				"select e.name, coalesce(e.lastName, 'default') from com.braintribe.model.access.hibernate.base.model.n8ive.Player e"));

		qra.assertContains("Wilt", "Chamberlain");
		qra.assertContains("Shaq", "default");
	}

	@Test
	public void simpleTypeParameter() throws Exception {
		player("Reggie", "Miller");
		player("Metta", "World Peace");
		session.commit();

		executeNativeQuery(queryRequest( //
				"select e.name from Player e where e.lastName = :val", //
				"val", "Miller"));

		qra.assertContains("Reggie");
		qra.assertNoMoreResults();
	}

	@Test
	public void collectionParameter() throws Exception {
		player("John", "Stockton");
		player("Steve", "Nash");
		player("Bill", "Russel");
		session.commit();

		executeNativeQuery(queryRequest( //
				"select e.name from Player e where e.lastName in (:val)", //
				"val", asSet("Stockton", "Nash")));

		qra.assertContains("John");
		qra.assertContains("Steve");
		qra.assertNoMoreResults();
	}

	@Test
	public void entityParameter() throws Exception {
		Player jMate = player("jMate");
		Player sMate = player("sMate");

		player("John", jMate);
		player("Steve", sMate);

		session.commit();

		executeNativeQuery(queryRequest( //
				"select e.name from Player e where e.teammate = :val", //
				"val", jMate));

		qra.assertContains("John");
		qra.assertNoMoreResults();
	}

	/**
	 * This doesn't really test anything ambiguous, I just use this to create the situation and see that using simple name in HQL ('AmbiguousEntity')
	 * leads to an exception.
	 */
	@Test
	public void ambiguous() throws Exception {
		ambigTop("Top");
		ambigSub("Sub");
		session.commit();

		executeNativeQuery(queryRequest("select e.name from com.braintribe.model.access.hibernate.base.model.n8ive.AmbiguousEntity e"));

		qra.assertContains("Top");
		qra.assertNoMoreResults();
	}

	private ExecuteNativeQuery queryRequest(String queryString, Object... params) {
		ExecuteNativeQuery result = ExecuteNativeQuery.T.create();
		result.setQuery(queryString);

		addParams(result, params);

		return result;
	}

	private void addParams(ExecuteNativeQuery result, Object... params) {
		if (params != null) {
			Map<String, Object> paramsMap = asMap(params);

			for (Entry<String, Object> e : paramsMap.entrySet()) {
				NativeQueryParameter p = newParam(e.getKey(), e.getValue());

				result.getParameters().add(p);
			}
		}
	}

	private NativeQueryParameter newParam(String name, Object value) {
		NativeQueryParameter p = NativeQueryParameter.T.create();
		p.setName(name);
		p.setValue(value);
		return p;
	}

}
