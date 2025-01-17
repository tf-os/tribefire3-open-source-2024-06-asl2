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
package tribefire.cortex.services.tribefire_web_platform_test.tests.testrunners;

import static com.braintribe.model.processing.query.fluent.EntityQueryBuilder.from;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.braintribe.model.meta.GmMetaModel;
import com.braintribe.model.processing.session.api.persistence.PersistenceGmSession;

import tribefire.cortex.services.tribefire_web_platform_test.tests.PlatformHolder;
import tribefire.cortex.testing.junit.runner.AuthorizingParallelRunner;

/**
 * This integration test only tests the authorization of the corresponding thread but not the parallelization itself.
 * There is a dedicated unit test for that.
 * 
 * @author Neidhart.Orlich
 *
 */
@RunWith(AuthorizingParallelRunner.class)
public class ParallelRunnerAuthorizationTest {

	@Test
	public void test1() throws Exception {
		test();
	}

	@Test
	public void test2() throws Exception {
		test();
	}

	@Test
	public void test3() throws Exception {
		test();
	}

	@Test
	public void test4() throws Exception {
		test();
	}

	private void test() throws Exception {
		PersistenceGmSession cortexSession = PlatformHolder.platformContract.requestUserRelated().cortexSessionSupplier().get();

		List<?> models = cortexSession.query() //
				.entities(from(GmMetaModel.T).done()) //
				.list();

		assertThat(models).isNotEmpty();
	}

}
