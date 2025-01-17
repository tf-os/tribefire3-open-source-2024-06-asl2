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
package com.braintribe.model.processing.manipulation.basic.mindelta;

import static com.braintribe.utils.lcd.CollectionTools2.newSet;

import java.util.Set;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.Property;

/**
 * @author peter.gazdik
 */
public class ChangeSetWithMinDelta {

	public static void apply(GenericEntity entity, Property property, Set<?> currentSet, Set<?> newSet) {
		/* if new size is less than half of old size, then it's definitely better to say new values rather than say what should be removed */
		if (2 * newSet.size() <= currentSet.size()) {
			if (currentSet.isEmpty()) {
				// both sets are empty
				return;
			}
			
			property.set(entity, newSet);
			return;
		}

		Set<Object> common = newSet();
		Set<Object> delta = newSet();

		/* it's worth to iterate over the smaller set, cause we might find out we want to do CVM instead; in other we still have to iterate
		 * over the the other set too */
		boolean currentIsSmaller = currentSet.size() < newSet.size();
		Set<Object> smaller = (Set<Object>) (currentIsSmaller ? currentSet : newSet);
		Set<Object> bigger = (Set<Object>) (currentIsSmaller ? newSet : currentSet);

		for (Object o: smaller) {
			if (bigger.contains(o)) {
				common.add(o);
			} else {
				delta.add(o);
			}
		}

		int changeValueSize = newSet.size();
		int addAndRemoveSize = currentSet.size() + newSet.size() - 2 * common.size();

		if (addAndRemoveSize == 0) {
			// in other words the collections are equal
			return;
		}

		if (changeValueSize <= addAndRemoveSize) {
			property.set(entity, newSet);
			return;
		}

		if (currentIsSmaller) {
			// current is smaller -> delta are those in current and are not in new, thus have to be removed
			if (!delta.isEmpty()) {
				// we need the check otherwise a manipulation would be tracked
				currentSet.removeAll(delta);
			}
			// we add all new values, those that are already in will not be part of manipulation
			((Set<Object>) currentSet).addAll(newSet);

		} else {
			// we remove everything that is not common
			currentSet.retainAll(common);
			// current is bigger -> delta are those in new that are not in current, thus have to be added
			((Set<Object>) currentSet).addAll(delta);
		}
	}

}
