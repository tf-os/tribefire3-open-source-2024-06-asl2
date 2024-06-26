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

import org.junit.BeforeClass;
import org.junit.experimental.categories.Category;

import com.braintribe.testing.category.SpecialEnvironment;
import com.braintribe.transport.messaging.api.MessagingConnection;
import com.braintribe.transport.messaging.api.MessagingConnectionProvider;
import com.braintribe.transport.messaging.api.MessagingContext;
import com.braintribe.transport.messaging.api.test.GmMessagingHeavyDistributionTest;

@Category(SpecialEnvironment.class)
public class RabbitMqMessagingHeavyDistributionTest extends GmMessagingHeavyDistributionTest {

	@Override
	protected MessagingConnectionProvider<? extends MessagingConnection> getMessagingConnectionProvider() {
		return RabbitMqMessagingConnectionProvider.instance.get();
	}

	@Override
	protected MessagingContext getMessagingContext() {
		return RabbitMqMessagingConnectionProvider.instance.getMessagingContext();
	}

	@BeforeClass
	public static void configure() {
		// Overriding test parameters
		maxConcurrentTests = 10;
		messages = new int[] { 1, 4, 8 };
		sessionGroup = 2;
		messageGroupForDestination = 4;
		messageGroupForProducer = 2;
		messageGroupForConsumer = 2;
	}

}