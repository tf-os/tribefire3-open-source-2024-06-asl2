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
package com.braintribe.testing.test;

import static com.braintribe.testing.junit.assertions.assertj.core.api.Assertions.assertThat;
import static com.braintribe.testing.junit.assertions.assertj.core.api.Assertions.assertThatExecuting;

import java.io.File;

import org.junit.Test;

import com.braintribe.common.lcd.AssertionException;
import com.braintribe.testing.internal.path.PathTools;

/**
 * Provides tests for {@link AbstractTest}.
 *
 * @author michael.lafite
 */
public class AbstractTestTest extends AbstractTest {

	@Test
	public void testLogging() {
		// this is supposed to create properly formatted log message (because of automated JUL configuration)
		// (no real test yet)
		logger.error("test ERROR message");
		logger.warn("test WARN message");
		logger.info("test INFO message");
		logger.debug("test DEBUG message");
		logger.trace("test TRACE message");

		Exception e = new NullPointerException("test exception");
		logger.warn("test WARN message with exception", e);
		logger.warn("test DEBUG message with exception", e);

		System.out.println("test System.out message");
		System.err.println("test System.err message");
		
		logger.info("test another INFO message");
	}

	@Test
	public void testResources() {
		assertThatExecuting(() -> testDir(Integer.class)).fails().with(AssertionException.class);
		assertThat(testDir()).hasSameAbsolutePathAs(new File("res/" + AbstractTestTest.class.getSimpleName()));
		assertThat(testDir()).doesNotHaveSameAbsolutePathAs(new File("res/../res/" + AbstractTestTest.class.getSimpleName()));
		assertThat(testDir()).hasSameCanonicalPathAs(new File("res/../res/" + AbstractTestTest.class.getSimpleName()));

		assertThat(testDir("subdir")).hasSameAbsolutePathAs(new File(testDir(), "subdir"));
		assertThat(testDir("notExists")).hasSameAbsolutePathAs(new File(testDir(), "notExists"));
		assertThatExecuting(() -> existingTestDir("subdir/notExists")).fails().with(AssertionException.class);

		assertThat(testFile("a")).hasSameAbsolutePathAs(new File(testDir(), "a"));
		assertThat(testFile("subdir/a")).hasSameAbsolutePathAs(new File(testDir(), "subdir/a"));
		assertThat(testFile("notExists.txt")).hasSameAbsolutePathAs(new File(testDir(), "notExists.txt"));
		assertThatExecuting(() -> existingTestFile("notExists.txt")).fails().with(AssertionException.class);
	}

	@Test
	public void testTempFiles() {

		{
			File tempDir = newTempDir();
			File tempDir2 = newTempDir();
			assertThat(tempDir).hasParent(PathTools.tempDir().toFile());
			assertThat(tempDir2).hasParent(PathTools.tempDir().toFile());
			assertThat(tempDir).doesNotHaveSamePathAs(tempDir2);
		}

		{
			String relativeFilePath = AbstractTestTest.class.getSimpleName() + "/a/b";
			File tempDir = newTempDir(relativeFilePath);
			File tempDir2 = newTempDir(relativeFilePath);
			assertThat(tempDir).hasAncestor(PathTools.tempDir().toFile());
			assertThat(tempDir2).hasAncestor(PathTools.tempDir().toFile());
			assertThat(tempDir).doesNotHaveSamePathAs(tempDir2);
		}

		{
			File tempFile = newTempFile();
			File tempFile2 = newTempFile();
			assertThat(tempFile).hasParent(PathTools.tempDir().toFile());
			assertThat(tempFile2).hasParent(PathTools.tempDir().toFile());
			assertThat(tempFile).doesNotHaveSamePathAs(tempFile2);
		}

		{
			String relativeFilePath = AbstractTestTest.class.getSimpleName() + "/a/b.txt";
			File tempFile = newTempFile(relativeFilePath);
			File tempFile2 = newTempFile(relativeFilePath);
			assertThat(tempFile).hasAncestor(PathTools.tempDir().toFile());
			assertThat(tempFile2).hasAncestor(PathTools.tempDir().toFile());
			assertThat(tempFile).doesNotHaveSamePathAs(tempFile2);
		}
	}

}
