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
package com.braintribe.model.processing.query.planner.core.cross.simple;

import static com.braintribe.model.processing.query.planner.builder.TupleSetBuilder.filteredSet;
import static com.braintribe.model.processing.query.planner.builder.TupleSetBuilder.sourceSet;
import static com.braintribe.utils.lcd.CollectionTools2.newList;
import static com.braintribe.utils.lcd.CollectionTools2.newSet;
import static java.util.Collections.emptyList;
import static java.util.Collections.singleton;
import static java.util.Collections.singletonList;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import com.braintribe.model.processing.query.planner.builder.TupleSetBuilder;
import com.braintribe.model.processing.query.planner.context.OrderedSourceDescriptor;
import com.braintribe.model.processing.query.planner.context.QueryOrderingManager;
import com.braintribe.model.processing.query.planner.context.QueryPlannerContext;
import com.braintribe.model.processing.query.planner.core.cross.FromGroup;
import com.braintribe.model.query.From;
import com.braintribe.model.query.conditions.Condition;
import com.braintribe.model.query.conditions.ConditionType;
import com.braintribe.model.query.conditions.Conjunction;
import com.braintribe.model.queryplan.set.FilteredSet;
import com.braintribe.model.queryplan.set.TupleSet;

/**
 * 
 */
public class CrossJoinOrderResolver {

	private final QueryPlannerContext context;

	public CrossJoinOrderResolver(QueryPlannerContext context) {
		this.context = context;
	}

	public TupleSet resolveCrossJoinOrder(Condition condition) {
		Set<From> froms = context.sourceManager().findAllFroms();
		FromGroup fromGroup = resolveForFroms(froms, condition);

		return finalize(fromGroup);
	}

	private TupleSet finalize(FromGroup fromGroup) {
		if (fromGroup.osds.isEmpty())
			return fromGroup.tupleSet;

		return IndexOrderedSetPostProcessor.postProcess(context, fromGroup.tupleSet);
	}

	private FromGroup resolveForFroms(Set<From> froms, Condition condition) {
		Set<FromGroup> groups = Collections.emptySet();

		if (condition == null)
			return conjunctionResolver().resolveFor(groups, froms, Collections.emptySet());

		/* On top level we consider a disjunction as a conjunction with exactly one operand. The reason is that there is a special disjunction
		 * optimization inside the ConjunctionResolver which we want to use. This optimization is not needed for disjunctions which are not on the top
		 * level. */
		if (condition.conditionType() == ConditionType.disjunction)
			return conjunctionResolver().resolveFor(groups, froms, Arrays.asList(condition));

		return resolveFor(groups, froms, condition);
	}

	/* package */ FromGroup resolveFor(Set<FromGroup> groups, Set<From> froms, Condition condition) {
		switch (condition.conditionType()) {
			case disjunction:
				return CrossJoinOrderResolver.filteredCartesianProduct(groups, froms, condition, context);
			case conjunction:
				return conjunctionResolver().resolveFor(groups, froms, ((Conjunction) condition).getOperands());
			default:
				return conjunctionResolver().resolveFor(groups, froms, singletonList(condition));
		}
	}

	private ConjunctionResolver conjunctionResolver() {
		return new ConjunctionResolver(this, context);
	}

	/* package */ static FromGroup filteredCartesianProduct(Set<FromGroup> groups, Set<From> froms, Condition condition,
			QueryPlannerContext context) {
		FromGroup cpGroup = CrossJoinOrderResolver.cartesianProduct(groups, froms, context);

		FilteredSet filteredSet = filteredSet(cpGroup.tupleSet, context.convertCondition(condition));

		return new FromGroup(filteredSet, cpGroup.froms, cpGroup.osds);
	}

	/* package */ static FromGroup cartesianProduct(Set<FromGroup> groups, Set<From> froms, QueryPlannerContext context) {
		return new CartesianProductHandler(groups, froms, context).doCrossProduct();
	}

	private static class CartesianProductHandler {

		private final QueryPlannerContext context;
		private final QueryOrderingManager om;
		private final Set<FromGroup> groups;
		private final Set<From> froms;

		private final List<TupleSet> operands = newList();
		private final Set<From> productFroms = newSet();
		private final List<OrderedSourceDescriptor> osds = newList();

		public CartesianProductHandler(Set<FromGroup> groups, Set<From> froms, QueryPlannerContext context) {
			this.context = context;
			this.om = context.orderingManager();
			this.groups = groups;
			this.froms = froms;
		}

		public FromGroup doCrossProduct() {
			List<FromGroup> allGroups = unifyFromWithGroups();

			// we sort groups by their osd index so that whatever we are sorting by first is also listed first
			// consider: SELCT p, c from Person p, Company c ORDER BY p.id, c.id
			// This makes sure we can do a CartesianProduct of 2 IndexOrderedSets iff Person is first operand, Company is second operand
			allGroups.sort(Comparator.comparing(group -> group.osdIndex));

			for (FromGroup group : allGroups) {
				operands.add(group.tupleSet);
				productFroms.addAll(group.froms);
				osds.addAll(group.osds);
			}

			if (operands.size() == 1)
				return new FromGroup(operands.get(0), productFroms, osds);
			else
				return new FromGroup(TupleSetBuilder.cartesianProduct(operands), productFroms, osds);
		}

		private List<FromGroup> unifyFromWithGroups() {
			List<FromGroup> allGroups = newList(groups);

			for (From from : froms)
				allGroups.add(toGroup(from));

			return allGroups;
		}

		private FromGroup toGroup(From from) {
			OrderedSourceDescriptor osd = om.findOsd(from);
			if (osd == null)
				return new FromGroup( //
						sourceSet(from, context), //
						singleton(from), //
						emptyList());
			else
				return new FromGroup( //
						osd.toIndexOrderedSet(context), //
						singleton(from), //
						singletonList(osd));
		}

	}

}
