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
package com.braintribe.model.access.security.query;

import java.util.Set;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.pr.criteria.RootCriterion;
import com.braintribe.model.generic.reflection.StandardCloningContext;
import com.braintribe.model.generic.reflection.StrategyOnCriterionMatch;
import com.braintribe.model.processing.aop.api.context.AroundContext;
import com.braintribe.model.processing.meta.cmd.builders.ModelMdResolver;
import com.braintribe.model.processing.session.api.persistence.PersistenceGmSession;
import com.braintribe.model.query.Query;
import com.braintribe.model.security.acl.AclTools;

/**
 * 
 */
public class QueryInterceptorContext<Q extends Query, R> {

	public final AroundContext<Q, R> aroundContext;
	public final SourcesDescriptor querySources;
	private final String userName;
	private final Set<String> userRoles;
	private final Q originalQuery;
	private final boolean needsHasAclChecks;

	private Q query;
	private boolean cloned = false;
	private ModelMdResolver metaDataResolver;

	public QueryInterceptorContext(AroundContext<Q, R> aroundContext) {
		PersistenceGmSession session = aroundContext.getSession();

		this.aroundContext = aroundContext;
		this.originalQuery = aroundContext.getRequest();
		this.query = originalQuery;
		this.querySources = QueryTools.findQuerySources(originalQuery);
		this.userName = session.getSessionAuthorization().getUserName();
		this.userRoles = session.getSessionAuthorization().getUserRoles();
		this.needsHasAclChecks = AclTools.supportsAcl(session) && !AclTools.isHasAclAdministrable(session);
	}

	public Q getOriginalQuery() {
		return originalQuery;
	}

	/**
	 * @see #getQueryToEdit()
	 */
	public Q getQueryToRead() {
		return query;
	}

	/**
	 * When we want to edit the query, we have to clone it first, so that our changes do not have a side-effect. In such
	 * case, this method should be used (instead of {@link #getQueryToRead()}) to ensure the returned query is cloned.
	 * 
	 * Initially, we keep a reference to the original query, but the first time we want to edit the query, we invoke
	 * this method and clone the query internally. From that moment on, we only have this cloned instance. The purpose
	 * is not to clone unless it is really needed.
	 */
	public Q getQueryToEdit() {
		return cloned ? query : cloneQuery();
	}

	public String getUserName() {
		return userName;
	}

	public Set<String> getUserRoles() {
		return userRoles;
	}

	public boolean needsHasAclChecks() {
		return needsHasAclChecks;
	}

	public String getModelName() {
		return getSession().getModelAccessory().getModel().getName();
	}
	
	public ModelMdResolver getMetaData() {
		if (metaDataResolver == null)
			metaDataResolver = getSession().getModelAccessory().getMetaData();

		return metaDataResolver;
	}

	public PersistenceGmSession getSession() {
		return aroundContext.getSession();
	}

	private Q cloneQuery() {
		cloned = true;

		query = cloneTopLevel(originalQuery);
		query.setRestriction(cloneTopLevel(originalQuery.getRestriction()));

		return query;
	}

	private <T extends GenericEntity> T cloneTopLevel(T entity) {
		if (entity == null)
			return null;

		return (T) entity.entityType().clone(new TopLevelPropertyCopyingContext(), entity, StrategyOnCriterionMatch.reference);
	}

	/**
	 * Context which copies all the properties there are.
	 */
	private class TopLevelPropertyCopyingContext extends StandardCloningContext {
		@Override
		public boolean isTraversionContextMatching() {
			return !(getTraversingStack().peek() instanceof RootCriterion);
		}
	}

}
