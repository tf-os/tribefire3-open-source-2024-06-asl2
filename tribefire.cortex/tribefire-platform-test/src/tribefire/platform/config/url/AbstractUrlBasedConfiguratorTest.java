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
package tribefire.platform.config.url;

import static com.braintribe.testing.junit.assertions.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.util.List;

import org.junit.Test;

import com.braintribe.cartridge.common.processing.configuration.url.model.RegistryEntry;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.meta.GmMetaModel;
import com.braintribe.model.processing.bootstrapping.TribefireRuntime;
import com.braintribe.utils.FileTools;

import tribefire.platform.impl.configuration.EnvironmentDenotationRegistry;

public class AbstractUrlBasedConfiguratorTest {

	private static final String json = "[{\"_type\":\"com.braintribe.cartridge.common.processing.configuration.url.model.RegistryEntry\",\"bindId\":\"model\",\"denotation\":{\"_type\":\"com.braintribe.model.meta.GmMetaModel\",\"name\":\"TestModel\"}}]";

	@Test
	public void testUrlResolutionWithUrl() throws Exception {
		File tempFile = File.createTempFile("json", ".properties");

		try {
			FileTools.writeStringToFile(tempFile, json);

			TribefireRuntime.setProperty("my-test-prop", tempFile.toURI().toString());
			
			AbstractUrlBasedConfigurator configurator = new AbstractUrlBasedConfigurator() {
				// @formatter:off
				@Override protected String buildDefaultFileName() { return "not used"; }
				@Override protected String buildUrlProperty() { return "my-test-prop"; }
				// @formatter:on
			};
			
			List<RegistryEntry> list = configurator.getEntries();
			
			assertThat(list).isNotNull().hasSize(1);
			assertThat(list.get(0)).isInstanceOf(RegistryEntry.class);
			assertThat(list.get(0).getBindId()).isEqualTo("model");
			
		} finally {
			FileTools.deleteFileSilently(tempFile);
		}
	}
	
	@Test
	public void testSharedUrlResolutionWithUrl() throws Exception {
		File tempFile = File.createTempFile("json", ".properties");

		try {
			FileTools.writeStringToFile(tempFile, json);

			TribefireRuntime.setProperty("TRIBEFIRE_CONFIGURATION_INJECTION_URL_SHARED", tempFile.toURI().toString());
			
			AbstractUrlBasedConfigurator configurator = new AbstractUrlBasedConfigurator() {
				// @formatter:off
				@Override protected String buildDefaultFileName() { return "not used"; }
				@Override protected String buildUrlProperty() { return "not used"; }
				// @formatter:on
			};
			
			List<RegistryEntry> list = configurator.getEntries();
			
			assertThat(list).isNotNull().hasSize(1);
			assertThat(list.get(0)).isInstanceOf(RegistryEntry.class);
			assertThat(list.get(0).getBindId()).isEqualTo("model");
			
		} finally {
			FileTools.deleteFileSilently(tempFile);
		}
	}

	@Test
	public void testMixedUrlResolutionWithUrl() throws Exception {
		File tempFile = File.createTempFile("json", ".properties");

		try {
			FileTools.writeStringToFile(tempFile, json);

			TribefireRuntime.setProperty("TRIBEFIRE_CONFIGURATION_INJECTION_URL_SHARED", tempFile.toURI().toString());
			TribefireRuntime.setProperty("my-test-prop", tempFile.toURI().toString());
			
			AbstractUrlBasedConfigurator configurator = new AbstractUrlBasedConfigurator() {
				// @formatter:off
				@Override protected String buildDefaultFileName() { return "not used"; }
				@Override protected String buildUrlProperty() { return "my-test-prop"; }
				// @formatter:on
			};
			
			List<RegistryEntry> list = configurator.getEntries();
			
			assertThat(list).isNotNull().hasSize(2);
			assertThat(list.get(0)).isInstanceOf(RegistryEntry.class);
			assertThat(list.get(1)).isInstanceOf(RegistryEntry.class);
			
			assertThat(list.get(0).getBindId()).isEqualTo("model");
			assertThat(list.get(1).getBindId()).isEqualTo("model");
			
		} finally {
			FileTools.deleteFileSilently(tempFile);
		}
	}

	@Test
	public void testUrlResolutionWithDefaultFilename() throws Exception {
		File tempFile = File.createTempFile("json", ".properties");

		try {
			FileTools.writeStringToFile(tempFile, json);

			AbstractUrlBasedConfigurator configurator = new AbstractUrlBasedConfigurator() {
				// @formatter:off
				@Override protected String buildDefaultFileName() { return tempFile.getAbsolutePath(); }
				@Override protected String buildUrlProperty() { return "undefined-prop"; }
				// @formatter:on
			};
			
			List<RegistryEntry> list = configurator.getEntries();
			
			assertThat(list).isNotNull().hasSize(1);
			assertThat(list.get(0)).isInstanceOf(RegistryEntry.class);
			assertThat(list.get(0).getBindId()).isEqualTo("model");
			
		} finally {
			FileTools.deleteFileSilently(tempFile);
		}
	}
	
	@Test
	public void testRegistration() throws Exception {
		File tempFile = File.createTempFile("json", ".properties");

		try {
			FileTools.writeStringToFile(tempFile, json);

			TribefireRuntime.setProperty("my-test-prop", tempFile.toURI().toString());
			
			AbstractUrlBasedConfigurator configurator = new AbstractUrlBasedConfigurator() {
				// @formatter:off
				@Override protected String buildDefaultFileName() { return "not used"; }
				@Override protected String buildUrlProperty() { return "my-test-prop"; }
				// @formatter:on
			};
			
			configurator.configure();
			
			GenericEntity entity = EnvironmentDenotationRegistry.getInstance().lookup("model");
			assertThat(entity).isNotNull();
			assertThat(entity).isNotNull().isInstanceOf(GmMetaModel.class);
			assertThat(((GmMetaModel) entity).getName()).isEqualTo("TestModel");
			
		} finally {
			FileTools.deleteFileSilently(tempFile);
		}
	}
}
