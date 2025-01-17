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
package com.braintribe.model.processing.cortex.service.model;

import java.util.List;

import com.braintribe.model.cortexapi.model.AddDependencies;
import com.braintribe.model.cortexapi.model.AddToCortexModel;
import com.braintribe.model.cortexapi.model.DependenciesAdded;
import com.braintribe.model.cortexapi.model.MergeModelsResponse;
import com.braintribe.model.meta.GmMetaModel;
import com.braintribe.model.modelnotification.OnModelChanged;
import com.braintribe.model.notification.Level;
import com.braintribe.model.notification.Notification;
import com.braintribe.model.processing.cortex.CortexModelNames;
import com.braintribe.model.processing.cortex.service.ServiceBase;
import com.braintribe.model.processing.query.fluent.EntityQueryBuilder;
import com.braintribe.model.processing.session.api.persistence.PersistenceGmSession;
import com.braintribe.model.query.EntityQuery;

public class CortexModelAdder extends ServiceBase {
	
	private AddToCortexModel request;
	private PersistenceGmSession session;
	private String targetModelName;

	public CortexModelAdder(AddToCortexModel request, PersistenceGmSession session) {
		this(request,session,CortexModelNames.TF_CORTEX_MODEL_NAME);
	}
	public CortexModelAdder(AddToCortexModel request, PersistenceGmSession session, String targetModelName) {
		this.request = request;
		this.session = session;
		this.targetModelName = targetModelName;
	}
	
	public MergeModelsResponse run() {
		List<GmMetaModel> modelsToAdd = request.getModels();

		MergeModelsResponse addingResponse = addDependencies(modelsToAdd);
		
		if (!(addingResponse instanceof DependenciesAdded)) {
			return addingResponse;
		}

		notifyInfo("Added "+modelsToAdd.size()+" dependencies to: "+targetModelName);
		for (GmMetaModel modelToAdd : modelsToAdd) {
			modelToAdd.deploy();
		}
		notifyInfo("Ensured types for "+modelsToAdd.size()+" models.");
		informModelChanged(targetModelName);
		notifyInfo("Refreshed model "+targetModelName);

		
		List<Notification> notifications = 
				com.braintribe.model.processing.notification.api.builder.Notifications.build()
					.add()
						.message().confirmInfo("Successfully added "+modelsToAdd.size()+" dependencies to "+targetModelName+"!\n\nPlease consider reloading the ControlCenter!\n\n Do you want to reload now?")
						.command().reload("Reload ControlCenter")
					.close()
				.list();
		
		
		return createResponse("Successfully added.", Level.INFO, false, DependenciesAdded.T, notifications);
	}

	private MergeModelsResponse addDependencies(List<GmMetaModel> modelsToAdd) {
		AddDependencies addDependencies = AddDependencies.T.create();
		addDependencies.setDependencies(modelsToAdd);
		addDependencies.setModel(findModel(targetModelName));
		
		return addDependencies.eval(session).get();
	}

	private void informModelChanged(String modelName) {
		OnModelChanged request = OnModelChanged.T.create();
		request.setModelName(modelName);
		request.eval(session).get();
	}

	
	private GmMetaModel findModel(String modelName) {
		EntityQuery modelQuery = EntityQueryBuilder.from(GmMetaModel.T).where().property("name").eq(modelName).done();
		return session.query().entities(modelQuery).unique();
	}

}
