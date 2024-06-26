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
package tribefire.extension.appconfiguration.model.deployment;

import com.braintribe.model.extensiondeployment.ServiceProcessor;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

/**
 * Deployable for the respective processor implementation (with the same name).
 */
public interface ExportLocalizationsToSpreadsheetProcessor extends ServiceProcessor {

	EntityType<ExportLocalizationsToSpreadsheetProcessor> T = EntityTypes.T(ExportLocalizationsToSpreadsheetProcessor.class);

	/** The id of the acess that contains the configuration with the localizations to export. */
	String getAccessId();
	void setAccessId(String accessId);
}