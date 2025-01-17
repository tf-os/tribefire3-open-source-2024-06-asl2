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
package com.braintribe.model.processing.accessory.test.wire.space;

import static com.braintribe.model.processing.accessory.impl.MdPerspectiveRegistry.DOMAIN_BASIC;
import static com.braintribe.model.processing.accessory.impl.MdPerspectiveRegistry.DOMAIN_ESSENTIAL;
import static com.braintribe.model.processing.accessory.impl.MdPerspectiveRegistry.PERSPECTIVE_PERSISTENCE_SESSION;
import static com.braintribe.model.processing.accessory.impl.MdPerspectiveRegistry.testMdDeclaredInModel;
import static com.braintribe.model.processing.accessory.test.cortex.MaTestConstants.CORTEX_MODEL_NAME;
import static com.braintribe.utils.lcd.CollectionTools2.asList;

import java.io.File;
import java.util.List;

import com.braintribe.gm._BasicMetaModel_;
import com.braintribe.model.access.smood.collaboration.deployment.CsaBuilder;
import com.braintribe.model.access.smood.collaboration.deployment.CsaDeployedUnit;
import com.braintribe.model.csa.CollaborativeSmoodConfiguration;
import com.braintribe.model.csa.ManInitializer;
import com.braintribe.model.generic.GMF;
import com.braintribe.model.meta.data.constraint.Mandatory;
import com.braintribe.model.processing.accessory.impl.DynamicModelAccessory;
import com.braintribe.model.processing.accessory.impl.MdPerspectiveRegistry;
import com.braintribe.model.processing.accessory.impl.PlatformModelAccessoryFactory;
import com.braintribe.model.processing.accessory.impl.PmeSupplierFromCortex;
import com.braintribe.model.processing.accessory.test.cortex.MaInit_1A_CortexModel;
import com.braintribe.model.processing.accessory.test.cortex.MaInit_1B_CustomModel;
import com.braintribe.model.processing.accessory.test.cortex.MaInit_2A_AccessAndServiceDomain;
import com.braintribe.model.processing.accessory.test.cortex.MaInit_3A_ExtensionMetaData;
import com.braintribe.model.processing.accessory.test.cortex.MaInit_4A_EssentialAndNonEssentialMd;
import com.braintribe.model.processing.accessory.test.wire.contract.MaTestContract;
import com.braintribe.model.processing.session.api.collaboration.PersistenceInitializer;
import com.braintribe.wire.api.annotation.Managed;

/**
 * @author peter.gazdik
 */
@Managed
public class MaTestSpace implements MaTestContract {

	@Override
	@Managed
	public PlatformModelAccessoryFactory platformModelAccessoryFactory() {
		PlatformModelAccessoryFactory bean = new PlatformModelAccessoryFactory();
		bean.setModelEssentialsSupplier(platformModelEssentialsSupplier());
		bean.setCortexSessionSupplier(() -> cortexCsaDu().newSession());

		return bean;
	}

	@Override
	@Managed
	public PmeSupplierFromCortex platformModelEssentialsSupplier() {
		PmeSupplierFromCortex bean = new PmeSupplierFromCortex();
		bean.setCortexSupplier(() -> cortexCsaDu().csa);
		bean.setMdPerspectiveRegistry(mdPerspectiveRegistry());

		return bean;
	}

	@Managed
	private MdPerspectiveRegistry mdPerspectiveRegistry() {
		MdPerspectiveRegistry bean = new MdPerspectiveRegistry();
		bean.extendMdDomain(DOMAIN_ESSENTIAL, testMdDeclaredInModel(Mandatory.T.getModel()));
		bean.extendMdDomain(DOMAIN_BASIC, testMdDeclaredInModel(GMF.getTypeReflection().getModel(_BasicMetaModel_.reflection.name())));
		bean.extendModelPerspective(PERSPECTIVE_PERSISTENCE_SESSION, DOMAIN_ESSENTIAL, DOMAIN_BASIC);

		return bean;
	}

	private CollaborativeSmoodConfiguration prepareNewConfiguration() {
		ManInitializer manInitializer = ManInitializer.T.create();
		manInitializer.setName("trunk");

		CollaborativeSmoodConfiguration configuration = CollaborativeSmoodConfiguration.T.create();
		configuration.getInitializers().add(manInitializer);
		return configuration;
	}

	@Override
	@Managed
	public CsaDeployedUnit cortexCsaDu() {
		/* Calling postConstruct on CSA leads to this method, because we call CSA.onModelAccessoryOutdated, which leads to
		 * PlatformModelEssentialsSupplier.ComponentModelResolution, which uses the cortexSessionSupplier, i.e. () -> cortexCsaDu().newSession() */
		/* We can therefore only call this after we have assigned a value to the bean in this method, otherwise Wire sees no value was set yet and
		 * tries to create a new CsaDeployedUnit, and we end up in an infinite loop. */

		CsaDeployedUnit bean = CsaBuilder.create() //
				.baseFolder(baseFolder()) //
				.cortex(true) //
				.mergeModelAndData(true) //
				.staticInitializers(staticCortexInitializers()) //
				.configurationSupplier(this::prepareNewConfiguration) //
				.selfModelName(CORTEX_MODEL_NAME) //
				.modelAccessory(cortexDynamicModelAccessory()) //
				.skipPostConstruct(true) //
				.done();

		bean.postConstruct();

		return bean;
	}

	@Managed
	private List<PersistenceInitializer> staticCortexInitializers() {
		return asList( //
				new MaInit_1A_CortexModel(), //
				new MaInit_1B_CustomModel(), //
				new MaInit_2A_AccessAndServiceDomain(), //
				new MaInit_3A_ExtensionMetaData(), //
				new MaInit_4A_EssentialAndNonEssentialMd() //
		);
	}

	@Managed
	private File baseFolder() {
		return new File("work/cortex");
	}

	@Managed
	public DynamicModelAccessory cortexDynamicModelAccessory() {
		DynamicModelAccessory bean = new DynamicModelAccessory(() -> platformModelAccessoryFactory().getForAccess("cortex"));
		return bean;
	}

}
