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
package com.braintribe.model.processing.smart.query.planner.core.combination;

import static com.braintribe.utils.lcd.CollectionTools2.newList;
import static com.braintribe.utils.lcd.CollectionTools2.newMap;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import com.braintribe.model.processing.smart.query.planner.SmartQueryPlannerException;
import com.braintribe.model.processing.smart.query.planner.context.SmartQueryPlannerContext;
import com.braintribe.model.processing.smart.query.planner.graph.EntitySourceNode;
import com.braintribe.model.processing.smart.query.planner.graph.SourceNodeGroup;
import com.braintribe.model.processing.smart.query.planner.graph.SourceNodeGroup.CombinationGroup;
import com.braintribe.model.processing.smart.query.planner.graph.SourceNodeGroup.DelegateJoinGroup;
import com.braintribe.model.processing.smart.query.planner.graph.SourceNodeGroup.SingleAccessGroup;

/**
 * 
 */
public class GroupSortingTools {

	public static List<EntitySourceNode> sortEntityNodesAlphabetically(Collection<EntitySourceNode> sans) {
		List<EntitySourceNode> result = newList(sans);
		Collections.sort(result, (san1, san2) -> str(san1).compareTo(str(san2)));

		return result;
	}

	private static String str(EntitySourceNode san) {
		return san.getDelegateGmType().getTypeSignature();
	}

	public static List<SourceNodeGroup> sortAlphabeticallyOrderedFirst(Collection<SourceNodeGroup> groups, SmartQueryPlannerContext context) {
		return sort(groups, buildCharStringMap(groups, context));
	}

	private static <T extends SourceNodeGroup> List<T> sort(Collection<T> collection, Map<T, List<String>> charStringMap) {
		List<T> result = newList(collection);
		Collections.sort(result, new CharStringBasedComparator<T>(charStringMap));

		return result;
	}

	private static Map<SourceNodeGroup, List<String>> buildCharStringMap(Collection<SourceNodeGroup> groups, SmartQueryPlannerContext context) {
		Map<SourceNodeGroup, List<String>> result = newMap();

		for (SourceNodeGroup group: groups) {
			List<String> charStrings = context.getCharStrings(group);
			Collections.sort(charStrings);

			result.put(group, charStrings);
		}

		return result;
	}

	public static int compareLists(List<String> l1, List<String> l2) {
		int result = l2.size() - l1.size();

		if (result != 0)
			return result;

		for (int i = 0; i < l1.size(); i++) {
			String s1 = l1.get(i);
			String s2 = l2.get(i);

			result = s1.compareTo(s2);

			if (result != 0)
				return result;
		}

		return 0;
	}

	private static class CharStringBasedComparator<T extends SourceNodeGroup> implements Comparator<T> {
		private final Map<T, List<String>> map;

		public CharStringBasedComparator(Map<T, List<String>> charStringMap) {
			this.map = charStringMap;
		}

		@Override
		public int compare(T t1, T t2) {
			if (isOrdered(t1))
				return -1;

			if (isOrdered(t2))
				return -1;

			return GroupSortingTools.compareLists(map.get(t1), map.get(t2));
		}

		private boolean isOrdered(SourceNodeGroup group) {
			switch (group.nodeGroupType()) {
				case combination:
					return isOrdered(((CombinationGroup) group).operands.get(0));
				case delegateQueryJoin:
					return isOrdered(((DelegateJoinGroup) group).materializedGroup);
				case singleSource:
				case singleAccessCombination:
					return ((SingleAccessGroup) group).orderAndPagination != null;
			}

			throw new SmartQueryPlannerException("Unknown NodeGroupType: " + group.nodeGroupType());
		}
	}

}
