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
package tribefire.platform.impl.web;

import java.io.IOException;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.BeforeClass;
import org.junit.Test;

import com.braintribe.common.attribute.AttributeContext;
import com.braintribe.common.attribute.AttributeContextBuilder;
import com.braintribe.model.extensiondeployment.AuthorizableWebTerminal;
import com.braintribe.model.extensiondeployment.AuthorizedWebTerminal;
import com.braintribe.model.extensiondeployment.WebTerminal;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.processing.service.common.context.UserSessionAspect;
import com.braintribe.model.usersession.UserSession;
import com.braintribe.testing.junit.assertions.assertj.core.api.Assertions;
import com.braintribe.utils.collection.impl.AttributeContexts;
import com.braintribe.utils.lcd.CollectionTools2;

public class ComponentDispatcherServletTest {

	@SuppressWarnings("serial")
	private static class MockServlet extends HttpServlet {
		@Override
		protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
			resp.setStatus(HttpServletResponse.SC_OK);
		}
	}

	private static ComponentDispatcherServlet dispatcherServlet;

	@BeforeClass
	public static void initialize() {
		WebTerminal unauthorizedTerminal = createTerminal(WebTerminal.T, "unauthorized");
		WebTerminal roledAuthorizableTerminal = createRoledTerminal(AuthorizableWebTerminal.T, "roled-authorizable",
				CollectionTools2.asSet("tf-admin"));
		WebTerminal authorizableTerminal = createTerminal(AuthorizableWebTerminal.T, "authorizable");
		WebTerminal roledAuthorizedTerminal = createRoledTerminal(AuthorizedWebTerminal.T, "roled-authorized", CollectionTools2.asSet("tf-admin"));
		WebTerminal authorizedTerminal = createTerminal(AuthorizedWebTerminal.T, "authorized");

		MockServlet servlet = new MockServlet();

		dispatcherServlet = new ComponentDispatcherServlet();

		dispatcherServlet.registerAndInitServlet(unauthorizedTerminal, servlet);
		dispatcherServlet.registerAndInitServlet(roledAuthorizedTerminal, servlet);
		dispatcherServlet.registerAndInitServlet(roledAuthorizableTerminal, servlet);
		dispatcherServlet.registerAndInitServlet(authorizedTerminal, servlet);
		dispatcherServlet.registerAndInitServlet(authorizableTerminal, servlet);
	}

	private static <T extends AuthorizableWebTerminal> T createRoledTerminal(EntityType<T> type, String id, Set<String> roles) {
		T terminal = createTerminal(type, id);
		terminal.setRoles(roles);
		return terminal;
	}

	private static <T extends WebTerminal> T createTerminal(EntityType<T> type, String id) {

		T terminal = type.create();
		terminal.setExternalId(id);
		terminal.setPathIdentifier(id);

		return terminal;
	}

	@Test
	public void testAuthorizedUnauthorized() throws Exception {
		testGeneric("authorized", false, false, HttpServletResponse.SC_UNAUTHORIZED);
	}

	@Test
	public void testAuthorizedAuthorized() throws Exception {
		testGeneric("authorized", true, false, HttpServletResponse.SC_OK);
	}

	@Test
	public void testRoleAuthorizedForbidden() throws Exception {
		testGeneric("roled-authorized", true, false, HttpServletResponse.SC_FORBIDDEN);
	}

	@Test
	public void testRoleAuthorizedAuthorized() throws Exception {
		testGeneric("roled-authorized", true, true, HttpServletResponse.SC_OK);
	}

	@Test
	public void testAuthorizableUnauthorized() throws Exception {
		testGeneric("authorizable", true, false, HttpServletResponse.SC_OK);
	}

	@Test
	public void testAuthorizableAuthorized() throws Exception {
		testGeneric("authorizable", true, true, HttpServletResponse.SC_OK);
	}

	@Test
	public void testRoleAuthorizableUnauthorized() throws Exception {
		testGeneric("roled-authorizable", false, false, HttpServletResponse.SC_OK);
	}

	@Test
	public void testRoleAuthorizableForbidden() throws Exception {
		testGeneric("roled-authorizable", true, false, HttpServletResponse.SC_FORBIDDEN);
	}

	@Test
	public void testRoleAuthorizableAuthorized() throws Exception {
		testGeneric("roled-authorizable", true, true, HttpServletResponse.SC_OK);
	}

	private void testGeneric(String path, boolean generateUserSession, boolean role, int expectedCode) throws Exception {

		AttributeContextBuilder contextBuilder = AttributeContexts.derivePeek();

		if (generateUserSession) {
			UserSession userSession = UserSession.T.create();
			if (role)
				userSession.getEffectiveRoles().add("tf-admin");

			contextBuilder.set(UserSessionAspect.class, userSession);
		}

		AttributeContext attributeContext = contextBuilder.build();

		AttributeContexts.push(attributeContext);

		try {
			MockHttpServletRequest request = new MockHttpServletRequest();
			request.setPathInfo(path);
			MockHttpServletResponse response = new MockHttpServletResponse();
			response.setStatus(HttpServletResponse.SC_OK);

			dispatcherServlet.service(request, response);

			Assertions.assertThat(response.getStatus()).isEqualTo(expectedCode);
		} finally {
			AttributeContexts.pop();
		}
	}

}
