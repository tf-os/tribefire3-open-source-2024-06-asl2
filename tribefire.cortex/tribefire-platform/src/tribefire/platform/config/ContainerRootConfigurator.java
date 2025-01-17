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
package tribefire.platform.config;

import java.util.Arrays;

import com.braintribe.config.configurator.Configurator;
import com.braintribe.config.configurator.ConfiguratorException;
import com.braintribe.config.configurator.ConfiguratorPriority;
import com.braintribe.config.configurator.ConfiguratorPriority.Level;
import com.braintribe.logging.Logger;
import com.braintribe.model.processing.bootstrapping.TribefireRuntime;

// TODO extract to web-app (or tomcat platform, in which case it could only use catalina.base)
@ConfiguratorPriority(value=Level.high, order=-100) // Mark this configurator with high priority to ensure it's running as first configurator.
public class ContainerRootConfigurator implements Configurator {
	
	private static final Logger log = Logger.getLogger(ContainerRootConfigurator.class);
	
	private static final String[] envVars = new String[] { "catalina.base", "jboss_home", "weblogic.home" };
	
	@Override
	public void configure() throws ConfiguratorException {
		
		
		if (isAlreadyConfigured()) {
			return;
		}
		
		for (String env : envVars) {
			
			String value = getProperty(env);
			
			if (value != null) {
				
				if (log.isInfoEnabled()) {
					log.info("Container root detected with [ "+env+" ]: [ "+value+" ]");
				}
				
				TribefireRuntime.setProperty(TribefireRuntime.ENVIRONMENT_CONTAINER_ROOT_DIR, value);
				
				return;
				
			} else if (log.isTraceEnabled()) {
				log.trace("Environment variable [ "+env+" ] not defined");
			}
		}
		
		if (log.isWarnEnabled()) {
			log.warn("No container root environment variable detected and no explicit container root configured. Setting container root to runtime directory. Inspected values: "+Arrays.asList(envVars));
			TribefireRuntime.setProperty(TribefireRuntime.ENVIRONMENT_CONTAINER_ROOT_DIR, "");
		}
		
	}

	private static boolean isAlreadyConfigured() {
		String containerRootDir = TribefireRuntime.getProperty(TribefireRuntime.ENVIRONMENT_CONTAINER_ROOT_DIR);
		if (containerRootDir != null) {
			log.info("Container root directory is explicitly defined. Skip automatic detection. ("+TribefireRuntime.ENVIRONMENT_CONTAINER_ROOT_DIR+"="+containerRootDir+") ");
			return true;
		}
		log.debug("No explicit container root directory configured ("+TribefireRuntime.ENVIRONMENT_CONTAINER_ROOT_DIR+"). Trying to detect automatically.");
		return false;
	}
	
	private static String getProperty(String propertyName) {
		
		String value = System.getProperty(propertyName);
		
		if (value != null)
			return value;
		
		return System.getenv(propertyName);
		
	}

	@Override
	public String toString() {
		String containerRootDir = TribefireRuntime.getProperty(TribefireRuntime.ENVIRONMENT_CONTAINER_ROOT_DIR);
		return "ContainerRootConfigurator ("+(containerRootDir != null ? containerRootDir : "<not yet set>")+")";
	}
}
