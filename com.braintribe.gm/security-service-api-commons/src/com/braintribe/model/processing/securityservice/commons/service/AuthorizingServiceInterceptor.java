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
package com.braintribe.model.processing.securityservice.commons.service;

import com.braintribe.gm.model.reason.Maybe;
import com.braintribe.gm.model.security.reason.AuthenticationFailure;
import com.braintribe.model.processing.service.api.ProceedContext;
import com.braintribe.model.processing.service.api.ReasonedServiceAroundProcessor;
import com.braintribe.model.processing.service.api.ServiceRequestContext;
import com.braintribe.model.service.api.AuthorizableRequest;

public class AuthorizingServiceInterceptor implements ReasonedServiceAroundProcessor<AuthorizableRequest, Object> {
	
	private static String getSessionId(AuthorizableRequest authorizedRequest) {
		String sessionId = authorizedRequest.getSessionId();
		
		if (sessionId == null) {
			// TODO: find out if this is still used some where
			sessionId = (String)authorizedRequest.getMetaData().get(AuthorizableRequest.sessionId);
			
			if ("".equals(sessionId))
				sessionId = null;
		}

		return sessionId;
	}

	
	@Override
	public Maybe<Object> processReasoned(ServiceRequestContext requestContext, AuthorizableRequest request, ProceedContext proceedContext) {
		Maybe<ServiceRequestContext> contextMaybe = new ContextualizedAuthorization<>(requestContext, requestContext, getSessionId(request), requestContext.summaryLogger())
				.withRequest(request)
				.authorizeReasoned(request.requiresAuthentication());
		
		if (contextMaybe.isUnsatisfiedBy(AuthenticationFailure.T))
			return contextMaybe.emptyCast();
		
		return proceedContext.proceedReasoned(contextMaybe.get(), request);
	}
}
