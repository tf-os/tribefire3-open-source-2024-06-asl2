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
package com.braintribe.model.access.hibernate.hql;

import java.util.Collection;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.hibernate.type.Type;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.persistence.NativeQueryParameter;

/**
 * @author peter.gazdik
 */
public class HibernateQueryBuilder<T> {

	private final Session session;
	private final Query<T> result;

	public HibernateQueryBuilder(Session session, String hql) {
		this.session = session;
		this.result = session.createQuery(hql);
	}

	public void setPagination(Integer maxResults, Integer firstResult) {
		if (maxResults != null && firstResult != null)
			setPagination(maxResults.intValue(), firstResult.intValue());
	}

	public void setPagination(int maxResults, int firstResult) {
		if (maxResults > 0)
			result.setMaxResults(maxResults);
		result.setFirstResult(firstResult);
	}

	public void setParameter(NativeQueryParameter parameter) {
		setParameter(parameter.getName(), parameter.getValue());
	}

	public void setParameter(String name, Object value) {
		if (value instanceof Collection<?>) {
			Collection<?> collection = (Collection<?>) value;
			result.setParameterList(name, collection);

		} else if (value instanceof GenericEntity) {
			GenericEntity entity = (GenericEntity) value;
			Class<?> entityClass = entity.entityType().getJavaType();
			Type hibernateType = session.getSessionFactory().getTypeHelper().entity(entityClass);
			result.setParameter(name, value, hibernateType);

		} else {
			result.setParameter(name, value);
		}
	}

	public Query<T> getResult() {
		return result;
	}

}
