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
package tribefire.extension.messaging.model.deployment.service;

import com.braintribe.model.extensiondeployment.check.CheckProcessor;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

public interface HealthCheckProcessor extends CheckProcessor {

	EntityType<HealthCheckProcessor> T = EntityTypes.T(HealthCheckProcessor.class);

	String messagingConnector = "messagingConnector";

	/*@Mandatory TODO Would probably not be further used @dmiex check when testing HealthCheck task: https://document-one.atlassian.net/browse/D1-3311
	@Name("Messaging Connector")
	@Description("Messaging Connector as low level connection to the tracer")
    MessagingConnectorDeployable getMessagingConnector();
	void setMessagingConnector(MessagingConnectorDeployable messagingConnectorDeployable);*/

}