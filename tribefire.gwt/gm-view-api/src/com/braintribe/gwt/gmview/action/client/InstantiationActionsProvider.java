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
package com.braintribe.gwt.gmview.action.client;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import com.braintribe.gwt.async.client.Future;
import com.braintribe.gwt.gmview.client.ModelEnvironmentSetListener;
import com.braintribe.gwt.ioc.client.Required;
import com.braintribe.model.generic.processing.pr.fluent.TC;
import com.braintribe.model.generic.session.exception.GmSessionException;
import com.braintribe.model.processing.query.fluent.EntityQueryBuilder;
import com.braintribe.model.processing.session.api.persistence.ModelEnvironmentDrivenGmSession;
import com.braintribe.model.query.EntityQuery;
import com.braintribe.model.query.EntityQueryResult;
import com.braintribe.model.workbench.InstantiationAction;
import com.braintribe.processing.async.api.AsyncCallback;
import com.braintribe.utils.i18n.I18nTools;
import com.google.gwt.core.client.Scheduler;

/**
 * Provider used for providing a list of {@link InstantiationAction}s.
 * 
 * @author michel.docouto
 *
 */
public class InstantiationActionsProvider implements Supplier<Future<List<InstantiationAction>>>, ModelEnvironmentSetListener {

	private ModelEnvironmentDrivenGmSession gmSession;
	private Future<List<InstantiationAction>> future;

	/**
	 * Configures the required session used for looking for {@link InstantiationAction}s.
	 */
	@Required
	public void setGmSession(ModelEnvironmentDrivenGmSession gmSession) {
		this.gmSession = gmSession;
	}

	@Override
	public void onModelEnvironmentSet() {
		if (future != null)
			future = null;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Future<List<InstantiationAction>> get() throws RuntimeException {
		if (future != null)
			return future;
		
		future = new Future<List<InstantiationAction>>();

		if (gmSession.getModelEnvironment() == null || gmSession.getModelEnvironment().getDataAccessId() == null) {
			Scheduler.get().scheduleDeferred(() -> future.onSuccess(null));
			return future;
		}

		gmSession.query().entities(prepareEntityQuery()).result(AsyncCallback.of( //
				entityQueryResultConvenience -> {
					if (entityQueryResultConvenience == null) {
						future.onSuccess(null);
						return;
					}
					
					try {
						EntityQueryResult result = entityQueryResultConvenience.result();
						if (result != null) {
							List<InstantiationAction> actionList = (List) result.getEntities();
							sortInstantiationActionList(actionList);
							
							future.onSuccess(actionList);
							return;
						}
					} catch (GmSessionException e) {
						future.onFailure(e);
					}

					future.onSuccess(null);
				}, future::onFailure));

		return future;
	}

	private EntityQuery prepareEntityQuery() {
		return EntityQueryBuilder.from(InstantiationAction.class).tc(TC.create().negation().joker().done()).orderBy("id").done();
	}
	
	private static void sortInstantiationActionList(List<InstantiationAction> list) {
        Collections.sort(list, (a, b) -> {
        	String aName = I18nTools.getLocalized(a.getDisplayName());
        	String bName = I18nTools.getLocalized(b.getDisplayName());
        	
            return aName.compareTo(bName);
        });
    }

}
