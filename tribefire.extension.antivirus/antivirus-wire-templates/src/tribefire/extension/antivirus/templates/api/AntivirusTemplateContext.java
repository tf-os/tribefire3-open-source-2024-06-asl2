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
package tribefire.extension.antivirus.templates.api;

import java.util.List;

import com.braintribe.cfg.ScopeContext;
import com.braintribe.model.deployment.Module;
import com.braintribe.model.descriptive.HasExternalId;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.wire.api.scope.InstanceConfiguration;

import tribefire.extension.antivirus.model.deployment.repository.configuration.ProviderSpecification;

/**
 * ANTIVIRUS Template Context - contains information for configuring ANTIVIRUS functionality
 * 
 *
 */
public interface AntivirusTemplateContext extends ScopeContext {

	// -----------------------------------------------------------------------
	// Common
	// -----------------------------------------------------------------------

	Module getAntivirusModule();

	String getGlobalIdPrefix();

	String getContext();

	List<ProviderSpecification> getProviderSpecifications();

	String getAntivirusContext();

	static AntivirusTemplateContextBuilder builder() {
		return new AntivirusTemplateContextImpl();
	}

	// -----------------------------------------------------------------------
	// CONTEXT METHODS
	// -----------------------------------------------------------------------

	<T extends GenericEntity> T create(EntityType<T> entityType, InstanceConfiguration instanceConfiguration);

	<T extends GenericEntity> T lookup(String globalId);

	<T extends HasExternalId> T lookupExternalId(String externalId);

}