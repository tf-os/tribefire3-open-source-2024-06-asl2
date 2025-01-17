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
package tribefire.platform.impl.session;

import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

import com.braintribe.cfg.Configurable;
import com.braintribe.cfg.Required;
import com.braintribe.model.access.AccessService;
import com.braintribe.model.accessapi.ModelEnvironment;
import com.braintribe.model.generic.session.exception.GmSessionRuntimeException;
import com.braintribe.model.meta.GmMetaModel;
import com.braintribe.model.processing.meta.cmd.CmdResolver;
import com.braintribe.model.processing.meta.cmd.CmdResolverImpl;
import com.braintribe.model.processing.meta.cmd.ResolutionContextBuilder;
import com.braintribe.model.processing.meta.cmd.context.aspects.AccessAspect;
import com.braintribe.model.processing.meta.cmd.context.aspects.AccessTypeAspect;
import com.braintribe.model.processing.meta.cmd.context.aspects.RoleAspect;
import com.braintribe.model.processing.meta.oracle.BasicModelOracle;
import com.braintribe.model.processing.meta.oracle.ModelOracle;
import com.braintribe.model.processing.session.api.managed.ManagedGmSession;
import com.braintribe.model.processing.session.impl.managed.AbstractModelAccessory;
import com.braintribe.model.processing.session.impl.managed.BasicManagedGmSession;
import com.braintribe.provider.Holder;

public class StaticModelAccessory extends AbstractModelAccessory {

	private AccessService accessService;
	private String accessId;

	private BasicManagedGmSession modelSession;

	private Supplier<Set<String>> userRolesProvider;
	private Supplier<?> sessionProvider;

	private ReentrantLock lock = new ReentrantLock();

	public StaticModelAccessory() {

	}

	@Required
	@Configurable
	public void setAccessId(String accessId) {
		this.accessId = accessId;
	}

	@Required
	@Configurable
	public void setAccessService(AccessService accessService) {
		this.accessService = accessService;
	}

	@Configurable
	public void setUserRolesProvider(Supplier<Set<String>> userRolesProvider) {
		this.userRolesProvider = userRolesProvider;
	}

	@Configurable
	public void setSessionProvider(Supplier<?> sessionProvider) {
		this.sessionProvider = sessionProvider;
	}

	public Supplier<?> getSessionProvider() {
		if (sessionProvider == null) {
			this.sessionProvider = new Holder<Object>(new Object());
		}
		return sessionProvider;
	}

	public void build() {
		if (modelSession != null) {
			return;
		}

		lock.lock();
		try {
			BasicManagedGmSession session = new BasicManagedGmSession();

			ModelEnvironment modelEnvironment = accessService.getModelEnvironment(accessId);
			GmMetaModel foreignModel = modelEnvironment.getDataModel();
			GmMetaModel mergedMetaModel = session.merge().adoptUnexposed(true).doFor(foreignModel);

			modelOracle = new BasicModelOracle(mergedMetaModel);

			ResolutionContextBuilder rcb = new ResolutionContextBuilder(modelOracle);
			rcb.addStaticAspect(AccessAspect.class, accessId);
			rcb.addDynamicAspectProvider(RoleAspect.class, userRolesProvider);
			rcb.addStaticAspect(AccessTypeAspect.class, modelEnvironment.getDataAccessDenotationType());
			rcb.setSessionProvider(getSessionProvider());
			cmdResolver = new CmdResolverImpl(rcb.build());

			modelSession = session;

		} catch (Exception e) {
			throw new GmSessionRuntimeException("Error while building model accessory. Error: " + e.getMessage(), e);
		} finally {
			lock.unlock();
		}
	}

	@Override
	public ManagedGmSession getModelSession() {
		ensureInitialized();
		return modelSession;
	}

	@Override
	public ModelOracle getOracle() {
		ensureInitialized();
		return modelOracle;
	}

	@Override
	// TODO why? @Deprecated
	public CmdResolver getCmdResolver() {
		ensureInitialized();
		return cmdResolver;
	}

	private void ensureInitialized() {
		if (this.modelSession == null) {
			this.build();
		}
	}

}
