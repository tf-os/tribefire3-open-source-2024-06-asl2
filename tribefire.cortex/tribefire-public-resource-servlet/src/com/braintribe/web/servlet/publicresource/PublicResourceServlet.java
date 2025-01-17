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
package com.braintribe.web.servlet.publicresource;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;

import com.braintribe.cfg.Configurable;
import com.braintribe.logging.Logger;

public class PublicResourceServlet extends HttpServlet {

	private static final long serialVersionUID = 4695919181704450507L;
	private Logger logger = Logger.getLogger(PublicResourceServlet.class);
	private Map<String, PublicResourceStreamer> streamerRegistry = Collections.emptyMap();
	
	@Configurable
	public void setResourceProviderRegistry(
			Map<String, PublicResourceStreamer> resourceProviderRegistry) {
		this.streamerRegistry = resourceProviderRegistry;
	}
	

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		String requestedType = req.getPathInfo();
		if (requestedType.startsWith("/")) {
			requestedType = requestedType.substring(1);
		}
		PublicResourceStreamer resourceProvider = streamerRegistry.get(requestedType);
		
		if (resourceProvider == null) {
			printNotFound(req, resp);
			return;
		}
		
		try {
			boolean streamed = resourceProvider.streamResource(req, resp);
			if (!streamed) {
				printNotFound(req, resp);
				return;
			}
		} catch (Exception e) {
			logger.error("Error while streaming requested resource",e);
			printNotFound(req, resp);
			return;
		}
		
	}
	

	protected void printNotFound(HttpServletRequest httpRequest,  HttpServletResponse httpResponse) throws IOException { 

		setCommonResponseHeaders(httpResponse, false);
		httpResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);
		httpResponse.setContentType("text/html");
		
		PrintWriter httpOut = httpResponse.getWriter();

		writeNotFound(httpRequest.getPathInfo(), httpOut);
		httpOut.flush();
	}
	
	
	protected void writeNotFound(String path, PrintWriter writer) {

		writer.println("<html>");
		writer.println("<head>");
		writer.println("<title>Error 404 No Resource Streamer found</title>");
		writer.println("</head>");
		writer.println("<body>");
		writer.println("<h3>HTTP ERROR 404</h3>");
		writer.print("<p>");
		writer.print(StringEscapeUtils.escapeHtml(path));
		writer.println(" not found</p>");
		
		writer.println("</body>");
		writer.println("</html>");

	}
	
	/**
	 * Sets the set of response header that is common to all responses (stream, resource listing, resource not found and et cetera)
	 * @param httpResponse The response that should be modified
	 * @param cacheableResponses Indicated whether the response is cacheable
	 */
	protected void setCommonResponseHeaders(HttpServletResponse httpResponse, boolean cacheableResponses) {
		
		final long ONE_DAY = 24 * 60 * 60 * 1000;
		final long now = System.currentTimeMillis();
		
		httpResponse.setDateHeader("Last-Modified", now);

		if (cacheableResponses) {
			httpResponse.setHeader("Cache-Control", "public,max-age=" + (ONE_DAY / 1000));
		} else {
			httpResponse.setHeader("Cache-Control", "no-store, no-cache");
			httpResponse.setHeader("Pragma", "no-cache");
			httpResponse.setDateHeader("Expires", 0);
		}
		
	}



}
