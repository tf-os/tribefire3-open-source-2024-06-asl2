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
package com.braintribe.model.processing.ddra.endpoints.api.v1.handlers;

import com.braintribe.ddra.endpoints.api.api.v1.ApiV1EndpointContext;
import com.braintribe.logging.Logger;
import com.braintribe.model.processing.session.api.managed.ModelAccessoryFactory;
import com.braintribe.model.processing.session.api.persistence.PersistenceGmSession;
import com.braintribe.model.processing.session.api.persistence.PersistenceGmSessionFactory;
import com.braintribe.model.resource.Resource;
import com.braintribe.model.resourceapi.request.ResourceDeleteRequest;
import com.braintribe.model.service.api.ServiceRequest;

public class ResourceDeleteHandler extends ResourceHandler {

	private static final Logger log = Logger.getLogger(ResourceDeleteHandler.class);

	public static boolean handleRequest(ApiV1EndpointContext context, ServiceRequest service, PersistenceGmSessionFactory gmSession,
			ModelAccessoryFactory modelAccessoryFactory) {
		if (!ResourceDeleteRequest.T.isInstance(service))
			return false;

		return new ResourceDeleteHandler(context, (ResourceDeleteRequest) service, gmSession, modelAccessoryFactory).handle();
	}

	private final ResourceDeleteRequest resourceDeleteRequest;

	public ResourceDeleteHandler(ApiV1EndpointContext context, ResourceDeleteRequest resourceDeleteRequest, PersistenceGmSessionFactory gmSession,
			ModelAccessoryFactory modelAccessoryFactory) {
		this.resourceDeleteRequest = resourceDeleteRequest;
		if (this.resourceDeleteRequest.getAccessId() == null)
			this.resourceDeleteRequest.setAccessId(context.getServiceDomain());
		this.sessionFactory = gmSession;
		this.modelAccessoryFactory = modelAccessoryFactory;
	}

	private boolean handle() {
		log.info("Handling resources remove for " + resourceDeleteRequest.getResourceId());
		PersistenceGmSession gmSession = openSession(resourceDeleteRequest.getAccessId());
		Resource resource = retrieveResource(gmSession, resourceDeleteRequest.getResourceId(), resourceDeleteRequest.getAccessId());

		// @formatter:off
		gmSession.resources()
				.delete(resource)
				.delete();
		// @formatter:on

		return true;
	}

}
