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

import static com.braintribe.utils.lcd.CollectionTools2.asSet;
import static com.braintribe.utils.lcd.CollectionTools2.newList;
import static com.braintribe.utils.lcd.CollectionTools2.newSet;
import static java.util.Collections.singleton;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.braintribe.model.processing.query.eval.api.repo.IndexInfo;
import com.braintribe.model.processing.query.planner.context.OrderedSourceDescriptor;
import com.braintribe.model.processing.query.planner.core.cross.FromGroup;
import com.braintribe.model.processing.query.planner.core.index.IndexKeys;
import com.braintribe.model.processing.query.planner.tools.Bound;
import com.braintribe.model.processing.query.planner.tools.ObjectComparator;
import com.braintribe.model.query.From;
import com.braintribe.model.query.Operand;
import com.braintribe.model.query.PropertyOperand;
import com.braintribe.model.query.conditions.Condition;
import com.braintribe.model.queryplan.set.IndexOrderedSet;
import com.braintribe.model.queryplan.set.IndexRange;
import com.braintribe.model.queryplan.set.IndexSubSet;

/**
 * 
 */
class StepDescription {

	Set<Condition> usedConditions = Collections.emptySet(); 

	public static StepDescription simple(Condition... conditions) {
		StepDescription result = new StepDescription();
		result.usedConditions = asSet(conditions);

		return result;
	}

	public static CrossProductStep crossProduct(Set<FromGroup> groups) {
		CrossProductStep result = new CrossProductStep();
		result.fromGroups = groups;

		return result;
	}

	public static IndexSubSetStepDescription indexValue(IndexInfo indexInfo, From from, IndexKeys keys, Condition condition) {
		IndexSubSetStepDescription result = new IndexSubSetStepDescription();
		result.from = from;
		result.indexInfo = indexInfo;
		result.keys = keys;
		result.usedConditions = asSet(condition);

		return result;
	}

	public static IndexRangeStepDescription indexRange(IndexInfo indexInfo, From from) {
		IndexRangeStepDescription result = new IndexRangeStepDescription();
		result.from = from;
		result.indexInfo = indexInfo;
		result.usedConditions = newSet();

		return result;
	}

	public static SingleSourceStepDescription singleSource(From from) {
		SingleSourceStepDescription result = new SingleSourceStepDescription();
		result.from = from;

		return result;
	}

	public static StaticSourceStepDescription staticSource(From from, Set<?> refsOrEntities, Condition condition) {
		StaticSourceStepDescription result = new StaticSourceStepDescription();
		result.from = from;
		result.refsOrEntities = refsOrEntities;
		result.usedConditions = singleton(condition);

		return result;
	}

	public static IndexOrderedSetStepDescription indexOrderedSet(OrderedSourceDescriptor osd) {
		IndexOrderedSetStepDescription result = new IndexOrderedSetStepDescription();
		result.indexInfo = osd.indexInfo;
		result.from = osd.from;
		result.osd = osd;

		return result;
	}
	
	public static IndexLookupJoinStepDescription lookupJoin(IndexInfo indexInfo, FromGroup sourceGroup, Operand sourceProperty, From joinedFrom,
			PropertyOperand joinedProperty, Condition condition) {

		IndexLookupJoinStepDescription result = new IndexLookupJoinStepDescription();

		result.indexInfo = indexInfo;
		result.sourceGroup = sourceGroup;
		result.sourceProperty = sourceProperty;
		result.joinedFrom = joinedFrom;
		result.joinedProperty = joinedProperty;
		result.usedConditions = singleton(condition);

		return result;
	}

	public static IndexRangeJoinStepDescription indexRangeJoin(IndexInfo indexInfo, FromGroup sourceGroup, From joinedFrom,
			PropertyOperand joinedProperty) {

		IndexRangeJoinStepDescription result = new IndexRangeJoinStepDescription();

		result.indexInfo = indexInfo;
		result.sourceGroup = sourceGroup;
		result.joinedProperty = joinedProperty;
		result.joinedFrom = joinedFrom;
		result.usedConditions = newSet();

		return result;
	}

	public static StepDescription mergeLookupJoin(FromGroup srcGroup, Operand srcOperand, FromGroup otherGroup, Operand otherOperand,
			Condition condition) {

		MergeLookupJoinStepDescription result = new MergeLookupJoinStepDescription();

		result.sourceGroup = srcGroup;
		result.sourceOperand = srcOperand;
		result.otherGroup = otherGroup;
		result.otherOperand = otherOperand;
		result.usedConditions = asSet(condition);

		return result;
	}

	public static MergeRangeJoinStepDescription mergeRangeJoin(FromGroup srcGroup, FromGroup otherGroup, Operand otherOperand, Condition condition) {

		MergeRangeJoinStepDescription result = new MergeRangeJoinStepDescription();

		result.sourceGroup = srcGroup;
		result.otherGroup = otherGroup;
		result.otherOperand = otherOperand;
		result.usedConditions = asSet(condition);

		return result;
	}

}

class CrossProductStep extends StepDescription {
	Set<FromGroup> fromGroups;
}

class SingleSourceStepDescription extends StepDescription {
	From from;
}

class StaticSourceStepDescription extends SingleSourceStepDescription {
	Set<?> refsOrEntities;
}

abstract class IndexStepDescription extends SingleSourceStepDescription {
	IndexInfo indexInfo;
}

/** Corresponds to {@link IndexSubSet}. */
class IndexSubSetStepDescription extends IndexStepDescription {
	IndexKeys keys;
}

/** Corresponds to {@link IndexRange}. */
class IndexRangeStepDescription extends IndexStepDescription {
	Object lowerBound;
	Object upperBound;
	// null value indicates the "inclusive" flag and corresponding "bound" value were not set yet
	Boolean lowerInclusive;
	Boolean upperInclusive;

	void setUpper(Object value, boolean inclusive) {
		int cmp = ObjectComparator.compare(upperBound, value);
		if (upperInclusive == null || cmp > 0) {
			upperBound = value;
			upperInclusive = inclusive;
		}

		if (cmp == 0 && !inclusive) {
			upperInclusive = false;
		}
	}

	void setLower(Object value, boolean inclusive) {
		int cmp = ObjectComparator.compare(lowerBound, value);
		if (lowerInclusive == null || cmp < 0) {
			lowerBound = value;
			lowerInclusive = inclusive;
		}

		if (cmp == 0 && !inclusive)
			lowerInclusive = false;
	}

	public Bound lowerBound() {
		return toBound(lowerBound, lowerInclusive);
	}

	public Bound upperBound() {
		return toBound(upperBound, upperInclusive);
	}

	private Bound toBound(Object value, Boolean inclusive) {
		return inclusive == null ? null : new Bound(value, inclusive);
	}
}

/** Corresponds to {@link IndexOrderedSet}. */
class IndexOrderedSetStepDescription extends IndexStepDescription {
	OrderedSourceDescriptor osd;
}

abstract class ImplicitJoinStepDescription extends StepDescription {
	IndexInfo indexInfo;
	FromGroup sourceGroup;
	PropertyOperand joinedProperty;
	From joinedFrom;
}

class IndexLookupJoinStepDescription extends ImplicitJoinStepDescription {
	Operand sourceProperty;
}

class IndexRangeJoinStepDescription extends ImplicitJoinStepDescription {
	List<Bound> upperBounds = newList();
	List<Bound> lowerBounds = newList();

	public void addUpper(Operand operand, boolean inclusive) {
		upperBounds.add(new Bound(operand, inclusive));
	}

	public void addLower(Operand operand, boolean inclusive) {
		lowerBounds.add(new Bound(operand, inclusive));
	}
}

abstract class MergeJoinStepDescription extends StepDescription {
	FromGroup sourceGroup;
	FromGroup otherGroup;
	Operand otherOperand;
}

class MergeLookupJoinStepDescription extends MergeJoinStepDescription {
	Operand sourceOperand;
}

class MergeRangeJoinStepDescription extends MergeJoinStepDescription {
	List<Bound> upperBounds = newList();
	List<Bound> lowerBounds = newList();

	public void addUpper(Operand operand, boolean inclusive) {
		upperBounds.add(new Bound(operand, inclusive));
	}

	public void addLower(Operand operand, boolean inclusive) {
		lowerBounds.add(new Bound(operand, inclusive));
	}
}
