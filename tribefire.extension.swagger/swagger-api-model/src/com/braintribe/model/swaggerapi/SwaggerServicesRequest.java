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
package com.braintribe.model.swaggerapi;

import com.braintribe.model.generic.annotation.Initializer;
import com.braintribe.model.generic.annotation.meta.Description;
import com.braintribe.model.generic.eval.EvalContext;
import com.braintribe.model.generic.eval.Evaluator;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.service.api.ServiceRequest;
import com.braintribe.model.swagger.v2_0.SwaggerApi;

public interface SwaggerServicesRequest extends SwaggerRequest {

	EntityType<SwaggerServicesRequest> T = EntityTypes.T(SwaggerServicesRequest.class);

	@Description("The id of the service domain the SwaggerApi should be created for.")
	String getServiceDomain();
	void setServiceDomain(String serviceDomain);

	@Description("Display POST requests as multipart requests if not stated otherwise via mapping")
	@Initializer("true")
	boolean getDefaultToMultipart();
	void setDefaultToMultipart(boolean serviceDomain);

	@Override
	EvalContext<? extends SwaggerApi> eval(Evaluator<ServiceRequest> evaluator);

}
