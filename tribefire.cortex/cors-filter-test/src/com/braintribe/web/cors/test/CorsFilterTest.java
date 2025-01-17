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
package com.braintribe.web.cors.test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Assert;
import org.junit.Test;

import com.braintribe.web.cors.CorsFilter;
import com.braintribe.web.cors.handler.CorsHandler;
import com.braintribe.web.cors.handler.CorsRequestType;

/**
 * <p>
 * Tests covering {@link com.braintribe.web.cors.CorsFilter} public method:
 * 
 * <ul>
 * <li>{@link com.braintribe.web.cors.CorsFilter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)}
 * </ul>
 * 
 */
public class CorsFilterTest extends CorsTest {

	// =====================//
	// == ACTUAL REQUESTS ==//
	// =====================//

	@Test
	public void testSuccessfulCrossOriginRequestWithPermissiveConfiguration() throws Exception {

		HttpServletRequest request = createHttpServletRequest("POST", "http", "server-host", "http://any-origin");
		HttpServletResponse response = createHttpServletResponse();

		CorsHandler corsHandler = createBasicCorsHandler(permissiveConfiguration);
		CorsFilter corsFilter = createCorsFilter(corsHandler);

		corsFilter.doFilter(request, response, createFilterChain());

		assertCorsResponseHeaders(CorsRequestType.actual, permissiveConfiguration, request, response);

	}

	@Test
	public void testSuccessfulCrossOriginRequestWithStrictConfiguration() throws Exception {

		HttpServletRequest request = createHttpServletRequest("POST", "http", "server-host", "http://valid-origin-1");
		HttpServletResponse response = createHttpServletResponse();

		CorsHandler corsHandler = createBasicCorsHandler(strictConfiguration);
		CorsFilter corsFilter = createCorsFilter(corsHandler);

		corsFilter.doFilter(request, response, createFilterChain());

		assertCorsResponseHeaders(CorsRequestType.actual, strictConfiguration, request, response);

	}

	@Test
	public void testSuccessfulCrossOriginRequestWithStrictConfigurationAndNonStandardPort() throws Exception {

		HttpServletRequest request = createHttpServletRequest("POST", "http", "server-host", "http://valid-origin-1:8080");
		HttpServletResponse response = createHttpServletResponse();

		CorsHandler corsHandler = createBasicCorsHandler(strictConfiguration);
		CorsFilter corsFilter = createCorsFilter(corsHandler);

		corsFilter.doFilter(request, response, createFilterChain());

		assertCorsResponseHeaders(CorsRequestType.actual, strictConfiguration, request, response);

	}

	@Test
	public void testCrossOriginRequestFromUnallowedOrigin() throws Exception {

		HttpServletRequest request = createHttpServletRequest("POST", "http", "server-host", "http://any-origin");
		HttpServletResponse response = createHttpServletResponse();

		CorsHandler corsHandler = createBasicCorsHandler(strictConfiguration);
		CorsFilter corsFilter = createCorsFilter(corsHandler);

		corsFilter.doFilter(request, response, createFilterChain());

		Assert.assertEquals("Unexpected response status: "+response.getStatus(), 403, response.getStatus());
		
	}

	@Test
	public void testCrossOriginRequestFromUnallowedOriginPort() throws Exception {

		HttpServletRequest request = createHttpServletRequest("POST", "http", "server-host", "http://valid-origin-1:7777");
		HttpServletResponse response = createHttpServletResponse();

		CorsHandler corsHandler = createBasicCorsHandler(strictConfiguration);
		CorsFilter corsFilter = createCorsFilter(corsHandler);

		corsFilter.doFilter(request, response, createFilterChain());

		Assert.assertEquals("Unexpected response status: "+response.getStatus(), 403, response.getStatus());
		
	}

	// =========================//
	// == PRE FLIGHT REQUESTS ==//
	// =========================//

	@Test
	public void testCrossOriginRequestFromUnallowedMethod() throws Exception {

		HttpServletRequest request = createHttpServletRequest("DELETE", "http", "server-host", "http://valid-origin-1");
		HttpServletResponse response = createHttpServletResponse();

		CorsHandler corsHandler = createBasicCorsHandler(strictConfiguration);
		CorsFilter corsFilter = createCorsFilter(corsHandler);

		corsFilter.doFilter(request, response, createFilterChain());

		Assert.assertEquals("Unexpected response status: "+response.getStatus(), 405, response.getStatus());
		
	}

	@Test
	public void testSuccessfulPreflightRequestWithPermissiveConfiguration() throws Exception {

		HttpServletRequest request = createHttpServletRequestForPreflight("POST", "http", "server-host", "http://any-origin");
		HttpServletResponse response = createHttpServletResponse();

		CorsHandler corsHandler = createBasicCorsHandler(permissiveConfiguration);
		CorsFilter corsFilter = createCorsFilter(corsHandler);

		corsFilter.doFilter(request, response, createFilterChain());

		assertCorsResponseHeaders(CorsRequestType.preflight, permissiveConfiguration, request, response);

	}

	@Test
	public void testSuccessfulPreflightRequestWithStrictConfiguration() throws Exception {

		HttpServletRequest request = createHttpServletRequestForPreflight("POST", "http", "server-host", "http://valid-origin-1");
		HttpServletResponse response = createHttpServletResponse();

		CorsHandler corsHandler = createBasicCorsHandler(strictConfiguration);
		CorsFilter corsFilter = createCorsFilter(corsHandler);

		corsFilter.doFilter(request, response, createFilterChain());
		
		assertCorsResponseHeaders(CorsRequestType.preflight, strictConfiguration, request, response);

	}

	@Test
	public void testPreflightRequestFromUnallowedOrigin() throws Exception {

		HttpServletRequest request = createHttpServletRequestForPreflight("POST", "http", "server-host", "http://any-origin");
		HttpServletResponse response = createHttpServletResponse();

		CorsHandler corsHandler = createBasicCorsHandler(strictConfiguration);
		CorsFilter corsFilter = createCorsFilter(corsHandler);

		corsFilter.doFilter(request, response, createFilterChain());
		
		Assert.assertEquals("Unexpected response status: "+response.getStatus(), 403, response.getStatus());
		
	}

}
