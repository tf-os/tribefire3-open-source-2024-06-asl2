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
package com.braintribe.utils.stream;

import java.io.IOException;
import java.io.InputStream;

public abstract class DelegateInputStream extends InputStream {
	private InputStream delegate;

	protected abstract InputStream openDelegate() throws IOException;

	protected InputStream getDelegate() throws IOException {
		if (delegate == null) {
			delegate = openDelegate();
		}

		return delegate;
	}

	@Override
	public int read() throws IOException {
		return getDelegate().read();
	}

	@Override
	public int read(byte[] b) throws IOException {
		return getDelegate().read(b);
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		return getDelegate().read(b, off, len);
	}
	@Override
	public long skip(long n) throws IOException {
		return getDelegate().skip(n);
	}

	@Override
	public int available() throws IOException {
		return getDelegate().available();
	}
	@Override
	public void close() throws IOException {
		if (delegate != null) {
			delegate.close();
		}
	}

	@Override
	@SuppressWarnings("sync-override")
	public void mark(int readlimit) {
		try {
			getDelegate().mark(readlimit);
		} catch (IOException e) {
			throw new RuntimeException("error while trying to get delegate", e);
		}
	}

	@Override
	@SuppressWarnings("sync-override")
	public void reset() throws IOException {
		getDelegate().reset();
	}

	@Override
	public boolean markSupported() {
		try {
			return getDelegate().markSupported();
		} catch (IOException e) {
			throw new RuntimeException("error while trying to get delegate", e);
		}
	}

}
