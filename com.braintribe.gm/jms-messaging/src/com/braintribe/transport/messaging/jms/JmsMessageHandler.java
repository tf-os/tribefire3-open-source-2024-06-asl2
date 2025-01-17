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

import com.braintribe.model.messaging.Destination;

public class JmsMessageHandler {

	private JmsSession session;
	private Destination destination;
	private javax.jms.Destination jmsDestination;
	
	public JmsSession getSession() {
		return session;
	}
	public void setSession(JmsSession session) {
		this.session = session;
	}
	public Destination getDestination() {
		return destination;
	}
	public void setDestination(Destination destination) {
		this.destination = destination;
	}
	public javax.jms.Destination getJmsDestination() {
		return jmsDestination;
	}
	public void setJmsDestination(javax.jms.Destination jmsDestination) {
		this.jmsDestination = jmsDestination;
	}

}
