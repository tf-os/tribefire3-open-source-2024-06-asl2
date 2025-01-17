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
package com.braintribe.model.processing.session;

import com.braintribe.model.processing.session.api.managed.ModelAccessoryFactory;
import com.braintribe.model.processing.session.api.persistence.PersistenceGmSession;
import com.braintribe.model.processing.session.api.persistence.PersistenceGmSessionFactory;
import com.braintribe.model.processing.session.api.resource.ResourceAccessFactory;

/**
 * Base interface for session factory builders. There are two sub-classes of this interface: 
 * {@link GmLocalSessionFactoryBuilder} (for creating a session within tribefire) and {@link GmRemoteSessionFactoryBuilder}
 * for creating sessions to a remote tribefire instance.
 */
public interface GmSessionFactoryBuilder {

	/**
	 * Specify a custom {@link ModelAccessoryFactory}. This method is optional and can usually be omitted.
	 * @param modelAccessoryFactory The {@link ModelAccessoryFactory} that should be used for the client session.
	 * @return The {@link GmSessionFactoryBuilder} that offers this method.
	 */
	GmSessionFactoryBuilder modelAccessoryFactory(ModelAccessoryFactory modelAccessoryFactory);

	/**
	 * Sets the {@link ResourceAccessFactory} to be used by the session factory builder. Within a Cartridge, <pre>ClientBaseContract.resourceAccessFactory()</pre> could be
	 * used to get an resource access factory.
	 * @param resourceAccessFactory The {@link ResourceAccessFactory} that should be used to access resources. 
	 * @return The {@link GmSessionFactoryBuilder} instance currently used.
	 */
	GmSessionFactoryBuilder resourceAccessFactory(ResourceAccessFactory<PersistenceGmSession> resourceAccessFactory);

	/**
	 * Finishes the {@link GmSessionFactoryBuilder} and creates the resulting {@link PersistenceGmSessionFactory} based on the configuration
	 * provided.
	 * @return A {@link PersistenceGmSessionFactory} based on the configuration.
	 * @throws GmSessionFactoryBuilderException Thrown when the {@link PersistenceGmSessionFactory} could not be created.
	 */
	PersistenceGmSessionFactory done() throws GmSessionFactoryBuilderException;
}
