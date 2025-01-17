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
package com.braintribe.model.access.hibernate;

import static com.braintribe.model.access.hibernate.HibernateAccessTools.deproxy;

import java.sql.Timestamp;
import java.util.Date;

import com.braintribe.model.access.AbstractAccess.AbstractQueryResultCloningContext;
import com.braintribe.model.access.hibernate.gm.CompositeIdValues;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.processing.traversing.engine.impl.clone.legacy.GmtCompatibleCloningContext;

public class HibernateCloningContext extends AbstractQueryResultCloningContext implements GmtCompatibleCloningContext {

	private final String defaultPartition;

	public HibernateCloningContext(String defaultPartition) {
		this.defaultPartition = defaultPartition;
	}

	@Override
	protected String defaultPartition() {
		return defaultPartition;
	}

	@Override
	public GenericEntity preProcessInstanceToBeCloned(GenericEntity instanceToBeCloned) {
		GenericEntity result = deproxy(instanceToBeCloned);
		ensureIdIsGmValue(result);
		return result;
	}

	/**
	 * In case of a composite ID, we use {@link CompositeIdValues} in the Hibernate mappings, which is not a GM value
	 * (let alone a scalar value) so it has to be adjusted.
	 */
	private void ensureIdIsGmValue(GenericEntity entity) {
		Object id = entity.getId();
		if (id instanceof CompositeIdValues) {
			CompositeIdValues compositeId = (CompositeIdValues) id;
			entity.setId(compositeId.encodeAsString());
		}
	}

	@Override
	public Object postProcessCloneValue(GenericModelType propertyType, Object clonedValue) {
		if (clonedValue instanceof Timestamp)
			return new Date(((Timestamp) clonedValue).getTime());
		else
			return super.postProcessCloneValue(propertyType, clonedValue);
	}

}
