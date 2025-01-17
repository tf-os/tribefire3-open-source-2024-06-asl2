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
package com.braintribe.gwt.gme.constellation.client;

import java.util.function.Supplier;

import com.braintribe.gwt.gmview.ddsarequest.client.DdsaRequestExecution;
import com.braintribe.gwt.gmview.ddsarequest.client.DdsaRequestExecution.RequestExecutionData;
import com.braintribe.gwt.ioc.client.Required;
import com.braintribe.model.processing.notification.api.NotificationFactory;
import com.braintribe.model.processing.session.api.persistence.PersistenceGmSession;
import com.braintribe.model.processing.session.impl.persistence.TransientPersistenceGmSession;
import com.braintribe.model.processing.workbench.action.api.WorkbenchActionContext;
import com.braintribe.model.processing.workbench.action.api.WorkbenchActionHandler;
import com.braintribe.model.service.api.ServiceRequest;
import com.braintribe.model.workbench.ExecutionType;
import com.braintribe.model.workbench.ServiceRequestAction;

/**
 * Handler for handling {@link ServiceRequestAction} within the workbench.
 * @author michel.docouto
 *
 */
public class ServiceRequestActionHandler implements WorkbenchActionHandler<ServiceRequestAction> {
	
	private ExplorerConstellation explorerConstellation;
	private PersistenceGmSession gmSession;
	private Supplier<? extends TransientPersistenceGmSession> transientSessionProvider;
	private Supplier<? extends NotificationFactory> notificationFactorySupplier;
	
	/**
	 * Configures the required {@link ExplorerConstellation} used for showing the object created by the {@link ServiceRequestAction} evaluation.
	 */
	@Required
	public void setExplorerConstellation(ExplorerConstellation explorerConstellation) {
		this.explorerConstellation = explorerConstellation;
	}
	
	/**
	 * Configures the {@link PersistenceGmSession} needed for evaluating the ServiceRequest.
	 */
	@Required
	public void setGmSession(PersistenceGmSession gmSession) {
		this.gmSession = gmSession;
	}
	
	/**
	 * Configures the required {@link Supplier} for {@link TransientPersistenceGmSession} used for the service execution.
	 */
	@Required
	public void setTransientSessionProvider(Supplier<? extends TransientPersistenceGmSession> transientSessionProvider) {
		this.transientSessionProvider = transientSessionProvider;
	}
	
	/**
	 * Configures the {@link NotificationFactory} used for broadcasting a notification.
	 */
	@Required
	public void setNotificationFactory(Supplier<? extends NotificationFactory> notificationFactorySupplier) {
		this.notificationFactorySupplier = notificationFactorySupplier;
	}
	
	@Override
	public void perform(WorkbenchActionContext<ServiceRequestAction> workbenchActionContext) {
		ServiceRequestAction serviceRequestAction = workbenchActionContext.getWorkbenchAction();
		ServiceRequest serviceRequest = serviceRequestAction.getRequest();
		
		ExecutionType executionType = serviceRequestAction.getExecutionType();
		if (isEditable(executionType)) {
			explorerConstellation.handleServiceRequestPanel(serviceRequest, true, ExecutionType.autoEditable.equals(executionType),
					serviceRequestAction.getAutoPaging(), serviceRequestAction.getPagingEditable());
			return;
		}
		
		RequestExecutionData requestExecutionData = new RequestExecutionData(serviceRequestAction.getRequest(), gmSession,
				explorerConstellation.getTransientGmSession(), explorerConstellation, transientSessionProvider, notificationFactorySupplier);
		
		DdsaRequestExecution.executeRequest(requestExecutionData);
		return;
	}
	
	private boolean isEditable(ExecutionType executionType) {
		return ExecutionType.autoEditable.equals(executionType) || ExecutionType.editable.equals(executionType);
	}

}
