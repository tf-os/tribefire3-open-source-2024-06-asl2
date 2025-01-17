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
package tribefire.module.wire.contract;

import java.util.Objects;

import com.braintribe.model.deployment.tribefire.connector.LocalTribefireConnection;
import com.braintribe.model.deployment.tribefire.connector.RemoteTribefireConnection;
import com.braintribe.model.deployment.tribefire.connector.TribefireConnection;
import com.braintribe.model.processing.deployment.api.ExpertContext;
import com.braintribe.model.processing.session.api.persistence.PersistenceGmSessionFactory;
import com.braintribe.wire.api.space.WireSpace;

public interface TribefireConnectionsContract extends WireSpace {

	PersistenceGmSessionFactory localTribefireConnection(ExpertContext<LocalTribefireConnection> context);

	PersistenceGmSessionFactory remoteTribefireConnection(ExpertContext<RemoteTribefireConnection> context);

	PersistenceGmSessionFactory localTribefireConnection(LocalTribefireConnection connection);

	PersistenceGmSessionFactory remoteTribefireConnection(RemoteTribefireConnection context);

	default PersistenceGmSessionFactory tribefireConnection(TribefireConnection connection) {
		Objects.requireNonNull(connection, "The connection must not be null.");

		if (connection instanceof LocalTribefireConnection)
			return localTribefireConnection((LocalTribefireConnection) connection);

		else if (connection instanceof RemoteTribefireConnection)
			return remoteTribefireConnection((RemoteTribefireConnection) connection);

		else
			throw new IllegalStateException("Unsupported tribefire connection type: " + connection);
	}

}
