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
package com.braintribe.servlet.test.mock;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

public class MockHttpServletResponse implements HttpServletResponse {

	private ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	private MockServletOutputStream servletOutputStream = new MockServletOutputStream(outputStream);
	private Map<String,String> headers = new HashMap<>();
	private int status = -1;
	
	public byte[] getBytes() {
		return outputStream.toByteArray();
	}
	
	@Override
	public String getCharacterEncoding() {
		return null;
	}

	@Override
	public String getContentType() {
		return null;
	}

	@Override
	public ServletOutputStream getOutputStream() throws IOException {
		return servletOutputStream;
	}

	@Override
	public PrintWriter getWriter() throws IOException {
		return null;
	}

	@Override
	public void setCharacterEncoding(String charset) {
		//Intentionally left empty
	}

	@Override
	public void setContentLength(int len) {
		//Intentionally left empty
	}

	@Override
	public void setContentType(String type) {
		//Intentionally left empty
	}

	@Override
	public void setBufferSize(int size) {
		//Intentionally left empty
	}

	@Override
	public int getBufferSize() {
		return 0;
	}

	@Override
	public void flushBuffer() throws IOException {
		//Intentionally left empty
	}

	@Override
	public void resetBuffer() {
		//Intentionally left empty
	}

	@Override
	public boolean isCommitted() {
		return false;
	}

	@Override
	public void reset() {
		//Intentionally left empty
	}

	@Override
	public void setLocale(Locale loc) {
		//Intentionally left empty
	}

	@Override
	public Locale getLocale() {
		return null;
	}

	@Override
	public void addCookie(Cookie cookie) {
		//Intentionally left empty
	}

	@Override
	public boolean containsHeader(String name) {
		return false;
	}

	@Override
	public String encodeURL(String url) {
		return null;
	}

	@Override
	public String encodeRedirectURL(String url) {
		return null;
	}

	@Deprecated
	@Override
	public String encodeUrl(String url) {
		return null;
	}

	@Deprecated
	@Override
	public String encodeRedirectUrl(String url) {
		return null;
	}

	@Override
	public void sendError(int sc, String msg) throws IOException {
		//Intentionally left empty
	}

	@Override
	public void sendError(int sc) throws IOException {
		//Intentionally left empty
	}

	@Override
	public void sendRedirect(String location) throws IOException {
		//Intentionally left empty
	}

	@Override
	public void setDateHeader(String name, long date) {
		//Intentionally left empty
	}

	@Override
	public void addDateHeader(String name, long date) {
		//Intentionally left empty
	}

	@Override
	public void setHeader(String name, String value) {
		//Intentionally left empty
	}

	@Override
	public void addHeader(String name, String value) {
		headers.put(name, value);
	}

	@Override
	public void setIntHeader(String name, int value) {
		//Intentionally left empty
	}

	@Override
	public void addIntHeader(String name, int value) {
		//Intentionally left empty
	}

	@Override
	public void setStatus(int sc) {
		status = sc;
	}

	@Deprecated
	@Override
	public void setStatus(int sc, String sm) {
		status = sc;
	}

	@Override
	public int getStatus() {
		return status;
	}

	@Override
	public String getHeader(String name) {
		return headers.get(name);
	}

	@Override
	public Collection<String> getHeaders(String name) {
		return null;
	}

	@Override
	public Collection<String> getHeaderNames() {
		return null;
	}

}
