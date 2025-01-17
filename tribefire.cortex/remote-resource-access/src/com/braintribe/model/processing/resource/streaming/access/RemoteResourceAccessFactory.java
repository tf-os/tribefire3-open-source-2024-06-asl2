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
package com.braintribe.model.processing.resource.streaming.access;

import java.util.function.Consumer;

import com.braintribe.cfg.Configurable;
import com.braintribe.cfg.Required;
import com.braintribe.codec.marshaller.api.MarshallerRegistry;
import com.braintribe.logging.Logger;
import com.braintribe.model.processing.session.api.persistence.PersistenceGmSession;
import com.braintribe.model.processing.session.api.resource.ResourceAccess;
import com.braintribe.model.resource.Resource;
import com.braintribe.transport.http.HttpClientProvider;
import com.braintribe.utils.stream.api.StreamPipeFactory;

public class RemoteResourceAccessFactory extends AbstractResourceAccessFactory {

	private static Logger logger = Logger.getLogger(RemoteResourceAccessFactory.class);
	
	private MarshallerRegistry marshallerRegistry;
	protected HttpClientProvider httpClientProvider = null;
	private int authorizationMaxRetries = 2;
	private Consumer<Throwable> authorizationFailureListener;
	private StreamPipeFactory streamPipeFactory;
	
	@Configurable
	@Required
	public void setStreamPipeFactory(StreamPipeFactory streamPipeFactory) {
		this.streamPipeFactory = streamPipeFactory;
	}
	
	@Configurable
	public void setHttpClientProvider(HttpClientProvider httpClientProvider) {
		this.httpClientProvider = httpClientProvider;
	}

	/**
	 * Sets the {@link MarshallerRegistry} from which {@link com.braintribe.codec.marshaller.api.Marshaller Marshaller}(s) will be retrieved for unmarshalling the uploaded {@link Resource}(s) 
	 * @param marshallerRegistry The marshaller registry
	 */
	@Configurable @Required
	public void setMarshallerRegistry(MarshallerRegistry marshallerRegistry) {
		this.marshallerRegistry = marshallerRegistry;
	}
	
	@Configurable
	public void setAuthorizationMaxRetries(int authorizationMaxRetries) {
		this.authorizationMaxRetries = authorizationMaxRetries;
	}
	
	@Configurable
	public void setAuthorizationFailureListener(Consumer<Throwable> authorizationFailureListener) {
		this.authorizationFailureListener = authorizationFailureListener;
	}

	@Override
	public ResourceAccess newInstance(PersistenceGmSession session) {
		RemoteResourceAccess resourceAccess = new RemoteResourceAccess(session);
		
		provideBaseStreamingUrl(resourceAccess);
		provideUploadResponseMimeType(resourceAccess);
		
		resourceAccess.setSessionIdProvider(sessionIdProvider);
		resourceAccess.setMarshallerRegistry(marshallerRegistry);
		resourceAccess.setStreamPipeFactory(streamPipeFactory);
		
		if (this.httpClientProvider != null) {
			resourceAccess.setHttpClientProvider(this.httpClientProvider);
		}

		resourceAccess.setAuthorizationMaxRetries(authorizationMaxRetries);
		
		if (authorizationFailureListener != null) {
			logger.debug(() -> "Forwarding authorization failure listener: "+authorizationFailureListener+" to the RemoteResourceAccess");
			resourceAccess.setAuthorizationFailureListener(authorizationFailureListener);
		} else {
			logger.debug(() -> "No authorization failure listener available for the RemoteResourceAccess");
		}

		return resourceAccess;
	}

}
