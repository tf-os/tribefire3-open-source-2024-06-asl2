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
package tribefire.extension.appconfiguration.app_configuration_initializer.wire.space;

import com.braintribe.wire.api.annotation.Import;
import com.braintribe.wire.api.annotation.Managed;

import tribefire.cortex.initializer.support.integrity.wire.contract.CoreInstancesContract;
import tribefire.extension.appconfiguration.app_configuration_initializer.wire.contract.AppConfigurationInitializerContract;
import tribefire.extension.appconfiguration.app_configuration_initializer.wire.contract.AppConfigurationInitializerMainContract;
import tribefire.extension.appconfiguration.app_configuration_initializer.wire.contract.AppConfigurationInitializerModelsContract;
import tribefire.extension.appconfiguration.app_configuration_initializer.wire.contract.ExistingInstancesContract;
import tribefire.extension.appconfiguration.app_configuration_initializer.wire.contract.RuntimePropertiesContract;

@Managed
public class AppConfigurationInitializerMainSpace implements AppConfigurationInitializerMainContract {

	@Import
	private AppConfigurationInitializerContract initializer;

	@Import
	private AppConfigurationInitializerModelsContract models;

	@Import
	private ExistingInstancesContract existingInstances;

	@Import
	private RuntimePropertiesContract properties;

	@Import
	private CoreInstancesContract coreInstances;

	@Override
	public AppConfigurationInitializerContract initializer() {
		return initializer;
	}

	@Override
	public AppConfigurationInitializerModelsContract models() {
		return models;
	}

	@Override
	public ExistingInstancesContract existingInstances() {
		return existingInstances;
	}

	@Override
	public RuntimePropertiesContract properties() {
		return properties;
	}

	@Override
	public CoreInstancesContract coreInstances() {
		return coreInstances;
	}
}
