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

import static com.braintribe.utils.lcd.CollectionTools2.first;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.braintribe.model.access.TmpQueryResultDepth;
import com.braintribe.model.access.hibernate.base.HibernateAccessRecyclingTestBase;
import com.braintribe.model.access.hibernate.base.model.simple.GraphNodeEntity;
import com.braintribe.model.generic.pr.criteria.TraversingCriterion;
import com.braintribe.model.generic.processing.pr.fluent.TC;
import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.generic.typecondition.TypeConditions;
import com.braintribe.model.meta.GmMetaModel;
import com.braintribe.model.processing.query.fluent.EntityQueryBuilder;
import com.braintribe.model.query.EntityQuery;
import com.braintribe.model.query.OrderingDirection;
import com.braintribe.utils.collection.impl.AttributeContexts;

/**
 * Basic tests for eager-loading based on TC.
 * 
 * @see HibernateAccessRecyclingTestBase
 * 
 * @author peter.gazdik
 */
public class EagerLoading_HbmTest extends HibernateAccessRecyclingTestBase {

	@Override
	protected GmMetaModel model() {
		return HibernateAccessRecyclingTestBase.hibernateModels.graph();
	}

	// do not load name of a second-level entity
	// @formatter:off
	private static final TraversingCriterion tc = TC.create()
			.pattern() 
				.conjunction()
					.property()
					.typeCondition(TypeConditions.isType(GraphNodeEntity.T))
					.close()
				.entity()
				.property("name")
			.close() //
			.done();
	// @formatter:on

	private static final Property NAME = GraphNodeEntity.T.getProperty("name");

	@Test
	public void queryTreeData() throws Exception {
		prepareGraph();

		GraphNodeEntity root = first(accessDriver.query(queryRoot()).getEntities());

		assertHasName(root);
		assertAbsentName(root.getNeighbor1());
		assertAbsentName(root.getNeighbor2());
	}

	@Test
	public void queryTreeData_breadthFirst() throws Exception {
		AttributeContexts.push(AttributeContexts.derivePeek().set(TmpQueryResultDepth.class, 2).build());

		try {
			run_queryTreeData_breadthFirst();
		} finally {
			AttributeContexts.pop();
		}
	}

	private void run_queryTreeData_breadthFirst() throws Exception {
		prepareGraph();

		GraphNodeEntity root = first(accessDriver.query(queryRoot()).getEntities());

		assertHasName(root);
		assertHasName(root.getNeighbor1());
		assertHasName(root.getNeighbor2());

		GraphNodeEntity c1 = root.getNeighbor1();
		assertAbsentName(c1.getNeighbor1());
		assertAbsentName(c1.getNeighbor2());
	}

	private void assertHasName(GraphNodeEntity node) {
		assertThat(NAME.isAbsent(node)).isFalse();
	}

	private void assertAbsentName(GraphNodeEntity node) {
		assertThat(NAME.isAbsent(node)).isTrue();
	}

	private EntityQuery queryRoot() {
		// [root, neighbor2, neigbor1]; in this order
		return EntityQueryBuilder //
				.from(GraphNodeEntity.T) //
				.where().negation().property("name").like("*grand*").orderBy(OrderingDirection.descending).property("name") //
				.tc(tc) //
				.done();
	}

	private void prepareGraph() {
		GraphNodeEntity gc11 = node("grandChild11");
		GraphNodeEntity gc12 = node("grandChild12");

		GraphNodeEntity c1 = node("child1", gc11, gc12);
		GraphNodeEntity c2 = node("child2");

		node("root", c1, c2);

		session.commit();

		resetGmSession();
	}

	private GraphNodeEntity node(String name, GraphNodeEntity neighbor1, GraphNodeEntity neighbor2) {
		GraphNodeEntity result = node(name);
		result.setNeighbor1(neighbor1);
		result.setNeighbor2(neighbor2);

		return result;
	}

	private GraphNodeEntity node(String name) {
		GraphNodeEntity result = session.create(GraphNodeEntity.T);
		result.setName(name);

		return result;
	}

}
