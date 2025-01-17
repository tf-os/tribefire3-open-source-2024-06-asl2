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
package com.braintribe.model.jinni.api;

import com.braintribe.model.generic.annotation.meta.Alias;
import com.braintribe.model.generic.annotation.meta.Description;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.service.api.PlatformRequest;

@Description("Updates the bt jars that extend Ant. Kind of. "
		+ "Downloads the \"latest\" (see 'version') devrock-ant-tasks zip, moves the old bt jars to a sub-folder (for possible manual recovery), and extracts new jars in place.")
public interface UpdateDevrockAntTasks extends PlatformRequest {

	EntityType<UpdateDevrockAntTasks> T = EntityTypes.T(UpdateDevrockAntTasks.class);

	@Description("The desired target devrok-ant-task version. "
			+ "As of right now, only the major.minor part is considered, and the highest version for given major.minor will be used for update."
			+ "\n\n" + "If no value is given here, current Jinni version will be used instead. "
			+ "This means if there is a newer devrock-ant-tasks version with a higher major.minor, you have to specify those explicitly.")
	@Alias("v")
	String getVersion();
	void setVersion(String version);

	@Description("If true, the update is executed even if the latest found version is not newer than current version.")
	@Alias("f")
	boolean getForce();
	void setForce(boolean force);

}
