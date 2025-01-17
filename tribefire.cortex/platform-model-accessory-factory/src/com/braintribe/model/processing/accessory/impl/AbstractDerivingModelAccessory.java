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
package com.braintribe.model.processing.accessory.impl;

import java.util.Set;
import java.util.function.Supplier;

import com.braintribe.cfg.Configurable;
import com.braintribe.cfg.Required;
import com.braintribe.logging.Logger;
import com.braintribe.model.processing.meta.cmd.CmdResolverImpl;
import com.braintribe.model.processing.meta.cmd.ResolutionContextBuilder;
import com.braintribe.model.processing.meta.cmd.context.aspects.RoleAspect;
import com.braintribe.model.processing.session.api.managed.ModelAccessory;
import com.braintribe.model.processing.session.api.managed.ModelAccessorySupplier;

/**
 * An {@link AbstractModelAccessory} which obtain its {@link #getModelSession()} and {@link #getOracle()} from a parent {@link ModelAccessory} as
 * supplied by a {@link ModelAccessorySupplier}.
 * 
 * @author dirk.scheffler
 */
public abstract class AbstractDerivingModelAccessory extends AbstractModelAccessory {

	// constants
	private static final Logger log = Logger.getLogger(AbstractDerivingModelAccessory.class);

	// configurable
	private ModelAccessorySupplier modelAccessorySupplier;
	private Supplier<Set<String>> userRolesProvider;

	// cached
	private ModelAccessory parentAccessory;
	private final String parentLoadMonitor = new String("model-accessory-parent-lock");

	@Required
	@Configurable
	public void setModelAccessorySupplier(ModelAccessorySupplier modelAccessorySupplier) {
		this.modelAccessorySupplier = modelAccessorySupplier;
	}

	@Override
	@Required
	@Configurable
	public void setSessionProvider(Supplier<?> sessionProvider) {
		super.setSessionProvider(sessionProvider);
	}

	@Required
	@Configurable
	public void setUserRolesProvider(Supplier<Set<String>> userRolesProvider) {
		this.userRolesProvider = userRolesProvider;
	}

	@Override
	protected void build() {
		ModelAccessory parentAccessory = getParentAccessory();

		modelSession = parentAccessory.getModelSession();
		modelOracle = parentAccessory.getOracle();

		ResolutionContextBuilder rcb = new ResolutionContextBuilder(modelOracle);
		rcb.addDynamicAspectProvider(RoleAspect.class, userRolesProvider);
		rcb.setSessionProvider(getSessionProvider());

		initializeContextBuilder(rcb);

		cmdResolver = new CmdResolverImpl(rcb.build());

		log.debug(() -> "Built " + this);
	}

	protected void initializeContextBuilder(@SuppressWarnings("unused") ResolutionContextBuilder rcb) {
		// To be optionally overridden by implementations
	}

	public ModelAccessory getParentAccessory() {
		if (parentAccessory == null)
			synchronized (parentLoadMonitor) {
				if (parentAccessory == null) {
					parentAccessory = modelAccessorySupplier.getForModel(getModelName());
					parentAccessory.addListener(this::onOutdated);
				}
			}

		return parentAccessory;
	}

	private void onOutdated() {
		log.debug(() -> "Notifying onOutdated from parent: " + parentAccessory + " to " + AbstractDerivingModelAccessory.this);
		outdated();
	}

	protected abstract String getModelName();

}
