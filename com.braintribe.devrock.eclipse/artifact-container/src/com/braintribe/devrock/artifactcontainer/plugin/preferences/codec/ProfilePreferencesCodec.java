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
package com.braintribe.devrock.artifactcontainer.plugin.preferences.codec;

import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.jface.preference.IPreferenceStore;

import com.braintribe.codec.Codec;
import com.braintribe.codec.CodecException;
import com.braintribe.devrock.artifactcontainer.plugin.preferences.ArtifactContainerPreferenceConstants;
import com.braintribe.model.malaclypse.cfg.preferences.ac.profile.ProfileActivationStatus;
import com.braintribe.model.malaclypse.cfg.preferences.ac.profile.ProfilePreferences;
import com.braintribe.model.malaclypse.cfg.preferences.ac.profile.ProfileRavenhurstSupport;
import com.braintribe.model.malaclypse.cfg.preferences.ac.ravenhurst.RavenhurstSupportLevel;
import com.braintribe.model.malaclypse.cfg.preferences.ac.ravenhurst.RepositoryRavenhurstSupport;

public class ProfilePreferencesCodec implements Codec<IPreferenceStore, ProfilePreferences> {
	
	private IPreferenceStore store;

	public ProfilePreferencesCodec(IPreferenceStore store) {
		this.store = store;
	}

	@Override
	public IPreferenceStore decode(ProfilePreferences profilePreferences) throws CodecException {		
		if (profilePreferences != null) {
			// profile settings (profile:activation)[,..]
 			Map<String, ProfileActivationStatus> profileSettings = profilePreferences.getProfileSettings(); 			
 			StringBuilder builder = new StringBuilder();
 			for (Entry<String, ProfileActivationStatus> entry : profileSettings.entrySet()) {
 				if (builder.length() > 0) {
 					builder.append(",");
 				} 				
 				builder.append( entry.getKey() + ":" + entry.getValue().toString()); 				
 			}
 			store.setValue( ArtifactContainerPreferenceConstants.PC_PROFILE_SETTINGS, builder.toString());
 			//repository settings (profile{repository:ravenhurst support[;repository:ravenhurst support]}[,..]
 			Map<String, ProfileRavenhurstSupport> repositorySettings = profilePreferences.getRepositoryStates();
 			builder = new StringBuilder();
 			for (Entry<String, ProfileRavenhurstSupport> entry : repositorySettings.entrySet()) {
 				if (builder.length() > 0) {
 					builder.append(",");
 				} 				
 				builder.append( entry.getKey() + "{");
 				for (RepositoryRavenhurstSupport repoEntry : entry.getValue().getRepositoryRavenhurstSupportLevels()) {
 					if (!builder.substring( builder.length()-1).equalsIgnoreCase("{")) {
 						builder.append(";");
 					}
 					builder.append( repoEntry.getRepositoryId() +":" + repoEntry.getRavenhurstSupportLevel().toString());
 				}
 				builder.append("}");
 			}
 			store.setValue( ArtifactContainerPreferenceConstants.PC_REPOSITORY_SETTINGS, builder.toString()); 			
 		}
		return store;
	}

	@Override
	public ProfilePreferences encode(IPreferenceStore store) throws CodecException {
		ProfilePreferences profilePreferences = ProfilePreferences.T.create();

		String profileSettingsAsString = store.getString( ArtifactContainerPreferenceConstants.PC_PROFILE_SETTINGS);
		if (profileSettingsAsString != null & profileSettingsAsString.length() > 0) {			
			Map<String, ProfileActivationStatus> statusMap = profilePreferences.getProfileSettings();
			String [] values = profileSettingsAsString.split( ",");
			for (String value : values) {
				String [] setting = value.split(":");
				String profileName = setting[0];
				String statusValue = setting[1];				
				statusMap.put( profileName, ProfileActivationStatus.valueOf(statusValue));												
			}
		}
		
		String repositorySettingsAsString = store.getString( ArtifactContainerPreferenceConstants.PC_REPOSITORY_SETTINGS);
		if (repositorySettingsAsString != null && repositorySettingsAsString.length()>0) {
			Map<String, ProfileRavenhurstSupport> repositorySettings = profilePreferences.getRepositoryStates();			
			String [] values = repositorySettingsAsString.split( ",");
			for (String value : values) {
				int openBracket = value.indexOf("{");
				int closeBracket = value.indexOf( "}");
				String profile = value.substring(0, openBracket);
				String reposStatesAsString = value.substring( openBracket+1, closeBracket);
				String [] repoStates = reposStatesAsString.split(";");
				Set<RepositoryRavenhurstSupport> profilesSettings = new HashSet<RepositoryRavenhurstSupport>();
				for (String repoState : repoStates) {
					String [] rvals = repoState.split(":");
					String name = rvals[0];
					RepositoryRavenhurstSupport repositoryRavenhurstSupport = RepositoryRavenhurstSupport.T.create();
					repositoryRavenhurstSupport.setRepositoryId(name);
					repositoryRavenhurstSupport.setRavenhurstSupportLevel(RavenhurstSupportLevel.valueOf( rvals[1]));
					profilesSettings.add(repositoryRavenhurstSupport); 
					
				}
				ProfileRavenhurstSupport profileRavenhurstSupport = ProfileRavenhurstSupport.T.create();
				profileRavenhurstSupport.setRepositoryRavenhurstSupportLevels(profilesSettings);
				repositorySettings.put(profile, profileRavenhurstSupport);
			}
		}
		return profilePreferences;
	}

	@Override
	public Class<IPreferenceStore> getValueClass() {
		return IPreferenceStore.class;
	}

}
