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
package com.braintribe.model.generic.stream;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.locks.ReentrantLock;

import com.braintribe.model.generic.session.DuplexStreamProvider;
import com.braintribe.model.generic.session.InputStreamProvider;
import com.braintribe.model.generic.session.OutputStreamProvider;
import com.braintribe.model.generic.session.OutputStreamer;
import com.braintribe.utils.stream.api.StreamPipe;
import com.braintribe.utils.stream.api.StreamPipeFactory;
import com.braintribe.utils.stream.api.StreamPipes;

/**
 * Utility methods for creating Input- and OutputStreamProviders
 * 
 * @author Neidhart.Orlich
 * @author Dirk.Schefler
 *
 */
public class StreamProviders {

	/**
	 * Creates a simple DuplexStreamProvider from an In- and an OutputStream.
	 * <p>
	 * <b>Note</b> that {@link InputStreamProvider#openInputStream()} and {@link OutputStreamProvider#openOutputStream()}
	 * can both only be <b>opened once</b>!
	 */
	public static DuplexStreamProvider from(InputStream in, OutputStream out) {
		return new StaticDuplexStreamProvider(in, out);
	}

	/**
	 * Creates a simple InputStreamProvider from an InputStream.
	 * <p>
	 * <b>Note</b> that {@link InputStreamProvider#openInputStream()} can both only be <b>opened once</b>!
	 */
	public static InputStreamProvider from(InputStream in) {
		return new StaticInputStreamProvider(in);
	}

	/**
	 * Creates an InputStreamProvider from an OutputStreamer. As opposed to other methods of this utility class input
	 * streams can be opened multiple times. A simple {@link StreamPipe} is used for buffering the OutputStreamer. If you
	 * want to use a special StreamPipe use {@link #from(OutputStreamer, StreamPipeFactory)}.
	 */
	public static InputStreamProvider from(OutputStreamer out) {
		return new InputStreamProviderAndOutputStreamConsumer(out);
	}

	/**
	 * Creates an InputStreamProvider from an OutputStreamer. As opposed to other methods of this utility class input
	 * streams can be opened multiple times.
	 */
	public static InputStreamProvider from(OutputStreamer out, StreamPipeFactory streamPipeFactory) {
		return new InputStreamProviderAndOutputStreamConsumer(out, streamPipeFactory);
	}

	/**
	 * Creates a simple OutputStreamProvider from an InputStream.
	 * <p>
	 * <b>Note</b> that {@link OutputStreamProvider#openOutputStream()} can both only be <b>opened once</b>!
	 */
	public static OutputStreamProvider from(OutputStream out) {
		return new StaticOutputStreamProvider(out);
	}

	private static class StaticInputStreamProvider implements InputStreamProvider {
		private final InputStream in;
		private boolean openedIn;

		public StaticInputStreamProvider(InputStream in) {
			this.in = in;
		}

		@Override
		public InputStream openInputStream() throws IOException {
			if (openedIn) {
				throw new IOException("input stream was already opened and can only opened once: " + in);
			} else {
				openedIn = true;
				return new com.braintribe.utils.stream.KeepAliveDelegateInputStream(in);
			}
		}
	}

	private static class StaticOutputStreamProvider implements OutputStreamProvider {
		private final OutputStream out;
		private boolean openedOut;

		public StaticOutputStreamProvider(OutputStream out) {
			this.out = out;
		}

		@Override
		public OutputStream openOutputStream() throws IOException {
			if (openedOut) {
				throw new IOException("output stream was already opened and can only opened once: " + out);
			} else {
				openedOut = true;
				return new com.braintribe.utils.stream.KeepAliveDelegateOutputStream(out);
			}
		}
	}

	private static class StaticDuplexStreamProvider extends StaticInputStreamProvider implements DuplexStreamProvider {
		private final OutputStream out;
		private boolean openedOut;

		public StaticDuplexStreamProvider(InputStream in, OutputStream out) {
			super(in);
			this.out = out;
		}

		@Override
		public OutputStream openOutputStream() throws IOException {
			if (openedOut) {
				throw new IOException("output stream was already opened and can only opened once: " + out);
			} else {
				openedOut = true;
				return new com.braintribe.utils.stream.KeepAliveDelegateOutputStream(out);
			}
		}

	}

	private static class InputStreamProviderAndOutputStreamConsumer implements InputStreamProvider, OutputStreamer {
		private final OutputStreamer streamer;
		private StreamPipe pipe;
		private final StreamPipeFactory pipeFactory;
		private ReentrantLock lock = new ReentrantLock();

		public InputStreamProviderAndOutputStreamConsumer(OutputStreamer streamer) {
			this(streamer, StreamPipes.simpleFactory());
		}

		public InputStreamProviderAndOutputStreamConsumer(OutputStreamer streamer, StreamPipeFactory pipeFactory) {
			super();
			this.streamer = streamer;
			this.pipeFactory = pipeFactory;
		}

		@Override
		public void writeTo(OutputStream out) throws IOException {
			streamer.writeTo(out);
		}

		private synchronized StreamPipe getPipe() throws IOException {
			if (pipe == null) {
				lock.lock();
				try {
					if (pipe == null) {
						pipe = pipeFactory.newPipe("stream-providers-input-pipe");

						try (OutputStream out = pipe.openOutputStream()) {
							streamer.writeTo(out);
						}
					}
				} finally {
					lock.unlock();
				}
			}

			return pipe;
		}

		@Override
		public InputStream openInputStream() throws IOException {
			return getPipe().openInputStream();
		}

	}
}
