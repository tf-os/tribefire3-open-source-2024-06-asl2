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
package com.braintribe.utils.collection.impl.map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.junit.Before;
import org.junit.Test;

import com.braintribe.utils.collection.impl.BtNavigableMap;

/**
 * @deprecated {@link BtNavigableMap} was only created for GWT compatibility. GWT now supports {@link TreeMap}, so use that!!!
 */
@Deprecated
public class BtNavigableMap_ValuesEntrySets_Tests extends BtNavigableMap_HighLevelTestBase {

	private static final long MAX = 100L;
	private static final Map<Long, Long> m = new HashMap<Long, Long>();
	private static final Long[] keys = new Long[(int) MAX];

	static {
		m.put(null, null);
		int counter = 0;
		for (long l = 1l; l <= MAX; l++) {
			m.put(l, getStandardValueForKey(l));
			keys[counter++] = l;
		}
	}

	@Override
	@Before
	public void setUp() {
		super.setUp();
	}

	@Test
	public void testPutAllAndContainsValue() {
		map.putAll(m);
		assertContains();
	}

	private void assertContains() {
		assertContains(keys);
		assertContainsValues();
	}

	private void assertContainsValues() {
		assertTrue(map.containsValue(getStandardValueForKey(null)));
		for (Long l: keys) {
			assertTrue(map.containsValue(getStandardValueForKey(l)));
		}
	}

	@Test
	public void testValueIterator() {
		map.putAll(m);
		Iterator<Long> it = map.values().iterator();
		boolean wasNull = false;
		while (it.hasNext()) {
			Long next = it.next();
			if (next != null) {
				if (next % 5 == 0) {
					it.remove();
				}
			} else {
				wasNull = true;
			}
		}
		assertContainsNonFiveMultiples();
		assertTrue("Null was not listed as value", wasNull);

	}

	@Test
	public void testValuesRemoveIterator() {
		map.putAll(m);
		Collection<Long> values = map.values();
		assertTrue(values.contains(null));
		for (Long l: keys) {
			if (l % 5 == 0) {
				values.remove(getStandardValueForKey(l));
			}
		}

		assertContainsNonFiveMultiples();
	}

	private void assertContainsNonFiveMultiples() {
		for (long l = 1l; l < 100l; l++) {
			assertEquals("Wrong 'contains' result for: " + l, l % 5 != 0, map.containsKey(l));
			assertEquals("Wrong 'contains' result for: " + l, l % 5 != 0, map.values().contains(getStandardValueForKey(l)));
		}
	}

	@Test
	public void testEntrySet() {
		map.putAll(m);
		Set<Entry<Long, Long>> entrySet = map.entrySet();
		for (Entry<Long, Long> entry: entrySet) {
			assertTrue(entrySet.contains(entry));
		}
		Entry<Long, Long> firstEntry = entrySet.iterator().next();
		entrySet.remove(firstEntry);
		assertFalse(entrySet.contains(firstEntry));

		entrySet.clear();
		assertSize(0);
	}

	@Test
	public void testEntrySetOfSubMap() {
		map.putAll(m);
		Set<Entry<Long, Long>> entrySet = map.subMap(10L, 80L).entrySet();
		for (Entry<Long, Long> entry: entrySet) {
			assertTrue(entrySet.contains(entry));
		}
		Entry<Long, Long> firstEntry = entrySet.iterator().next();
		assertEquals("Wrong first entry key", (Long) 10L, firstEntry.getKey());
		entrySet.remove(firstEntry);
		assertFalse(entrySet.contains(firstEntry));

		entrySet.clear();
		assertSize(31); // 100 - (80-10) + 1 (null)
	}

}
