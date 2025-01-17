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

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import com.braintribe.model.processing.service.api.HttpRequestSupplierAspect;
import com.braintribe.model.processing.service.api.ServicePreProcessor;
import com.braintribe.model.processing.service.api.ServiceRequestContext;
import com.braintribe.model.resourceapi.stream.HasStreamCondition;
import com.braintribe.model.resourceapi.stream.HasStreamRange;
import com.braintribe.model.resourceapi.stream.condition.FingerprintMismatch;
import com.braintribe.model.resourceapi.stream.condition.ModifiedSince;
import com.braintribe.model.resourceapi.stream.range.StreamRange;
import com.braintribe.model.service.api.ServiceRequest;
import com.braintribe.utils.lcd.StringTools;

public class HttpStreamingPreProcessor implements ServicePreProcessor<ServiceRequest>{

	@Override
	public ServiceRequest process(ServiceRequestContext requestContext, ServiceRequest serviceRequest) {
		requestContext.findAttribute(HttpRequestSupplierAspect.class) //
			.flatMap(s -> s.getFor(serviceRequest))
			.ifPresent(httpRequest -> processStreamHeaders(httpRequest, serviceRequest));
		
		return serviceRequest;
	}

	private void processStreamHeaders(HttpServletRequest httpRequest, ServiceRequest serviceRequest) {
		if (serviceRequest instanceof HasStreamCondition) {
			HasStreamCondition hasStreamCondition = (HasStreamCondition) serviceRequest;
			
			String ifNonMatchHeader = httpRequest.getHeader("If-None-Match");
			long ifModifiedSinceHeader = httpRequest.getDateHeader("If-Modified-Since");
			
			if (ifNonMatchHeader != null) {
				FingerprintMismatch streamCondition = FingerprintMismatch.T.create();
				streamCondition.setFingerprint(ifNonMatchHeader);
				hasStreamCondition.setCondition(streamCondition);
			}
			else if (ifModifiedSinceHeader != -1) {
				ModifiedSince streamCondition = ModifiedSince.T.create();
				streamCondition.setDate(new Date(ifModifiedSinceHeader));
				hasStreamCondition.setCondition(streamCondition);
			}
			
		}
		
		if (serviceRequest instanceof HasStreamRange) {
			HasStreamRange hasStreamRange = (HasStreamRange) serviceRequest;
			
			parseRangeHeader(httpRequest, hasStreamRange);
		}
	}
	
	private void parseRangeHeader(HttpServletRequest httpRequest, HasStreamRange hasStreamRange) {
		
		String rangeHeader = httpRequest.getHeader("Range");
		if (StringTools.isBlank(rangeHeader)) {
			return;
		}
		
		try {
			
			int index = rangeHeader.indexOf('=');
			if (index == -1) {
				throw new IllegalStateException("There is no '=' sign.");
			}
			String unit = rangeHeader.substring(0, index).trim();
			if (StringTools.isBlank(unit) || !unit.equalsIgnoreCase("bytes")) {
				throw new IllegalStateException("Only unit 'bytes' is supported.");
			}
			String rangeSpec = rangeHeader.substring(index+1).trim();
			index = rangeSpec.indexOf('-');
			if (index == -1) {
				throw new IllegalStateException("The range value "+rangeSpec+" does not contain '-'");
			}
			String start = rangeSpec.substring(0, index).trim();
			String end = null;
			if (index < rangeSpec.length()-1) {
				end = rangeSpec.substring(index+1).trim();
			}
			long startLong = Long.parseLong(start);
			long endLong = -1;
			if (!StringTools.isBlank(end)) {
				endLong = Long.parseLong(end);
			}
			
			StreamRange streamRange = StreamRange.T.create();
			
			streamRange.setStart(startLong);
			streamRange.setEnd(endLong);
			
			hasStreamRange.setRange(streamRange);
		} catch (Exception e) {
			throw new IllegalStateException("Unable to parse Range header \""+rangeHeader+"\".", e);
		}
		
	}
}
