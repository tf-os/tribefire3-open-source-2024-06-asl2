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
package com.braintribe.devrock.artifactcontainer.plugin.malaclypse.scope.overrides;

import java.io.File;

import org.eclipse.core.runtime.IStatus;

import com.braintribe.build.artifact.representations.RepresentationException;
import com.braintribe.build.artifact.representations.artifact.maven.settings.persistence.AbstractMavenSettingsPersistenceExpert;
import com.braintribe.build.artifact.representations.artifact.maven.settings.persistence.MavenSettingsPersistenceExpert;
import com.braintribe.build.artifact.virtualenvironment.VirtualPropertyResolver;
import com.braintribe.cfg.Configurable;
import com.braintribe.cfg.Required;
import com.braintribe.devrock.artifactcontainer.ArtifactContainerPlugin;
import com.braintribe.devrock.artifactcontainer.ArtifactContainerStatus;
import com.braintribe.devrock.artifactcontainer.plugin.preferences.maven.HasMavenTokens;
import com.braintribe.model.malaclypse.cfg.preferences.mv.MavenPreferences;
import com.braintribe.model.maven.settings.Settings;

/**
 * an implementation of the {@link MavenSettingsPersistenceExpert} that allows for overrides via the preferences, and the {@link VirtualPropertyResolver}
 * @author pit
 *
 */
public class OverrideMavenSettingsPersistenceExpert extends AbstractMavenSettingsPersistenceExpert implements MavenSettingsPersistenceExpert, HasMavenTokens {
	private ArtifactContainerPlugin plugin = ArtifactContainerPlugin.getInstance();
	private VirtualPropertyResolver propertyResolver;
	
	@Configurable  @Required
	public void setPropertyResolver(VirtualPropertyResolver propertyResolver) {
		this.propertyResolver = propertyResolver;
	}

	@Override
	public Settings loadSettings() throws RepresentationException {
		MavenPreferences mavenPreferences = plugin.getArtifactContainerPreferences(false).getMavenPreferences();
		
		String userOverride = mavenPreferences.getUserSettingsOverride();
		if (userOverride == null) {
			userOverride = LOCAL_SETTINGS;
		}
		
		String resolvedUserOverride = propertyResolver.resolve(userOverride);
		File userFile = new File( resolvedUserOverride);
		Settings localSettings = null;
		if (userFile.exists()) { 
			localSettings = loadSettings( userFile);
		}
		
		String confOverride = mavenPreferences.getConfSettingsOverride();
		if (confOverride == null) {
			confOverride = REMOTE_SETTINGS;
		}
		
		String resolvedConfOverride = propertyResolver.resolve(confOverride);
		File confFile = new File( resolvedConfOverride);
		Settings confSettings = null;
		if (confFile.exists()) { 
			confSettings = loadSettings( confFile);
		}
		if (localSettings == null && confSettings == null) {
			String msg = "no settings found for [" + resolvedUserOverride + "] & [" + resolvedConfOverride +"]";
			ArtifactContainerStatus status = new ArtifactContainerStatus(msg, IStatus.ERROR);
			ArtifactContainerPlugin.getInstance().log(status);	
			throw new RepresentationException( msg);
		}
		if (localSettings != null && confSettings != null){ 
			return mergeSettings(localSettings, confSettings);
		} else {
			if (localSettings != null) {
				return localSettings;
			} else { 
				return confSettings;
			}
		}		
	}

}