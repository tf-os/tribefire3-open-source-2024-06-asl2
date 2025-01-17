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
package com.braintribe.model.processing.vde.impl.async;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.braintribe.gwt.async.client.DeferredExecutor;
import com.braintribe.gwt.async.client.Future;
import com.braintribe.gwt.async.testing.Futures;
import com.braintribe.model.processing.vde.clone.async.DeferredExecutorAspect;
import com.braintribe.model.processing.vde.evaluator.VDE;
import com.braintribe.model.processing.vde.evaluator.api.ValueDescriptorEvaluator;
import com.braintribe.model.processing.vde.evaluator.api.VdeContext;
import com.braintribe.model.processing.vde.evaluator.api.VdeRegistry;
import com.braintribe.model.processing.vde.evaluator.api.VdeResult;
import com.braintribe.model.processing.vde.evaluator.api.VdeRuntimeException;
import com.braintribe.model.processing.vde.evaluator.impl.VdeResultImpl;
import com.braintribe.model.processing.vde.impl.async.model.AsyncVde;
import com.braintribe.model.processing.vde.impl.async.model.AsyncVdeWrapper;
import com.braintribe.model.processing.vde.test.VdeTest;
import com.braintribe.processing.async.api.AsyncCallback;

/**
 * @author peter.gazdik
 */
public class AsyncEvaluationVdeTest extends VdeTest {

	private static final String HELLO = "hello";

	@Test
	public void testEval() throws Exception {
		AsyncVde vde = AsyncVde.T.create();
		vde.setResult(HELLO);

		AsyncVdeWrapper wrapper = AsyncVdeWrapper.T.create();
		wrapper.setAObject(vde);

		VdeRegistry registry = VDE.extendedRegistry() //
				.withConcreteExpert(AsyncVde.class, asyncVdEvalutor) //
				.done();

		Future<Object> promise = new Future<>();
		evaluate().withRegistry(registry).forValue(wrapper, promise);
		Futures.waitFor(promise);

		AsyncVdeWrapper result = (AsyncVdeWrapper) promise.getResult();
		assertThat(result).isNotNull();
		assertThat(result.getAObject()).isEqualTo(HELLO);
	}

	private final ValueDescriptorEvaluator<AsyncVde> asyncVdEvalutor = new ValueDescriptorEvaluator<AsyncVde>() {

		@Override
		public VdeResult evaluate(VdeContext context, AsyncVde valueDescriptor) throws VdeRuntimeException {
			throw new UnsupportedOperationException("Method 'AsyncEvaluationVdeTest.enclosing_method' is not supported!");
		}

		@Override
		public void evaluateAsync(VdeContext context, AsyncVde valueDescriptor, AsyncCallback<VdeResult> callback) {
			DeferredExecutor deferredExecutor = context.get(DeferredExecutorAspect.class);

			// In our test, the job that gets here and calls the execute method is the last one
			// Submit Job1: Clone evaluate wrapper
			// Start Job1
			// Submit Job2: Process cloned AsyncVde
			// Done Job1
			// Start Job2
			// THIS CODE IS CALLED - new task is registered on deferred executor
			// Job2 Done - No Jobs left
			// The tack here is called - creates VdeResultImpl and notifies callback
			// Submit Job3 - Processing of evaluated Wrapper, because this result ("hello") was the only property value missing (all others were
			// . . cloned within their tasks synchronously)
			// . . This submit must therefore start the queue again, as there were no jobs left
			deferredExecutor.execute(() -> {
				callback.onSuccess(new VdeResultImpl(valueDescriptor.getResult()));
			});
		}

	};

}
