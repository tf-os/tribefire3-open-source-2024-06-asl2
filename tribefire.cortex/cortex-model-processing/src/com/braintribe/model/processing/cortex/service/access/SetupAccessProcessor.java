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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.braintribe.cfg.Configurable;
import com.braintribe.cfg.Required;
import com.braintribe.logging.Logger;
import com.braintribe.model.cortexapi.access.ConfigureWorkbench;
import com.braintribe.model.cortexapi.access.ExplorerStyle;
import com.braintribe.model.cortexapi.access.SetupAccessRequest;
import com.braintribe.model.cortexapi.access.SetupAccessResponse;
import com.braintribe.model.cortexapi.access.SetupAspects;
import com.braintribe.model.cortexapi.access.SetupWorkbench;
import com.braintribe.model.extensiondeployment.AccessAspect;
import com.braintribe.model.processing.accessrequest.api.AccessRequestContext;
import com.braintribe.model.processing.accessrequest.api.AccessRequestProcessor;
import com.braintribe.model.processing.accessrequest.api.AccessRequestProcessors;
import com.braintribe.model.processing.session.api.persistence.PersistenceGmSessionFactory;
import com.braintribe.model.workbench.instruction.WorkbenchInstruction;

import tribefire.cortex._WorkbenchModel_;

public class SetupAccessProcessor implements AccessRequestProcessor<SetupAccessRequest, SetupAccessResponse> {
	
	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(SetupAccessProcessor.class);
	private String baseWorkbenchModelName = _WorkbenchModel_.reflection.name();
	private String baseWorkbenchAccessId = "workbench";
	private List<AccessAspect> defaultAspects;
	private PersistenceGmSessionFactory sessionFactory;
	private List<WorkbenchInstruction> standardWorkbenchInstructions;
	private Map<ExplorerStyle, List<WorkbenchInstruction>> styleInstructions = new HashMap<>();
	
	private final AccessRequestProcessor<SetupAccessRequest, SetupAccessResponse> dispatcher = AccessRequestProcessors.dispatcher(config->{
		config.register(SetupWorkbench.T, this::setupWorkbench);
		config.register(SetupAspects.T, this::setupAspects);
		config.register(ConfigureWorkbench.T, this::configureWorkbench);
	});

	
	@Required
	@Configurable
	public void setDefaultAspects(List<AccessAspect> defaultAspects) {
		this.defaultAspects = defaultAspects;
	}
	
	@Configurable
	public void setBaseWorkbenchModelName(String baseWorkbenchModelName) {
		this.baseWorkbenchModelName = baseWorkbenchModelName;
	}
	
	@Configurable
	public void setBaseWorkbenchAccessId(String baseWorkbenchAccessId) {
		this.baseWorkbenchAccessId = baseWorkbenchAccessId;
	}
	
	@Configurable
	@Required
	public void setSessionFactory(PersistenceGmSessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	@Configurable
	@Required
	public void setStandardWorkbenchInstructions(List<WorkbenchInstruction> standardWorkbenchInstructions) {
		this.standardWorkbenchInstructions = standardWorkbenchInstructions;
	}
	
	@Configurable
	public void setStyleInstructions(Map<ExplorerStyle, List<WorkbenchInstruction>> styleInstructions) {
		this.styleInstructions = styleInstructions;
	}
	
	@Override
	public SetupAccessResponse process(AccessRequestContext<SetupAccessRequest> context) {
		return dispatcher.process(context);
	}
	
	public SetupAccessResponse setupWorkbench(AccessRequestContext<SetupWorkbench> context) {
		SetupWorkbench request = context.getRequest();
		WorkbenchSetup setup = new WorkbenchSetup(
			context.getSession(), 
			request.getAccess(),
			baseWorkbenchModelName, 
			baseWorkbenchAccessId);
		
		return setup.run(
				request.getResetExistingAccess(), 
				request.getResetExistingModel());
	}

	public SetupAccessResponse setupAspects(AccessRequestContext<SetupAspects> context) {
		SetupAspects request = context.getRequest();
		AspectSetup setup = 
			new AspectSetup(
				context.getSession(),
				request.getAccess(),
				defaultAspects);
		
		return setup.run(
				request.getResetToDefault());
	}

	public SetupAccessResponse configureWorkbench(AccessRequestContext<ConfigureWorkbench> context) {
		ConfigureWorkbench request = context.getRequest();
		WorkbenchConfiguration configuration = 
			new WorkbenchConfiguration(
				request.getAccess(),
				standardWorkbenchInstructions,
				styleInstructions,
				sessionFactory);
		
		return configuration.run(
				request.getEnsureStandardFolders(), 
				request.getExplorerStyle(), 
				request.getInstructions());
	}

}
