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
package tribefire.cortex.services.tribefire_web_platform_test.tests.hardwired;

import static com.braintribe.testing.junit.assertions.assertj.core.api.Assertions.assertThat;
import static com.braintribe.testing.junit.assertions.gm.assertj.core.api.GmAssertions.assertThat;
import static com.braintribe.utils.lcd.CollectionTools2.asList;
import static java.util.Objects.requireNonNull;

import java.io.ByteArrayOutputStream;

import org.junit.Before;
import org.junit.Test;

import com.braintribe.codec.marshaller.api.Marshaller;
import com.braintribe.model.deployment.DeploymentStatus;
import com.braintribe.model.deployment.HardwiredDeployable;
import com.braintribe.model.deployment.Module;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.marshallerdeployment.HardwiredMarshaller;
import com.braintribe.model.processing.session.api.persistence.PersistenceGmSession;

import tribefire.cortex.services.tribefire_web_platform_test.impl.hardwired.ModuleTestMarshaller;
import tribefire.cortex.services.tribefire_web_platform_test.tests.PlatformHolder;
import tribefire.module.wire.contract.TribefireWebPlatformContract;

/**
 * @author peter.gazdik
 */
public class HardwiredDeployablesTests {

	private static final String TEST_MARSHALLER_MIME_TYPE = "module-test";
	private static final String TEST_MARSHALLER_MIME_TYPE2 = "module-test2";

	private TribefireWebPlatformContract platform;

	@Before
	public void setup() {
		platform = PlatformHolder.platformContract;
	}

	// ################################################
	// ## . . . . . . . Module Loading . . . . . . . ##
	// ################################################

	public static void bindHardwired(TribefireWebPlatformContract tfPlatform) {
		tfPlatform.hardwiredDeployables().bindMarshaller( //
				"marshaller.module-test", "Module Test Marshaller", new ModuleTestMarshaller(), TEST_MARSHALLER_MIME_TYPE);

		// bind marshaller generically to test a different path of binding
		tfPlatform.hardwiredDeployables().bind(hardwiredMarshaller()) //
				.component(tfPlatform.binders().marshaller(), ModuleTestMarshaller::new);
	}

	private static HardwiredMarshaller hardwiredMarshaller() {
		HardwiredMarshaller result = newHd(HardwiredMarshaller.T, "marshaller.module-test2", "Module Test Marshaller 2", "marshaller");
		result.setMimeTypes(asList(TEST_MARSHALLER_MIME_TYPE2));

		return result;
	}

	private static final <HD extends HardwiredDeployable> HD newHd(EntityType<HD> deployableType, String externalId, String name, String type) {
		HD deployable = deployableType.create("hardwired:" + type + "/" + externalId);
		deployable.setExternalId(externalId);
		deployable.setName(name);
		deployable.setAutoDeploy(true);
		deployable.setDeploymentStatus(DeploymentStatus.deployed);

		return deployable;
	}

	// ################################################
	// ## . . . . . . . . . Tests . . . . . . . . . .##
	// ################################################

	@Test
	public void marshaller() throws Exception {
		testMarshaller("hardwired:marshaller/marshaller.module-test", TEST_MARSHALLER_MIME_TYPE);
	}

	@Test
	public void marshallerBoundGenerically() throws Exception {
		testMarshaller("hardwired:marshaller/marshaller.module-test2", TEST_MARSHALLER_MIME_TYPE2);
	}

	private void testMarshaller(String globalId, String mimeType) throws Exception {
		assertDenotationTypeInCortex(HardwiredMarshaller.T, globalId);

		Marshaller marshaller = platform.marshalling().registry().getMarshaller(mimeType);
		requireNonNull(marshaller, "Test marshaller not found");

		ByteArrayOutputStream os = new ByteArrayOutputStream();

		marshaller.marshall(os, 456L);

		assertThat(os.toString()).isEqualTo("456L");
	}

	private void assertDenotationTypeInCortex(EntityType<? extends HardwiredDeployable> type, String globalId) {
		PersistenceGmSession cortexSession = platform.systemUserRelated().cortexSessionSupplier().get();
		HardwiredDeployable hd = cortexSession.findEntityByGlobalId(globalId);

		assertThat(hd).isNotNull().isInstanceOf(type);

		Module module = hd.getModule();
		assertThat(module).isNotNull();
		assertThat(module.getName()).isEqualTo("tribefire-web-platform-test-module");
	}

}
