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
package com.braintribe.model.processing.deployment.utils;

import java.util.function.Function;

import com.braintribe.cfg.Required;
import com.braintribe.model.access.AccessIdentificationLookup;
import com.braintribe.model.access.AccessService;
import com.braintribe.model.access.IncrementalAccess;
import com.braintribe.model.meta.GmMetaModel;



public class AccessLookupModelProvider implements Function<IncrementalAccess, GmMetaModel>{
	
	private AccessService accessService;
	private AccessIdentificationLookup accessIdentificationLookup;

	@Required
	public void setAccessService(AccessService accessService) {
		this.accessService = accessService;
	}
	@Required
	public void setAccessIdentificationLookup(AccessIdentificationLookup accessIdentificationLookup) {
		this.accessIdentificationLookup = accessIdentificationLookup;
	}

	@Override
	public GmMetaModel apply(IncrementalAccess access) throws RuntimeException {
		try {
			String accessId = accessIdentificationLookup.lookupAccessId(access);
			return accessService.getModelEnvironment(accessId).getDataModel();

		} catch (Exception e) {
			throw new RuntimeException("Error while providing meta model for Acccess: "+access,e);
		}
	}

}
