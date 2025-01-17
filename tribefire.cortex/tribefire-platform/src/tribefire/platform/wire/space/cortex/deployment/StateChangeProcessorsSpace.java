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
package tribefire.platform.wire.space.cortex.deployment;

import com.braintribe.model.processing.deployment.processor.BidiPropertyStateChangeProcessor;
import com.braintribe.model.processing.deployment.processor.MetaDataStateChangeProcessorRule;
import com.braintribe.model.processing.license.glf.processor.LicenseActivatedProcessor;
import com.braintribe.model.processing.license.glf.processor.LicenseUploadedProcessor;
import com.braintribe.model.processing.web.cors.CortexCorsStateChangeProcessor;
import com.braintribe.wire.api.annotation.Import;
import com.braintribe.wire.api.annotation.Managed;
import com.braintribe.wire.api.space.WireSpace;

import tribefire.platform.impl.model.ModelChangeNotifier;
import tribefire.platform.wire.space.rpc.RpcSpace;
import tribefire.platform.wire.space.security.AuthContextSpace;
import tribefire.platform.wire.space.security.servlets.SecurityServletSpace;
import tribefire.platform.wire.space.system.LicenseSpace;

@Managed
public class StateChangeProcessorsSpace implements WireSpace {

	@Import
	private DeploymentSpace deployment;

	@Import
	private LicenseSpace license;

	@Import
	private AuthContextSpace authContext;

	@Import
	private SecurityServletSpace securityServlet;

	@Import
	private RpcSpace rpc;
	
	@Managed
	public LicenseUploadedProcessor licenseUpload() {
		LicenseUploadedProcessor bean = new LicenseUploadedProcessor();
		bean.setUsernameProvider(authContext.currentUser().userNameProvider());
		return bean;
	}

	@Managed
	public LicenseActivatedProcessor licenseActivated() {
		LicenseActivatedProcessor bean = new LicenseActivatedProcessor();
		bean.setLicenseManager(license.manager());
		return bean;
	}

	@Managed
	public BidiPropertyStateChangeProcessor bidiProperty() {
		BidiPropertyStateChangeProcessor bean = new BidiPropertyStateChangeProcessor();
		return bean;
	}

	@Managed 
	public MetaDataStateChangeProcessorRule<?> metadata() {
		MetaDataStateChangeProcessorRule<?> bean = new MetaDataStateChangeProcessorRule<>();
		bean.setDeployRegistry(deployment.registry());
		return bean;
	}

	@Managed
	public CortexCorsStateChangeProcessor cors() {
		CortexCorsStateChangeProcessor bean = new CortexCorsStateChangeProcessor();
		bean.setCortexCorsHandler(securityServlet.cortexCorsHandler());
		return bean;
	}

	@Managed
	public ModelChangeNotifier modelAccessoryNotifier() {
		ModelChangeNotifier bean = new ModelChangeNotifier();
		bean.setRequestEvaluator(rpc.serviceRequestEvaluator());
		return bean;
	}
}
