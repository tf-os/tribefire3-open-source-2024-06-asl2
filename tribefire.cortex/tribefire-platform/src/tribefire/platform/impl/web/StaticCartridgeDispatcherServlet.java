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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import com.braintribe.cfg.Configurable;
import com.braintribe.cfg.DestructionAware;
import com.braintribe.cfg.Required;
import com.braintribe.logging.Logger;
import com.braintribe.utils.StringTools;

public class StaticCartridgeDispatcherServlet extends HttpServlet implements DestructionAware {

	private static final long serialVersionUID = -1L;
	private static final Logger logger = Logger.getLogger(StaticCartridgeDispatcherServlet.class);

	private Map<String, HttpServlet> delegates = new ConcurrentHashMap<String, HttpServlet>();
	private ReentrantLock delegatesLock = new ReentrantLock();
	private Function<String,HttpServlet> cartridgeStaticProxySupplier; 

	private int pathKeyLevelCount = 1;

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String pathInfo = request.getPathInfo();
		if (pathInfo == null)
			throw new ServletException("The request does not contain valid path info. pathInfo is null");
		else if (pathInfo.startsWith("/"))
			// remove preceding "/" from path info
			pathInfo = pathInfo.substring(1);

		int idx = 0;

		for (int i = 0; i < this.pathKeyLevelCount && idx != -1; i++) {
			if (pathInfo.charAt(idx) == '/') {
				idx += 1;
			}
			idx = pathInfo.indexOf('/', idx);
		}

		String key = pathInfo;
		if (idx != -1) {
			key = pathInfo.substring(0, idx);
			pathInfo = pathInfo.substring(idx, pathInfo.length());
		} else {
			pathInfo = null;
		}

		HttpServlet delegate = getDelegate(key);

		if (delegate == null) {
			response.sendError(404, "No servlet registered for: " + key);
		} else {
			HttpServletRequestWrapper requestWrapper = new HttpServletRequestWrapperImpl(request, pathInfo);
			delegate.service(requestWrapper, response);
		}

	}

	private HttpServlet getDelegate(String cartridgeExternalId) {
		if (StringTools.isBlank(cartridgeExternalId)) {
			return null;
		}
		HttpServlet servlet = delegates.get(cartridgeExternalId);
		if (servlet == null) {
			delegatesLock.lock();
			try {
				servlet = delegates.get(cartridgeExternalId);
				if (servlet == null) {
					logger.debug(() -> "Creating a new static delegate proxy for "+cartridgeExternalId);
					servlet = cartridgeStaticProxySupplier.apply(cartridgeExternalId);
					if (servlet != null) {
						logger.debug(() -> "Successfully created a new static delegate proxy for "+cartridgeExternalId);
						delegates.put(cartridgeExternalId, servlet);
					} else {
						logger.debug(() -> "Creating a new static delegate proxy for "+cartridgeExternalId+" returned null.");
					}
				}
			} finally {
				delegatesLock.unlock();
			}
		}
		return servlet;
	}
	
	private static class HttpServletRequestWrapperImpl extends HttpServletRequestWrapper {

		private String pathInfo;

		public HttpServletRequestWrapperImpl(HttpServletRequest request, String pathInfo) {
			super(request);
			this.pathInfo = pathInfo;
		}

		@Override
		public String getPathInfo() {
			return this.pathInfo;
		}
	}


	@Required
	@Configurable
	public void setCartridgeStaticProxySupplier(Function<String, HttpServlet> cartridgeStaticProxySupplier) {
		this.cartridgeStaticProxySupplier = cartridgeStaticProxySupplier;
	}

	@Override
	public void preDestroy() {
		logger.debug(() -> "Destroying "+delegates.size()+" proxy servlets");
		delegates.values().forEach(d -> d.destroy());
	}

}
