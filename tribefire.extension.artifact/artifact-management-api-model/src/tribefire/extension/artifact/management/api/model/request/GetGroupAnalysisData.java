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

import java.util.List;

import com.braintribe.model.generic.annotation.Initializer;
import com.braintribe.model.generic.annotation.meta.Alias;
import com.braintribe.model.generic.annotation.meta.Description;
import com.braintribe.model.generic.eval.EvalContext;
import com.braintribe.model.generic.eval.Evaluator;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.service.api.ServiceRequest;

import tribefire.extension.artifact.management.api.model.data.ArtifactVersions;

/**
 * request to get the group analysis data
 * @author pit
 *
 */
@Description("Extracts analysis data on the group level for the sources pointed to")
public interface GetGroupAnalysisData extends ArtifactManagementRequest {
	
	EntityType<GetGroupAnalysisData> T = EntityTypes.T(GetGroupAnalysisData.class);
	
	String groupLocation = "groupLocation";
	String includeFilterExpression = "includeFilterExpression";
	String excludeFilterExpression = "excludeFilterExpression";
	String sort = "sort";
	String simplifyOutput = "simplifyOutput";
	String enforceRanges = "enforceRanges";
	String leniency = "leniency";
	String output = "output";
	String display = "display";
	String includeSelfreferences = "includeSelfreferences";
	
	
	@Alias("l")
	@Initializer("'.'")
	@Description("Fully qualified path to the group's source directory")
	String getGroupLocation();
	void setGroupLocation(String value);
	
	@Description("regular expression that groups need to match in order to be processed")
	@Alias("i")
	String getIncludeFilterExpression();
	void setIncludeFilterExpression(String value);
	
	@Description("regular expression that groups need to match in order to be NOT processed")
	@Alias("x")
	String getExcludeFilterExpression();
	void setExcludeFilterExpression(String value);

	@Initializer("true")
	@Description("whether to sort the output")	
	boolean getSort();
	void setSort(boolean value);
	
	@Description("whether to simplify resulting version ranges to their lower bound only")
	@Alias("s")
	boolean getSimplifyOutput();
	void setSimplifyOutput(boolean value);


	@Description("whether to regard the use of a non-ranged version to a processed group as a problem")
	@Alias("f")
	boolean getRequireRanges();
	void setRequireRanges(boolean value);

	@Initializer("false")
	@Description("whether to include references within the group in the output")
	@Alias("r")	
	boolean getIncludeSelfreferences();
	void setIncludeSelfreferences(boolean value);


	
	@Override
	EvalContext<List<ArtifactVersions>> eval(Evaluator<ServiceRequest> evaluator);
}
