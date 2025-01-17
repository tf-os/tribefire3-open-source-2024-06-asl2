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
package com.braintribe.model.processing.ddra.endpoints.api.v1.handlers;

import com.braintribe.exception.AuthorizationException;
import com.braintribe.model.generic.session.exception.GmSessionException;
import com.braintribe.model.meta.data.prompt.Visible;
import com.braintribe.model.processing.query.fluent.EntityQueryBuilder;
import com.braintribe.model.processing.session.api.managed.ModelAccessory;
import com.braintribe.model.processing.session.api.managed.ModelAccessoryFactory;
import com.braintribe.model.processing.session.api.managed.NotFoundException;
import com.braintribe.model.processing.session.api.persistence.PersistenceGmSession;
import com.braintribe.model.processing.session.api.persistence.PersistenceGmSessionFactory;
import com.braintribe.model.query.EntityQuery;
import com.braintribe.model.resource.Resource;

import java.util.Objects;

public abstract class ResourceHandler {

	protected PersistenceGmSessionFactory sessionFactory;
	protected ModelAccessoryFactory modelAccessoryFactory;

	protected PersistenceGmSession openSession(String accessId) {
		try {
			return sessionFactory.newSession(accessId);
		} catch (GmSessionException e) {
			throw new RuntimeException("Error while creating session for access with id: " + accessId, e);
		}
	}

	protected Resource retrieveResource(PersistenceGmSession gmSession, String resourceId, String accessId) {

		Objects.requireNonNull(gmSession, "gmSession");
		Objects.requireNonNull(resourceId, "resourceId");

		EntityQuery query = EntityQueryBuilder.from(Resource.T).where().property(Resource.id).eq(resourceId).done();

		Resource resource = null;

		try {
			resource = gmSession.query().entities(query).unique();
		} catch (NotFoundException e) {
			throw e;
		} catch (GmSessionException e) {
			throw new RuntimeException(
					"Failed to obtain resource: [ " + resourceId + " ] from access [ " + gmSession.getAccessId() + " ]: " + e.getMessage(), e);
		}

		if (resource == null)
			throw new NotFoundException("Resource [ " + resourceId + " ] not found");

		if (this.modelAccessoryFactory != null) {
			ModelAccessory modelAccessory = this.modelAccessoryFactory.getForAccess(accessId);
			boolean visible = modelAccessory.getMetaData().entity(resource).is(Visible.T);
			if (!visible)
				throw new AuthorizationException("Insufficient privileges to retrieve resource.");
		}

		return resource;

	}

}
