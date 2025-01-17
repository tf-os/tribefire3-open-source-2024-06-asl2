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
package com.braintribe.gwt.gme.constellation.client.expert;

import java.util.List;
import java.util.function.Function;

import com.braintribe.gwt.async.client.Future;
import com.braintribe.gwt.async.client.MultiLoader;
import com.braintribe.gwt.gme.constellation.client.CustomizationConstellation;
import com.braintribe.gwt.gme.constellation.client.LocalizedText;
import com.braintribe.gwt.gme.constellation.client.TransientGmSession;
import com.braintribe.gwt.ioc.client.InitializableBean;
import com.braintribe.gwt.logging.client.ErrorDialog;
import com.braintribe.gwt.logging.client.Profiling;
import com.braintribe.gwt.logging.client.ProfilingHandle;
import com.braintribe.model.accessapi.ModelEnvironment;
import com.braintribe.model.generic.GMF;
import com.braintribe.model.generic.GmfException;
import com.braintribe.model.generic.manipulation.Manipulation;
import com.braintribe.model.generic.reflection.GenericModelTypeReflection;
import com.braintribe.model.meta.GmMetaModel;
import com.braintribe.model.processing.session.api.persistence.ModelEnvironmentDrivenGmSession;
import com.braintribe.model.processing.session.api.transaction.Transaction;

public class ModelEnvironmentUtil implements InitializableBean{
	private static final GenericModelTypeReflection typeReflection =  GMF.getTypeReflection();
	private boolean useItwAsync = false;
	
	//private GmRpcService<AccessService> accessService;
	private ModelEnvironmentDrivenGmSession gmSession;
	private TransientGmSession transientSession;
	private Function<String, Future<ModelEnvironment>> modelEnvironmentProvider;
	private ModelEnvironmentDrivenSessionUpdater modelEnvironmentDrivenSessionUpdater;
	
	/*public void setAccessService(GmRpcService<AccessService> accessService) {
		this.accessService = accessService;
	}*/
	
	public void setGmSession(ModelEnvironmentDrivenGmSession gmSession) {
		this.gmSession = gmSession;
	}
	
	public void setTransientSession(TransientGmSession transientSession) {
		this.transientSession = transientSession;
	}
	
	public void setModelEnvironmentDrivenSessionUpdater(
			ModelEnvironmentDrivenSessionUpdater modelEnvironmentDrivenSessionUpdater) {
		this.modelEnvironmentDrivenSessionUpdater = modelEnvironmentDrivenSessionUpdater;
	}
	
	public void setModelEnvironmentProvider(Function<String, Future<ModelEnvironment>> modelEnvironmentProvider) {
		this.modelEnvironmentProvider = modelEnvironmentProvider;
	}
	
	@Override
	public void intializeBean() throws Exception {
		modelEnvironmentProvider.apply(null);
	}
	
	public Future<ModelEnvironment> loadModelEnvironment(String accessId) {
		Future<ModelEnvironment> future = new Future<>();
		modelEnvironmentProvider.apply(accessId) //
				.andThen(modelEnvironment -> {
					ensureModelTypes(modelEnvironment) //
							.andThen((r) -> {
								gmSession.configureModelEnvironment(modelEnvironment, com.braintribe.processing.async.api.AsyncCallback.of( //
										v -> {
											if (modelEnvironmentDrivenSessionUpdater != null)
												modelEnvironmentDrivenSessionUpdater.updateModelEnvironment(modelEnvironment);
											// handleModelEnvironmentSet().get(future);
											future.onSuccess(modelEnvironment);
										}, future::onFailure));
							}).onError(future::onFailure);
				}).onError(e -> {
					ErrorDialog.show(LocalizedText.INSTANCE.errorGettingModelEnvironment(), e, true);
					e.printStackTrace();
				});
		
		return future;
	}
	
	public Future<Void> ensureModelTypes(final ModelEnvironment modelEnvironment) {
		GmMetaModel serviceModel = modelEnvironment.getServiceModel();
		if (useItwAsync) {
			final Future<Void> future = new Future<>();
			MultiLoader itwMultiLoader = new MultiLoader();
			if (modelEnvironment.getDataModel() != null)
				itwMultiLoader.add("dataModel", ensureModel(modelEnvironment.getDataModel()));
			if (modelEnvironment.getWorkbenchModel() != null)
				itwMultiLoader.add("workbenchModel", ensureModel(modelEnvironment.getWorkbenchModel()));
			if (serviceModel != null)
				itwMultiLoader.add("transientModel", ensureModel(serviceModel));
			
			itwMultiLoader.load() //
					.andThen(result -> {
						prepareTransientSession(serviceModel);
						future.onSuccess(null);
					}).onError(future::onFailure);
			
			return future;
		}
		
		try {
			if (modelEnvironment.getDataModel() != null) {
				ProfilingHandle ph = Profiling.start(CustomizationConstellation.class, "Ensuring Data Model", false);
				typeReflection.deploy(modelEnvironment.getDataModel());
				ph.stop();
			}
			if (modelEnvironment.getWorkbenchModel() != null) {
				ProfilingHandle ph = Profiling.start(CustomizationConstellation.class, "Ensuring Workbench Model", false);
				typeReflection.deploy(modelEnvironment.getWorkbenchModel());
				ph.stop();
			}
			if (serviceModel != null) {
				ProfilingHandle ph = Profiling.start(CustomizationConstellation.class, "Ensuring Service Model", false);
				typeReflection.deploy(serviceModel);
				ph.stop();
				prepareTransientSession(serviceModel);
			} else
				prepareTransientSession(null);
		} catch (GmfException ex) {
			ex.printStackTrace();
		}
		
		return new Future<Void>(null);
	}
	
	private void prepareTransientSession(GmMetaModel transientModel) {
		Transaction transaction = transientSession.getTransaction();
		List<Manipulation> manipulations = transaction.getManipulationsDone();
		if (manipulations != null && !manipulations.isEmpty())
			transaction.undo(manipulations.size());
		
		transientSession.cleanup();
		GmMetaModel currentTransientGmMetaModel = transientSession.getTransientGmMetaModel();
		if (currentTransientGmMetaModel != transientModel)
			transientSession.configureGmMetaModel(transientModel);
	}
	
	private Future<Void> ensureModel(GmMetaModel model) {
		Future<Void> future = new Future<Void>();
		typeReflection.deploy(model, future);
		return future;
	}

}
