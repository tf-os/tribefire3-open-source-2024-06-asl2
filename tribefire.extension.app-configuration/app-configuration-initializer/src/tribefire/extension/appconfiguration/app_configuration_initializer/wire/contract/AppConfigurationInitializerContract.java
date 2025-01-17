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
package tribefire.extension.appconfiguration.app_configuration_initializer.wire.contract;

import com.braintribe.model.accessdeployment.IncrementalAccess;
import com.braintribe.model.extensiondeployment.meta.ProcessWith;
import com.braintribe.model.meta.data.prompt.Hidden;
import com.braintribe.wire.api.space.WireSpace;

import tribefire.extension.appconfiguration.model.deployment.AppConfigurationProcessor;
import tribefire.extension.appconfiguration.model.deployment.ExportLocalizationsToSpreadsheetProcessor;
import tribefire.extension.appconfiguration.model.deployment.ImportLocalizationsFromSpreadsheetProcessor;
import tribefire.extension.js.model.deployment.ViewWithJsUxComponent;

public interface AppConfigurationInitializerContract extends WireSpace {

	IncrementalAccess appConfigurationAccess();

	AppConfigurationProcessor appConfigurationProcessor();

	ProcessWith processWithAppConfigurationProcessor();

	ImportLocalizationsFromSpreadsheetProcessor importLocalizationsFromSpreadsheetProcessor();

	ProcessWith processWithImportLocalizationsFromSpreadsheetProcessor();

	ExportLocalizationsToSpreadsheetProcessor exportLocalizationsToSpreadsheetProcessor();

	ProcessWith processWithExportLocalizationsToSpreadsheetProcessor();

	Hidden hiddenForNonAdminAndApi();

	Hidden hiddenForNonAdminAndGme();

	ViewWithJsUxComponent viewWithJsUxComponent();
}
