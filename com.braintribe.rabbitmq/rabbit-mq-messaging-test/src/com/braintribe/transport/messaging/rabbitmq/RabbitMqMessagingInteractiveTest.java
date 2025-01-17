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
package com.braintribe.transport.messaging.rabbitmq;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.braintribe.model.messaging.Destination;
import com.braintribe.model.messaging.Message;
import com.braintribe.model.messaging.Queue;
import com.braintribe.testing.category.SpecialEnvironment;
import com.braintribe.testing.category.VerySlow;
import com.braintribe.transport.messaging.api.MessageConsumer;
import com.braintribe.transport.messaging.api.MessageProducer;
import com.braintribe.transport.messaging.api.MessagingConnection;
import com.braintribe.transport.messaging.api.MessagingConnectionProvider;
import com.braintribe.transport.messaging.api.MessagingContext;
import com.braintribe.transport.messaging.api.MessagingSession;
import com.braintribe.transport.messaging.api.test.GmMessagingTest;

@Category(SpecialEnvironment.class)
public class RabbitMqMessagingInteractiveTest extends GmMessagingTest {
	
	@Override
	protected MessagingConnectionProvider<? extends MessagingConnection> getMessagingConnectionProvider() {
		return RabbitMqMessagingConnectionProvider.instance.get();
	}

	@Override
	protected MessagingContext getMessagingContext() {
		return RabbitMqMessagingConnectionProvider.instance.getMessagingContext();
	}
	
	@Category(VerySlow.class)
	@Test
	public void testConnectionAutoRecovery() throws Exception {
		
		MessagingConnectionProvider<? extends MessagingConnection> connectionProvider =  getMessagingConnectionProvider();
		MessagingConnection connection = connectionProvider.provideMessagingConnection();
		MessagingSession session = connection.createMessagingSession();
		session.open();

		Destination queue = createDestination(Queue.class, session, getMethodName());

		Message outgoingMessage = createMessage(session, true, 120000);
		
		MessageProducer producer = session.createMessageProducer(queue);
		producer.sendMessage(outgoingMessage);

		System.out.println("Please restart the messaging server/engine at this point an press any key when done.");
		//System.in.read();
		Thread.sleep(60000);
		System.out.println("Continuing...");
		
		MessageConsumer consumer = session.createMessageConsumer(queue);
		
		Message incomingMessage = consumer.receive();
		
		assertEquals(outgoingMessage, incomingMessage);
		
		session.close();
		connection.close();
		
	}
	

}
