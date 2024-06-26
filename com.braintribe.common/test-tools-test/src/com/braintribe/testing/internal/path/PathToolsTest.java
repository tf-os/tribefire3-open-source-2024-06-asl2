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
package com.braintribe.testing.internal.path;

import static com.braintribe.testing.junit.assertions.assertj.core.api.Assertions.assertThat;

import java.nio.file.Paths;

import org.junit.Test;

import com.braintribe.testing.test.AbstractTest;

/**
 * Provides tests for {@link PathTools}.
 *
 * @author michael.lafite
 */
public class PathToolsTest extends AbstractTest {

	@Test
	public void testCanonicalPath() {
		assertThat(PathTools.canonicalPath("x/y/./.././y/z").toString()).endsWith(Paths.get("x/y/z").toString());
	}
}