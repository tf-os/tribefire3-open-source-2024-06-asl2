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
package com.braintribe.model.processing.resource.server.provider;

import java.util.function.Supplier;

import com.braintribe.model.processing.resource.server.request.ResourceStreamingRequest;


public class ResourceStreamingAccessIdProvider extends StreamingServletRequestBasedProvider implements Supplier<String> {

	@Override
	public String get() throws RuntimeException {

		ResourceStreamingRequest request = getStreamingServletRequestInfoProvider()
				.get().getResourceStreamingRequest();

		if (request == null)
			return null;
		
		return request.getAccessId();
	}
}
