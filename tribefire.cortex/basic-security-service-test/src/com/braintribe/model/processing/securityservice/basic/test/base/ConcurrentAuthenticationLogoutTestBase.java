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
package com.braintribe.model.processing.securityservice.basic.test.base;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

import com.braintribe.common.lcd.Numbers;

public class ConcurrentAuthenticationLogoutTestBase extends SecurityServiceTest {

	@Test
	public void testConcurrentAuthentication() throws Exception {

		if (!testConfig.getEnableLongRunning()) {
			System.out.println("suppressed test as testConfig.getEnableLongRunning() is " + testConfig.getEnableLongRunning());
			return;
		}

		int tasks = 1000;
		int sessions = tasks * 5;

		final Map<String, Boolean> userSessionIds = new ConcurrentHashMap<>(sessions);

		ExecutorService executor = Executors.newFixedThreadPool(400);

		Callable<Boolean> authentications = new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				userSessionIds.put(quickOpenSession("cortex").getSessionId(), true);
				userSessionIds.put(quickOpenSession("john.smith").getSessionId(), true);
				userSessionIds.put(quickOpenSession("mary.williams").getSessionId(), true);
				userSessionIds.put(quickOpenSession("robert.taylor").getSessionId(), true);
				userSessionIds.put(quickOpenSession("steven.brown").getSessionId(), true);
				return true;
			}
		};

		while (tasks-- > 0) {
			executor.submit(authentications);
		}

		Thread.sleep(10000);

		int created = userSessionIds.size();
		while (created < sessions) {
			System.out.println("waiting for all " + sessions + " sessions to be created. " + created + " so far.");
			Thread.sleep(5000);
			created = userSessionIds.size();
		}

		System.out.println("done. " + userSessionIds.size() + " user sessions created. checking their deregistration in 2 min.");

		Thread.sleep(Numbers.MILLISECONDS_PER_MINUTE * 2);

		for (String sessionId : userSessionIds.keySet()) {
			assertUserSessionLoggedOut(sessionId);
		}

		System.out.println("success. all " + userSessionIds.size() + " user sessions created were deregistered after 2 min.");

	}

	@Test
	public void testConcurrentAuthenticationLogout() throws Exception {

		if (!testConfig.getEnableLongRunning()) {
			System.out.println("suppressed test as testConfig.getEnableLongRunning() is " + testConfig.getEnableLongRunning());
			return;
		}

		int tasks = 1000;
		int sessions = tasks * 5;

		final AtomicInteger loggedInCount = new AtomicInteger();
		final AtomicInteger loggedOutCount = new AtomicInteger();

		final Map<String, Boolean> loggedIn = new HashMap<>(sessions);
		final Map<String, Boolean> loggedOut = new HashMap<>(sessions);

		ExecutorService executor = Executors.newFixedThreadPool(400);
		final ExecutorService logOutExecutor = Executors.newFixedThreadPool(400);

		Callable<Boolean> authentications = new Callable<Boolean>() {

			private void openSessionAndLogout(String userId) {
				final String sessionId = quickOpenSession(userId).getSessionId();

				loggedIn.put(sessionId, true);
				loggedInCount.incrementAndGet();

				logOutExecutor.submit(new Callable<Boolean>() {
					@Override
					public Boolean call() throws Exception {
						Thread.sleep(5000);
						loggedOut.put(sessionId, logout(sessionId));
						loggedOutCount.incrementAndGet();
						return true;
					}

				});

			}

			@Override
			public Boolean call() throws Exception {
				openSessionAndLogout("cortex");
				openSessionAndLogout("john.smith");
				openSessionAndLogout("mary.williams");
				openSessionAndLogout("robert.taylor");
				openSessionAndLogout("steven.brown");
				return true;
			}

		};

		while (tasks-- > 0) {
			executor.submit(authentications);
		}

		Thread.sleep(10000);

		while (loggedInCount.get() < sessions) {
			System.out.println("waiting for all " + sessions + " sessions to be created. " + loggedInCount.get() + " so far.");
			Thread.sleep(5000);
		}

		System.out.println("done. " + loggedIn.size() + " user sessions created. checking their destruction.");

		while (loggedOutCount.get() < sessions) {
			System.out.println("waiting for all " + sessions + " sessions to be destroyed. " + loggedOutCount.get() + " so far.");
			Thread.sleep(5000);
		}

		System.out.println("done. " + loggedOutCount.get() + " user sessions logged out.");

		Thread.sleep(Numbers.MILLISECONDS_PER_MINUTE);

		for (String sessionId : loggedIn.keySet()) {
			assertUserSessionLoggedOut(sessionId);
		}

		System.out.println("success. all " + loggedInCount.get() + " user sessions created were deregistered.");

	}

}
