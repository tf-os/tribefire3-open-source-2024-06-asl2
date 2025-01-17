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
package com.braintribe.transport.messaging.jms;

import java.util.function.Supplier;

import com.braintribe.cfg.Configurable;
import com.braintribe.cfg.LifecycleAware;
import com.braintribe.logging.Logger;
import com.braintribe.transport.messaging.api.MessagingConnection;
import com.braintribe.transport.messaging.api.MessagingContext;
import com.braintribe.transport.messaging.api.MessagingException;
import com.braintribe.transport.messaging.api.MessagingSession;

/**
 * Abstract implementation of the {@link Supplier} interface that returns a {@link MessagingSession} object.
 * <br><br>
 * Any subclass has to implement the {@link #initialize()} method which in turn has to set the property
 * {@link AbstractJmsMessagingSessionProvider#messageConnection}.
 */
public abstract class AbstractJmsMessagingSessionProvider implements Supplier<MessagingSession>, LifecycleAware {

	private static final Logger logger = Logger.getLogger(AbstractJmsMessagingSessionProvider.class);

	protected MessagingConnection messageConnection;

	protected JmsConnectionProvider connectionProvider = null;
	protected com.braintribe.model.messaging.jms.JmsConnection connectionDenotation = null;
	protected MessagingContext messagingContext;
	protected boolean optionalConnection = false;

	@Configurable
	public void setConnectionDenotation(com.braintribe.model.messaging.jms.JmsConnection connectionDenotation) {
		this.connectionDenotation = connectionDenotation;
	}

	@Configurable
	public void setMessagingContext(MessagingContext messagingContext) {
		this.messagingContext = messagingContext;
	}

	@Configurable
	public void setOptionalConnection(boolean optionalConnection) {
		this.optionalConnection = optionalConnection;
	}

	@Override
	public void postConstruct() {
		initialize();
	}

	@Override
	public void preDestroy() {
		shutdown();
	}
	
	public abstract void initialize() throws MessagingException;

	public void shutdown() {
		if (messageConnection != null) {
			try {
				messageConnection.close();
			} catch(Exception e) {
				logger.error("Error while closing message connection.", e);
			}
		}
		if (this.connectionProvider != null) {
			try {
				this.connectionProvider.close();
			} catch(Exception e) {
				logger.error("Error while closing message connection provider.", e);
			}
		}
	}

	@Override
	public MessagingSession get() throws RuntimeException {

		if (messageConnection == null) {
			return null;
		}

		try {
			return messageConnection.createMessagingSession();
		} catch (MessagingException e) {
			throw new RuntimeException("error while creating messaging session", e);
		}
	}

	protected void initializeMessagingConnection() throws MessagingException {

		try {
			messageConnection = connectionProvider.provideMessagingConnection();
		} catch (MessagingException e) {
			if (optionalConnection) {
				if (logger.isWarnEnabled()) {
					String msg = "Unable to establish a messaging connection. Messaging sessions won't be provided";
					logger.warn(msg);
					logger.debug(msg, e);
				}
				return;
			} else {
				throw e;
			}
		}
	
	}


}
