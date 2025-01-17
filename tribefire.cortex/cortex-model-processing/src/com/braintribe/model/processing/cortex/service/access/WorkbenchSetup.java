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
package com.braintribe.model.processing.cortex.service.access;

import com.braintribe.logging.Logger;
import com.braintribe.model.accessdeployment.IncrementalAccess;
import com.braintribe.model.accessdeployment.smood.CollaborativeSmoodAccess;
import com.braintribe.model.cortexapi.access.SetupAccessResponse;
import com.braintribe.model.deploymentapi.request.DeployWithDeployables;
import com.braintribe.model.generic.reflection.Model;
import com.braintribe.model.meta.GmMetaModel;
import com.braintribe.model.notification.Level;
import com.braintribe.model.processing.cortex.service.ServiceBase;
import com.braintribe.model.processing.notification.api.builder.Notifications;
import com.braintribe.model.processing.session.api.persistence.PersistenceGmSession;
import com.braintribe.model.processing.session.api.transaction.NestedTransaction;
import com.braintribe.model.processing.session.api.transaction.Transaction;
import com.braintribe.utils.CollectionTools;

public class WorkbenchSetup extends ServiceBase{

	private static final Logger logger = Logger.getLogger(WorkbenchSetup.class);
	
	private final PersistenceGmSession session;
	private final IncrementalAccess access;
	private final String baseWorkbenchModelName;
	private final String baseWorkbenchAccessId;
	

	public WorkbenchSetup(PersistenceGmSession session, IncrementalAccess access, String baseWorkbenchModelName, String baseWorkbenchAccessId) {
		this.session = session;
		this.access = access;
		this.baseWorkbenchModelName = baseWorkbenchModelName;
		this.baseWorkbenchAccessId = baseWorkbenchAccessId;
	}
	
	
	public SetupAccessResponse run(boolean resetAccess, boolean resetModel) {

		GmMetaModel dataModel = access.getMetaModel();
		GmMetaModel serviceModel = access.getServiceModel();
		GmMetaModel workbenchModelBase = findModel(session, baseWorkbenchModelName, true);
		String wbModelName = createWbModelName(dataModel.getName());
		
		IncrementalAccess wbAccess = null;
		IncrementalAccess existingWbAccess = access.getWorkbenchAccess();
		String wbExternalid = access.getExternalId()+".wb";
		
		if (existingWbAccess == null) {
			notifyInfo("No workbench access assigned to access: "+access.getExternalId(), logger);
			existingWbAccess = findAccess(session, wbExternalid);
			if (existingWbAccess != null) {
				notifyInfo("Reuse existing WorkbenchAccess: "+existingWbAccess.getExternalId()+" which was not assigned to access: "+access.getExternalId(),logger);
			}
		}

		
		Transaction transaction = session.getTransaction();
		NestedTransaction nestedTransaction = transaction.beginNestedTransaction();
		
		if (existingWbAccess == null) {
			wbAccess = session.create(CollaborativeSmoodAccess.T,"workbench:access/"+wbExternalid);
			notifyInfo("Created new WorkbenchAccess for access: "+access.getExternalId(),logger);
		} else {
			if (resetAccess) {
				notifyInfo("Reuse existing WorkbenchAccess: "+existingWbAccess.getExternalId()+" of access: "+access.getExternalId()+"  (reset=true)",logger);
				wbAccess = existingWbAccess;
			} else {
				nestedTransaction.rollback();
				return createResponse("There is already a WorkbenchAccess configured for: "+access.getExternalId(), Level.WARNING, SetupAccessResponse.T);
			}
		}

		GmMetaModel wbModel = null;
		GmMetaModel existingWbModel = findModel(session, wbModelName, false);
		if (existingWbModel == null) {
			wbModel = session.create(GmMetaModel.T, Model.modelGlobalId(wbModelName));
			notifyInfo("Created new WorkbenchModel: "+wbModelName+" for access: "+access.getExternalId(),logger);
		} else {
			if (resetModel) {
				notifyInfo("Reuse existing WorkbenchModel: "+existingWbModel.getName()+"  (reset=true)",logger);
				wbModel = existingWbModel;
			} else {
				nestedTransaction.rollback();
				return createResponse("A WorkbenchModel with name: "+wbModelName+ " already exists.", Level.WARNING, SetupAccessResponse.T);
			}
		}
		
		nestedTransaction.commit();
		
		wbModel.setName(wbModelName);
		wbModel.setVersion(dataModel.getVersion());
		wbModel.getDependencies().clear();
		wbModel.getDependencies().add(dataModel);
		wbModel.getDependencies().add(workbenchModelBase);
		if (serviceModel != null) {
			wbModel.getDependencies().add(serviceModel);
		}
		
		wbAccess.setExternalId(wbExternalid);
		wbAccess.setName(createWbAccessName(access.getName()));
		wbAccess.setMetaModel(wbModel);
		
		IncrementalAccess baseWorkbenchAccess = findAccess(session, baseWorkbenchAccessId);
		if (baseWorkbenchAccess != null) {
			wbAccess.setWorkbenchAccess(baseWorkbenchAccess);
		}
		
		access.setWorkbenchAccess(wbAccess);

		// @formatter:off
		addNotifications(
				Notifications.build()
				.add()
					.command().refresh("Refresh Access")
				.close()
				.list()
			);
		// @formatter:on
		
		// DEVCX-595 automatically commmit and deploy after setup
		session.commit();
		
		DeployWithDeployables deploy = DeployWithDeployables.T.create();
		deploy.setDeployables((CollectionTools.getSet(wbAccess)));
		deploy.eval(session).get();
		
		return createResponse("Ensured workbench for access: "+access.getExternalId(), SetupAccessResponse.T);

	}
	
	// hook for chaining the setup step silently
	protected IncrementalAccess runEmbedded(boolean resetAccess, boolean resetModel) {
		run(resetAccess, resetModel);
		return access;
	}
	
	private String createWbModelName(String name) {
		String baseName = name;
		if (baseName.toLowerCase().endsWith("model")) {
			baseName = baseName.substring(0,baseName.length()-"model".length());
		}
		return baseName+"WorkbenchModel";
	}
	
	private String createWbAccessName(String name) {
		String baseName = name;
		if (baseName.toLowerCase().endsWith("access")) {
			baseName = baseName.substring(0,baseName.length()-"access".length());
		}
		return baseName+"Workbench Access";
	}


}
