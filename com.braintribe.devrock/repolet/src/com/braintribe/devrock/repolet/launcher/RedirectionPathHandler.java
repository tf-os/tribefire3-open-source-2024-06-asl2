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
package com.braintribe.devrock.repolet.launcher;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.braintribe.cfg.Configurable;
import com.braintribe.cfg.Required;
import com.braintribe.devrock.repolet.Repolet;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;

/**
 * simple redirector, i.e. is configured via a {@link Map} of Prefix to {@link Repolet}, and redirects the calls to the appropriate Repolet 
 * @author pit
 *
 */
public class RedirectionPathHandler implements HttpHandler {

	private Map<String, Repolet> contextToHandlerMap;
	private List<String> keyList;
	private boolean useNewNameDetection = true;
	
	@Configurable @Required
	public void setContextToHandlerMap(Map<String, Repolet> contextToHandlerMap) {
		this.contextToHandlerMap = contextToHandlerMap;
		keyList = new ArrayList<>( contextToHandlerMap.keySet());
		// sort so the longer repolet names are first in the list 
		keyList.sort( new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				Integer l1 = o1.length();
				Integer l2 = o2.length();
				return l1.compareTo(l2) * -1;
			}			
		});
	}
	
	/**
	 * expected format : '/<repolet-name>[/...]
	 * @param path
	 * @return
	 */
	private String extractRepoletKey( String path) {
		int i = path.indexOf( '/', 1);
		String key;
		if (i > 0) {
			key = path.substring(1, i);
		}
		else {
			key = path.substring(1);
		}
		System.out.println("Path: [" + path + "], archive : [" + key + "]");
		return key;
	}

	@Override
	public void handleRequest(HttpServerExchange exchange) throws Exception {						
		String path = exchange.getRequestPath();
		/*
		 * idea is that the repolet names are sorted according their length, and we test with the longest
		 * first, then the issue with longer names repeating shorter ones is avoided
		 */
		if (useNewNameDetection) {
			for (String key : keyList) {
				if (path.contains(key)) {
					Repolet repolet = contextToHandlerMap.get(key);
					repolet.handleRequest(exchange, path);
					return;
				}
			}
		}
		else {			
			for (Entry<String, Repolet> entry : contextToHandlerMap.entrySet()) {
				String key = entry.getKey();				
				if (
						/*
						 * needs to be contains(..) for now, as it is also needed to detect the name in the other
						 * urls. Consequence is that then names are constructed in such a way, that no name may exist that is a 
						 * longer form of a shorter name. 
						 * So 'archive' and 'archive-b' are not allowed, 'archive-a' and 'archive-b' are fine
						 */
						path.contains(key)					
					) {						
						entry.getValue().handleRequest(exchange, path);
					return;
				}
			}
		}
		//
		
		exchange.setResponseCode( 404);
		exchange.getResponseSender().send("404 : no repolet found for [" + path + "]");
		exchange.endExchange();
		
	}
	
	
}
