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
package tribefire.extension.opentracing.wire.space;

import com.braintribe.model.processing.deployment.api.ExpertContext;
import com.braintribe.wire.api.annotation.Import;
import com.braintribe.wire.api.annotation.Managed;
import com.braintribe.wire.api.space.WireSpace;

import tribefire.extension.opentracing.service.OpentracingAspect;
import tribefire.extension.opentracing.service.OpentracingServiceProcessor;
import tribefire.module.wire.contract.TribefireWebPlatformContract;
import tribefire.module.wire.contract.WebPlatformResourcesContract;

/**
 *
 */
@Managed
public class DeployablesSpace implements WireSpace {

	@Import
	private TribefireWebPlatformContract tfPlatform;

	@Import
	private WebPlatformResourcesContract resources;

	@Managed
	public OpentracingServiceProcessor opentracingServiceProcessor(
			ExpertContext<tribefire.extension.opentracing.model.deployment.service.OpentracingServiceProcessor> context) {

		tribefire.extension.opentracing.model.deployment.service.OpentracingServiceProcessor deployable = context.getDeployable();

		OpentracingServiceProcessor bean = new OpentracingServiceProcessor();
		bean.setDeployable(deployable);

		return bean;
	}

	@Managed
	public OpentracingAspect opentracingAspect(ExpertContext<tribefire.extension.opentracing.model.deployment.service.OpentracingAspect> context) {

		tribefire.extension.opentracing.model.deployment.service.OpentracingAspect deployable = context.getDeployable();

		OpentracingAspect bean = new OpentracingAspect();

		return bean;
	}

}
