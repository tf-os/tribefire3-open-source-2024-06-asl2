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
package tribefire.platform.impl.bootstrapping;

import com.braintribe.cfg.Configurable;
import com.braintribe.cfg.Required;
import com.braintribe.gm.model.persistence.reflection.api.GetAccessIds;
import com.braintribe.gm.model.persistence.reflection.api.GetMetaModel;
import com.braintribe.gm.model.persistence.reflection.api.GetMetaModelForTypes;
import com.braintribe.gm.model.persistence.reflection.api.GetModelAndWorkbenchEnvironment;
import com.braintribe.gm.model.persistence.reflection.api.GetModelEnvironment;
import com.braintribe.gm.model.persistence.reflection.api.GetModelEnvironmentForDomain;
import com.braintribe.gm.model.persistence.reflection.api.GetModelEnvironmentServices;
import com.braintribe.gm.model.persistence.reflection.api.GetModelEnvironmentServicesForDomain;
import com.braintribe.gm.model.persistence.reflection.api.PersistenceReflectionRequest;
import com.braintribe.model.access.AccessService;
import com.braintribe.model.processing.service.impl.AbstractDispatchingServiceProcessor;
import com.braintribe.model.processing.service.impl.DispatchConfiguration;

public class PersistenceReflectionServiceProcessor extends AbstractDispatchingServiceProcessor<PersistenceReflectionRequest, Object>{
	private AccessService accessService;
	
	@Configurable @Required
	public void setAccessService(AccessService accessService) {
		this.accessService = accessService;
	}
	
	@Override
	protected void configureDispatching(DispatchConfiguration<PersistenceReflectionRequest, Object> dispatching) {
		dispatching.register(GetMetaModel.T, // 
				(c,r) -> accessService.getMetaModel(r.getAccessId()));
		dispatching.register(GetModelAndWorkbenchEnvironment.T, // 
				(c,r) -> accessService.getModelAndWorkbenchEnvironment(r.getAccessId(), r.getFoldersByPerspective()));
		dispatching.register(GetModelEnvironment.T, // 
				(c,r) -> accessService.getModelEnvironment(r.getAccessId()));
		dispatching.register(GetModelEnvironmentForDomain.T, // 
				(c,r) -> accessService.getModelEnvironmentForDomain(r.getAccessId(), r.getAccessDomain()));
		dispatching.register(GetModelEnvironmentServices.T, // 
				(c,r) -> accessService.getModelEnvironmentServices(r.getAccessId()));
		dispatching.register(GetModelEnvironmentServicesForDomain.T, // 
				(c,r) -> accessService.getModelEnvironmentServicesForDomain(r.getAccessId(), r.getAccessDomain()));
		dispatching.register(GetAccessIds.T, // 
				(c,r) -> accessService.getAccessIds());
		dispatching.register(GetMetaModelForTypes.T, // 
				(c,r) -> accessService.getMetaModelForTypes(r.getTypeSignatures()));
	}
}
