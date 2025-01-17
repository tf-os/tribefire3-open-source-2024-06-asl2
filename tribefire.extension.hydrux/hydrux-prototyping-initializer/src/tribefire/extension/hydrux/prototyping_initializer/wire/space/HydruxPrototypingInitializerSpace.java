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
package tribefire.extension.hydrux.prototyping_initializer.wire.space;

import com.braintribe.model.accessdeployment.smood.CollaborativeSmoodAccess;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.meta.GmMetaModel;
import com.braintribe.model.processing.meta.editor.ModelMetaDataEditor;
import com.braintribe.model.service.api.ServiceRequest;
import com.braintribe.wire.api.annotation.Import;
import com.braintribe.wire.api.annotation.Managed;

import tribefire.cortex.initializer.support.wire.space.AbstractInitializerSpace;
import tribefire.extension.hydrux.model.data.HxNamedEntity;
import tribefire.extension.hydrux.model.deployment.HxApplication;
import tribefire.extension.hydrux.model.deployment.HxScope;
import tribefire.extension.hydrux.model.deployment.prototyping.HxMainView;
import tribefire.extension.hydrux.prototyping_initializer.wire.contract.HydruxPrototypingInitializerContract;
import tribefire.module.wire.contract.ModelApiContract;

@Managed
public class HydruxPrototypingInitializerSpace extends AbstractInitializerSpace implements HydruxPrototypingInitializerContract {

	private static final String PROTOTYPING_ACCESS_ID = HxMainView.PROTOTYPING_DOMAIN_ID;

	@Import
	private ModelApiContract modelApi;

	@Managed
	@Override
	public CollaborativeSmoodAccess access() {
		CollaborativeSmoodAccess bean = create(CollaborativeSmoodAccess.T);
		bean.setExternalId(PROTOTYPING_ACCESS_ID);
		bean.setName("Hydrux Prototyping Access");
		bean.setMetaModel(configuredDataModel());
		bean.setServiceModel(configuredServiceModel());

		return bean;
	}

	@Managed
	private GmMetaModel configuredDataModel() {
		GmMetaModel bean = create(GmMetaModel.T);
		bean.setName("tribefire.extension.hydrux:hydrux-prototyping-configured-data-model");
		bean.getDependencies().add(modelOf(HxNamedEntity.T));

		return bean;
	}

	@Managed
	private GmMetaModel configuredServiceModel() {
		GmMetaModel bean = create(GmMetaModel.T);
		bean.setName("tribefire.extension.hydrux:hydrux-prototyping-configured-service-model");
		bean.getDependencies().add(serviceApiModel());

		configureHxMetaData(bean);

		return bean;
	}

	private GmMetaModel serviceApiModel() {
		return modelOf(ServiceRequest.T);
	}

	private void configureHxMetaData(GmMetaModel model) {
		ModelMetaDataEditor mdEditor = modelApi.newMetaDataEditor(model).done();
		mdEditor.addModelMetaData(defaultHxApplication());
	}

	@Managed
	private HxApplication defaultHxApplication() {
		HxApplication bean = create(HxApplication.T);
		bean.setRootScope(hxDefaultScope());
		bean.setTitle("Hydrux Prototyping Demo Application");
		bean.setApplicationId("hx-demo-default");
		bean.setView(hxMainView());
		bean.setConflictPriority(-100d);

		return bean;
	}

	@Managed
	private HxMainView hxMainView() {
		HxMainView bean = create(HxMainView.T);
		// we do not set the module, that must be set dynamically, because this must work with any module

		return bean;
	}

	@Managed
	private HxScope hxDefaultScope() {
		HxScope bean = create(HxScope.T);
		bean.setName("root");

		return bean;
	}

	private GmMetaModel modelOf(EntityType<?> type) {
		return initializerSupport.session().getEntityByGlobalId(type.getModel().globalId());
	}

}
