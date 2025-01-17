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
package com.braintribe.model.processing.service.common;

import java.util.Map;
import java.util.Map.Entry;

import com.braintribe.cfg.Configurable;
import com.braintribe.logging.Logger;
import com.braintribe.model.processing.service.api.ServiceProcessor;
import com.braintribe.model.processing.service.api.ServiceRequestContext;
import com.braintribe.model.service.api.AsynchronousRequest;
import com.braintribe.model.service.api.InstanceId;
import com.braintribe.model.service.api.MulticastRequest;
import com.braintribe.model.service.api.ServiceRequest;
import com.braintribe.model.service.api.UnicastRequest;
import com.braintribe.model.service.api.result.MulticastResponse;
import com.braintribe.model.service.api.result.ServiceResult;
import com.braintribe.processing.async.api.AsyncCallback;

/**
 * <p>
 * A {@link ServiceProcessor} which processes the {@link ServiceRequest}(s) wrapped by the incoming
 * {@link UnicastRequest}(s) against the instance as given by {@link UnicastRequest#getAddressee()}.
 * 
 * <p>
 * This implementation addresses specific instances through the re-evaluation of fully addressed (given application id
 * and node id) {@link MulticastRequest} instances.
 * 
 */
public class UnicastProcessor implements ServiceProcessor<UnicastRequest, Object> {

	private static final Logger log = Logger.getLogger(UnicastProcessor.class);
	
	private InstanceId currentInstance;
	
	@Configurable
	public void setCurrentInstance(InstanceId currentInstance) {
		this.currentInstance = currentInstance;
	}

	@Override
	public Object process(ServiceRequestContext context, UnicastRequest request) {

		InstanceId addressee = request.getAddressee();
		
		if (addressee == null) {
			throw new IllegalArgumentException("The addressee is not given in the incoming " + UnicastRequest.T.getTypeSignature());
		}

		if (addressee.getApplicationId() == null) {
			throw new IllegalArgumentException("The addressee is missig the application id in the incoming " + UnicastRequest.T.getTypeSignature());
		}

		if (addressee.getNodeId() == null) {
			throw new IllegalArgumentException("The addressee is missig the node id in the incoming " + UnicastRequest.T.getTypeSignature());
		}
		
		
		if (isCurrentInstance(addressee)) {
			return processLocally(context, request);
		}
		else {
			return processRemotely(context, request);
		}
	}
	
	private Object processRemotely(ServiceRequestContext context, UnicastRequest request) {
		InstanceId addressee = request.getAddressee();
		ServiceRequest payloadRequest = request.getServiceRequest();

		MulticastRequest multicastRequest = MulticastRequest.T.create();
		multicastRequest.setAddressee(addressee);
		multicastRequest.setTimeout(request.getTimeout());
		multicastRequest.setServiceRequest(payloadRequest);
		
		if (request.getAsynchronous()) {
			multicastRequest.eval(context).get(null);
			return null;
		}
		else {
			MulticastResponse multicastResponse = multicastRequest.eval(context).get();
			
			Map<InstanceId, ServiceResult> responses = multicastResponse.getResponses();
			
			if (responses.size() != 1)
				throw new IllegalStateException("Invalid response count from MulticastRequest with fully qualified addressee: " + addressee);
			
			Entry<InstanceId, ServiceResult> entry = responses.entrySet().iterator().next();
			ServiceResult result = entry.getValue();
			InstanceId responseOrigin = entry.getKey();
			
			log.trace(() -> "Processing result from " + responseOrigin + ": " + result);
			
			Object response = ServiceResults.evaluate(result);
			return response;
		}
	}

	private Object processLocally(ServiceRequestContext context, UnicastRequest request) {
		ServiceRequest payloadRequest = request.getServiceRequest();
		if (request.getAsynchronous()) {
			payloadRequest.eval(context).get(new AsyncCallback<Object>() {
				@Override
				public void onFailure(Throwable t) {
					log.error("Error while executing UnicastRequest payload locally and asynchronously: " + payloadRequest, t);
				}
				@Override
				public void onSuccess(Object future) { /* noop */ }
			});
			
			return null;
		}
		else {
			return payloadRequest.eval(context).get();
		}
	}

	private boolean isCurrentInstance(InstanceId instanceId) {
		if (currentInstance == null)
			return false;
		
		return currentInstance.getApplicationId().equals(instanceId.getApplicationId()) && currentInstance.getNodeId().equals(instanceId.getNodeId());
	}

}
