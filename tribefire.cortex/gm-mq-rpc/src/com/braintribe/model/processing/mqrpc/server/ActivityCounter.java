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
package com.braintribe.model.processing.mqrpc.server;

import java.util.concurrent.locks.ReentrantLock;

public class ActivityCounter {
	private volatile int count;
	private ReentrantLock lock = new ReentrantLock();

	public void inc() {
		lock.lock();
		try {
			count++;
		} finally {
			lock.unlock();
		}
	}

	public void dec() {
		lock.lock();
		try {
			if (--count == 0) {
				notify();
			}
		} finally {
			lock.unlock();
		}
	}

	public boolean awaitZeroActivity(long maxWaitInMs) {
		lock.lock();
		try {
			if (count == 0)
				return true;

			try {
				wait(maxWaitInMs);
			} catch (InterruptedException e) {
				// ignore;
			}

			return count == 0;
		} finally {
			lock.unlock();
		}
	}
}
