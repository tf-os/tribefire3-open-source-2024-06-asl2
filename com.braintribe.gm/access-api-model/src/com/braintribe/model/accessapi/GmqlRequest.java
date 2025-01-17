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
package com.braintribe.model.accessapi;

import com.braintribe.model.generic.annotation.meta.Description;
import com.braintribe.model.generic.annotation.meta.Mandatory;
import com.braintribe.model.generic.eval.EvalContext;
import com.braintribe.model.generic.eval.Evaluator;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.query.QueryResult;
import com.braintribe.model.service.api.AuthorizedRequest;
import com.braintribe.model.service.api.ServiceRequest;

@Description("Executes the passed GMQL statement on the access identified by domainId.")
public interface GmqlRequest extends ServiceRequest, AuthorizedRequest {

	final EntityType<GmqlRequest> T = EntityTypes.T(GmqlRequest.class);

	@Mandatory
	@Description("The id of the access which should run the GMQL statement.")
	String getAccessId();
	void setAccessId(String accessId);

	@Mandatory
	@Description("The GMQL statement to run against the access.")
	String getStatement();

	void setStatement(String statement);

	@Deprecated
	@Description("The GmqlRequest was mistakenly deriving from DomainRequest which introduced this property. "
			+ "Now this is changed and the accessId property should be used instead. "
			+ "If no accessId is given we currently still support domainId as an alternative to ensure backward compatibility.")
	String getDomainId();
	@Deprecated
	void setDomainId(String domainId);

	@Override
	EvalContext<? extends QueryResult> eval(Evaluator<ServiceRequest> evaluator);

}
