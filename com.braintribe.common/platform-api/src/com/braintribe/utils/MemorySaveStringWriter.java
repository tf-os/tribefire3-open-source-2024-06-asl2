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
package com.braintribe.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;

import com.braintribe.common.lcd.Numbers;
import com.braintribe.logging.Logger;

/**
 * This is a slight variation of a StringWriter. The problem with the StringWriter is that it allocates memory by doubling it every time the buffer
 * gets too small. This is a problem for large buffers. Hence, this Writer stores the buffer in memory until it reaches a limit. After that, an
 * external file will be used to store the buffer.
 *
 * @author roman.kurmanowytsch
 *
 */
public class MemorySaveStringWriter extends Writer {

	protected static Logger logger = Logger.getLogger(MemorySaveStringWriter.class);

	protected StringWriter stringWriter = new StringWriter();
	protected FileOutputStream outputStream = null;
	protected BufferedWriter outputWriter = null;
	protected File temporaryFile = null;
	protected long bufferLimit = 10 * Numbers.MEGABYTE;
	boolean outputStreamWriterClosed = false;

	public MemorySaveStringWriter(long bufferLimit) {
		if (bufferLimit > 0) {
			this.bufferLimit = bufferLimit;
		}
	}

	@Override
	public void write(char[] cbuf, int off, int len) throws IOException {
		if (this.outputWriter != null) {
			this.outputWriter.write(cbuf, off, len);
		} else {
			this.stringWriter.write(cbuf, off, len);
			this.checkLength();
		}
	}

	protected void checkLength() throws IOException {
		int length = this.stringWriter.getBuffer().length();
		if (length > this.bufferLimit) {
			logger.debug("Switching from memory to file based buffer. Reached limit " + this.bufferLimit + ": " + length);
			this.temporaryFile = File.createTempFile("stringbuffer", ".txt");
			this.outputStream = new FileOutputStream(temporaryFile);
			this.outputWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
			this.stringWriter.flush();
			this.stringWriter.close();
			this.outputWriter.write(this.stringWriter.toString());
			this.stringWriter = null;
		}
	}

	@Override
	public void flush() throws IOException {
		if (this.outputWriter != null) {
			this.outputWriter.flush();
		} else {
			this.stringWriter.flush();
		}
	}

	@Override
	public void close() throws IOException {
		if (this.outputWriter != null) {
			this.outputWriter.close();
			this.outputStreamWriterClosed = true;
		} else {
			this.stringWriter.close();
		}
	}

	@Override
	public String toString() {
		try {
			return this.getString();
		} catch (IOException e) {
			throw new RuntimeException("Could not get string from buffer.", e);
		}
	}

	public String getString() throws IOException {
		if (this.outputWriter != null) {
			if (!this.outputStreamWriterClosed) {
				this.close();
			}
			int length = (int) this.temporaryFile.length();
			StringWriter writer = null;
			BufferedReader reader = null;
			try {
				reader = new BufferedReader(new InputStreamReader(new FileInputStream(this.temporaryFile), "UTF-8"));
				writer = new StringWriter(length + 100); // Feeling uncomfortable to assign exactly the same size;
															// adding some space for padding, just in case
				String line = null;
				boolean firstLine = true;
				while ((line = reader.readLine()) != null) {
					if (!firstLine) {
						writer.write("\n");
					} else {
						firstLine = false;
					}
					writer.write(line);
				}
			} finally {
				if (reader != null) {
					try {
						reader.close();
					} catch (Exception e) {
						logger.error("Could not close reader from " + this.temporaryFile, e);
					}
				}
			}
			return writer.toString();
		} else {
			return this.stringWriter.toString();
		}
	}

	public void destroy() {
		if (this.temporaryFile != null) {
			try {
				this.outputWriter.close();
				this.outputStream.close();
				this.temporaryFile.delete();
			} catch (Exception e) {
				logger.error("Could not delete temporary file " + this.temporaryFile, e);
			} finally {
				this.temporaryFile = null;
			}
		}
	}
}
