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
package tribefire.cortex.testing.main_qa.wire.space;

import com.braintribe.model.meta.GmMetaModel;
import com.braintribe.model.processing.deployment.api.ExpertContext;
import com.braintribe.provider.Holder;
import com.braintribe.wire.api.annotation.Import;
import com.braintribe.wire.api.annotation.Managed;
import com.braintribe.wire.api.space.WireSpace;

import tribefire.cortex.testing.processing.extensions.TestBasicTemplateBasedApp;
import tribefire.cortex.testing.processing.extensions.TestInMemoryAccess;
import tribefire.cortex.testing.processing.services.TestDataAccessServiceProcessor;
import tribefire.cortex.testing.processing.services.TestEchoServiceProcessor;
import tribefire.module.wire.contract.TribefireWebPlatformContract;

@Managed  
public class MainQaDeployablesSpace implements WireSpace {

	@Import
	private TribefireWebPlatformContract tfPlatform;

	@Managed
	public TestInMemoryAccess testInMemoryAccess(ExpertContext<com.braintribe.qa.cartridge.main.model.deployment.access.TestInMemoryAccess> context) {

		TestInMemoryAccess bean = new TestInMemoryAccess();

		com.braintribe.qa.cartridge.main.model.deployment.access.TestInMemoryAccess deployable = context.getDeployable();

		Holder<GmMetaModel> metaModelProvider = new Holder<>(deployable.getMetaModel());

		bean.setMetaModelProvider(metaModelProvider);
		bean.setAccessId(deployable.getExternalId());
		return bean;
	}

	@Managed
	public TestBasicTemplateBasedApp testBasicTemplateBasedApp(ExpertContext<com.braintribe.qa.cartridge.main.model.deployment.terminal.TestBasicTemplateBasedApp> context) {
		
		TestBasicTemplateBasedApp bean = new TestBasicTemplateBasedApp();
		
		com.braintribe.qa.cartridge.main.model.deployment.terminal.TestBasicTemplateBasedApp deployable = context.getDeployable();
		bean.setTimestamp(deployable.getTimestamp());
		return bean;
	}

	@Managed
	public TestEchoServiceProcessor testEchoProcessor() {
		TestEchoServiceProcessor bean = new TestEchoServiceProcessor();
		return bean;
	}
	
	@Managed
	public TestDataAccessServiceProcessor testDataAccessProcessor() {
		TestDataAccessServiceProcessor bean = new TestDataAccessServiceProcessor();
		return bean;
	}
	
}
