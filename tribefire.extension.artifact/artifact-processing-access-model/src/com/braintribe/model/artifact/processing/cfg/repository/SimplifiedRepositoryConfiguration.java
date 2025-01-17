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
package com.braintribe.model.artifact.processing.cfg.repository;

import java.util.List;

import com.braintribe.model.artifact.processing.cfg.repository.details.Repository;
import com.braintribe.model.generic.annotation.meta.Mandatory;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

/**
 * a simplified (aka reduced to the minimum yet still fully capable, modelled) abstraction of a Maven style XML declaration based config
 * 
 * @author pit
 *
 */
public interface SimplifiedRepositoryConfiguration extends RepositoryConfiguration {
		
	final EntityType<SimplifiedRepositoryConfiguration> T = EntityTypes.T(SimplifiedRepositoryConfiguration.class);

	/**
	 * @return - a {@link List} of the {@link Repository} to be processed by the services 
	 */
	@Mandatory
	List<Repository> getRepositories();
	/**
	 * @param repositories - a {@link List} of the {@link Repository} to be processed by the services
	 */
	void setRepositories( List<Repository> repositories);
	
	/**
	 * @return - the expression that points to the local repository within the server's storage
	 */
	@Mandatory
	String getLocalRepositoryExpression();	
	/**
	 * @param expression - the expression that points to the local repository within the server's storage
	 */
	void setLocalRepositoryExpression( String expression);

}
