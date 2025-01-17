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
package tribefire.extension.messaging.integration.test.connector;

import static org.junit.Assert.assertFalse;
import static tribefire.extension.messaging.integration.test.StaticTestVariables.KAFKA_URL;
import static tribefire.extension.messaging.integration.test.StaticTestVariables.PULSAR_SERVICE_URL;
import static tribefire.extension.messaging.integration.test.StaticTestVariables.PULSAR_URL;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Ignore;
import org.junit.Test;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;

import tribefire.extension.messaging.connector.api.AbstractConsumerMessagingConnector;
import tribefire.extension.messaging.connector.api.AbstractProducerMessagingConnector;
import tribefire.extension.messaging.connector.kafka.KafkaConsumerMessagingConnectorImpl;
import tribefire.extension.messaging.connector.kafka.KafkaProducerMessagingConnectorImpl;
import tribefire.extension.messaging.connector.pulsar.PulsarConsumerMessagingConnectorImpl;
import tribefire.extension.messaging.connector.pulsar.PulsarProducerMessagingConnectorImpl;
import tribefire.extension.messaging.model.Message;
import tribefire.extension.messaging.model.ResourceBinaryPersistence;
import tribefire.extension.messaging.model.deployment.event.EventEndpoint;
import tribefire.extension.messaging.model.deployment.event.EventEndpointConfiguration;
import tribefire.extension.messaging.model.deployment.event.KafkaEndpoint;
import tribefire.extension.messaging.model.deployment.event.PulsarEndpoint;
import tribefire.extension.messaging.service.test.model.TestGetObjectRequest;

public class ConnectorTest {
	private static final String TEST_CONFIG = "TEST_CONFIG";
	private static final String TEST_ENDPT = "TEST_ENDPT";
	private static final String TEST_REQUEST = "TEST_REQUEST";

	@Test
	@Ignore
	public void kafkaConnectorTest() {
		performConnectorTest("kafka", KafkaEndpoint.T);
	}

	@Test
	@Ignore
	public void pulsarConnectorTest() {
		performConnectorTest("pulsar", PulsarEndpoint.T);
	}

	// -----------------------------------------------------------------------
	// HELPERS
	// -----------------------------------------------------------------------

	private <T extends EventEndpoint, P extends AbstractProducerMessagingConnector, C extends AbstractConsumerMessagingConnector> void performConnectorTest(
			String testedConnector, EntityType<T> connectorType) {
		EventEndpointConfiguration config = getEndpointConfig(connectorType);
		P producer;
		C consumer;
		if (connectorType.getShortName().equals(KafkaEndpoint.T.getShortName())) {
			producer = (P) new KafkaProducerMessagingConnectorImpl(config, null);
			consumer = (C) new KafkaConsumerMessagingConnectorImpl(config);
		} else if (connectorType.getShortName().equals(PulsarEndpoint.T.getShortName())) {
			producer = (P) new PulsarProducerMessagingConnectorImpl(config, ClassLoader.getPlatformClassLoader());
			consumer = (C) new PulsarConsumerMessagingConnectorImpl(config, ClassLoader.getPlatformClassLoader());
		} else {
			throw new IllegalArgumentException("Unsupported connector type !!!");
		}

		List<Message> msgs = List.of(getMessage(config));
		producer.sendMessage(msgs, null, null);

		List<Message> consumedMsgs = consumer.consumeMessages();

		producer.destroy();
		consumer.finalizeConsume();
		assertFalse("Could not consume messages from " + testedConnector + " !!!", consumedMsgs.isEmpty());
	}

	private Message getMessage(EventEndpointConfiguration config) {
		Message message = Message.T.create();
		message.setId("ID");
		message.setTopics(config.getTopics());
		message.setResourceBinaryPersistence(ResourceBinaryPersistence.NONE);
		message.setContext("TEST");
		message.setValues(getMsgValueMap());
		return message;
	}

	private Map<String, GenericEntity> getMsgValueMap() {
		TestGetObjectRequest request = TestGetObjectRequest.T.create();
		request.setId(TEST_REQUEST);
		request.setGlobalId(TEST_REQUEST);
		request.setRelatedObjId("!!! TEST_DATA_HERE !!!");
		return Map.of("request", request);
	}

	private <T extends EventEndpoint> EventEndpointConfiguration getEndpointConfig(EntityType<T> type) {
		EventEndpointConfiguration configuration = EventEndpointConfiguration.T.create();
		configuration.setId(TEST_CONFIG);
		configuration.setTopics(Set.of("test"));
		configuration.setGlobalId(TEST_CONFIG);
		configuration.setId(TEST_CONFIG);
		configuration.setEventEndpoint(getEndpt(type));
		return configuration;
	}

	private <T extends EventEndpoint> T getEndpt(EntityType<T> type) {
		T endpt = type.create();
		endpt.setId(TEST_ENDPT);
		endpt.setGlobalId(TEST_ENDPT);
		endpt.setName(TEST_ENDPT);
		String connectionUrl;
		if (endpt instanceof PulsarEndpoint p) {
			p.setAdminUrl(PULSAR_SERVICE_URL);
			connectionUrl = PULSAR_URL;
		} else {
			connectionUrl = KAFKA_URL;
		}
		endpt.setConnectionUrl(connectionUrl);
		return endpt;
	}
}
