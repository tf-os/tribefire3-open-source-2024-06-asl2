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
package com.braintribe.model.processing.websocket.server;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.braintribe.logging.Logger;
import com.braintribe.model.processing.session.GmSessionFactories;
import com.braintribe.model.processing.websocket.server.client.ClientSocket;

/**
 * Class used for integration testing of connection part of websocket server implemented in {@link GmWebSocketServer} class.
 * 
 */
//ignored until IT CI is ready
@Ignore
public class GmWebSocketServerConnectionTest extends AbstractGmWebSocketServerTest {

	private static Logger LOGGER = Logger.getLogger(GmWebSocketServerConnectionTest.class);
	
	private static String cortexSessionId;
	
	@BeforeClass
	public static void beforeClass() throws Exception {
		
		setup();
		
		cortexSessionId = GmSessionFactories.remote(instanceUri).authentication("cortex", "cortex").done().newSession("cortex")
				.getSessionAuthorization().getSessionId();
	}
	
	/**
	 * Used for testing connection to websocket server when session and client id are missing as query params. <br>
	 * Expected: client fails to connect.
	 */
	@Test
	public void testWsClientConnection_MissingSessionAndClientId() throws Exception {
		
		LOGGER.info("Testing connection to ws server - missing session and client id");
		
		final ClientSocket clientSocket = connectToWsServer(wsEndpointUri(instanceHost, instancePort, null, null, TYPE_GM_JSON), null);
		assertThatCode(() -> awaitWsConnectionProcedureToFinish(clientSocket, false)).doesNotThrowAnyException();
		assertThat(clientSocket.ping()).isFalse();
		
		LOGGER.info("Testing connection to ws server - missing session and client id -> succeeded with no errors");
	}
	
	/**
	 * Used for testing connection to websocket server when session id is missing as query param. <br>
	 * Expected: client fails to connect.
	 */
	@Test
	public void testWsClientConnection_MissingSessionId() throws Exception {
		
		LOGGER.info("Testing connection to ws server - missing session id");
		
		final ClientSocket clientSocket = connectToWsServer(wsEndpointUri(instanceHost, instancePort, null, "test_client_id", TYPE_GM_JSON), null);
		assertThatCode(() -> awaitWsConnectionProcedureToFinish(clientSocket, false)).doesNotThrowAnyException();
		assertThat(clientSocket.ping()).isFalse();
		
		LOGGER.info("Testing connection to ws server - missing session id -> succeeded with no errors");
	}
	
	/**
	 * Used for testing connection to websocket server when client id is missing as query param. <br>
	 * Expected: client fails to connect.
	 */
	@Test
	public void testWsClientConnection_MissingClientId() throws Exception {
		
		LOGGER.info("Testing connection to ws server - missing client id");
		
		final ClientSocket clientSocket = connectToWsServer(wsEndpointUri(instanceHost, instancePort, cortexSessionId, null, TYPE_GM_JSON), null);
		assertThatCode(() -> awaitWsConnectionProcedureToFinish(clientSocket, false)).doesNotThrowAnyException();
		assertThat(clientSocket.ping()).isFalse();
		
		LOGGER.info("Testing connection to ws server - missing client id -> succeeded with no errors");
	}
	
	/**
	 * Used for testing connection to websocket server when unsupported response type is present as value of accept query param. <br>
	 * Expected: client fails to connect.
	 */
	@Test
	public void testWsClientConnection_UnsupportedResponseType() throws Exception {
		
		LOGGER.info("Testing connection to ws server - unsupported response type");
		
		final ClientSocket clientSocket = connectToWsServer(wsEndpointUri(instanceHost, instancePort, cortexSessionId, "test_client_id", "unsupported_response_type"), null);
		assertThatCode(() -> awaitWsConnectionProcedureToFinish(clientSocket, false)).doesNotThrowAnyException();
		assertThat(clientSocket.ping()).isFalse();
		
		LOGGER.info("Testing connection to ws server - unsupported response type -> succeeded with no errors");
	}

	/**
	 * Used for testing connection to websocket server when all valid info is present as query params. <br>
	 * Expected: client connects successfully.
	 */
	@Test
	public void testWsClientConnection_ValidInfo() throws Exception {
		
		LOGGER.info("Testing connection to ws server - valid info");
		
		final ClientSocket clientSocket = connectToWsServer(wsEndpointUri(instanceHost, instancePort, cortexSessionId, "test_client_id", TYPE_GM_JSON), null);
		assertThatCode(() -> awaitWsConnectionProcedureToFinish(clientSocket, true)).doesNotThrowAnyException();
		assertThat(clientSocket.ping()).isTrue();
		
		LOGGER.info("Testing connection to ws server - valid info -> succeeded with no errors");
	}
	
	/**
	 * Used for testing closing of client connection to websocket server. <br>
	 * Expected: client disconnects successfully.
	 */
	@Test
	public void testWsClientConnectionClose() throws Exception {
		
		LOGGER.info("Testing closing of ws server connection");
		
		final ClientSocket clientSocket = connectToWsServer(wsEndpointUri(instanceHost, instancePort, cortexSessionId, "test_client_id", TYPE_GM_JSON), null);
		awaitWsConnectionProcedureToFinish(clientSocket, true);
		
		clientSocket.closeConnection();
		assertThatCode(() -> awaitWsConnectionProcedureToFinish(clientSocket, false)).doesNotThrowAnyException();
		assertThat(clientSocket.ping()).isFalse();
		
		LOGGER.info("Testing closing of ws server connection -> succeeded with no errors");
	}
	
	/**
	 * Used for testing closing of client connection to websocket server due to an error. <br>
	 * Expected: client disconnects successfully.
	 */
	@Test
	public void testWsClientConnectionClose_Error() throws Exception {
		
		LOGGER.info("Testing closing of ws server connection - error");
		
		final ClientSocket clientSocket = connectToWsServer(wsEndpointUri(instanceHost, instancePort, cortexSessionId, "test_client_id", TYPE_GM_JSON), null);
		awaitWsConnectionProcedureToFinish(clientSocket, true);
		
		clientSocket.harshlyCloseConnection();
		assertThatCode(() -> awaitWsConnectionProcedureToFinish(clientSocket, false)).doesNotThrowAnyException();
		assertThat(clientSocket.ping()).isFalse();
		
		LOGGER.info("Testing closing of ws server connection - error -> succeeded with no errors");
	}

}
