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
package tribefire.platform.impl.multicast.wire.space;

import java.util.HashMap;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.braintribe.codec.marshaller.api.ConfigurableMarshallerRegistry;
import com.braintribe.codec.marshaller.api.Marshaller;
import com.braintribe.codec.marshaller.bin.Bin2Marshaller;
import com.braintribe.codec.marshaller.common.BasicConfigurableMarshallerRegistry;
import com.braintribe.gm.service.wire.common.contract.CommonServiceProcessingContract;
import com.braintribe.gm.service.wire.common.contract.ServiceProcessingConfigurationContract;
import com.braintribe.model.generic.eval.Evaluator;
import com.braintribe.model.messaging.dmb.GmDmbMqMessaging;
import com.braintribe.model.processing.service.common.ConfigurableDispatchingServiceProcessor;
import com.braintribe.model.service.api.InstanceId;
import com.braintribe.model.service.api.MulticastRequest;
import com.braintribe.model.service.api.ServiceRequest;
import com.braintribe.transport.messaging.api.MessagingContext;
import com.braintribe.transport.messaging.api.MessagingSessionProvider;
import com.braintribe.transport.messaging.dbm.GmDmbMqConnectionProvider;
import com.braintribe.transport.messaging.impl.StandardMessagingSessionProvider;
import com.braintribe.wire.api.annotation.Import;
import com.braintribe.wire.api.annotation.Managed;
import com.braintribe.wire.api.context.WireContextConfiguration;

import tribefire.platform.impl.multicast.TestMulticastConsumer;
import tribefire.platform.impl.multicast.wire.contract.MulticastProcessorContract;
import tribefire.platform.impl.multicast.MulticastProcessor;
import tribefire.platform.impl.topology.CartridgeLiveInstances;

@Managed
public class MulticastProcessorSpace implements MulticastProcessorContract {

	@Import
	private ServiceProcessingConfigurationContract serviceProcessingConfiguration;

	@Import
	private CommonServiceProcessingContract commonServiceProcessing;

	@Override
	public void onLoaded(WireContextConfiguration configuration) {
		serviceProcessingConfiguration.registerServiceConfigurer(this::bindRequests);
	}

	private void bindRequests(ConfigurableDispatchingServiceProcessor bean) {
		bean.removeInterceptor("auth");

		bean.register(MulticastRequest.T, processor());
		// bean.register(TestRequest.T, processor());
	}

	@Override
	public Evaluator<ServiceRequest> evaluator() {
		return commonServiceProcessing.evaluator();
	}

	@Managed
	private MulticastProcessor processor() {
		MulticastProcessor bean = new MulticastProcessor();
		bean.setMessagingSessionProvider(sessionProvider(INSTANCE_A1));
		bean.setRequestTopicName("request");
		bean.setResponseTopicName("response");
		bean.setSenderId(INSTANCE_A1);
		bean.setLiveInstances(liveInstances());
		bean.setMetaDataProvider(HashMap::new);
		bean.setDefaultResponseTimeout(DEFAULT_TIMEOUT);
		bean.setLocalCallOptimizationEnabled(false);
		return bean;
	}

	@Managed
	@Override
	public TestMulticastConsumer consumer(InstanceId instanceId) {
		TestMulticastConsumer bean = new TestMulticastConsumer();
		bean.setConsumerId(instanceId);
		bean.setExecutor(new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>()));
		bean.setMessagingSessionProvider(sessionProvider(instanceId));
		bean.setRequestTopicName("request");
		return bean;
	}

	@Managed
	@Override
	public CartridgeLiveInstances liveInstances() {
		CartridgeLiveInstances bean = new CartridgeLiveInstances();
		bean.setCurrentInstanceId(INSTANCE_A1);
		bean.setEnabled(true);
		bean.setAliveAge(30000); // 30 seconds
		bean.setMaxHeartbeatAge(30000); // 30 seconds
		bean.setCleanupInterval(60000); // 1 minute
		return bean;
	}

	@Override
	@Managed
	public MessagingSessionProvider sessionProvider(InstanceId instanceId) {
		StandardMessagingSessionProvider bean = new StandardMessagingSessionProvider();
		bean.setMessagingConnectionProvider(messagingConnectionProvider(instanceId));
		return bean;
	}

	private GmDmbMqConnectionProvider messagingConnectionProvider(InstanceId instanceId) {
		return messaging().createConnectionProvider(this.messagingDenotation(), context(instanceId));
	}

	@Managed
	private MessagingContext context(InstanceId instanceId) {
		MessagingContext bean = new MessagingContext();
		bean.setMarshaller(binMarshaller());
		bean.setApplicationId(instanceId.getApplicationId());
		bean.setNodeId(instanceId.getNodeId());
		return bean;
	}

	@Managed
	private com.braintribe.transport.messaging.dbm.GmDmbMqMessaging messaging() {
		return new com.braintribe.transport.messaging.dbm.GmDmbMqMessaging();
	}

	@Managed
	private GmDmbMqMessaging messagingDenotation() {
		return GmDmbMqMessaging.T.create();
	}

	@Managed
	private ConfigurableMarshallerRegistry registry() {

		BasicConfigurableMarshallerRegistry bean = new BasicConfigurableMarshallerRegistry();

		bean.registerMarshaller("application/gm", binMarshaller());
		bean.registerMarshaller("gm/bin", binMarshaller());

		return bean;

	}

	@Managed
	private Marshaller binMarshaller() {
		Bin2Marshaller bean = new Bin2Marshaller();
		return bean;
	}

}
