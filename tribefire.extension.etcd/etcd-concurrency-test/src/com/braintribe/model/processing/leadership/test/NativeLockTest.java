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
package com.braintribe.model.processing.leadership.test;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.braintribe.integration.etcd.supplier.ClientSupplier;
import com.braintribe.testing.category.SpecialEnvironment;
import com.braintribe.utils.StringTools;
import com.braintribe.utils.date.NanoClock;
import com.braintribe.utils.lcd.CollectionTools2;

import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.Lock;
import io.etcd.jetcd.lease.LeaseGrantResponse;

//Ignore, was just used for some curious tests

@Category(SpecialEnvironment.class)
public class NativeLockTest {

	@Test
	public void testLocking() throws Exception {

		ClientSupplier clientSupplier = new ClientSupplier(CollectionTools2.asList("http://localhost:2379"), null, null);
		Client client = clientSupplier.get();
		Lock lockClient = client.getLockClient();
		String lockId = "/lock-test";
		ByteSequence bsLockId = ByteSequence.from(lockId, StandardCharsets.UTF_8);


		ExecutorService service = Executors.newFixedThreadPool(5);
		try {

			Future<?> f1 = service.submit(() -> {
				
				LeaseGrantResponse leaseGrantResponse;
				try {
					leaseGrantResponse = client.getLeaseClient().grant(20).get();
				} catch (Exception e2) {
					throw new RuntimeException("Lease error 1", e2);
				}
				long leaseId = leaseGrantResponse.getID();
				
				System.out.println("Thread 1: Trying to get lock.");
				Instant start = NanoClock.INSTANCE.instant();
				try {
					lockClient.lock(bsLockId, leaseId).get();
				} catch (Exception e1) {
					throw new RuntimeException("Lock error 1", e1);
				}
				System.out.println("Thread 1: Got lock after "+StringTools.prettyPrintDuration(start, true, null));
				System.out.println("Thread 1: Keeping lock for 20 seconds.");
				try {
					Thread.sleep(20000L);
				} catch (InterruptedException e) {
					e.printStackTrace();
					throw new RuntimeException("Interrupted");
				}
				System.out.println("Thread 1: Unlocking.");
				lockClient.unlock(bsLockId);
				System.out.println("Thread 1: Done.");
			});
			
			Future<?> f2 = service.submit(() -> {
				
				LeaseGrantResponse leaseGrantResponse;
				try {
					leaseGrantResponse = client.getLeaseClient().grant(20).get();
				} catch (Exception e2) {
					throw new RuntimeException("Lease error 2", e2);
				}
				long leaseId = leaseGrantResponse.getID();

				System.out.println("Thread 2: Waiting for 5 seconds.");
				try {
					Thread.sleep(5000L);
				} catch (InterruptedException e) {
					e.printStackTrace();
					throw new RuntimeException("Interrupted");
				}
				System.out.println("Thread 2: Trying to get lock.");
				Instant start = NanoClock.INSTANCE.instant();
				try {
					lockClient.lock(bsLockId, leaseId).get();
				} catch (Exception e1) {
					throw new RuntimeException("Lock error 2", e1);
				}
				System.out.println("Thread 2: Got lock after "+StringTools.prettyPrintDuration(start, true, null));
				System.out.println("Thread 2: Keeping lock for 20 seconds.");
				try {
					Thread.sleep(20000L);
				} catch (InterruptedException e) {
					e.printStackTrace();
					throw new RuntimeException("Interrupted");
				}
				System.out.println("Thread 2: Unlocking.");
				lockClient.unlock(bsLockId);
				System.out.println("Thread 2: Done.");
			});
			
			Future<?> f3 = service.submit(() -> {
				
				LeaseGrantResponse leaseGrantResponse;
				try {
					leaseGrantResponse = client.getLeaseClient().grant(20).get();
				} catch (Exception e2) {
					throw new RuntimeException("Lease error 3", e2);
				}
				long leaseId = leaseGrantResponse.getID();

				System.out.println("Thread 3: Waiting for 20 seconds.");
				try {
					Thread.sleep(20000L);
				} catch (InterruptedException e) {
					e.printStackTrace();
					throw new RuntimeException("Interrupted");
				}
				System.out.println("Thread 3: Trying to get lock.");
				Instant start = NanoClock.INSTANCE.instant();
				try {
					lockClient.lock(bsLockId, leaseId).get();
				} catch (Exception e1) {
					throw new RuntimeException("Lock error 3", e1);
				}
				System.out.println("Thread 3: Got lock after "+StringTools.prettyPrintDuration(start, true, null));
				System.out.println("Thread 3: Keeping lock for 20 seconds.");
				try {
					Thread.sleep(20000L);
				} catch (InterruptedException e) {
					e.printStackTrace();
					throw new RuntimeException("Interrupted");
				}
				System.out.println("Thread 3: Unlocking.");
				lockClient.unlock(bsLockId);
				System.out.println("Thread 3: Done.");
			});
			
			List<Future<?>> futures = CollectionTools2.asList(f1, f2, f3);
			for (Future<?> f : futures) {
				f.get();
			}
			
		} finally {
			service.shutdown();
		}
	}
}
