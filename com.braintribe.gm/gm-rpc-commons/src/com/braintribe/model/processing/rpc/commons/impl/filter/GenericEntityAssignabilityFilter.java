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
package com.braintribe.model.processing.rpc.commons.impl.filter;

import java.util.function.Predicate;

import com.braintribe.model.generic.GMF;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.GenericModelTypeReflection;

public class GenericEntityAssignabilityFilter implements Predicate<GenericEntity> {

	private final GenericModelTypeReflection typeReflection = GMF.getTypeReflection();
	private EntityType<?> entityType;

	public void setType(Class<? extends GenericEntity> type) {
		this.entityType = typeReflection.getEntityType(type);
	}

	@Override
	public boolean test(GenericEntity obj) {
		if (obj == null || entityType == null) {
			return false;
		}

		return entityType.isInstance(obj);

	}

}