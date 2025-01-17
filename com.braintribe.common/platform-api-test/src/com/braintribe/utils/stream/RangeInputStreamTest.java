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

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Test;

import com.braintribe.utils.IOTools;

public class RangeInputStreamTest {

	@Test
	public void testRanges() throws Exception {

		byte[] source = new byte[1024];
		(new Random()).nextBytes(source);

		byte[] actual;

		try (InputStream in = new RangeInputStream(new ByteArrayInputStream(source), 0, 10)) {
			actual = IOTools.slurpBytes(in);
		}
		assertSubArrayEqual(actual, source, 0, 10);

		try (InputStream in = new RangeInputStream(new ByteArrayInputStream(source), 10, 20)) {
			actual = IOTools.slurpBytes(in);
		}
		assertSubArrayEqual(actual, source, 10, 10);

		try (InputStream in = new RangeInputStream(new ByteArrayInputStream(source), 1, 11)) {
			actual = IOTools.slurpBytes(in);
		}
		assertSubArrayEqual(actual, source, 1, 10);

		try (InputStream in = new RangeInputStream(new ByteArrayInputStream(source), 1, -1)) {
			actual = IOTools.slurpBytes(in);
		}
		assertSubArrayEqual(actual, source, 1, 1023);

		try (InputStream in = new RangeInputStream(new ByteArrayInputStream(source), 0, 1)) {
			actual = IOTools.slurpBytes(in);
		}
		assertSubArrayEqual(actual, source, 0, 1);
		assertThat(actual.length).isEqualTo(1);

	}

	protected void assertSubArrayEqual(byte[] actual, byte[] source, int start, int length) {

		assertThat(actual).isNotNull();
		assertThat(actual).hasSize(length);

		byte[] expected = new byte[length];
		System.arraycopy(source, start, expected, 0, length);

		assertThat(actual).isEqualTo(expected);
	}

	@Test
	public void testClose() throws Exception {

		byte[] source = new byte[1024];
		(new Random()).nextBytes(source);

		byte[] expected = new byte[10];
		System.arraycopy(source, 0, expected, 0, 10);

		AtomicBoolean closed = new AtomicBoolean(false);

		ByteArrayInputStream sourceIn = new ByteArrayInputStream(source) {
			@Override
			public void close() throws java.io.IOException {
				super.close();
				closed.set(true);
			}
		};

		try (InputStream in = new RangeInputStream(sourceIn, 0, 10)) {
			byte[] actual = IOTools.slurpBytes(in);

			assertThat(actual).isEqualTo(expected);
		}

		assertThat(closed.get()).isTrue();
	}

	@Test
	public void testAvailable() throws Exception {

		byte[] source = new byte[1024];
		(new Random()).nextBytes(source);

		try (RangeInputStream in = new RangeInputStream(new ByteArrayInputStream(source), 0, -1)) {
			assertThat(in.available()).isEqualTo(1024);
		}
		try (RangeInputStream in = new RangeInputStream(new ByteArrayInputStream(source), 0, 10)) {
			assertThat(in.available()).isEqualTo(10);
		}
		try (RangeInputStream in = new RangeInputStream(new ByteArrayInputStream(source), 10, 20)) {
			assertThat(in.available()).isEqualTo(10);
		}

	}
}
