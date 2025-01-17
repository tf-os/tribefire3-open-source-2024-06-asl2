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
package com.braintribe.model.processing.webrpc.client;

import com.braintribe.cfg.Required;
import com.braintribe.gm.model.reason.Maybe;
import com.braintribe.model.deployment.remote.RemoteDomainIdMapping;
import com.braintribe.model.processing.meta.cmd.builders.ModelMdResolver;
import com.braintribe.model.processing.service.api.ProceedContext;
import com.braintribe.model.processing.service.api.ServiceRequestContext;
import com.braintribe.model.processing.session.api.managed.ModelAccessoryFactory;
import com.braintribe.model.securityservice.OpenUserSession;
import com.braintribe.model.securityservice.OpenUserSessionResponse;
import com.braintribe.model.service.api.DomainRequest;
import com.braintribe.model.service.api.ExecuteInDomain;
import com.braintribe.model.service.api.ServiceRequest;

/**
 * Implementation for the denotation type {@link com.braintribe.model.deployment.remote.RemotifyingInterceptor}.
 * 
 * @author peter.gazdik
 */
public class RemotifyingInterceptor extends RemoteAuthentifyingInterceptor {

	private ModelAccessoryFactory modelAccessoryFactory;

	@Required
	public void setModelAccessoryFactory(ModelAccessoryFactory modelAccessoryFactory) {
		this.modelAccessoryFactory = modelAccessoryFactory;
	}

	@Override
	public Maybe<? extends Object> processReasoned(ServiceRequestContext context, ServiceRequest request, ProceedContext proceedContext) {
		if (!needsSpecialHandling(request))
			return proceedContext.proceed(context, request);
		else
			return new RemotificationInterception(context, request, proceedContext).proceedReasoned();
	}

	@Override
	protected boolean needsSpecialHandling(ServiceRequest request) {
		return super.needsSpecialHandling(request) || request instanceof DomainRequest;
	}

	// #################################################################
	// ## . . . . . . . . . Actual Remotification . . . . . . . . . . ##
	// #################################################################

	private class RemotificationInterception extends RemoteAuthenticationInterception {

		public RemotificationInterception(ServiceRequestContext requestContext, ServiceRequest request, ProceedContext proceedContext) {
			super(requestContext, request, proceedContext);
		}

		@Override
		public Maybe<Object> proceedReasoned() {
			ensureCorrectRemoteDomainId();

			return super.proceedReasoned();
		}

		//
		// Remote Domain Id
		//

		private void ensureCorrectRemoteDomainId() {
			if (remoteRequest instanceof DomainRequest && requestContext.getDomainId() != null)
				replaceRemoteDomainId((DomainRequest) remoteRequest);
		}

		private void replaceRemoteDomainId(DomainRequest request) {
			request.setDomainId(resolveRemoteDomainId(request));
		}

		private String resolveRemoteDomainId(DomainRequest request) {
			RemoteDomainIdMapping rsm = mdResolver().entityType(request.entityType()).meta(RemoteDomainIdMapping.T).exclusive();

			if (rsm == null)
				throwCannotMapDomain(request, "no RemoteServerMapping meta-data is configured.");

			String remoteDomainId = rsm.getDomainId();
			if (remoteDomainId == null)
				throwCannotMapDomain(request, "the configured RemoteServerMapping meta-data has no domainId. MetaData: " + rsm);

			return remoteDomainId;
		}

		private ModelMdResolver mdResolver() {
			String domainId = requestContext.getDomainId();

			return modelAccessoryFactory.getForServiceDomain(domainId).getMetaData();
		}

		private void throwCannotMapDomain(DomainRequest request, String reason) {
			throw new IllegalStateException("Cannot resolve remote domainId for request " + request + " of type "
					+ request.entityType().getTypeSignature() + " in domain '" + request.getDomainId() + "'" + " on a remote server as " + reason);
		}

		//
		// evalOpenUserSession
		//

		@Override
		protected OpenUserSessionResponse evalOpenUserSession(OpenUserSession request) {
			ServiceRequest sr = executeInLocalDomainIfNeeded(request);

			return (OpenUserSessionResponse) requestContext.eval(sr).get();
		}

		/**
		 * Why {@link ExecuteInDomain}?
		 * 
		 * We are running in Server A and want to delegate the original request (in {@link #proceed()}) to Server B.
		 * First we need to authenticate on B, i.e. we want to eval {@link OpenUserSession} there. However, evaluating
		 * it directly on our requestContext would mean authenticating on our current server (A). Thus we wrap the
		 * request in {@link ExecuteInDomain} and use our current domain, which we know is backed by a service processor
		 * that sends the request to B.
		 */
		private ServiceRequest executeInLocalDomainIfNeeded(OpenUserSession request) {
			String domainId = requestContext.getDomainId();
			if (domainId == null)
				// probably unreachable?
				return request;

			ExecuteInDomain eid = ExecuteInDomain.T.create();
			eid.setDomainId(domainId);
			eid.setServiceRequest(request);

			return eid;
		}

	}

}
