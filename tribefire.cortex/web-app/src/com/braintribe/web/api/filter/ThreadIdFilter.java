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
package com.braintribe.web.api.filter;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import com.braintribe.logging.Logger;

public class ThreadIdFilter implements Filter {

	private static final Logger logger = Logger.getLogger(ThreadIdFilter.class);
	
	private static AtomicLong threadIdCounter = new AtomicLong(0);

	@Override
	public void destroy() {
		//nothing to do
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
		String servletPath = null;
		if (request instanceof HttpServletRequest) {
			servletPath = ((HttpServletRequest) request).getServletPath();
		} else {
			servletPath = "n/a";
		}
		
		String originalThreadName = null;
		try {
			originalThreadName = Thread.currentThread().getName();
		} catch(Exception e) {
			logger.debug("Could not get the thread's name", e);
		}
		
		try {
			Long threadId = threadIdCounter.incrementAndGet();
			String threadIdString = Long.toString(threadId.longValue(), 36);
			String context = servletPath+"#"+threadIdString;
			logger.pushContext(context);
			
			try {
				Thread.currentThread().setName(context);
			} catch(Exception e) {
				logger.debug("Could not set thread name "+context, e);
				originalThreadName = null;
			}
			
			filterChain.doFilter(request, response);

		} finally {
			
			if (originalThreadName != null) {
				try {
					Thread.currentThread().setName(originalThreadName);
				} catch(Exception e) {
					logger.debug("Could not reset the thread's name to "+originalThreadName);
				}
			}

			logger.removeContext();
			logger.clearMdc();
		}
		
	}

	@Override
	public void init(FilterConfig config) throws ServletException {
		//nothing to do
	}

}
