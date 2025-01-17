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
package com.braintribe.model.processing.lock.dmb.impl;

import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.StampedLock;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.StandardMBean;

import com.braintribe.collections.EvictingConcurrentHashMap;
import com.braintribe.common.lcd.Numbers;
import com.braintribe.model.processing.lock.api.LockBuilder;
import com.braintribe.model.processing.lock.api.LockManager;
import com.braintribe.model.processing.lock.impl.AbstractLockBuilder;

/**
 * Implementation of the {@link LockManager} interface that relies on <code>MBeans</code> to share the locks across
 * Classloader-boundaries.
 * <p>
 * The locks are, in contrast to the original implementation, not {@link ReentrantLock} locks, but rather
 * {@link StampedLock} locks. This is necessary because the {@link ReentrantLock} requires that the
 * {@link ReentrantLock#unlock()} method is called from the same thread as {@link ReentrantLock#lock()}. This is not
 * always possible as the InternalizingLockService may use this LockManager and it cannot make this guarantee.
 * <p>
 * The locks are stored in a {@link EvictingConcurrentHashMap} so that the number of stored locks does not get
 * excessive. Only locks that are not currently in <code>locked</code> state will be evicted from the map.
 * <p>
 * Implementation note: The previous implementations using weak references to prevent stale locks was not correct.
 * Because of the InternalizingLockService, locks may become unreferenced and removed by the GC, although a remote
 * instance may still have a reference to this lock.
 *
 * @author roman.kurmanowytsch
 */
@Deprecated
public class DmbLockManager implements LockManager {
	public static final String DmbLockMapObjectName = "com.braintribe.tribefire:type=Locks";

	protected EvictingConcurrentHashMap<String, StampedLockEntry> locks = null;
	protected int evictionThreshold = 1_000;
	protected long evictionInterval = Numbers.MILLISECONDS_PER_MINUTE;

	public DmbLockManager() throws Exception {
		Map<String, Object> lockManagerData = acquirelockManagerData();
		locks = (EvictingConcurrentHashMap<String, StampedLockEntry>) lockManagerData.get("locks");
	}

	private static Map<String, Object> acquirelockManagerData() throws Exception {
		MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();

		synchronized (mbs) {
			ObjectName name = new ObjectName(DmbLockMapObjectName);

			if (!mbs.isRegistered(name)) {
				@SuppressWarnings("rawtypes")
				StandardMBean mbean = new StandardMBean(new HashMap(), Map.class);
				mbs.registerMBean(mbean, name);
			}

			Map<String, Object> lockManagerData = DynamicMBeanProxy.create(Map.class, name);

			// Initial setup of the locks map. Note the eviction policy that allows only locks to be removed that
			// are not in <code>locked</code> state.
			EvictingConcurrentHashMap<String, StampedLockEntry> locks = new EvictingConcurrentHashMap<>(e -> !e.getValue().isLocked());
			locks.setEvictionInterval(Numbers.MILLISECONDS_PER_MINUTE);
			locks.setEvictionThreshold(1_000);
			lockManagerData.put("locks", locks);

			return lockManagerData;
		}
	}

	private StampedLockEntry acquireLock(String id) {
		synchronized (locks) {
			StampedLockEntry lock = locks.get(id);

			if (lock == null) {
				StampedLock stampedLock = new StampedLock();
				lock = new StampedLockEntry(stampedLock);

				locks.put(id, lock);
			}

			return lock;
		}
	}

	@Override
	public LockBuilder forIdentifier(String id) {
		return new AbstractLockBuilder(id) {
			@Override
			public Lock exclusive() {
				return acquireLock(identifier).asWriteLock();
			}

			@Override
			public Lock shared() {
				return acquireLock(identifier).asReadLock();
			}
		};
	}

	public void setEvictionThreshold(int evictionThreshold) {
		this.evictionThreshold = evictionThreshold;
		if (locks != null) {
			locks.setEvictionThreshold(evictionThreshold);
		}
	}
	public void setEvictionInterval(long evictionInterval) {
		this.evictionInterval = evictionInterval;
		if (locks != null) {
			locks.setEvictionInterval(evictionInterval);
		}
	}

	@Override
	public String description() {
		return "DMB Lock Manager";
	}

}
