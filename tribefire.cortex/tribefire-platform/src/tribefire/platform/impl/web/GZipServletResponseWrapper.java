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
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.function.Consumer;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 * This is a wrapper around a {@link HttpServletResponse} instance. When either {@link #getOutputStream()} or
 * {@link #getWriter()} are called, a {@link GZipServletOutputStream} object will be created and returned.
 * <br><br>
 *  This class keeps a reference to the original {@link HttpServletResponse} object. This is necessary
 *  because the {@link GZipServletOutputStream} might signal (via the {@link #accept(Boolean)} method)
 *  that the output stream is switching to GZIP encoding. In that case, the {@code Content-Encoding} header
 *  must be set prior to the actual output being written.
 */
class GZipServletResponseWrapper extends HttpServletResponseWrapper implements Consumer<Boolean> {

	private GZipServletOutputStream gzipOutputStream = null;
	private PrintWriter             printWriter      = null;
	private int bufferSize = 16384;
	private int zipThreshold = 16384;
	private HttpServletResponse originalResponse;

	public GZipServletResponseWrapper(HttpServletResponse response) {
		super(response);
		originalResponse = response;
	}
	public GZipServletResponseWrapper(HttpServletResponse response, int bufferSize, int zipThreshold) {
		super(response);
		originalResponse = response;
		if (bufferSize > 0) {
			this.bufferSize = bufferSize;
		}
		if (zipThreshold > 0) {
			this.zipThreshold = zipThreshold;
		}
	}

	public void close() throws IOException {

		//PrintWriter.close does not throw exceptions.
		//Hence no try-catch block.
		if (this.printWriter != null) {
			this.printWriter.close();
		}

		if (this.gzipOutputStream != null) {
			this.gzipOutputStream.close();
		}
	}


	/**
	 * Flush OutputStream or PrintWriter
	 *
	 * @throws IOException Thrown in the event of an IO error
	 */

	@Override
	public void flushBuffer() throws IOException {

		//PrintWriter.flush() does not throw exception
		if (this.printWriter != null) {
			this.printWriter.flush();
		}

		IOException exception1 = null;
		try {
			if (this.gzipOutputStream != null) {
				this.gzipOutputStream.flush();
			}
		} catch(IOException e) {
			exception1 = e;
		}

		IOException exception2 = null;
		try {
			super.flushBuffer();
		} catch(IOException e){
			exception2 = e;
		}

		if (exception1 != null) throw exception1;
		if (exception2 != null) throw exception2;
	}

	@Override
	public ServletOutputStream getOutputStream() throws IOException {
		if (this.printWriter != null) {
			throw new IllegalStateException("PrintWriter obtained already - cannot get OutputStream");
		}
		if (this.gzipOutputStream == null) {
			this.gzipOutputStream = new GZipServletOutputStream(getResponse().getOutputStream(), bufferSize, zipThreshold, this);
		}
		return this.gzipOutputStream;
	}

	@Override
	public PrintWriter getWriter() throws IOException {
		if (this.printWriter == null && this.gzipOutputStream != null) {
			throw new IllegalStateException("OutputStream obtained already - cannot get PrintWriter");
		}
		if (this.printWriter == null) {
			this.gzipOutputStream = new GZipServletOutputStream(getResponse().getOutputStream(), bufferSize, zipThreshold, this);
			this.printWriter      = new PrintWriter(new OutputStreamWriter(this.gzipOutputStream, getResponse().getCharacterEncoding()));
		}
		return this.printWriter;
	}


	@Override
	public void setContentLength(int len) {
		//ignore, since content length of zipped content
		//does not match content length of unzipped content.
	}
	
	/**
	 * Called when the underlying {@link GZipServletOutputStream} is switching to GZIP encoding.
	 */
	@Override
	public void accept(Boolean switchToZip) {
		if (switchToZip) {
			originalResponse.addHeader("Content-Encoding", "gzip");
		}
	}
}
