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
package com.braintribe.utils.io;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.StandardOpenOption;

import com.braintribe.common.lcd.function.CheckedConsumer;
import com.braintribe.common.lcd.function.CheckedSupplier;
import com.braintribe.exception.Exceptions;
import com.braintribe.utils.FileTools;
import com.braintribe.utils.IOTools;

/**
 * {@link WriterBuilder} implementation for writing to a {@link File}.
 *
 * @author peter.gazdik
 */
public class BasicFileWriterBuilder implements FileWriterBuilder {

	private final File file;
	private boolean append;
	private boolean notBuffered;

	private String charsetName = StandardCharsets.UTF_8.name();

	public BasicFileWriterBuilder(File file) {
		this.file = file;
	}

	@Override
	public FileWriterBuilder append(boolean append) {
		this.append = append;
		return this;
	}

	@Override
	public WriterBuilder<File> notBuffered() {
		notBuffered = true;
		return this;
	}

	@Override
	public CharsetWriterBuilder<File> withCharset(String charsetName) {
		this.charsetName = charsetName;
		return this;
	}

	@Override
	public CharsetWriterBuilder<File> withCharset(Charset charset) {
		this.charsetName = charset.name();
		return this;
	}

	@Override
	public File bytes(byte[] bytes) {
		FileTools.ensureParentFolder(file);
		FileTools.writeBytesToFile(file, bytes, append);
		return file;
	}

	@Override
	public File lines(Iterable<? extends CharSequence> lines) {
		FileTools.ensureParentFolder(file);

		try {
			Files.write(file.toPath(), lines, writeOptions());
		} catch (IOException e) {
			throw new UncheckedIOException("Error while writing to file: " + file.getAbsolutePath(), e);
		}

		return file;
	}

	private OpenOption[] writeOptions() {
		return append ? new OpenOption[] { StandardOpenOption.APPEND } : new OpenOption[0];
	}

	@Override
	public File string(String string) {
		return usingWriter(w -> w.write(string));
	}

	@Override
	public File fromInputStreamFactory(CheckedSupplier<InputStream, ? extends Exception> isSupplier) {
		return fromInputStreamFactory(isSupplier, e -> {
			throw Exceptions.unchecked(e, "Error while writing file: " + file.getAbsolutePath());
		});
	}

	@Override
	public File fromInputStream(InputStream is) {
		return usingOutputStream(os -> IOTools.pump(is, os));
	}

	@Override
	public File usingOutputStream(CheckedConsumer<OutputStream, IOException> outputStreamConsumer) throws UncheckedIOException {
		FileTools.ensureParentFolder(file);

		try (OutputStream os = outputStream()) {
			outputStreamConsumer.accept(os);
		} catch (IOException e) {
			throw new UncheckedIOException("Error while writing to file: " + file.getAbsolutePath(), e);
		}

		return file;
	}

	private OutputStream outputStream() throws FileNotFoundException {
		OutputStream result = new FileOutputStream(file, append);
		if (!notBuffered) {
			result = new BufferedOutputStream(result);
		}

		return result;
	}

	@Override
	public File usingWriter(CheckedConsumer<Writer, IOException> writerConsumer) {
		FileTools.ensureParentFolder(file);

		try (Writer writer = writer(Charset.forName(charsetName))) {
			writerConsumer.accept(writer);

		} catch (IOException e) {
			throw new UncheckedIOException("Error while writing to file: " + file.getAbsolutePath(), e);
		}

		return file;
	}

	private BufferedWriter writer(Charset charset) throws FileNotFoundException {
		return new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, append), charset));
	}

}
