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
package com.braintribe.gwt.ioc.gme.client.expert;

import java.util.function.Function;

import com.braintribe.gwt.async.client.Future;
import com.braintribe.gwt.ioc.client.Required;
import com.braintribe.gwt.logging.client.Logger;
import com.braintribe.gwt.logging.client.Profiling;
import com.braintribe.gwt.logging.client.ProfilingHandle;
import com.braintribe.model.accessapi.ModelEnvironment;
import com.braintribe.model.generic.GMF;
import com.braintribe.model.generic.GmfException;
import com.braintribe.model.generic.reflection.GenericModelTypeReflection;
import com.braintribe.model.meta.GmMetaModel;
import com.braintribe.model.meta.data.prompt.Visible;
import com.braintribe.model.processing.session.api.persistence.ModelEnvironmentDrivenGmSession;
import com.google.gwt.core.client.Scheduler;

/**
 * Expert responsible for changing the accessId, and updating all model environments within the sessions.
 * @author michel.docouto
 *
 */
public class AccessIdChanger implements Function<String, Future<Boolean>> {
	
	private Function<String, Future<ModelEnvironment>> modelEnvironmentChanger;
	private ModelEnvironmentDrivenGmSession gmSession;
	private static final GenericModelTypeReflection typeReflection =  GMF.getTypeReflection();
	private static Logger logger = new Logger(AccessIdChanger.class);
	
	/**
	 * Configures the expert used for changing the modelEnvironment, in case the received session has a different access.
	 */
	@Required
	public void setModelEnvironmentChanger(Function<String, Future<ModelEnvironment>> modelEnvironmentChanger) {
		this.modelEnvironmentChanger = modelEnvironmentChanger;
	}
	
	/**
	 * Configures the required session which will be updated.
	 */
	@Required
	public void setGmSession(ModelEnvironmentDrivenGmSession gmSession) {
		this.gmSession = gmSession;
	}
	
	@Override
	public Future<Boolean> apply(String accessId) {
		Future<Boolean> future = new Future<>();
		modelEnvironmentChanger.apply(accessId).andThen(me -> {
			exchangeModelEnvironment(me).get(future);
		});
		
		return future;
	}
	
	private Future<Boolean> exchangeModelEnvironment(ModelEnvironment modelEnvironment) {
		Future<Boolean> future = new Future<>();
		ensureModelTypes(modelEnvironment) //
		.andThen(result -> {
			gmSession.configureModelEnvironment(modelEnvironment, com.braintribe.processing.async.api.AsyncCallback.of( //
					v -> {
						if (!isModelVisible()) {
							future.onSuccess(false);
							return;
						}

						//if (modelEnvironmentDrivenSessionUpdater != null)
							//modelEnvironmentDrivenSessionUpdater.updateModelEnvironment(modelEnvironment);
						handleModelEnvironmentSet().get(future);

					}, future::onFailure));
		}).onError(future::onFailure);
		
		return future;
	}
	
	private Future<Void> ensureModelTypes(final ModelEnvironment modelEnvironment) {
		GmMetaModel serviceModel = modelEnvironment.getServiceModel();
		
		try {
			if (modelEnvironment.getDataModel() != null) {
				ProfilingHandle ph = Profiling.start(AccessIdChanger.class, "Ensuring Data Model", false);
				typeReflection.deploy(modelEnvironment.getDataModel());
				ph.stop();
			}
			
			if (modelEnvironment.getWorkbenchModel() != null) {
				ProfilingHandle ph = Profiling.start(AccessIdChanger.class, "Ensuring Workbench Model", false);
				typeReflection.deploy(modelEnvironment.getWorkbenchModel());
				ph.stop();
			}
			
			//if (serviceModel == null)
				//prepareTransientSession(null);
			//else {
				ProfilingHandle ph = Profiling.start(AccessIdChanger.class, "Ensuring Service Model", false);
				typeReflection.deploy(serviceModel);
				ph.stop();
				//prepareTransientSession(serviceModel);
			//}
		} catch (GmfException ex) {
			logger.error("Error while ensuring model types.", ex);
			ex.printStackTrace();
		}
		
		return new Future<Void>(null);
	}
	
	private boolean isModelVisible() {
		return gmSession.getModelAccessory().getMetaData().useCase("clientLogon").is(Visible.T);
	}
	
	private Future<Boolean> handleModelEnvironmentSet() {
		final Future<Boolean> future = new Future<>();
		
		//if (sessionReadyLoader == null) {
			//fireModelEnvironmentSet();
			//MaskController.maskScreenOpaque = false;
			//progressUnmask();
			//prepareCompleteProgress().andThen(V -> future.onSuccess(true));
			Scheduler.get().scheduleDeferred(() -> future.onSuccess(true));
			return future;
		//}
		
		/*final ProfilingHandle ph = Profiling.start(CustomizationConstellation.class, "Running Session Ready Loader (async)", true, true);
		sessionReadyLoader.load(AsyncCallbacks.of( //
				result -> {
					initializationProfiling.stop();
					ph.stop();
					fireModelEnvironmentSet();
					progressUnmask();
					prepareCompleteProgress().andThen(V -> future.onSuccess(true));
				}, e -> {
					initializationProfiling.stop();
					ph.stop();
					fireModelEnvironmentSet();
					progressUnmask();
					ErrorDialog.show("Error while calling the session ready loader.", e);
					e.printStackTrace();
					future.onFailure(e);
				}));
		
		return future;*/
	}

}
