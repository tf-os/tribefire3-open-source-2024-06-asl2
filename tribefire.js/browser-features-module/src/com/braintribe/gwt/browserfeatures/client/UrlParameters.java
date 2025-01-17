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
package com.braintribe.gwt.browserfeatures.client;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Window;

/**
 * This class extracts and decodes url parameters from the javascript object window.location and makes it accessible
 * through methods.
 * 
 * @author dirk.scheffler
 *
 */
public class UrlParameters {
	private boolean assumePrefix = true;
	private static UrlParameters instance;
	private static UrlParameters hashInstance;
	private static final String META_PARAM_PREFIX = "requestMeta.";
	private static Map<String, Object> requestMetaParameters;

	public static UrlParameters getInstance() {
		if (instance == null)
			instance = new UrlParameters(Window.Location.getQueryString());

		return instance;
	}

	public static UrlParameters getHashInstance() {
		if (hashInstance == null)
			hashInstance = new UrlParameters(Window.Location.getQueryString(), Window.Location.getHash(), true);

		return hashInstance;
	}

	public static String getFailSafeParameter(String parameter) {
		if (getHashInstance().containsParameter(parameter))
			return getHashInstance().getParameter(parameter);
		
		if (getInstance().containsParameter(parameter))
			return getInstance().getParameter(parameter);
		
		return null;
	}
	
	public static String getFailSafeParameter(String parameter, String def) {
		if (getHashInstance().containsParameter(parameter))
			return getHashInstance().getParameter(parameter);
		
		if (getInstance().containsParameter(parameter))
			return getInstance().getParameter(parameter);
		
		return def;
	}

	public static Map<String, Object> getRequestMetaParameters() {
		if (requestMetaParameters == null) {
			requestMetaParameters = new HashMap<>();
			getHashInstance().getParameters().forEach((k, v) -> {
				if (k.startsWith(META_PARAM_PREFIX)) {
					// String key = k.substring(k.lastIndexOf(".") + 1);
					requestMetaParameters.put(k, v);
				}
			});
			getInstance().getParameters().forEach((k, v) -> {
				if (k.startsWith(META_PARAM_PREFIX)) {
					// String key = k.substring(k.lastIndexOf(".") + 1);
					requestMetaParameters.put(k, v);
				}
			});
		}
		return requestMetaParameters;
	}

	public static void reset() {
		instance = null;
		hashInstance = null;
		requestMetaParameters = null;
	}

	private Map<String, String> parameters = new HashMap<>();

	public UrlParameters(String queryString) {
		this(queryString, null, true);
	}

	public UrlParameters(String queryString, boolean assumePrefix) {
		this(queryString, null, assumePrefix);
	}

	public UrlParameters(String queryString, String hashString, boolean assumePrefix) {
		this.assumePrefix = assumePrefix;
		prepareMapForString(queryString);
		prepareMapForString(hashString);

		HashChangeEventDistributor.getInstance().addHashChangeListener(listener -> {
			reset();
		});
	}

	private void prepareMapForString(String string) {
		if (string == null || string.isEmpty())
			return;
		
		String qs = string.substring(assumePrefix ? 1 : 0);
		String[] kvPairs = qs.split("&");
		for (String kvPair : kvPairs) {
			int index = kvPair.indexOf('=');
			if (index != -1) {
				String encodedKey = kvPair.substring(0, index);
				String encodedValue = kvPair.substring(index + 1);
				String key = URL.decodeQueryString(encodedKey);
				String value = URL.decodeQueryString(encodedValue);
				parameters.put(key, value);
			} else {
				String key = URL.decodeQueryString(kvPair);
				parameters.put(key, "");
			}
		}
	}

	public Map<String, String> getParameters() {
		return parameters;
	}

	public boolean containsParameter(String name) {
		return parameters.containsKey(name);
	}

	public String getParameter(String name) {
		return getParameter(name, null);
	}

	public String getParameter(String name, String def) {
		return parameters.containsKey(name) ? parameters.get(name) : def;
	}
}
