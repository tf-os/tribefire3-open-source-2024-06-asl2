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
package com.braintribe.artifact.processing.wire;

import com.braintribe.build.artifact.representations.artifact.maven.settings.LocalRepositoryLocationProvider;
import com.braintribe.build.artifact.representations.artifact.maven.settings.persistence.MavenSettingsPersistenceExpert;
import com.braintribe.build.artifact.representations.artifact.pom.listener.PomReaderNotificationListener;
import com.braintribe.build.artifacts.mc.wire.buildwalk.contract.MavenSettingsContract;
import com.braintribe.build.artifacts.mc.wire.buildwalk.contract.NotificationContract;
import com.braintribe.cfg.Configurable;
import com.braintribe.ve.api.VirtualEnvironment;

import tribefire.cortex.asset.resolving.wire.contract.PlatformAssetResolvingConfigurationContract;

public class ArtifactProcessingPlatformAssetResolvingConfigurationSpace implements PlatformAssetResolvingConfigurationContract, MavenSettingsContract, NotificationContract {
	
	private MavenSettingsPersistenceExpert persistenceExpert;
	private LocalRepositoryLocationProvider localRepositoryProvider;
	private VirtualEnvironment ve;
	private PomReaderNotificationListener pomNotificationListener;
	
	public ArtifactProcessingPlatformAssetResolvingConfigurationSpace() {		
	}

	public ArtifactProcessingPlatformAssetResolvingConfigurationSpace(MavenSettingsPersistenceExpert persistenceExpert, LocalRepositoryLocationProvider localRepositoryProvider, VirtualEnvironment ve, PomReaderNotificationListener pomNotificationListener) {
		this.persistenceExpert = persistenceExpert;
		this.localRepositoryProvider = localRepositoryProvider;
		this.ve = ve;
		this.pomNotificationListener = pomNotificationListener;
	}

	@Override
	public PomReaderNotificationListener pomReaderNotificationListener() {
		return pomNotificationListener;
	}
	
	@Configurable
	public void setPomNotificationListener(PomReaderNotificationListener pomNotificationListener) {
		this.pomNotificationListener = pomNotificationListener;
	}

	@Override
	public MavenSettingsPersistenceExpert settingsPersistenceExpert() {
		return persistenceExpert;
	}
	
	@Configurable
	public void setSettingsPersistenceExpert(MavenSettingsPersistenceExpert persistenceExpert) {
		this.persistenceExpert = persistenceExpert;
	}	

	@Override
	public LocalRepositoryLocationProvider localRepositoryLocationProvider() {
		return localRepositoryProvider;
	}
	
	@Configurable
	public void setLocalRepositoryProvider(LocalRepositoryLocationProvider localRepositoryProvider) {
		this.localRepositoryProvider = localRepositoryProvider;
	}

	@Override
	public VirtualEnvironment virtualEnvironment() {
		return ve;
	}
	
	@Configurable
	public void setVirtualEnvironment(VirtualEnvironment ve) {
		this.ve = ve;
	}
}
