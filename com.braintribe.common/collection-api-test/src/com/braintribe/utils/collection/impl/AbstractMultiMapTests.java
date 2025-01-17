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
package com.braintribe.utils.collection.impl;

import static com.braintribe.utils.collection.impl.CollectionTestTools.getStandardValueForKey;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.List;

import com.braintribe.utils.collection.api.MultiMap;

/**
 * 
 */
public abstract class AbstractMultiMapTests<T extends MultiMap<Long, Long>> {

	protected T multiMap;

	protected void addNewEntriesFor(Long... keys) {
		for (Long key : keys) {
			assertTrue(standardPut(key));
		}
	}

	protected void addEntriesFor(Long... keys) {
		for (Long key : keys) {
			standardPut(key);
		}
	}

	protected boolean standardPut(Long l) {
		return put(l, getStandardValueForKey(l));
	}

	protected boolean put(Long l, Long value) {
		return multiMap.put2(l, value);
	}

	protected boolean standardRemove(Long l) {
		return remove(l, getStandardValueForKey(l));
	}

	protected boolean remove(Long key, Long value) {
		return multiMap.remove(key, value);
	}

	protected void standardAdd() {
		put(null, 0L);

		for (long i = 0; i < 200; i++) {
			for (int j = 0; j <= i % 3; j++)
				put(i, 100 * i + 10 * j);

			if (i % 5 == 0)
				put(i, null);
		}

		assertSize(440);
	}

	protected int numberOfValuesForKey(long i) {
		return ((int) i) % 3 + (i % 5 == 0 ? 1 : 0) + 1;
	}

	protected void standardContains() {
		assertContainsKeyValue(null, 0L);

		for (long i = 0; i < 200; i++) {
			for (int j = 0; j <= i % 3; j++) {
				assertContainsKeyValue(i, 100 * i + 10 * j);
			}
			if (i % 5 == 0) {
				assertContainsKeyValue(i, null);
			}
		}

		assertSize(440);
	}

	protected void assertContains(Long... keys) {
		for (Long key : keys) {
			assertContainsKey(key);
		}
	}

	private void assertContainsKey(Long key) {
		assertTrue("Key not found in the map: " + key, multiMap.containsKey(key));
		Long value = getStandardValueForKey(key);
		assertEquals("Value not found in the map", value, multiMap.get(key));
	}

	protected void assertContainsKeyValue(Long key, Long value) {
		assertContainsKeyValue(multiMap, key, value);
	}

	protected void assertValueForKey(MultiMap<Long, Long> multiMap, Long key, Long expectedValue) {
		assertThat(multiMap.get(key)).isEqualTo(expectedValue);
	}

	protected void assertContainsKeyValue(MultiMap<Long, Long> multiMap, Long key, Long value) {
		assertTrue("Entry [" + key + ", " + value + "] not found", multiMap.containsKeyValue(key, value));
		assertTrue("Entry [" + key + ", " + value + "] not found", multiMap.getAll(key).contains(value));
	}

	protected void assertContainsKeyValues(MultiMap<Long, Long> multiMap, Long key, Long... values) {
		Collection<Long> all = multiMap.getAll(key);

		assertThat(all).containsOnly(values);

		if (all instanceof List) {
			// also check order
			assertThat(all).containsExactly(values);
		}
	}

	protected void assertNotContains(Long... keys) {
		for (Long key : keys) {
			assertNotContains(key, getStandardValueForKey(key));
		}
	}

	protected void assertNotContains(Long key, Long value) {
		assertFalse("Entry [" + key + ", " + value + "] SHOULD NOT have been found", multiMap.containsKeyValue(key, value));
	}

	protected void assertSize(int expectedSize) {
		assertSize(multiMap, expectedSize);
	}

	protected void assertEmpty() {
		assertEmpty(multiMap);
	}

	protected void assertEmpty(MultiMap<Long, Long> multiMap) {
		assertTrue("Map should be empty!", multiMap.isEmpty());
		assertSize(multiMap, 0);
	}

	protected void assertSize(MultiMap<Long, Long> multiMap, int expectedSize) {
		assertEquals("Wrong size", expectedSize, multiMap.size());
	}

}
