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
package tribefire.extension.messaging.connector.kafka;

import static java.lang.String.format;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.KafkaException;

import com.braintribe.gm.model.reason.Reasons;
import com.braintribe.gm.model.reason.UnsatisfiedMaybeTunneling;
import com.braintribe.logging.Logger;
import com.braintribe.model.check.service.CheckResultEntry;

import tribefire.extension.messaging.connector.api.AbstractProducerMessagingConnector;
import tribefire.extension.messaging.model.deployment.connector.properties.KafkaProperties;
import tribefire.extension.messaging.model.deployment.event.EventEndpoint;
import tribefire.extension.messaging.model.deployment.event.EventEndpointConfiguration;
import tribefire.extension.messaging.service.reason.connection.KafkaConnectionError;
import tribefire.extension.messaging.service.reason.validation.ArgumentNotSatisfied;

/**
 */
public class KafkaProducerMessagingConnectorImpl extends AbstractProducerMessagingConnector {
	private static final Logger logger = Logger.getLogger(KafkaProducerMessagingConnectorImpl.class);

	private final Producer<String, byte[]> producer;
	private final String logIdString;
	private final Properties properties;

	public KafkaProducerMessagingConnectorImpl(EventEndpointConfiguration destinationConfig, String binaryPersistenceExternalId) {
		if (destinationConfig == null) {
			throw new UnsatisfiedMaybeTunneling(Reasons.build(ArgumentNotSatisfied.T)
					.text(format("Empty configuration is provided for %s!", "PulsarProducerConnector")).toMaybe());
		}
		this.setBinaryPersistenceExternalId(binaryPersistenceExternalId);
		EventEndpoint eventEndpoint = destinationConfig.getEventEndpoint();
		KafkaProperties kafkaProperties = KafkaProperties.T.create();
		kafkaProperties.setServiceUrls(List.of(eventEndpoint.getConnectionUrl()));
		this.properties = kafkaProperties.getKafkaProperties(eventEndpoint.getGlobalId());
		this.logIdString = "[" + eventEndpoint.getGlobalId() + "] ";

		try {
			this.producer = new KafkaProducer<>(properties);
		} catch (KafkaException e) {
			throw new UnsatisfiedMaybeTunneling(Reasons.build(KafkaConnectionError.T).text(e.getMessage() + " " + e.getCause())
					.enrich(r -> r.addThrowableInformation(e)).toMaybe());
		}
	}

	// -----------------------------------------------------------------------
	// LifecycleAware
	// -----------------------------------------------------------------------

	@Override
	public void destroy() {
		this.producer.close();
	}

	// -----------------------------------------------------------------------
	// PRODUCE
	// -----------------------------------------------------------------------

	@Override
	protected void deliverMessageString(byte[] message, Set<String> topics) {
		logger.info(() -> logIdString + "Sending message to topics: '" + topics + "'...");

		topics.forEach(topic -> {
			ProducerRecord<String, byte[]> kafkaMsg = new ProducerRecord<>(topic, message);
			try {
				Future<RecordMetadata> future = producer.send(kafkaMsg);
				RecordMetadata recordMetadata = future.get();

				logger.info(() -> logIdString + "Sent message to topic: '" + topic + "'!");

			} catch (KafkaException | InterruptedException | ExecutionException e) {
				throw new UnsatisfiedMaybeTunneling(Reasons.build(KafkaConnectionError.T)
						.text("Error in delivering message to kafka : '" + Arrays.toString(message) + "' with topic: '" + topic + "'")
						.enrich(r -> r.addThrowableInformation(e)).toMaybe());
			}
		});
	}

	// -----------------------------------------------------------------------
	// HEALTH
	// -----------------------------------------------------------------------

	@Override
	public CheckResultEntry actualHealth() {
		HashSet<String> metricsNames = new HashSet<>(Arrays.asList("record-send-total", "outgoing-byte-total"));
		KafkaServerHealthChecker checker = new KafkaServerHealthChecker(this.properties, metricsNames, producer::metrics);
		return checker.checkServer("Producer");
	}

	// -----------------------------------------------------------------------
	// GETTER & SETTER
	// -----------------------------------------------------------------------

	@Override
	public String getExternalId() {
		return this.properties.getProperty("client.id");
	}

}
