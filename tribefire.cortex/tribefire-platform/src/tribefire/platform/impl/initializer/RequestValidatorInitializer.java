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
package tribefire.platform.impl.initializer;

import com.braintribe.model.cortex.preprocessor.RequestValidatorPreProcessor;
import com.braintribe.model.processing.session.api.collaboration.ManipulationPersistenceException;
import com.braintribe.model.processing.session.api.collaboration.PersistenceInitializationContext;
import com.braintribe.model.processing.session.api.collaboration.SimplePersistenceInitializer;
import com.braintribe.model.processing.session.api.managed.ManagedGmSession;

public class RequestValidatorInitializer extends SimplePersistenceInitializer {

	@Override
	public void initializeData(PersistenceInitializationContext context) throws ManipulationPersistenceException {
		ManagedGmSession session = context.getSession();
		
		RequestValidatorPreProcessor requestValidatorPreProcessor = session.create(RequestValidatorPreProcessor.T, "default:preprocessor/requestValidator");
		String externalId = "preProcessor.requestValidator.default";
		
		requestValidatorPreProcessor.setExternalId(externalId);
		requestValidatorPreProcessor.setName("Default Request Validator");
		
	}

}
