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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Assert;
import org.junit.Test;

import com.braintribe.model.processing.securityservice.commons.provider.AuthenticatingUserSessionProvider;
import com.braintribe.model.securityservice.credentials.Credentials;
import com.braintribe.model.usersession.UserSession;

public class AuthenticatingUserSessionProviderTestBase extends SecurityServiceTest {

	/**
	 * Unlike AuthenticatingUserSessionProvider from SecurityServiceApiCommons#1.0 , AuthenticatingUserSessionProvider from
	 * SecurityServiceApiCommons#1.1 will not renew the session automatically. It is reactive, an authorization failure must
	 * be signaled.
	 */
	private final Exception authorizationFailureSignal = new Exception("Signal for renewing the session.");

	@Test
	public void testConcurrentAccess() throws Exception {

		final AuthenticatingUserSessionProvider<Credentials> provider = getProviderForUserPasswordCredentials();

		List<Callable<Boolean>> callers = new ArrayList<Callable<Boolean>>();

		for (int i = 0; i < 200; i++) {
			callers.add(new Callable<Boolean>() {
				@Override
				public Boolean call() throws Exception {
					try {
						UserSession userSession = provider.get();

						if (testConfig.getEnableExpiration()) {
							Thread.sleep(19000);
						}

						assertUserSession(userSession);

						if (testConfig.getEnableExpiration()) {
							Thread.sleep(21000);

						}
						provider.accept(authorizationFailureSignal);
						userSession = provider.get();
						assertUserSession(userSession);

					} catch (Exception e) {
						e.printStackTrace();
						return false;
					}
					return true;
				}
			});
		}

		ExecutorService executor = Executors.newFixedThreadPool(1100);
		executor.invokeAll(callers);

	}

	@Test
	public void testProvideWithUserPasswordCredentials() throws Exception {
		testProvide(getProviderForUserPasswordCredentials());
	}

	@Test
	public void testRenewUponLogoutWithUserPasswordCredentials() throws Exception {
		testRenewUponLogout(getProviderForUserPasswordCredentials());
	}

	@Test
	public void testTimeoutWithUserPasswordCredentials() throws Exception {

		if (!testConfig.getEnableExpiration()) {
			System.out.println("suppressed test as testConfig.getEnableExpiration() is " + testConfig.getEnableExpiration());
			return;
		}

		testTimeout(getProviderForUserPasswordCredentials());
	}

	protected void testProvide(AuthenticatingUserSessionProvider<Credentials> provider) throws Exception {

		UserSession userSession = provider.get();

		assertUserSession(userSession);

		provider.preDestroy();

		Assert.assertFalse("Session [ " + userSession.getSessionId() + " ] should be invalid after preDestroy()",
				isValidSession(userSession.getSessionId()));

	}

	protected void testRenewUponLogout(AuthenticatingUserSessionProvider<Credentials> provider) throws Exception {

		UserSession userSession = provider.get();

		assertUserSession(userSession);

		String firstSessionId = userSession.getSessionId();

		logout(firstSessionId);

		Assert.assertFalse("Session [ " + firstSessionId + " ] should be invalid after logout()", isValidSession(firstSessionId));

		// signal authorization failure in order to re-authenticate
		provider.accept(authorizationFailureSignal);
		userSession = provider.get();

		Assert.assertNotEquals("Different UserSession should have been provided", firstSessionId, userSession.getSessionId());

		assertUserSession(userSession);

		provider.preDestroy();

		Assert.assertFalse("Session [ " + firstSessionId + " ] should be invalid after preDestroy()", isValidSession(firstSessionId));
		Assert.assertFalse("Session [ " + userSession.getSessionId() + " ] should be invalid after preDestroy()",
				isValidSession(userSession.getSessionId()));

	}

	protected void testTimeout(AuthenticatingUserSessionProvider<Credentials> provider) throws Exception {

		UserSession userSession = provider.get();

		assertUserSession(userSession);

		String firstSessionId = userSession.getSessionId();

		Thread.sleep(21000);

		Assert.assertFalse("Session [ " + firstSessionId + " ] should have expired", isValidSession(firstSessionId));

		// signal authorization failure in order to re-authenticate
		provider.accept(authorizationFailureSignal);
		userSession = provider.get();

		Assert.assertNotEquals("Different UserSession should have been provided", firstSessionId, userSession.getSessionId());

		assertUserSession(userSession);

		provider.preDestroy();

		Assert.assertFalse("Session [ " + firstSessionId + " ] should be invalid after preDestroy()", isValidSession(firstSessionId));
		Assert.assertFalse("Session [ " + userSession.getSessionId() + " ] should be invalid after preDestroy()",
				isValidSession(userSession.getSessionId()));

	}

	private AuthenticatingUserSessionProvider<Credentials> getProviderForUserPasswordCredentials() {
		Credentials credentials = createUserPasswordCredentials(createUserNameIdentification("robert.taylor"), "3333");
		return getProvider(credentials);
	}

	private <T extends Credentials> AuthenticatingUserSessionProvider<T> getProvider(T credentials) {
		AuthenticatingUserSessionProvider<T> p = new AuthenticatingUserSessionProvider<T>();
		p.setCredentials(credentials);
		p.setEvaluator(evaluator);
		return p;
	}

}
