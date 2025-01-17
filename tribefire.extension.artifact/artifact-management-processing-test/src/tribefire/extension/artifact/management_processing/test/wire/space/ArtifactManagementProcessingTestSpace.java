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
package tribefire.extension.artifact.management_processing.test.wire.space;

import com.braintribe.gm.service.wire.common.contract.CommonServiceProcessingContract;
import com.braintribe.gm.service.wire.common.contract.ServiceProcessingConfigurationContract;
import com.braintribe.model.generic.eval.Evaluator;
import com.braintribe.model.processing.securityservice.commons.service.InMemorySecurityServiceProcessor;
import com.braintribe.model.processing.service.common.ConfigurableDispatchingServiceProcessor;
import com.braintribe.model.service.api.ServiceRequest;
import com.braintribe.ve.api.VirtualEnvironment;
import com.braintribe.wire.api.annotation.Import;
import com.braintribe.wire.api.annotation.Managed;
import com.braintribe.wire.api.context.WireContextConfiguration;

import tribefire.extension.artifact.management.api.model.request.ArtifactManagementRequest;
import tribefire.extension.artifact.management.processing.ArtifactManagementProcessor;
import tribefire.extension.artifact.management_processing.test.wire.contract.ArtifactManagementProcessingTestConfigurationContract;
import tribefire.extension.artifact.management_processing.test.wire.contract.ArtifactManagementProcessingTestContract;

@Managed
public class ArtifactManagementProcessingTestSpace implements ArtifactManagementProcessingTestContract {

	@Import
	private ServiceProcessingConfigurationContract serviceProcessingConfiguration;
	
	@Import
	private CommonServiceProcessingContract commonServiceProcessing;
	
	@Import 
	private ArtifactManagementProcessingTestConfigurationContract cfg;
	
	@Override
	public void onLoaded(WireContextConfiguration configuration) {
		serviceProcessingConfiguration.registerServiceConfigurer(this::configureServices);
		serviceProcessingConfiguration.registerSecurityConfigurer(this::configureSecurity);
	}
	
	private void configureServices(ConfigurableDispatchingServiceProcessor bean) {
		bean.removeInterceptor("auth"); // remove this line if you want your requests authorized while testing
		
		bean.register(ArtifactManagementRequest.T, artifactManagementProcessor());
	}

	private void configureSecurity(InMemorySecurityServiceProcessor bean) {
		// TODO configure security IF your requests are to be authorized while testing
		// (make sure the 'auth' interceptor is not removed in that case in the 'configureServices' method)
	}
	
	@Override
	public Evaluator<ServiceRequest> evaluator() {
		return commonServiceProcessing.evaluator();
	}
	
	private ArtifactManagementProcessor artifactManagementProcessor() {
		ArtifactManagementProcessor bean = new ArtifactManagementProcessor();
		bean.setVirtualEnvironment( virtualEnvironment());
		return bean;
	}
	
	protected VirtualEnvironment virtualEnvironment() {
		return cfg.virtualEnviroment();
	}
	
}
