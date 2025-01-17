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
package com.braintribe.transport.messaging.dbm;

import java.util.UUID;

import com.braintribe.logging.Logger;
import com.braintribe.model.messaging.Destination;
import com.braintribe.model.messaging.Message;
import com.braintribe.transport.messaging.api.MessageProducer;
import com.braintribe.transport.messaging.api.MessagingException;

/**
 * <p>
 * {@link MessageProducer} implementation for {@link GmDmbMqMessaging}.
 * 
 * @see MessageProducer
 */
public class GmDmbMqMessageProducer extends GmDmbMqMessageHandler implements MessageProducer {

	private static final Long defaultTimeToLive = Long.valueOf(0L);
	private static final Integer defaultPriority = Integer.valueOf(5);

	private Long timeToLive = defaultTimeToLive;
	private Integer priority = defaultPriority;

	private static final Logger log = Logger.getLogger(GmDmbMqMessageProducer.class);

	public GmDmbMqMessageProducer() {
		super();
	}

	@Override
	public void sendMessage(Message message) throws MessagingException {

		if (getDestination() == null) {
			throw new UnsupportedOperationException(
					"This method is not supported as no destination was assigned to the message producer at creation time");
		}

		sendMessage(message, getDestination());
	}

	@Override
	public void sendMessage(Message message, Destination destination) throws MessagingException {

		if (message == null) {
			throw new IllegalArgumentException("message cannot be null");
		}

		if (destination == null) {
			throw new IllegalArgumentException("destination cannot be null");
		}

		message.setMessageId(UUID.randomUUID().toString());
		message.setDestination(destination);

		if (message.getPriority() == null) {
			message.setPriority(priority);
		} else {
			message.setPriority(normalizePriority(message.getPriority()));
		}

		if (message.getTimeToLive() == null) {
			message.setTimeToLive(timeToLive);
		}

		if (message.getTimeToLive() > 0L) {
			message.setExpiration(System.currentTimeMillis() + message.getTimeToLive());
		} else {
			message.setExpiration(0L);
		}

		GmDmbMqSession session = getSession();

		session.getMessagingContext().enrichOutbound(message);

		byte[] messageBytes = session.getMessagingContext().marshallMessage(message);

		char destinationType = getDestinationType(message.getDestination());

		session.getConnection().getMessagingMBean().sendMessage(destinationType, message.getDestination().getName(), message.getMessageId(),
				messageBytes, message.getPriority(), message.getExpiration(), message.getHeaders(), message.getProperties());

		log.trace(() -> "Successfully notified [ " + message + " ] to MessageMBean");

	}

	@Override
	public void close() throws MessagingException {
		log.trace(() -> "No-op close() for this implementation");
	}

	@Override
	public Long getTimeToLive() {
		return timeToLive;
	}

	@Override
	public void setTimeToLive(Long timeToLive) {
		if (timeToLive == null) {
			this.timeToLive = defaultTimeToLive;
		} else {
			this.timeToLive = timeToLive;
		}
	}

	@Override
	public Integer getPriority() {
		return priority;
	}

	@Override
	public void setPriority(Integer priority) {
		if (priority == null) {
			this.priority = defaultPriority;
		} else {
			this.priority = normalizePriority(priority);
		}
	}

	/**
	 * <p>
	 * Ensures that the given not-null priority fits the 0-9 range.
	 * 
	 * @param priorityCandidate
	 *            The priority to be normalized
	 * @return A priority between 0 and 9, or {@code null} if {@code null} was given.
	 */
	protected Integer normalizePriority(Integer priorityCandidate) {
		if (priorityCandidate == null) {
			return null;
		} else if (priorityCandidate < 0) {
			return 0;
		} else if (priorityCandidate > 9) {
			return 9;
		} else {
			return priorityCandidate;
		}
	}

}
