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

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SOPHeaderEnricher implements Filter {
	
	private String allowedOrigins = "*";
	
	public void setAllowedOrigins(String allowedOrigins) {
		this.allowedOrigins = allowedOrigins;
	}

	@Override
	public void destroy() {
		//Nothing to do
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		if(request instanceof HttpServletRequest)
			((HttpServletRequest)request).setAttribute("Access-Control-Allow-Origin", allowedOrigins);
		if(response instanceof HttpServletResponse){
			((HttpServletResponse)response).setHeader("Access-Control-Allow-Origin", allowedOrigins);
			((HttpServletResponse)response).setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
		}
		chain.doFilter(request, response);	
	}

	@Override
	public void init(FilterConfig config) throws ServletException {
		//Nothing to do
	}


}
