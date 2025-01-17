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
package com.braintribe.model.processing.access.service.api.registry;

import com.braintribe.cfg.Required;
import com.braintribe.model.access.IncrementalAccess;

/**
 * An {@link AccessRegistrationInfo} contains the information required for registering an {@link IncrementalAccess} in an
 * {@link RegistryBasedAccessService}.
 * 
 * 
 */
public class AccessRegistrationInfo  {

	private IncrementalAccess access;
	private String accessId;
	private String accessDenotationType;
	private String modelName;
	private String modelAccessId;
	private String workbenchModelName;
	private String workbenchAccessId;
	private String resourceAccessFactoryId;
	private String name;
	private String description;
	private String serviceModelName;
	
	public IncrementalAccess getAccess() {
		return access;
	}

	@Required
	public void setAccess(IncrementalAccess access) {
		this.access = access;
	}

	public String getAccessId() {
		return accessId;
	}

	@Required
	public void setAccessId(String accessId) {
		this.accessId = accessId;
	}
	
	public String getAccessDenotationType() {
		return accessDenotationType;
	}

	public void setAccessDenotationType(String accessDenotationType) {
		this.accessDenotationType = accessDenotationType;
	}

	public String getWorkbenchAccessId() {
		return workbenchAccessId;
	}

	public void setWorkbenchAccessId(String workbenchAccessId) {
		this.workbenchAccessId = workbenchAccessId;
	}

	public String getModelAccessId() {
		return modelAccessId;
	}

	@Required
	public void setModelAccessId(String metaModelAccessId) {
		this.modelAccessId = metaModelAccessId;
	}

	public String getModelName() {
		return modelName;
	}

	@Required
	public void setModelName(String dataMetaModelName) {
		this.modelName = dataMetaModelName;
	}

	public String getWorkbenchModelName() {
		return workbenchModelName;
	}

	public void setWorkbenchModelName(String workbenchMetaModelName) {
		this.workbenchModelName = workbenchMetaModelName;
	}

	public String getResourceAccessFactoryId() {
		return resourceAccessFactoryId;
	}

	public void setResourceAccessFactoryId(String resourceAccessFactoryId) {
		this.resourceAccessFactoryId = resourceAccessFactoryId;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getDescription() {
		return description;
	}

	public void setServiceModelName(String transientModelName) {
		this.serviceModelName = transientModelName;
	}
	
	public String getServiceModelName() {
		return serviceModelName;
	}
}
