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
package com.braintribe.devrock.eclipse.model.workspace;

import com.braintribe.devrock.eclipse.model.storage.StorageLockerPayload;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.annotation.Initializer;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

/**
 * alternative (and future) content of the workspace export package
 * 
 * @author pit
 */
public interface ExportPackage extends GenericEntity {
		
	EntityType<ExportPackage> T = EntityTypes.T(ExportPackage.class);


	String workspace = "workspace";
	String storageLockerPayload = "storageLockerPayload";
	String version = "version";
	
	Workspace getWorkspace();
	void setWorkspace(Workspace value);

	StorageLockerPayload getStorageLockerPayload();
	void setStorageLockerPayload(StorageLockerPayload value);

	@Initializer("'1.0'")
	String getVersion();
	void setVersion(String value);


}
