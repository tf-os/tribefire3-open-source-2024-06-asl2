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
package com.braintribe.web.servlet.deploymentreflection;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;

import com.braintribe.cartridge.common.api.topology.LiveInstances;
import com.braintribe.model.deploymentreflection.request.WireKind;
import com.braintribe.utils.StringTools;

/**
 * Class providing utility methods used by the {@link DeploymentReflectionServlet}.
 * @author christina.wilpernig
 */
public class DeploymentReflectionServletUtils {

	protected LiveInstances liveInstances;

	public DeploymentReflectionServletUtils(LiveInstances liveInstances) {
		super();
		this.liveInstances = liveInstances;
	}

	public Set<String> getCartridgeIds() {
		Set<String> cartridgeIds = new TreeSet<>();
		liveInstances.liveInstances().stream().forEach(instance -> {
			String[] split = instance.split("@");
			cartridgeIds.add(split[0]);
		});
		return cartridgeIds;
	}

	public Set<String> getNodeIds() {
		Set<String> nodeIds = new TreeSet<>();
		liveInstances.liveInstances().stream().forEach(instance -> {
			String[] split = instance.split("@");
			nodeIds.add(split[1]);
		});
		return nodeIds;
	}

	public List<String> getWireKinds() {
		List<String> wireKinds = new ArrayList<String>();
		for (WireKind k : WireKind.values()) {
			wireKinds.add(k.toString());
		}
		return wireKinds;
	}

	public static Boolean getSingleParameterAsBoolean(HttpServletRequest req, String key) {
		Map<String, String[]> parameters = req.getParameterMap();
		if (parameters == null || parameters.isEmpty()) {
			return null;
		}
		String[] values = parameters.get(key);
		if (values == null || values.length == 0) {
			return null;
		}
		return Boolean.parseBoolean(values[0]);
	}

	public static String getSingleParameterAsString(HttpServletRequest req, String key) {
		Map<String, String[]> parameters = req.getParameterMap();
		if (parameters == null || parameters.isEmpty()) {
			return null;
		}
		String[] values = parameters.get(key);
		if (values == null || values.length == 0) {
			return null;
		}
		return values[0];
	}
	
	public static String getParameterMapAsString(HttpServletRequest req) {
		StringBuilder result = new StringBuilder();
		Map<String, String[]> parameterMap = req.getParameterMap();
		if (parameterMap != null) {
			boolean first = true;
			for (Map.Entry<String,String[]> entry : parameterMap.entrySet()) {
				String key = entry.getKey();
				String[] values = entry.getValue();
				String valuesAsString = StringTools.createStringFromArray(values);
				if (!first) {
					result.append(", ");
				} else {
					first = false;
				}
				result.append(key != null ? key : "null");
				result.append("=");
				result.append(valuesAsString != null ? valuesAsString : "null");
			}
		}
		return result.toString();
	}

}
