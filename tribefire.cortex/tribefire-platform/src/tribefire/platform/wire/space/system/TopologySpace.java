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
package tribefire.platform.wire.space.system;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.braintribe.execution.ExtendedScheduledThreadPoolExecutor;
import com.braintribe.execution.virtual.CountingVirtualThreadFactory;
import com.braintribe.logging.Logger;
import com.braintribe.model.extensiondeployment.HardwiredWorker;
import com.braintribe.wire.api.annotation.Import;
import com.braintribe.wire.api.annotation.Managed;

import tribefire.module.wire.contract.TopologyContract;
import tribefire.platform.impl.topology.CartridgeLiveInstances;
import tribefire.platform.impl.topology.HeartbeatManager;
import tribefire.platform.wire.space.common.CartridgeInformationSpace;
import tribefire.platform.wire.space.common.MessagingSpace;
import tribefire.platform.wire.space.cortex.deployment.DeploymentSpace;

@Managed
public class TopologySpace implements TopologyContract {

	private static final Logger logger = Logger.getLogger(TopologySpace.class);

	@Import
	private CartridgeInformationSpace cartridgeInformation;

	@Import
	private MessagingSpace messaging;

	@Import
	private DeploymentSpace deployment;

	@Override
	@Managed
	public CartridgeLiveInstances liveInstances() {
		CartridgeLiveInstances bean = new CartridgeLiveInstances();
		bean.setCurrentInstanceId(cartridgeInformation.instanceId());
		bean.setEnabled(consumeHeartbeats());
		bean.setAliveAge(30000); // 30 seconds
		bean.setMaxHeartbeatAge(30000); // 30 seconds
		bean.setCleanupInterval(600000); // 10 minutes
		bean.addListener("*", deployment.processor());
		return bean;
	}

	@Managed
	public HeartbeatManager heartbeatManager() {
		logger.info(() -> "Starting TFS HeartbeatManager signalling instance id: " + cartridgeInformation.instanceId());
		HeartbeatManager bean = new HeartbeatManager();
		bean.setCurrentInstanceId(cartridgeInformation.instanceId());
		bean.setConsumptionEnabled(consumeHeartbeats());
		bean.setHeartbeatConsumer(liveInstances());
		bean.setBroadcastingEnabled(true);
		bean.setBroadcastingService(heartbeatBroadcastingService());
		bean.setBroadcastingInterval(10L);
		bean.setBroadcastingIntervalUnit(TimeUnit.SECONDS);
		bean.setMessagingSessionProvider(messaging.sessionProvider());
		bean.setTopicName(messaging.destinations().heartbeatTopicName());
		bean.setAutoHeartbeatBroadcastingStart(false);
		return bean;
	}

	@Managed
	public HardwiredWorker heartbeatManagerDeployable() {
		HardwiredWorker bean = HardwiredWorker.T.create();
		bean.setExternalId("heartbeat-manager");
		bean.setName("Heartbeat Manager");
		bean.setGlobalId("hardwired:worker/" + bean.getExternalId());
		return bean;
	}

	public boolean consumeHeartbeats() {
		return true; // Whether the platform should track live instances. Could be later configurable.
	}

	@Managed
	private ScheduledExecutorService heartbeatBroadcastingService() {
		ExtendedScheduledThreadPoolExecutor bean = new ExtendedScheduledThreadPoolExecutor(1,
				new CountingVirtualThreadFactory("tribefire.heartbeat.sender[" + cartridgeInformation.instanceId() + "]-"));
		bean.setDescription("Master Heartbeat Sender");
		return bean;
	}

}
