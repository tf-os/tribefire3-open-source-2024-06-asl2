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
package com.braintribe.model.processing.session.api.persistence;

import com.braintribe.model.accessapi.ManipulationResponse;
import com.braintribe.model.generic.enhance.EnhancedEntity;
import com.braintribe.model.generic.eval.EvalContext;
import com.braintribe.model.generic.eval.Evaluator;
import com.braintribe.model.generic.manipulation.util.HistorySuspension;
import com.braintribe.model.generic.session.exception.GmSessionException;
import com.braintribe.model.processing.session.api.managed.ManagedGmSession;
import com.braintribe.model.processing.session.api.managed.SessionQueryBuilder;
import com.braintribe.model.processing.session.api.persistence.aspects.SessionAspect;
import com.braintribe.model.processing.session.api.persistence.auth.SessionAuthorization;
import com.braintribe.model.processing.session.api.transaction.Transaction;
import com.braintribe.model.service.api.ServiceRequest;
import com.braintribe.processing.async.api.AsyncCallback;

/**
 * An extension of {@link ManagedGmSession} that manages entities coming from some persistence layer underneath. This is
 * as close as we get to a hibernate session in the GM-session API. This session provides a transaction support for
 * manipulation of entities - especially the {@code #commit()} functionality, and an option of doing a rollback, using
 * the {@link Transaction} retrieved via {@link #getTransaction()} method (rollback = undo all done manipulations).
 * <p>
 * This session also distinguishes between two types of queries, the ones that are forwarded to the persistence layer (
 * {@link #query()}) and ones that only access the instantiated entities "attached" to this session (
 * {@link #queryCache()}).
 * 
 * <h3>Querying attached entities</h3>
 * 
 * This session keeps track of the entities attached to it in a similar way as {@link ManagedGmSession} does, but the
 * queries and entity references are resolved by delegating to the underlying access, which means the query may cause
 * new entities to be retrieved and attached to the session. In order to only work with entities currently attached to
 * the session, use {@link #queryCache()} method.
 */
public interface PersistenceGmSession extends ManagedGmSession, HistorySuspension, Evaluator<ServiceRequest> {

	SessionQueryBuilder queryCache();

	SessionQueryBuilder queryDetached();

	@Override
	PersistenceSessionQueryBuilder query();

	Transaction getTransaction();

	ManipulationResponse commit() throws GmSessionException;
	void commit(AsyncCallback<ManipulationResponse> callback);

	CommitContext prepareCommit();

	String getAccessId();

	@Override
	PersistenceManipulationListenerRegistry listeners();

	<T> T getSessionAspect(Class<SessionAspect<T>> aspectClass);

	SessionAuthorization getSessionAuthorization();

	/**
	 * Makes all the entities attached to this session shallow. That means all the nullable properties are set to
	 * <tt>null</tt> and marked as absent and the entity is marked as shallow. <br/>
	 * 
	 * Shallow instance: <br/>
	 * A shallow instance is an instance in a state where none of the properties except for id are loaded. It is marked
	 * by a special flag (see {@link EnhancedEntity#flags()}) and is recognized by the LazyLoader.
	 * 
	 * @throws IllegalStateException
	 *             if invoked when there are manipulations done (but not committed) on the session.
	 */
	void shallowifyInstances();

	@Override
	default <T> EvalContext<T> eval(ServiceRequest evaluable) {
		throw new UnsupportedOperationException("eval is not implemented by " + getClass());
	}

	/**
	 * @return new session with an empty cache, but bound to the same persistence and created in the same way as this
	 *         session.
	 */
	default PersistenceGmSession newEquivalentSession() {
		throw new UnsupportedOperationException("Method 'PersistenceGmSession.newEquivalentSession' is not supported!");
	}

}
