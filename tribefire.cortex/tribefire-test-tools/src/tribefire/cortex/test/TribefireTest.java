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
package tribefire.cortex.test;

import java.util.function.Supplier;

import com.braintribe.model.processing.tfconstants.TribefireComponent;
import com.braintribe.model.processing.tfconstants.TribefireUrlBuilder;
import com.braintribe.utils.lcd.CommonTools;

/**
 * Provides a set of simple helpers to make writing tribefire tests more convenient.
 * 
 * @author michael.lafite
 */
// TODO: the helpers for url/user/password are based on what's used in ImpApiFactory. Code should be in a single place.
public interface TribefireTest {

	/** Returns the tribefire-services url. */
	default String tribefireServicesURL() {
		return getConfigProperty(() -> new TribefireUrlBuilder().http().buildFor(TribefireComponent.Services), "TRIBEFIRE_SERVICES_URL",
				"TRIBEFIRE_PUBLIC_SERVICES_URL", "QA_FORCE_URL");
	}

	/** Returns the default user used to connect to tribefire. */
	default String tribefireDefaultUser() {
		return getConfigProperty(() -> "cortex", "TRIBEFIRE_DEFAULT_USER", "QA_FORCE_USERNAME");
	}

	/** Returns the default password used to connect to tribefire. */
	default String tribefireDefaultPassword() {
		return getConfigProperty(() -> "cortex", "TRIBEFIRE_DEFAULT_PASSWORD", "QA_FORCE_PASSWORD");
	}

	/**
	 * Returns the value of the specified configuration property.
	 * 
	 * @param defaultValueSuplier
	 *            a supplier which provides a default value (in case the property is not set).
	 * @param propertyNames
	 *            one ore more property names that will be checked. Each property is searched in system properties and
	 *            in environment variables. System properties are converted to lower case and "_" is converted to ".".
	 */
	default String getConfigProperty(Supplier<String> defaultValueSuplier, String... propertyNames) {
		String result = null;
		for (String propertyName : propertyNames) {
			String systemPropertyName = propertyName.toLowerCase().replace("_", ".");

			result = System.getProperty(systemPropertyName);
			if (CommonTools.isEmpty(result)) {
				result = System.getenv(propertyName);
			}
			if (result != null) {
				break;
			}
		}
		if (result == null) {
			result = defaultValueSuplier.get();
		}
		return result;
	}
}
