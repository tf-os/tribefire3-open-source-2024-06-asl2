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
package com.braintribe.model.platform.setup.api;

import java.util.List;

import com.braintribe.model.generic.annotation.Initializer;
import com.braintribe.model.generic.annotation.meta.Description;
import com.braintribe.model.generic.eval.EvalContext;
import com.braintribe.model.generic.eval.Evaluator;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.service.api.ServiceRequest;

/**
 * Increments the revisions of (specified) artifacts in an artifact group. For further information see type and properties descriptions.
 * 
 * @author michael.lafite
 */
@Description("Increments the revisions of (specified) artifacts in an artifact group, e.g. from 123-pc to 124-pc.")
public interface IncrementRevisions extends SetupRequest {
	EntityType<IncrementRevisions> T = EntityTypes.T(IncrementRevisions.class);

	@Override
	EvalContext<List<String>> eval(Evaluator<ServiceRequest> evaluator);

	@Description("The root folder of the artifact group to process. By default, the revision update will be performed in the current working directory.")
	@Initializer("'.'")
	String getGroupFolder();
	void setGroupFolder(String groupFolder);

	@Description("The delta to add to the current revision.")
	@Initializer("1")
	int getDelta();
	void setDelta(int delta);

	@Description("An include regex which can be used to select only specific artifacts. By default, all artifacts will be included.")
	@Initializer("'.+'")
	String getIncludeRegex();
	void setIncludeRegex(String includeRegex);

	@Description("An exclude regex which can be used to filter out certain artifacts. By default, no artifacts will be filtered out.")
	@Initializer("''")
	String getExcludeRegex();
	void setExcludeRegex(String excludeRegex);
}
