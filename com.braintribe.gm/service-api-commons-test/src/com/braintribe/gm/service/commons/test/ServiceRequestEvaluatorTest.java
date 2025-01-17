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
package com.braintribe.gm.service.commons.test;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.braintribe.gm.service.commons.test.model.EvalTestServiceRequest1;
import com.braintribe.gm.service.commons.test.model.EvalTestServiceRequest2;
import com.braintribe.gm.service.commons.test.model.EvalTestServiceRequest3;
import com.braintribe.gm.service.commons.test.wire.ServiceApiCommonsTestWireModule;
import com.braintribe.gm.service.commons.test.wire.contract.ServiceApiCommonsTestContract;
import com.braintribe.model.generic.eval.Evaluator;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.processing.service.common.eval.AbstractServiceRequestEvaluator;
import com.braintribe.model.securityservice.OpenUserSession;
import com.braintribe.model.securityservice.credentials.UserPasswordCredentials;
import com.braintribe.model.service.api.AuthorizableRequest;
import com.braintribe.model.service.api.ServiceRequest;
import com.braintribe.processing.async.api.AsyncCallback;
import com.braintribe.wire.api.Wire;
import com.braintribe.wire.api.context.WireContext;

/**
 * <p>
 * {@link AbstractServiceRequestEvaluator} tests.
 * 
 */
public class ServiceRequestEvaluatorTest {

	protected static WireContext<ServiceApiCommonsTestContract> context;
	protected static Evaluator<ServiceRequest> evaluator;
	protected static ServiceApiCommonsTestContract testContract;
	protected static String sessionId;
	
	@BeforeClass
	public static void beforeClass() {
		context = Wire.context(ServiceApiCommonsTestWireModule.INSTANCE);
		testContract = context.contract();
		evaluator = testContract.evaluator();
		sessionId = validSessionId();
	}
	
	@AfterClass
	public static void afterClass() {
		context.shutdown();
	}

	public static String validSessionId() {
		OpenUserSession openUserSession = OpenUserSession.T.create();
		
		openUserSession.setCredentials(UserPasswordCredentials.forUserName("tester", "7357"));
		
		return openUserSession.eval(evaluator).get().getUserSession().getSessionId();
	}
	
	public static <R extends AuthorizableRequest> R createAuthorized(EntityType<R> requestType) {
		R request = requestType.create();
		request.setSessionId(sessionId);
		return request;
	}
	
	@Test
	public void testService1() throws Exception {

		ServiceRequest request = EvalTestServiceRequest1.T.create();

		Object response = evaluator.eval(request).get();

		Assert.assertNotNull(response);
		Assert.assertTrue(response instanceof String);

	}

	@Test
	public void testService2() throws Exception {

		ServiceRequest request = createAuthorized(EvalTestServiceRequest2.T);

		Object response = evaluator.eval(request).get();

		Assert.assertNotNull(response);
		Assert.assertTrue(response instanceof Long);

	}

	@Test
	public void testService3() throws Exception {

		ServiceRequest request = createAuthorized(EvalTestServiceRequest3.T);

		Object response = evaluator.eval(request).get();

		Assert.assertNotNull(response);
		Assert.assertTrue(response instanceof Integer);

	}

	@Test
	public void testService1Async() throws Exception {

		ServiceRequest request = EvalTestServiceRequest1.T.create();

		BlockingAsyncCallback callback = new BlockingAsyncCallback();

		evaluator.eval(request).get(callback);

		Object response = callback.get(10000);

		Assert.assertNotNull(response);
		Assert.assertTrue(response instanceof String);

	}

	@Test
	public void testService2Async() throws Exception {

		EvalTestServiceRequest2 request = createAuthorized(EvalTestServiceRequest2.T);

		BlockingAsyncCallback callback = new BlockingAsyncCallback();

		evaluator.eval(request).get(callback);

		Object response = callback.get(10000);

		Assert.assertNotNull(response);
		Assert.assertTrue(response instanceof Long);

	}

	@Test
	public void testService3Async() throws Exception {

		EvalTestServiceRequest3 request = createAuthorized(EvalTestServiceRequest3.T);

		BlockingAsyncCallback callback = new BlockingAsyncCallback();

		evaluator.eval(request).get(callback);

		Object response = callback.get(10000);

		Assert.assertNotNull(response);
		Assert.assertTrue(response instanceof Integer);

	}

	protected class BlockingAsyncCallback implements AsyncCallback<Object> {

		private ArrayBlockingQueue<Object> queue = new ArrayBlockingQueue<Object>(1);

		@Override
		public void onSuccess(Object future) {
			try {
				queue.put(future);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void onFailure(Throwable t) {
			t.printStackTrace();
			try {
				queue.put(t);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		public Object get(long timeout) throws Exception {
			Object r = queue.poll(timeout, TimeUnit.MILLISECONDS);
			if (r instanceof Throwable) {
				Throwable t = (Throwable) r;
				Assert.fail(t + ":" + t.getMessage());
			}
			return r;
		}

	}
}
