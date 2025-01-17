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

import static com.braintribe.testing.junit.assertions.gm.assertj.core.api.GmAssertions.assertThat;

import org.junit.Before;
import org.junit.Test;

import com.braintribe.model.accessdeployment.smood.CollaborativeSmoodAccess;
import com.braintribe.model.generic.reflection.Model;
import com.braintribe.model.meta.GmMetaModel;
import com.braintribe.model.meta.data.display.Color;
import com.braintribe.model.processing.session.api.collaboration.PersistenceInitializationContext;
import com.braintribe.model.processing.session.api.managed.ManagedGmSession;
import com.braintribe.model.processing.session.api.managed.ModelAccessory;
import com.braintribe.model.processing.session.api.managed.ModelAccessoryFactory;
import com.braintribe.model.user.User;

import tribefire.cortex.services.model.test.hardwired.mdselector.CustomMdSelector4Test;
import tribefire.cortex.services.tribefire_web_platform_test.impl.hardwired.CustomMdSelector4TestExpert;
import tribefire.cortex.services.tribefire_web_platform_test.tests.PlatformHolder;
import tribefire.module.api.InitializerBindingBuilder;
import tribefire.module.wire.contract.TribefireWebPlatformContract;
import tribefire.module.wire.contract.UserRelated;

/**
 * @author peter.gazdik
 */
public class HardwiredCmdSelectorExpertTests {

	private static final String TEST_ACCESS_NAME = "cmd-selector-expert-test-access";
	private static final String TEST_MODEL_NAME = "test:cmd-selector-expert-test-model";
	private static final String TEST_MODEL_GID = Model.modelGlobalId(TEST_MODEL_NAME);

	private static final String COLOR_MD_GLOBAL_ID = "test:md:cmd-selector-expert-color";
	private static final String CUSTOM_MD_SELECTOR_GLOBAL_ID = "test:selector:custom-expert";

	private TribefireWebPlatformContract platform;

	@Before
	public void setup() {
		platform = PlatformHolder.platformContract;
	}

	// ################################################
	// ## . . . . . . . Module Loading . . . . . . . ##
	// ################################################

	public static void bindHardwired(TribefireWebPlatformContract tfPlatform) {
		tfPlatform.hardwiredExperts().bindMetaDataSelectorExpert(CustomMdSelector4Test.T, new CustomMdSelector4TestExpert());
	}

	public static void bindInitializers(InitializerBindingBuilder bindings) {
		bindings.bind("cortex", HardwiredCmdSelectorExpertTests::createCmdSelectorExpertTestAccess);
	}

	private static void createCmdSelectorExpertTestAccess(PersistenceInitializationContext ctx) {
		ManagedGmSession session = ctx.getSession();

		CustomMdSelector4Test selector = session.create(CustomMdSelector4Test.T, CUSTOM_MD_SELECTOR_GLOBAL_ID);
		selector.setIsActive(true);

		Color color = session.create(Color.T, COLOR_MD_GLOBAL_ID);
		color.setSelector(selector);

		Model uModel = User.T.getModel();
		GmMetaModel userModel = session.getEntityByGlobalId(uModel.globalId());

		GmMetaModel model = session.create(GmMetaModel.T, TEST_MODEL_GID);
		model.setName(TEST_MODEL_NAME);
		model.getDependencies().add(userModel);
		model.setVersion("1.0");
		model.getMetaData().add(color);

		CollaborativeSmoodAccess sa = session.create(CollaborativeSmoodAccess.T, "access:custom-cmd-selector-expert-test");
		sa.setExternalId(TEST_ACCESS_NAME);
		sa.setName(TEST_ACCESS_NAME);
		sa.setMetaModel(model);
	}

	// ################################################
	// ## . . . . . . . . . Tests . . . . . . . . . .##
	// ################################################

	// also test that a custom model with a custom created CmdResolver has the selector expert

	@Test
	public void customMdSelectorOnAccessModel() throws Exception {
		checkCustomSelectorMdOk(platform.systemUserRelated());
		checkCustomSelectorMdOk(platform.requestUserRelated());
	}

	private void checkCustomSelectorMdOk(UserRelated userRelated) {
		ModelAccessoryFactory maf = userRelated.modelAccessoryFactory();
		ModelAccessory ma = maf.getForAccess(TEST_ACCESS_NAME);

		assertThat(modelColor(ma)).isNotNull();
	}

	private Color modelColor(ModelAccessory ma) {
		return ma.getCmdResolver().getMetaData().meta(Color.T).exclusive();
	}

}
