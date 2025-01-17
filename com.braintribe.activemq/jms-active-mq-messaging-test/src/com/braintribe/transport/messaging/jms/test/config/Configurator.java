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
package com.braintribe.transport.messaging.jms.test.config;

import java.net.URI;

import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.TransportConnector;
import org.apache.activemq.broker.jmx.ManagementContext;

public class Configurator {

	private static TestConfiguration tc = new TestConfiguration();
	private static BrokerService broker;

	public Configurator() throws Exception {
		this.initialize();
	}

	public void initialize() throws Exception {
		tc.setProviderURL("tcp://localhost:61636?soTimeout=5000&daemon=true");
		startBroker();
	}

	private void startBroker() throws Exception {
		TransportConnector connector = new TransportConnector();
		connector.setUri(new URI("tcp://localhost:61636"));

		ManagementContext context = new ManagementContext();
		context.setBrokerName("defaultBroker");
		context.setConnectorHost("localhost");
		context.setConnectorPort(10099);
		context.setUseMBeanServer(true);
		context.setCreateConnector(true);

		broker = new BrokerService();
		broker.setBrokerId("defaultBroker");
		broker.setBrokerName("defaultBroker");
		broker.setUseJmx(true);
		broker.setPersistent(false);

		broker.addConnector(connector);
		broker.setManagementContext(context);
		broker.start();
	}

	public void close() {
		try {
			broker.stop();
		} catch (Exception e) {
			throw new RuntimeException("Broker couldn't stop.", e);
		}
	}

	public TestConfiguration getTestConfiguration() {
		return tc;
	}

}
