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
package com.braintribe.model.processing.index;

import java.util.Map;

/**
 * Implementation of {@link Index} with concurrent cache of values.
 */
public abstract class ConcurrentCachedIndex<K, V> implements Index<K, V> {
	
	protected Map<K, V> index = IndexTools.newCacheMap();

	/**
	 * Retrieves the indexed value if present, in other case asks for new value to be provided via
	 * {@link #provideValueFor(Object)} method.
	 * <p>
	 * This method guarantees that a value is created only once for given key.
	 */
	@Override
	public final V acquireFor(K key) {
		V value = index.get(key);
		if (value == null) {
			return acquireSynchronized(key);
		}

		return value;
	}

	private synchronized V acquireSynchronized(K key) {
		// For whatever reason the computeIfAbsent leads to a deadlock in tests
		// return index.computeIfAbsent(key, this::provideValueFor);
		
		V value = index.get(key);
		if (value == null) {
			value = provideValueFor(key);
			index.put(key, value);
		}

		return value;
	}

	protected abstract V provideValueFor(K key);
}
