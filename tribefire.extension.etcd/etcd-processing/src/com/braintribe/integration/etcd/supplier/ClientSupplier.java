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
package com.braintribe.integration.etcd.supplier;

import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.braintribe.common.lcd.Numbers;
import com.braintribe.logging.Logger;
import com.braintribe.util.network.NetworkTools;
import com.braintribe.utils.CommonTools;
import com.braintribe.utils.lcd.StringTools;

import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;

/**
 * {@link Supplier} to get a etcd client with or without authentication
 * 
 *
 */
public class ClientSupplier implements Supplier<Client> {

	private static final Logger logger = Logger.getLogger(ClientSupplier.class);

	public static final String ETCD_CLIENT_PASSWORD = "ETCD_CLIENT_PASSWORD";

	protected List<String> endpointUrls;
	protected Map<String, String> resolvedHosts;
	protected long nextHostResolving = -1L;
	protected String username;
	protected String password;

	public ClientSupplier(List<String> endpointUrls, String username, String password) {
		if (endpointUrls == null || endpointUrls.isEmpty()) {
			throw new IllegalArgumentException("The endpointUrls must not be null or empty.");
		}

		this.endpointUrls = endpointUrls;
		this.username = username;
		this.password = password;
	}

	@Override
	public Client get() {
		List<URI> endpointUris = endpointUrls.stream().map(u -> {
			try {
				return new URI(u);
			} catch (URISyntaxException e) {
				throw new RuntimeException(e);
			}
		}).collect(Collectors.toList());

		if (CommonTools.isEmpty(username) && CommonTools.isEmpty(password)) {
			Client client = Client.builder().endpoints(endpointUris).build();
			return client;
		} else if (!CommonTools.isEmpty(username) && CommonTools.isEmpty(password)) {
			// username set via connection - read password from the environment
			ByteSequence bsUsername = ByteSequence.from(username, StandardCharsets.UTF_8);
			password = System.getenv(ETCD_CLIENT_PASSWORD);
			if (CommonTools.isEmpty(password)) {
				throw new IllegalStateException("No password is set from the environment for endpointUrls: '"
						+ StringTools.createStringFromCollection(endpointUrls, ",") + "' username: '" + username + "'");
			}
			ByteSequence bsPassword = ByteSequence.from(password, StandardCharsets.UTF_8);

			Client client = Client.builder().endpoints(endpointUris).user(bsUsername).password(bsPassword).build();
			return client;
		} else {
			// username and password set via connection
			ByteSequence bsUsername = ByteSequence.from(username, StandardCharsets.UTF_8);
			ByteSequence bsPassword = ByteSequence.from(password, StandardCharsets.UTF_8);

			Client client = Client.builder().endpoints(endpointUris).user(bsUsername).password(bsPassword).build();
			return client;
		}
	}

	@Override
	public String toString() {

		long now = System.currentTimeMillis();
		if (endpointUrls != null && !endpointUrls.isEmpty() && (resolvedHosts == null || now > nextHostResolving)) {
			nextHostResolving = now + Numbers.MILLISECONDS_PER_MINUTE * 5;
			Map<String, String> map = new LinkedHashMap<>();
			for (String url : endpointUrls) {
				try {
					URI uri = new URI(url);
					String host = uri.getHost();
					if (!map.containsKey(host)) {
						List<InetAddress> addresses = NetworkTools.resolveHostname(host, Duration.ofSeconds(5));
						String addressesString = addresses.stream().map(a -> a.getHostAddress()).collect(Collectors.joining(","));
						map.put(host, addressesString);
					}

				} catch (Exception e) {
					logger.debug(() -> "Error while trying to resolve host from URL " + url, e);
				}
			}

			resolvedHosts = map;
		}

		StringBuilder sb = new StringBuilder();
		if (endpointUrls == null || endpointUrls.isEmpty()) {
			sb.append("No URLs specified.");
		} else {
			sb.append(StringTools.join(", ", endpointUrls));
			if (resolvedHosts != null && !resolvedHosts.isEmpty()) {
				sb.append("; DNS resolution: ");
				String resolutionInformation = resolvedHosts.entrySet().stream().map(e -> e.getKey() + "=" + e.getValue())
						.collect(Collectors.joining(", "));
				sb.append(resolutionInformation);
			}
		}

		return sb.toString();
	}

}
