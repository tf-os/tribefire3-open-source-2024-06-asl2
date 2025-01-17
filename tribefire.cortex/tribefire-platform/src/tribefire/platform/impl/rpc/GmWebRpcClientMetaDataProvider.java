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
package tribefire.platform.impl.rpc;

import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import com.braintribe.cfg.Configurable;
import com.braintribe.cfg.Required;
import com.braintribe.logging.Logger;
import com.braintribe.logging.ndc.mbean.NestedDiagnosticContext;
import com.braintribe.model.processing.bootstrapping.TribefireRuntime;
import com.braintribe.model.processing.rpc.commons.api.RpcConstants;


public class GmWebRpcClientMetaDataProvider implements Supplier<Map<String, Object>> {
	
	private final static Logger logger = Logger.getLogger(GmWebRpcClientMetaDataProvider.class);
	
	protected boolean includeNdc = true;
	protected boolean includeThreadName = true;
	protected boolean includeNodeId = true;

	private Supplier<String> sessionIdProvider;
	
	@Required @Configurable
	public void setSessionIdProvider(Supplier<String> sessionIdProvider) {
		this.sessionIdProvider = sessionIdProvider;
	}
	
	@Override
	public Map<String, Object> get() throws RuntimeException {
		String sessionId = sessionIdProvider.get();
		Map<String, Object> metaData = new HashMap<String, Object>();
		metaData.put(RpcConstants.RPC_META_SESSIONID, sessionId);
		
		boolean trace = logger.isTraceEnabled();
		
		String threadName = null;
		if (this.includeThreadName) {
			try {
				threadName = Thread.currentThread().getName();
				if (threadName == null) {
					threadName = "unknown";
				}
				metaData.put(RpcConstants.RPC_META_THREADNAME, threadName);
			} catch(Exception e) {
				if (trace) logger.trace("Could not add thread name to meta data.", e);
			}
		}
		if (this.includeNdc) {
			try {
				Deque<String> ndc = NestedDiagnosticContext.getNdc();
				if (ndc != null && !ndc.isEmpty()) {
					boolean addNdc = true;
					if (ndc.size() == 1) {
						String ndcEntry = ndc.getFirst();
						if (ndcEntry != null && threadName.equals(ndcEntry)) {
							//The NDC just contains the thread name. No sense in sending it twice.
							addNdc = false;
						}
					}
					if (addNdc) {
						metaData.put(RpcConstants.RPC_META_NDC, ndc.toString());
					}
				}
			} catch(Exception e) {
				if (trace) logger.trace("Could not add NDC to meta data.", e);
			}
		}
		if (this.includeNodeId) {
			try {
				String nodeId = TribefireRuntime.getProperty(TribefireRuntime.ENVIRONMENT_NODE_ID);
				if (nodeId != null) {
					metaData.put(RpcConstants.RPC_META_NODEID, nodeId);
				}
			} catch(Exception e) {
				if (trace) logger.trace("Could not add the node Id to meta data.", e);
			}
		}

		return metaData;
	}
	
	@Configurable
	public void setIncludeNdc(boolean includeNdc) {
		this.includeNdc = includeNdc;
	}
	@Configurable
	public void setIncludeThreadName(boolean includeThreadName) {
		this.includeThreadName = includeThreadName;
	}
	@Configurable
	public void setIncludeNodeId(boolean includeNodeId) {
		this.includeNodeId = includeNodeId;
	}

}

