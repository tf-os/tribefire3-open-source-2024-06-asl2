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
package tribefire.extension.artifact.management.api.model.request;

import java.util.Map;

import com.braintribe.model.generic.annotation.Initializer;
import com.braintribe.model.generic.annotation.meta.Alias;
import com.braintribe.model.generic.annotation.meta.Description;
import com.braintribe.model.generic.annotation.meta.Mandatory;
import com.braintribe.model.generic.eval.EvalContext;
import com.braintribe.model.generic.eval.Evaluator;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.service.api.ServiceRequest;
import com.braintribe.model.service.api.result.Neutral;

@Description("Updates dependencies for the given group. The service is expecting a normalized group structure, "
		+ "meaning that a parent artifact is given. The process checks the properties section in the parent pom. "
		+ "For each specified group dependency we search the respective property defined and replace its version as given by argument "
		+ "groupDependencies.")
public interface UpdateGroupDependencies extends ArtifactManagementRequest { 

	EntityType<UpdateGroupDependencies> T = EntityTypes.T(UpdateGroupDependencies.class);
	
	String groupFolder = "groupFolder";
	String groupDependencies = "groupDependencies";
	String failOnMissingGroupsInParent = "failOnMissingGroupsInParent";

	@Mandatory
	@Initializer("'.'")
	@Alias("g")
	@Description("The root folder of the artifact group.")
	String getGroupFolder();
	void setGroupFolder(String groupFolder);
	
	@Mandatory
	@Alias("d")
	@Description("The map of groups and the respective new versions. In order to auto-convert versions to ranges add '~' at the end "
			+ "of the version, e.g. 3.0~")
	Map<String,String> getGroupDependencies();
	void setGroupDependencies(Map<String,String> groupDependencies);

	@Initializer("true")
	@Alias("f")
	@Description("If true, the process will fail if a group specified in groupDependencies is not found.")
	boolean getFailOnMissingGroupsInParent();
	void setFailOnMissingGroupsInParent(boolean failOnMissingGroupsInParent);

	@Override
	EvalContext<Neutral> eval(Evaluator<ServiceRequest> evaluator);
			
}
