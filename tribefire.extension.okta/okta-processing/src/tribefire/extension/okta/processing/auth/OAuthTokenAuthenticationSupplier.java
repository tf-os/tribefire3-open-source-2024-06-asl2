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
package tribefire.extension.okta.processing.auth;

import java.security.PrivateKey;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import com.braintribe.cfg.Configurable;
import com.braintribe.cfg.Required;
import com.braintribe.model.generic.eval.Evaluator;
import com.braintribe.model.service.api.ServiceRequest;

import io.jsonwebtoken.Jwts;
import tribefire.extension.okta.api.model.auth.GetOauthAccessToken;
import tribefire.extension.okta.api.model.auth.HasAuthorization;
import tribefire.extension.okta.api.model.auth.OauthAccessToken;
import tribefire.extension.okta.model.OktaError;
import tribrefire.extension.okta.common.OktaCommons;

public class OAuthTokenAuthenticationSupplier implements AuthenticationSupplier, OktaCommons {

	private PrivateKey privateKey;

	private String clientId;
	private Duration expirationDuration = Duration.of(5L, ChronoUnit.MINUTES);
	private String audience;
	private Set<String> scopes = Set.of("okta.users.read", "okta.groups.read", "okta.apps.read");
	private Evaluator<ServiceRequest> evaluator;
	private String oktaDomainId = "default.access.okta";

	private String jwtToken;
	private String tokenType;
	private Instant validUntil;

	private ReentrantLock updateLock = new ReentrantLock();

	@Override
	public void authorizeRequest(HasAuthorization request) {
		updateToken(request);
		request.setAuthorization(tokenType + " " + jwtToken);
	}

	private void updateToken(HasAuthorization originalRequest) {
		Instant now = Instant.now();

		if (jwtToken != null && now.isBefore(validUntil)) {
			return;
		}

		updateLock.lock();
		try {
			if (jwtToken != null && now.isBefore(validUntil)) {
				return;
			}
			//@formatter:off
			String jwt = Jwts.builder()
			        .setAudience(audience)
			        .setIssuedAt(Date.from(now))
			        .setExpiration(Date.from(now.plus(expirationDuration)))
			        .setIssuer(clientId)
			        .setSubject(clientId)
			        .setId(UUID.randomUUID().toString())
			        .signWith(privateKey)
			        .compact();
			//@formatter:on

			GetOauthAccessToken request = GetOauthAccessToken.T.create();
			request.setClientAssertion(jwt);
			request.setClientAssertionType("urn:ietf:params:oauth:client-assertion-type:jwt-bearer");
			request.setScope(scopes.stream().collect(Collectors.joining(" ")));
			request.setGrantType("client_credentials");
			request.setDomainId(oktaDomainId);
			Object result = request.eval(evaluator).get();
			if (result instanceof OauthAccessToken oauthAccessToken) {
				jwtToken = oauthAccessToken.getAccess_token();
				Integer expires_in = oauthAccessToken.getExpires_in();
				if (expires_in != null) {
					validUntil = now.plus((long) (expires_in.doubleValue() * 0.9), ChronoUnit.SECONDS);
				}
				tokenType = oauthAccessToken.getToken_type();
			} else if (result instanceof OktaError error) {
				throw new IllegalStateException("Could not create an OAuth JWT token: " + error.errorMessage());
			} else {
				throw new IllegalStateException("Could not create an OAuth JWT token: " + result);
			}
		} finally {
			updateLock.unlock();
		}
	}

	// ***************************************************************************************************
	// Setters
	// ***************************************************************************************************

	@Configurable
	@Required
	public void setPrivateKey(PrivateKey privateKey) {
		this.privateKey = privateKey;
	}

	@Configurable
	@Required
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	@Configurable
	public void setExpirationDuration(Duration expirationDuration) {
		this.expirationDuration = expirationDuration;
	}

	@Configurable
	@Required
	public void setAudience(String audience) {
		this.audience = audience;
	}

	@Configurable
	public void setScopes(Set<String> scopes) {
		this.scopes = scopes;
	}

	@Configurable
	@Required
	public void setEvaluator(Evaluator<ServiceRequest> evaluator) {
		this.evaluator = evaluator;
	}

	@Configurable
	@Required
	public void setOktaDomainId(String oktaDomainId) {
		this.oktaDomainId = oktaDomainId;
	}

}
