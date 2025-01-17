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
package com.braintribe.model.jinni.api.template;

import java.util.Set;

import com.braintribe.model.generic.annotation.meta.Description;
import com.braintribe.model.generic.annotation.meta.Mandatory;
import com.braintribe.model.generic.annotation.meta.PositionalArguments;
import com.braintribe.model.generic.eval.EvalContext;
import com.braintribe.model.generic.eval.Evaluator;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.service.api.ServiceRequest;
import com.braintribe.model.service.api.result.Neutral;

/**
 * @author peter.gazdik
 */
@Description("Generates an extension consisting primarily of a module, possibly also with an initializer, access/api models, processing, etc.\n\n"
		+ "Use --parts and --samples to control what is generated. "
		+ "To generate everything available call something like:\n\n    create-extension xyz --samples all")
@PositionalArguments({ "name" })
public interface CreateExtension extends CreateArtifactsRequest {

	EntityType<CreateExtension> T = EntityTypes.T(CreateExtension.class);

	@Description("The base name for the artifacts that will be created. Example:\n"
			+ "For '--name my-project', artifacts like 'my-project-module', 'my-project-deployment-model' and others are created.")
	@Mandatory
	String getName();
	void setName(String name);

	@Description("Specifies what artifacts should be generated (models, initializer, processing, etc.). "
			+ "If no parts are specified, all parts are generated (equivalent to '--parts all'), which is the most common case. "
			+ "When specifying parts selectively, not some parts imply other parts are generated, e.g. processingTest -> processing.")
	Set<ExtensionPart> getParts();
	void setParts(Set<ExtensionPart> parts);

	@Description("Specifies what kinds of sample code should be generated on top of the basic artifacts structure. "
			+ "The sample code involves deployable types, data and/or api types, expert implementation with tests, initialization. "
			+ "Hence specifying at least one sampe implies generating almost all parts.")
	Set<ExtensionSample> getSamples();
	void setSamples(Set<ExtensionSample> samples);

	@Description("Specifies whether to overwrite existing artifacts (if relevant). "
			+ "If false, this task fails with an error, though it might create some artifacts first (which haven't existed yet) before the error happens. "
			+ "This value is simply passed to the partial create tasks (e.g. 'create-module').")
	boolean getOverwrite();
	void setOverwrite(boolean overwrite);

	@Override
	EvalContext<? extends Neutral> eval(Evaluator<ServiceRequest> evaluator);

}
