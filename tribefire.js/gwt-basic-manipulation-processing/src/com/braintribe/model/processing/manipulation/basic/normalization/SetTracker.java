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
package com.braintribe.model.processing.manipulation.basic.normalization;

import static com.braintribe.model.generic.manipulation.util.ManipulationBuilder.addManipulation;
import static com.braintribe.model.generic.manipulation.util.ManipulationBuilder.changeValue;
import static com.braintribe.model.generic.manipulation.util.ManipulationBuilder.removeManipulation;
import static com.braintribe.utils.lcd.CollectionTools.removeAllWhenEquivalentSets;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.braintribe.cc.lcd.CodingSet;
import com.braintribe.model.generic.manipulation.AddManipulation;
import com.braintribe.model.generic.manipulation.AtomicManipulation;
import com.braintribe.model.generic.manipulation.ChangeValueManipulation;
import com.braintribe.model.generic.manipulation.Owner;
import com.braintribe.model.generic.manipulation.RemoveManipulation;
import com.braintribe.model.generic.value.EntityReference;
import com.braintribe.utils.lcd.CollectionTools;

/**
 * Tracks manipulations for property of type {@link Set} and merges all the manipulations into either one {@link ChangeValueManipulation} or
 * one or both of {@link RemoveManipulation}, {@link AddManipulation} (if both, they will be in this order). It is very similar to what
 * {@link MapTracker} does (basically this is same as map tracker if for each entry the key was equal to the value).
 */
class SetTracker extends CollectionTracker {

	private Set<Object> adds = CodingSet.create(ElementHashingComparator.INSTANCE);
	private final Set<Object> removes = CodingSet.create(ElementHashingComparator.INSTANCE);

	private boolean startsWithClear = false;

	public SetTracker(Owner owner, String propertySignature) {
		super(owner, propertySignature);
	}

	@Override
	public void onClearCollection() {
		startsWithClear = true;
	}

	@Override
	public void onChangeValue(ChangeValueManipulation m) {
		startsWithClear = true;

		Set<?> newValue = (Set<?>) m.getNewValue();
		if (newValue == null) {
			adds = CodingSet.create(ElementHashingComparator.INSTANCE);
		} else {
			adds = adds == null ? CodingSet.create(ElementHashingComparator.INSTANCE) : adds;
			insertAll(newValue);
		}
	}

	@Override
	public void onBulkInsert(AddManipulation m) {
		insertAll(m.getItemsToAdd().keySet());
	}

	@Override
	public void onBulkRemove(RemoveManipulation m) {
		removeAll(m.getItemsToRemove().keySet());
	}

	private void removeAll(Set<?> set) {
		set = ensureComparable(set);

		adds.removeAll(set);

		if (!startsWithClear) {
			removes.addAll(set);
		}
	}

	private void insertAll(Collection<?> set) {
		set = ensureComparable(set);

		if (!startsWithClear) {
			removes.removeAll(set);
		}

		adds.addAll(set);
	}

	@Override
	public void appendAggregateManipulations(List<AtomicManipulation> manipulations, Set<EntityReference> entitiesToDelete) {
		removeDeletedEntities(entitiesToDelete);

		if (startsWithClear) {
			ChangeValueManipulation cvm = changeValue(newValue(), owner);
			manipulations.add(cvm);

		} else {
			if (!removes.isEmpty()) {
				RemoveManipulation rm = removeManipulation(CollectionTools.getIdentityMap(removes), owner);
				manipulations.add(rm);
			}

			if (!adds.isEmpty()) {
				AddManipulation im = addManipulation(addsMap(), owner);
				manipulations.add(im);
			}
		}
	}

	private void removeDeletedEntities(Set<EntityReference> entitiesToDelete) {
		if (adds != null)
			removeAllWhenEquivalentSets(adds, entitiesToDelete);
		removeAllWhenEquivalentSets(removes, entitiesToDelete);
	}

	private Set<?> newValue() {
		return adds;
	}

	private Map<Object, Object> addsMap() {
		Map<Object, Object> result = new HashMap<Object, Object>();

		for (Object o: adds) {
			result.put(o, o);
		}

		return result;
	}

}
