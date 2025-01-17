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
package tribefire.platform.impl.deployment;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

import com.braintribe.cfg.Required;
import com.braintribe.model.accessapi.AccessRequest;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.processing.accessrequest.api.AccessRequestContext;
import com.braintribe.model.processing.accessrequest.api.AccessRequestProcessor;
import com.braintribe.model.processing.core.expert.api.MutableDenotationMap;
import com.braintribe.model.processing.core.expert.impl.ConfigurableGmExpertRegistry;
import com.braintribe.model.processing.service.api.ServiceProcessor;
import com.braintribe.model.processing.service.api.ServiceRequestContext;
import com.braintribe.model.service.api.ServiceRequest;

import tribefire.platform.impl.binding.AccessRequestProcessorBinder;

/**
 * <p>
 * This class was introduced to:
 * <ul>
 * <li>Support type-safe hardwired {@link ServiceProcessor} bindings</li>
 * <li>Support type-safe hardwired {@link AccessRequestProcessor} bindings</li>
 * <li>Use a common way of creating a {@link ServiceProcessor} from an existing {@link AccessRequestProcessor} via an
 * existing {@link AccessRequestProcessorBinder}</li>
 * </ul>
 */
public class HardwiredServiceProcessorRegistration {

	private final Map<EntityType<? extends ServiceRequest>, ServiceProcessor<? extends ServiceRequest, ? extends Object>> experts = new IdentityHashMap<>();
	private AccessRequestProcessorBinder processorBinder;

	@Required
	public void setProcessorBinder(AccessRequestProcessorBinder processorBinder) {
		this.processorBinder = processorBinder;
	}

	public void register(ConfigurableGmExpertRegistry bean) {
		experts.forEach((k, v) -> bean.add(ServiceProcessor.class, k.getJavaType(), v));
	}

	public void register(MutableDenotationMap<ServiceRequest, ServiceProcessor<? extends ServiceRequest, ?>> map) {
		experts.forEach((k, v) -> map.put(k, v));
	}

	public <T extends ServiceRequest> void bindServiceRequest(EntityType<T> denotationType, ServiceProcessor<? super T, ?> processor) {
		experts.put(denotationType, processor);
	}

	public <T extends AccessRequest> void bindAccessRequest(EntityType<T> denotationType, AccessRequestProcessor<? super T, ?> processor) {
		experts.put(denotationType, (ServiceProcessor<T, ?>) processorBinder.bind(new HardwiredDeploymentContext<>(processor)));
	}

	public <T extends ServiceRequest> void bindServiceRequestLazy(EntityType<T> requestType,
			Supplier<ServiceProcessor<? super T, ?>> processorSupplier) {

		bindServiceRequest(requestType, new DelegatingServiceProcessor<T>(processorSupplier));
	}

	public <T extends AccessRequest> void bindAccessRequestLazy(EntityType<T> requestType,
			Supplier<AccessRequestProcessor<? super T, ?>> processorSupplier) {
		bindAccessRequest(requestType, new DelegatingAccessRequestProcessor<T>(processorSupplier));
	}

	private static class DelegatingServiceProcessor<P extends ServiceRequest> implements ServiceProcessor<P, Object> {

		private final Supplier<ServiceProcessor<? super P, ?>> processorSupplier;
		private ServiceProcessor<? super P, ?> delegate;
		private ReentrantLock lock = new ReentrantLock();

		public DelegatingServiceProcessor(Supplier<ServiceProcessor<? super P, ?>> processorSupplier) {
			this.processorSupplier = processorSupplier;
		}

		@Override
		public Object process(ServiceRequestContext requestContext, P request) {
			if (delegate == null)
				resolveDelegate();

			return delegate.process(requestContext, request);
		}

		private void resolveDelegate() {
			if (delegate == null) {
				lock.lock();
				try {
					if (delegate == null) {
						delegate = processorSupplier.get();
					}
				} finally {
					lock.unlock();
				}
			}
		}

	}

	private static class DelegatingAccessRequestProcessor<P extends AccessRequest> implements AccessRequestProcessor<P, Object> {

		private final Supplier<AccessRequestProcessor<? super P, ?>> processorSupplier;
		private AccessRequestProcessor<P, ?> delegate;
		private ReentrantLock lock = new ReentrantLock();

		public DelegatingAccessRequestProcessor(Supplier<AccessRequestProcessor<? super P, ?>> processorSupplier) {
			this.processorSupplier = processorSupplier;
		}

		@Override
		public Object process(AccessRequestContext<P> context) {
			if (delegate == null)
				resolveDelegate();

			return delegate.process(context);
		}

		private void resolveDelegate() {
			if (delegate == null) {
				lock.lock();
				try {
					if (delegate == null) {
						delegate = (AccessRequestProcessor<P, ?>) processorSupplier.get();
					}
				} finally {
					lock.unlock();
				}
			}
		}

	}
}
