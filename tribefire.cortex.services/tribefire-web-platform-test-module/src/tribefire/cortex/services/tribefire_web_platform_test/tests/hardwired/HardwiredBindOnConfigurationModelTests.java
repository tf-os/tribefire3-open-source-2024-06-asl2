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
import static com.braintribe.utils.lcd.CollectionTools2.first;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;

import com.braintribe.model.accessapi.QueryAndSelect;
import com.braintribe.model.accessapi.QueryEntities;
import com.braintribe.model.accessapi.QueryProperty;
import com.braintribe.model.accessapi.QueryRequest;
import com.braintribe.model.extensiondeployment.meta.ProcessWith;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.Model;
import com.braintribe.model.meta.GmMetaModel;
import com.braintribe.model.processing.meta.cmd.CmdResolver;
import com.braintribe.model.processing.meta.cmd.CmdResolverImpl;
import com.braintribe.model.processing.meta.cmd.extended.EntityMdDescriptor;
import com.braintribe.model.processing.meta.oracle.BasicModelOracle;
import com.braintribe.model.processing.session.api.persistence.PersistenceGmSession;
import com.braintribe.model.query.EntityQueryResult;
import com.braintribe.model.query.PropertyQueryResult;
import com.braintribe.model.query.SelectQueryResult;

import tribefire.cortex.services.tribefire_web_platform_test.tests.PlatformHolder;
import tribefire.module.wire.contract.TribefireWebPlatformContract;

/**
 * @author peter.gazdik
 */
public class HardwiredBindOnConfigurationModelTests {

	private static final String CONFIGURATION_MODEL_NAME = "tribefire.platform:configuration-model-for-test";

	private TribefireWebPlatformContract platform;

	@Before
	public void setup() {
		platform = PlatformHolder.platformContract;
	}

	// ################################################
	// ## . . . . . . . Module Loading . . . . . . . ##
	// ################################################

	public static void bindHardwired(TribefireWebPlatformContract tfPlatform) {
		tfPlatform.hardwiredDeployables().bindOnNewConfigurationModel(CONFIGURATION_MODEL_NAME) //
				.serviceProcessor("test:serviceProcessor:bound-on-conf-model:select", "Select Query Processor Bound on Config Model", //
						QueryAndSelect.T, (ctx, r) -> SelectQueryResult.T.create());

		tfPlatform.hardwiredDeployables().bindOnConfigurationModel(CONFIGURATION_MODEL_NAME) //
				.serviceProcessor("test:serviceProcessor:bound-on-conf-model:entity", "Entity Query Processor Bound on Config Model", //
						QueryEntities.T, (ctx, r) -> EntityQueryResult.T.create());

		tfPlatform.hardwiredDeployables().bindOnExistingConfigurationModel(CONFIGURATION_MODEL_NAME) //
				.serviceProcessor("test:serviceProcessor:bound-on-conf-model:property", "Property Query Processor Bound on Config Model", //
						QueryProperty.T, (ctx, r) -> PropertyQueryResult.T.create());
	}

	// ################################################
	// ## . . . . . . . . . Tests . . . . . . . . . .##
	// ################################################

	@Test
	public void configuredOnNewModel() throws Exception {
		PersistenceGmSession cortexSession = platform.systemUserRelated().cortexSessionSupplier().get();

		GmMetaModel model = cortexSession.findEntityByGlobalId(Model.modelGlobalId(CONFIGURATION_MODEL_NAME));
		assertThat(model).isNotNull();

		assertThat(model.getName()).isEqualTo(CONFIGURATION_MODEL_NAME);

		assertThat(model.getDependencies()).isNotEmpty();
		assertThat(first(model.getDependencies()).getName()).isEqualTo(QueryAndSelect.T.getModel().name());

		CmdResolver cmdResolver = CmdResolverImpl.create(new BasicModelOracle(model)).done();

		checkProcessWithOn(cmdResolver, model, QueryAndSelect.T);
		checkProcessWithOn(cmdResolver, model, QueryEntities.T);
		checkProcessWithOn(cmdResolver, model, QueryProperty.T);
	}

	private void checkProcessWithOn(CmdResolver cmdResolver, GmMetaModel model, EntityType<? extends QueryRequest> requestType) {
		EntityMdDescriptor md = cmdResolver.getMetaData().entityType(requestType).meta(ProcessWith.T).exclusiveExtended();

		assertThat(md).isNotNull();
		assertThat(md.getOwnerModel()).isEqualTo(model);
	}

}
