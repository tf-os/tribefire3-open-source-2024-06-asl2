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
package tribefire.extension.demo.model.deployment;

import com.braintribe.model.accessdeployment.IncrementalAccess;

import com.braintribe.model.generic.annotation.Initializer;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

/**
 * The denotation type for the actual DemoAccess implementation which can be
 * configured and deployed in the ControlCenter. The properties configured here
 * will be transfered to the DemoAccess implementation during deployment.
 */
public interface DemoAccess extends IncrementalAccess {
	
	EntityType<DemoAccess> T = EntityTypes.T(DemoAccess.class);

	/**
	 * If set false the DemoAccess will start with an empty population.
	 */
	@Initializer("true")
	boolean getInitDefaultPopulation();
	void setInitDefaultPopulation(boolean initDefaultPopulation);

}