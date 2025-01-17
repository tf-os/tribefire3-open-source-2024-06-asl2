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
package com.braintribe.model.processing.accessory.test.cortex;

import static com.braintribe.model.processing.accessory.test.cortex.MaTestConstants.CORTEX_MODEL_NAME;
import static com.braintribe.model.processing.accessory.test.cortex.MaTestConstants.CORTEX_SERVICE_MODEL_NAME;
import static com.braintribe.utils.lcd.CollectionTools2.asList;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import com.braintribe.model.access.collaboration.persistence.ModelsPersistenceInitializer;
import com.braintribe.model.accessapi.QueryRequest;
import com.braintribe.model.accessdeployment.IncrementalAccess;
import com.braintribe.model.extensiondeployment.meta.StreamWith;
import com.braintribe.model.generic.builder.meta.MetaModelBuilder;
import com.braintribe.model.meta.GmMetaModel;
import com.braintribe.model.resource.Resource;
import com.braintribe.model.service.api.ServiceRequest;

/**
 * @author peter.gazdik
 */
public class MaInit_1A_CortexModel extends ModelsPersistenceInitializer {

	private final String[] cortexDataModelDeps = { //
			GmMetaModel.T.getModel().name(), // meta-model
			IncrementalAccess.T.getModel().name(), // access-deployment-model
			ServiceRequest.T.getModel().name(), // service-domain-model
			Resource.T.getModel().name(), // resource-model
			StreamWith.T.getModel().name(), // extension-deployment-model
			QueryRequest.T.getModel().name() // access-api-model
	};

	private final String[] cortexServiceModelDeps = { //
			ServiceRequest.T.getModel().name() // service-api-model
	};

	@Override
	protected Collection<GmMetaModel> getModels() {
		return asList(cortexModel(), cortexServiceModel());
	}

	private GmMetaModel cortexModel() {
		List<GmMetaModel> cortexDeps = toGmModels(Stream.of(cortexDataModelDeps));

		GmMetaModel cortexModel = MetaModelBuilder.metaModel(CORTEX_MODEL_NAME);
		cortexModel.setDependencies(cortexDeps);

		return cortexModel;
	}

	private GmMetaModel cortexServiceModel() {
		List<GmMetaModel> cortexDeps = toGmModels(Stream.of(cortexServiceModelDeps));

		GmMetaModel cortexModel = MetaModelBuilder.metaModel(CORTEX_SERVICE_MODEL_NAME);
		cortexModel.setDependencies(cortexDeps);

		return cortexModel;
	}

}
