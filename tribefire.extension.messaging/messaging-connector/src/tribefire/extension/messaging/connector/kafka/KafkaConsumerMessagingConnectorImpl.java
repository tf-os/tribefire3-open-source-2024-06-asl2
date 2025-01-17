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

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.TopicPartition;

import com.braintribe.gm.model.reason.Reasons;
import com.braintribe.gm.model.reason.UnsatisfiedMaybeTunneling;
import com.braintribe.logging.Logger;
import com.braintribe.model.check.service.CheckResultEntry;

import tribefire.extension.messaging.connector.api.AbstractConsumerMessagingConnector;
import tribefire.extension.messaging.model.deployment.connector.properties.KafkaProperties;
import tribefire.extension.messaging.model.deployment.event.EventEndpoint;
import tribefire.extension.messaging.model.deployment.event.EventEndpointConfiguration;
import tribefire.extension.messaging.service.reason.connection.KafkaConnectionError;
import tribefire.extension.messaging.service.reason.validation.ArgumentNotSatisfied;

/**
 */
public class KafkaConsumerMessagingConnectorImpl extends AbstractConsumerMessagingConnector {
	private static final Logger logger = Logger.getLogger(KafkaConsumerMessagingConnectorImpl.class);

	private Consumer<String, byte[]> kafkaConsumer;

	private final Properties properties;
	private final KafkaProperties kafkaProperties;
	private final String externalId;

	public KafkaConsumerMessagingConnectorImpl(EventEndpointConfiguration destinationConfig) {
		if (destinationConfig == null) {
			throw new UnsatisfiedMaybeTunneling(Reasons.build(ArgumentNotSatisfied.T)
					.text(format("Empty configuration is provided for %s!", "PulsarProducerConnector")).toMaybe());
		}
		EventEndpoint eventEndpoint = destinationConfig.getEventEndpoint();
		kafkaProperties = KafkaProperties.T.create();
		kafkaProperties.setServiceUrls(List.of(eventEndpoint.getConnectionUrl()));
		kafkaProperties.setTopicsToListen(destinationConfig.getTopics().stream().toList());
		this.properties = kafkaProperties.getKafkaProperties(eventEndpoint.getGlobalId());
		this.externalId = "[" + eventEndpoint.getGlobalId() + "] ";

		try {
			this.kafkaConsumer = new KafkaConsumer<>(properties);
			this.kafkaConsumer.subscribe(kafkaProperties.getTopicsToListen());
		} catch (KafkaException e) {
			throw new UnsatisfiedMaybeTunneling(Reasons.build(KafkaConnectionError.T).text(e.getMessage() + " " + e.getCause())
					.enrich(r -> r.addThrowableInformation(e)).toMaybe());
		}
	}

	// -----------------------------------------------------------------------
	// CONSUME
	// -----------------------------------------------------------------------

	@Override
	protected List<byte[]> consumerConsume() {
		ConsumerRecords<String, byte[]> poll;
		try {
			logger.info(() -> externalId + "Polling messages from kafka cluster...");
			poll = kafkaConsumer.poll(Duration.ofSeconds(kafkaProperties.getPollDuration()));
			List<byte[]> records = new ArrayList<>();
			poll.forEach(r -> records.add(r.value()));
			logger.info(() -> externalId + "Polled " + records.size() + " messages.");
			return records;
		} catch (KafkaException e) {
			throw new UnsatisfiedMaybeTunneling(
					Reasons.build(KafkaConnectionError.T).text(externalId + "Error in polling messages from kafka: " + e.getMessage())
							.enrich(r -> r.addThrowableInformation(e)).toMaybe());
		}
	}

	@Override
	public void finalizeConsume() {
		if (kafkaConsumer != null) {
			try {
				this.kafkaConsumer.unsubscribe();
				this.kafkaConsumer.close();
			} catch (KafkaException e) {
				logger.warn(() -> externalId + "Could not unsubscribe" + e.getMessage());
				throw new UnsatisfiedMaybeTunneling(
						Reasons.build(KafkaConnectionError.T).text("Error in closing").enrich(r -> r.addThrowableInformation(e)).toMaybe());
			}
		}
	}

	// -----------------------------------------------------------------------
	// HEALTH
	// -----------------------------------------------------------------------

	@Override
	public CheckResultEntry actualHealth() {
		Set<String> metricsNames = new HashSet<>(Arrays.asList("last-poll-seconds-ago", "records-consumed-total", "bytes-consumed-total"));
		KafkaServerHealthChecker checker = new KafkaServerHealthChecker(this.properties, metricsNames, kafkaConsumer::metrics);
		return checker.checkServer("Consumer");
	}

	// -----------------------------------------------------------------------
	// GETTER & SETTER
	// -----------------------------------------------------------------------

	@Override
	public String getExternalId() {
		return kafkaProperties.getGlobalId();
	}

	public void setOffset(Collection<TopicPartition> partitions) {
		ConsumerRecords<String, byte[]> poll = kafkaConsumer.poll(Duration.ofSeconds(kafkaProperties.getPollDuration()));
		logger.info("Loaded: " + poll.count() + "records, reset position to last record");
	}
}
