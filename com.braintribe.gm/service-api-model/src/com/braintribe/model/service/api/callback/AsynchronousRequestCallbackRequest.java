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
package com.braintribe.model.service.api.callback;

import com.braintribe.model.generic.annotation.Abstract;
import com.braintribe.model.generic.annotation.meta.Description;
import com.braintribe.model.generic.annotation.meta.Name;
import com.braintribe.model.generic.eval.EvalContext;
import com.braintribe.model.generic.eval.Evaluator;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.service.api.DispatchableRequest;
import com.braintribe.model.service.api.DomainRequest;
import com.braintribe.model.service.api.ServiceRequest;

@Abstract
public interface AsynchronousRequestCallbackRequest extends DispatchableRequest, DomainRequest {

	final EntityType<AsynchronousRequestCallbackRequest> T = EntityTypes.T(AsynchronousRequestCallbackRequest.class);
	
	String customData = "customData";
	String correlationId = "correlationId";

	void setCustomData(String customData);
	@Name("Custom Data")
	@Description("Any kind of data that has been provided when the asynchronous request was created.")
	String getCustomData();
	
	void setCorrelationId(String correlationId);
	@Name("Correlation Id")
	@Description("A unique Id identifying the asynchronous request.")
	String getCorrelationId();
	
	@Override
	EvalContext<Boolean> eval(Evaluator<ServiceRequest> evaluator);

}
