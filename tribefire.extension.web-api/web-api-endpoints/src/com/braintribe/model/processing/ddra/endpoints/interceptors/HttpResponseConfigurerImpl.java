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
package com.braintribe.model.processing.ddra.endpoints.interceptors;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import javax.servlet.http.HttpServletResponse;

import com.braintribe.model.processing.service.api.HttpResponseConfigurer;

public class HttpResponseConfigurerImpl implements HttpResponseConfigurer {
	Map<Object, Consumer<HttpServletResponse>> registry = new HashMap<>();

	public HttpResponseConfigurerImpl() {

	}

	@Override
	public void applyFor(Object response, Consumer<HttpServletResponse> consumer) {
		registry.put(response, consumer);
	}
	
	public void consume(Object serviceResponse, HttpServletResponse htttpResponse) {
		registry.getOrDefault(serviceResponse, r -> {/* noop */}).accept(htttpResponse);
	}
}
