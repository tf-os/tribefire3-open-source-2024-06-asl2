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
package tribefire.extension.etcd.etcd_vitals.wire.space;

import com.braintribe.codec.marshaller.json.JsonStreamMarshaller;
import com.braintribe.integration.etcd.supplier.ClientSupplier;
import com.braintribe.integration.etcd.supplier.ComposeClientSupplier;
import com.braintribe.model.processing.deployment.api.ExpertContext;
import com.braintribe.model.processing.deployment.api.binding.DenotationBindingBuilder;
import com.braintribe.wire.api.annotation.Import;
import com.braintribe.wire.api.annotation.Managed;

import tribefire.extension.etcd.vitals.model.deployment.EtcdComposeDcsaSharedStorage;
import tribefire.extension.etcd.vitals.model.deployment.EtcdComposeLeadershipManager;
import tribefire.extension.etcd.vitals.model.deployment.EtcdComposeLockManager;
import tribefire.extension.etcd.vitals.model.deployment.EtcdDcsaSharedStorage;
import tribefire.extension.etcd.vitals.model.deployment.EtcdLeadershipManager;
import tribefire.extension.etcd.vitals.model.deployment.EtcdLockManager;
import tribefire.extension.etcd.vitals.model.deployment.EtcdMessaging;
import tribefire.module.wire.contract.ClusterBindersContract;
import tribefire.module.wire.contract.MessagingContract;
import tribefire.module.wire.contract.TribefireModuleContract;
import tribefire.module.wire.contract.TribefireWebPlatformContract;
import tribefire.module.wire.contract.WebPlatformHardwiredExpertsContract;

/**
 * This module's javadoc is yet be written.
 */
@Managed
public class EtcdVitalsModuleSpace implements TribefireModuleContract {

	@Import
	private TribefireWebPlatformContract tfPlatform;

	@Import
	private MessagingContract messaging;

	@Import
	private ClusterBindersContract clusterBinders;

	@Import
	private WebPlatformHardwiredExpertsContract hardwiredExperts;

	@Override
	public void bindHardwired() {
		EtcdPluggablesEdr2ccMorphers.bindMorphers(hardwiredExperts.denotationTransformationRegistry());
	}

	@Override
	public void bindDeployables(DenotationBindingBuilder bindings) {
		bindings.bind(EtcdMessaging.T) //
				.component(clusterBinders.messaging()) //
				.expertFactory(this::messaging);

//		bindings.bind(EtcdLockManager.T) //
//				.component(clusterBinders.lockingManager()) //
//				.expertFactory(this::lockManager);
//
//		bindings.bind(EtcdLeadershipManager.T) //
//				.component(clusterBinders.leadershipManager()) //
//				.expertFactory(this::leadershipManager);

		bindings.bind(EtcdDcsaSharedStorage.T) //
				.component(clusterBinders.dcsaSharedStorage()) //
				.expertFactory(this::dcsaSharedStorage);

//		bindings.bind(EtcdComposeLockManager.T) //
//				.component(clusterBinders.lockingManager()) //
//				.expertFactory(this::composeLockManager);
//
//		bindings.bind(EtcdComposeLeadershipManager.T) //
//				.component(clusterBinders.leadershipManager()) //
//				.expertFactory(this::composeLeadershipManager);

		bindings.bind(EtcdComposeDcsaSharedStorage.T) //
				.component(clusterBinders.dcsaSharedStorage()) //
				.expertFactory(this::composeDcsaSharedStorage);
	}

	private com.braintribe.transport.messaging.etcd.EtcdConnectionProvider messaging(ExpertContext<EtcdMessaging> expertContext) {
		EtcdMessaging deployable = expertContext.getDeployable();

		com.braintribe.transport.messaging.etcd.EtcdConnectionProvider expert = new com.braintribe.transport.messaging.etcd.EtcdConnectionProvider();
		expert.setConnectionConfiguration(connectionConfig(deployable));
		expert.setMessagingContext(messaging.context());

		return expert;
	}

	private com.braintribe.model.messaging.etcd.EtcdMessaging connectionConfig(EtcdMessaging deployable) {
		com.braintribe.model.messaging.etcd.EtcdMessaging result = com.braintribe.model.messaging.etcd.EtcdMessaging.T.create();
		result.setEndpointUrls(deployable.getEndpointUrls());
		result.setProject(deployable.getProject());
		result.setUsername(deployable.getUsername());
		result.setPassword(deployable.getPassword());

		return result;
	}

	private com.braintribe.model.processing.lock.etcd.EtcdLockManager lockManager(ExpertContext<EtcdLockManager> expertContext) {
		EtcdLockManager deployable = expertContext.getDeployable();

		com.braintribe.model.processing.lock.etcd.EtcdLockManager expert = new com.braintribe.model.processing.lock.etcd.EtcdLockManager();
		expert.setClientSupplier(new ClientSupplier(deployable.getEndpointUrls(), deployable.getUsername(), deployable.getPassword()));
		expert.setIdentifierPrefix(deployable.getProject());
		expert.postConstruct();

		return expert;
	}

	private com.braintribe.model.processing.leadership.etcd.EtcdLeadershipManager leadershipManager(
			ExpertContext<EtcdLeadershipManager> expertContext) {
		EtcdLeadershipManager deployable = expertContext.getDeployable();

		com.braintribe.model.processing.leadership.etcd.EtcdLeadershipManager expert = new com.braintribe.model.processing.leadership.etcd.EtcdLeadershipManager();
		expert.setClientSupplier(new ClientSupplier(deployable.getEndpointUrls(), deployable.getUsername(), deployable.getPassword()));
		Long defaultLeadershipTimeout = deployable.getDefaultLeadershipTimeout();
		if (defaultLeadershipTimeout != null && defaultLeadershipTimeout > 0L) {
			expert.setDefaultLeadershipTimeout(defaultLeadershipTimeout);
		}
		Long defaultCandidateTimeout = deployable.getDefaultCandidateTimeout();
		if (defaultCandidateTimeout != null && defaultCandidateTimeout > 0L) {
			expert.setDefaultCandidateTimeout(defaultCandidateTimeout);
		}
		Long checkInterval = deployable.getCheckInterval();
		if (checkInterval != null && checkInterval > 0L) {
			expert.setCheckInterval(checkInterval);
		}
		expert.postConstruct();

		expert.setLocalInstanceId(tfPlatform.platformReflection().instanceId());
		expert.setRequestEvaluator(tfPlatform.requestUserRelated().evaluator());
		expert.setUserSessionScoping(tfPlatform.masterUserAuthContext().userSessionScoping());
		expert.setSessionIdSupplier(tfPlatform.systemUserRelated().userSessionIdSupplier());
		expert.pluginPostConstruct();

		return expert;
	}

	private com.braintribe.model.access.collaboration.distributed.api.EtcdDcsaSharedStorage dcsaSharedStorage(
			ExpertContext<EtcdDcsaSharedStorage> expertContext) {
		EtcdDcsaSharedStorage deployable = expertContext.getDeployable();

		com.braintribe.model.access.collaboration.distributed.api.EtcdDcsaSharedStorage expert = new com.braintribe.model.access.collaboration.distributed.api.EtcdDcsaSharedStorage();
		expert.setClientSupplier(new ClientSupplier(deployable.getEndpointUrls(), deployable.getUsername(), deployable.getPassword()));
		expert.setProject(deployable.getProject());
		JsonStreamMarshaller marshaller = new JsonStreamMarshaller();
		expert.setMarshaller(marshaller);

		return expert;
	}

	private com.braintribe.model.processing.lock.etcd.EtcdLockManager composeLockManager(ExpertContext<EtcdComposeLockManager> expertContext) {
		EtcdComposeLockManager deployable = expertContext.getDeployable();

		com.braintribe.model.processing.lock.etcd.EtcdLockManager expert = new com.braintribe.model.processing.lock.etcd.EtcdLockManager();
		expert.setClientSupplier(new ComposeClientSupplier(deployable.getEndpointUrls(), deployable.getUsername(), deployable.getPassword(),
				deployable.getAuthority(), deployable.getAuthorityPrefix(), deployable.getCertificate()));
		expert.setIdentifierPrefix(deployable.getProject());
		expert.postConstruct();

		return expert;
	}

	private com.braintribe.model.processing.leadership.etcd.EtcdLeadershipManager composeLeadershipManager(
			ExpertContext<EtcdComposeLeadershipManager> expertContext) {

		EtcdComposeLeadershipManager deployable = expertContext.getDeployable();

		com.braintribe.model.processing.leadership.etcd.EtcdLeadershipManager expert = new com.braintribe.model.processing.leadership.etcd.EtcdLeadershipManager();
		expert.setClientSupplier(new ComposeClientSupplier(deployable.getEndpointUrls(), deployable.getUsername(), deployable.getPassword(),
				deployable.getAuthority(), deployable.getAuthorityPrefix(), deployable.getCertificate()));
		Long defaultLeadershipTimeout = deployable.getDefaultLeadershipTimeout();
		if (defaultLeadershipTimeout != null && defaultLeadershipTimeout > 0L) {
			expert.setDefaultLeadershipTimeout(defaultLeadershipTimeout);
		}
		Long defaultCandidateTimeout = deployable.getDefaultCandidateTimeout();
		if (defaultCandidateTimeout != null && defaultCandidateTimeout > 0L) {
			expert.setDefaultCandidateTimeout(defaultCandidateTimeout);
		}
		Long checkInterval = deployable.getCheckInterval();
		if (checkInterval != null && checkInterval > 0L) {
			expert.setCheckInterval(checkInterval);
		}
		expert.postConstruct();

		return expert;
	}

	private com.braintribe.model.access.collaboration.distributed.api.EtcdDcsaSharedStorage composeDcsaSharedStorage(
			ExpertContext<EtcdComposeDcsaSharedStorage> expertContext) {

		EtcdComposeDcsaSharedStorage deployable = expertContext.getDeployable();

		com.braintribe.model.access.collaboration.distributed.api.EtcdDcsaSharedStorage expert = new com.braintribe.model.access.collaboration.distributed.api.EtcdDcsaSharedStorage();
		expert.setClientSupplier(new ComposeClientSupplier(deployable.getEndpointUrls(), deployable.getUsername(), deployable.getPassword(),
				deployable.getAuthority(), deployable.getAuthorityPrefix(), deployable.getCertificate()));
		expert.setProject(deployable.getProject());
		JsonStreamMarshaller marshaller = new JsonStreamMarshaller();
		expert.setMarshaller(marshaller);

		return expert;
	}
}