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
package com.braintribe.model.processing.lock.etcd.worker;

import java.io.File;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

import com.braintribe.model.processing.lock.etcd.EtcdLockManager;
import com.braintribe.model.processing.lock.etcd.impl.EtcdLockManagerTest;
import com.braintribe.model.processing.lock.etcd.remote.ThreadCompleteListener;
import com.braintribe.model.processing.lock.etcd.wire.contract.EtcdLockingTestContract;
import com.braintribe.utils.DateTools;
import com.braintribe.utils.FileTools;

public class Worker extends Thread {

	private EtcdLockingTestContract configuration;
	private String workerId;
	private int iterations;
	private ThreadCompleteListener listener;
	private int failProbability;
	private File file;
	private long maxWait;

	public Worker(EtcdLockingTestContract configuration, String workerId, int iterations, File file, long maxWait) {
		this.configuration = configuration;
		this.workerId = workerId;
		this.file = file;
		this.iterations = iterations;
		this.maxWait = maxWait;
	}

	private void print(String text) {
		System.out.println(DateTools.encode(new Date(), DateTools.LEGACY_DATETIME_WITH_MS_FORMAT)+" [Worker/"+workerId+"]: "+text);
		System.out.flush();
	}

	@Override
	public void run() {

		try {
			EtcdLockManager lockManager = configuration.lockManager();
			Lock lock = null;
			if (this.failProbability > 0) {
				lock = lockManager.forIdentifier(EtcdLockManagerTest.IDENTIFIER).lockTtl(EtcdLockManagerTest.LOCK_TIMEOUT, TimeUnit.MILLISECONDS).shared();
			} else {
				lock = lockManager.forIdentifier(EtcdLockManagerTest.IDENTIFIER).shared();
			}

			long start = System.currentTimeMillis();
			int expectedNumber = -1;
			for (;;) {

				boolean gotLock = false;
				try {
					gotLock = lock.tryLock(EtcdLockManagerTest.LOCK_TRY_WAIT, TimeUnit.MILLISECONDS);
				} catch (InterruptedException e) {
					print("Got interrupted.");
					return;
				}
				if (gotLock) {

					print("Got lock");
					boolean doUnlock = true;

					try {
						for (int i=0; i<iterations; ++i) {

							String content = FileTools.readStringFromFile(file);
							int number = Integer.parseInt(content);
							if (expectedNumber == -1) {
								expectedNumber = number;
							} else {
								if (number != expectedNumber) {
									FileTools.writeStringToFile(file, "Worker "+workerId+" expected "+expectedNumber+" but got "+number);
									print("Unexpected number: "+number+", expected: "+expectedNumber);
									return;
								}
							}
							number++;
							expectedNumber = number;
							print("Writing "+number+" (iteration: "+i+")");
							FileTools.writeStringToFile(file, ""+number);


							if (this.failProbability > 0) {
								int randomNumber = (new Random(System.currentTimeMillis())).nextInt(100);
								if (randomNumber < this.failProbability) {
									print("Quitting without notice; keeping lock to check timeout functionality");
									doUnlock = false;
									return;
								}
							}

							try {
								Thread.sleep(EtcdLockManagerTest.INTERVAL);
							} catch (InterruptedException e) {
								print("Got interrupted while waiting for the next run.");
								return;
							}


						}
					} finally {
						if (doUnlock) {
							print("Unlocking");
							lock.unlock();
						} else {
							print("Deliberately not unlocking. Let's see what happens.");
						}
					}
					return;
				} else {
					if ((System.currentTimeMillis() - start) > maxWait) {
						print("Waited too long for a lock. Aborting.");
						return;
					}
				}
			}

		} finally {
			if (listener != null) {
				listener.notifyOfThreadComplete();
			}
		}
	}
	public void setFailProbability(int failProbability) {
		this.failProbability = failProbability;
	}

	public void registerManger(ThreadCompleteListener listener) {
		this.listener = listener;
	}
}
