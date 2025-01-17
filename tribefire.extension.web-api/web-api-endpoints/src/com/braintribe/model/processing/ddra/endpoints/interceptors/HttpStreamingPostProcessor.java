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

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import com.braintribe.ddra.endpoints.api.DdraEndpointAspect;
import com.braintribe.model.cache.CacheControl;
import com.braintribe.model.ddra.endpoints.api.v1.ApiV1DdraEndpoint;
import com.braintribe.model.processing.service.api.HttpResponseConfigurerAspect;
import com.braintribe.model.processing.service.api.ServicePostProcessor;
import com.braintribe.model.processing.service.api.ServiceRequestContext;
import com.braintribe.model.resourceapi.stream.GetBinaryResponse;
import com.braintribe.model.resourceapi.stream.range.RangeResponse;

public class HttpStreamingPostProcessor implements ServicePostProcessor<GetBinaryResponse> {

	@Override
	public Object process(ServiceRequestContext requestContext, GetBinaryResponse response) {
		ApiV1DdraEndpoint ddraEndpoint = requestContext.findAttribute(DdraEndpointAspect.class) //
				.filter(d -> d instanceof ApiV1DdraEndpoint) //
				.map(d -> (ApiV1DdraEndpoint) d) //
				.orElse(null);

		// TODO: There is no way to disable response modification in non-ddra usecases. Find a more generic solution
		if (ddraEndpoint != null && !ddraEndpoint.getDownloadResource())
			return response;

		requestContext.findAttribute(HttpResponseConfigurerAspect.class) //
				.ifPresent(c -> {
					c.applyFor(response, httpResponse -> {
						if (response.getResource() == null) {
							httpResponse.setStatus(304);
						} else {
							setContentRangeHeaders(httpResponse, response);
							setCacheControlHeaders(httpResponse, response.getCacheControl());
						}
					});
				});

		return response;
	}

	private static void setContentRangeHeaders(HttpServletResponse response, RangeResponse streamBinaryResponse) {
		if (streamBinaryResponse.getRanged()) {
			Long size = streamBinaryResponse.getSize();
			String sizeString = (size != null) ? String.valueOf(size) : "*";
			String rangeStart = String.valueOf(streamBinaryResponse.getRangeStart());
			String rangeEnd = String.valueOf(streamBinaryResponse.getRangeEnd());
			String contentRange = "bytes ".concat(rangeStart).concat("-").concat(rangeEnd).concat("/").concat(sizeString);
			response.setHeader("Content-Range", contentRange);
			response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);

			if (size != null && size <= Integer.MAX_VALUE)
				response.setContentLength(size.intValue());
		}

		response.setHeader("Accept-Ranges", "bytes");
	}

	private static void setDefaultCacheControlHeaders(HttpServletResponse response) {
		response.setHeader("Cache-Control", "no-store, no-cache");
		response.setHeader("Pragma", "no-cache");
		response.setDateHeader("Expires", 0);
	}

	private static void setCacheControlHeaders(HttpServletResponse response, CacheControl cacheControl) {
		if (cacheControl == null) {
			setDefaultCacheControlHeaders(response);
			return;
		}

		List<String> ccParts = new ArrayList<>();

		if (cacheControl.getType() != null) {
			switch (cacheControl.getType()) {
				case noCache:
					ccParts.add("no-cache");
					response.setHeader("Pragma", "no-cache");
					break;
				case noStore:
					ccParts.add("no-store");
					break;
				case privateCache:
					ccParts.add("private");
					break;
				case publicCache:
					ccParts.add("public");
					break;
			}
		}

		if (cacheControl.getMaxAge() != null) {
			ccParts.add("max-age=" + cacheControl.getMaxAge());
		}

		if (cacheControl.getMustRevalidate()) {
			ccParts.add("must-revalidate");
		}

		if (ccParts.isEmpty()) {
			setDefaultCacheControlHeaders(response);
		} else {
			response.setHeader("Cache-Control", String.join(", ", ccParts));
		}

		if (cacheControl.getLastModified() != null) {
			response.setDateHeader("Last-Modified", cacheControl.getLastModified().getTime());
		}

		if (cacheControl.getFingerprint() != null) {
			response.setHeader("ETag", cacheControl.getFingerprint());
		}
	}

}
