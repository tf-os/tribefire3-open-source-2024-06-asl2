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
package com.braintribe.model.processing.query.fluent;

import static com.braintribe.utils.lcd.CollectionTools2.newList;
import static com.braintribe.utils.lcd.CollectionTools2.newMap;
import static com.braintribe.utils.lcd.CollectionTools2.newSet;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.pr.criteria.TraversingCriterion;
import com.braintribe.model.generic.processing.pr.fluent.CriterionBuilder;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.StandardCloningContext;
import com.braintribe.model.generic.reflection.StrategyOnCriterionMatch;
import com.braintribe.model.generic.value.EnumReference;
import com.braintribe.model.generic.value.ValueDescriptor;
import com.braintribe.model.query.CascadedOrdering;
import com.braintribe.model.query.Join;
import com.braintribe.model.query.Ordering;
import com.braintribe.model.query.OrderingDirection;
import com.braintribe.model.query.Paging;
import com.braintribe.model.query.Query;
import com.braintribe.model.query.Restriction;
import com.braintribe.model.query.SimpleOrdering;
import com.braintribe.model.query.Source;

public abstract class AbstractQueryBuilder<Q extends Query> implements SourceRegistry {
	protected Q query = null;
	protected final Function<EntityType<?>, GenericEntity> factory;

	private final Map<String, Source> sources = newMap();
	protected final Map<Join, EarlySource> earlySources = newMap();

	protected AbstractQueryBuilder(Q query) {
		this(query, EntityType::create);
	}

	protected AbstractQueryBuilder(Q query, Function<EntityType<?>, GenericEntity> factory) {
		this.query = query;
		this.factory = factory;
	}

	protected void registerSource(String alias, Source source) {
		Source existingSource = sources.get(alias);
		if (existingSource == null) {
			sources.put(alias, source);
			return;
		}

		EarlySource earlySource = earlySources.get(existingSource);
		if (earlySource == null || earlySource.source != null)
			throw new QueryBuilderException("the alias " + alias + " is already defined.");

		source.setName(alias);
		earlySource.source = source;
	}

	public ConditionBuilder<? extends AbstractQueryBuilder<Q>> where() {
		return new ConditionBuilder<>(this, this, aquireRestriction()::setCondition);
	}

	protected Restriction aquireRestriction() {
		Restriction restriction = query.getRestriction();
		if (restriction == null) {
			restriction = newGe(Restriction.T);
			query.setRestriction(restriction);
		}
		return restriction;
	}

	protected Paging aquirePaging() {
		Restriction restriction = aquireRestriction();
		Paging paging = restriction.getPaging();

		if (paging == null) {
			paging = newGe(Paging.T);
			paging.setPageSize(20);
			restriction.setPaging(paging);
		}
		return paging;
	}

	protected SimpleOrdering aquireSimpleOrdering() {
		Ordering ordering = query.getOrdering();
		if (ordering == null) {
			SimpleOrdering simpleOrdering = newGe(SimpleOrdering.T);
			simpleOrdering.setDirection(OrderingDirection.ascending);
			query.setOrdering(simpleOrdering);
			ordering = simpleOrdering;
		} else if (!(ordering instanceof SimpleOrdering)) {
			throw new QueryBuilderException("you cannot have both a SimpleOrdering and a CascadingOrdering");
		}

		return (SimpleOrdering) ordering;
	}

	protected CascadedOrdering aquireCascadedOrdering() {
		Ordering ordering = query.getOrdering();
		if (ordering == null) {
			CascadedOrdering cascadedOrdering = newGe(CascadedOrdering.T);
			cascadedOrdering.setOrderings(newList());
			query.setOrdering(cascadedOrdering);
			return cascadedOrdering;
		}

		if (!(ordering instanceof CascadedOrdering))
			throw new QueryBuilderException("you cannot have both a SimpleOrdering and a CascadingOrdering");

		return (CascadedOrdering) ordering;
	}

	public AbstractQueryBuilder<Q> limit(int limit) {
		aquirePaging().setPageSize(limit);
		return this;
	}

	public AbstractQueryBuilder<Q> paging(int pageSize, int pageStart) {
		Paging paging = aquirePaging();
		paging.setPageSize(pageSize);
		paging.setStartIndex(pageStart);
		return this;
	}

	public OperandBuilder<? extends AbstractQueryBuilder<Q>> orderBy() {
		return new OperandBuilder<>(this, this, aquireSimpleOrdering()::setOrderBy);
	}

	public OperandBuilder<? extends AbstractQueryBuilder<Q>> orderBy(OrderingDirection orderingDirection) {
		orderingDirection(orderingDirection);
		return orderBy();
	}

	public CascadedOrderingBuilder<? extends AbstractQueryBuilder<Q>> orderByCascade() {
		return new CascadedOrderingBuilder<>(this, this, aquireCascadedOrdering()::setOrderings);
	}

	/** @deprecated You should not see this. It is overridden on SelectQuery level and makes little sense for entity/property. */
	@Deprecated
	public AbstractQueryBuilder<Q> distinct() {
		return distinct(true);
	}

	/** @deprecated You should not see this. It is overridden on SelectQuery level and makes little sense for entity/property. */
	@Deprecated
	public AbstractQueryBuilder<Q> distinct(boolean distinct) {
		query.setDistinct(distinct);
		return this;
	}

	public AbstractQueryBuilder<Q> orderBy(String propertyName) {
		return orderBy().property(propertyName);
	}

	public AbstractQueryBuilder<Q> orderBy(String propertyName, OrderingDirection orderingDirection) {
		return orderBy(orderingDirection).property(null, propertyName);
	}

	public AbstractQueryBuilder<Q> orderingDirection(OrderingDirection orderingDirection) {
		aquireSimpleOrdering().setDirection(orderingDirection);
		return this;
	}

	public AbstractQueryBuilder<Q> tc(TraversingCriterion traversingCriterion) {
		query.setTraversingCriterion(traversingCriterion);
		return this;
	}

	public CriterionBuilder<? extends AbstractQueryBuilder<Q>> tc() {
		return new CriterionBuilder<>(this, this::tc);
	}

	public Q done() {
		if (earlySources.isEmpty())
			return query;
		else
			return (Q) Query.T.clone(new DelegateSourceResolvingCc(), query, StrategyOnCriterionMatch.skip);
	}

	private final class DelegateSourceResolvingCc extends StandardCloningContext {
		@Override
		public GenericEntity preProcessInstanceToBeCloned(GenericEntity instanceToBeCloned) {
			EarlySource earlySource = earlySources.get(instanceToBeCloned);
			if (earlySource == null)
				return instanceToBeCloned;

			if (earlySource.source == null)
				throw new IllegalStateException("Unable to resolve source: " + earlySource.alias);

			return earlySource.source;
		}
	}

	@Override
	public Source acquireSource(String alias) {
		if (alias == null)
			return null;

		Source source = sources.get(alias);
		if (source == null) {
			source = newEarlySource(alias);
			source.setName(alias);
			sources.put(alias, source);
		}

		return source;
	}

	@Override
	public Join acquireJoin(String alias) {
		Source source = sources.get(alias);
		if (source == null) {
			source = newEarlySource(alias);
			source.setName(alias);
			sources.put(alias, source);
		}

		return (Join) source;
	}

	private Join newEarlySource(String alias) {
		Join fakeFrom = newGe(Join.T);

		EarlySource earlySource = new EarlySource();
		earlySource.alias = alias;

		earlySources.put(fakeFrom, earlySource);

		return fakeFrom;
	}

	public static class EarlySource {
		public Source source;
		public String alias;
	}

	public static Object adaptValue(Object value) {
		if (value instanceof Set<?>) {
			Set<Object> adaptedSet = newSet();
			for (Object object : (Set<?>) value)
				adaptedSet.add(adaptValue(object));

			return adaptedSet;
		}

		if (value instanceof ValueDescriptor)
			return value;

		if (value instanceof GenericEntity)
			return ((GenericEntity) value).reference();

		if (value instanceof Enum)
			return EnumReference.of((Enum<?>) value);

		return value;
	}

	@Override
	public <GE extends GenericEntity> GE newGe(EntityType<GE> et) {
		return (GE) factory.apply(et);
	}

	// Query getter than enables the decorator pattern on extending query builders
	public Q getQuery() {
		return query;
	}

}
