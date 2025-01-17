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
package tribefire.platform.impl.deployment;

import java.util.function.Supplier;

import com.braintribe.cfg.Required;
import com.braintribe.model.deployment.Deployable;
import com.braintribe.model.processing.deployment.api.DeployRegistry;
import com.braintribe.model.processing.deployment.api.DeployRegistryListener;
import com.braintribe.model.processing.deployment.api.DeployedUnit;

/**
 * Wrapper around a supplier that throws an exception if the {@link #get()} method is called before cortex is deployed. Useful for cases when
 * resolving the to-be-supplied object depends on the cortex or triggers cortex deployment (e.g. DCSA shared storage).
 * 
 * @author peter.gazdik
 */
public class PostCortexSupplier<T> implements Supplier<T>, DeployRegistryListener {

	private String componentName;
	private Supplier<? extends T> delegate;
	private DeployRegistry deployRegistry;

	private volatile boolean cortexDeployed;

	@Required
	public void setSuppliedCommponentName(String componentName) {
		this.componentName = componentName;
	}

	@Required
	public void setDelegate(Supplier<? extends T> delegate) {
		this.delegate = delegate;
	}

	@Required
	public void setDeployRegistry(DeployRegistry deployRegistry) {
		this.deployRegistry = deployRegistry;
		this.deployRegistry.addListener(this);
	}

	@Override
	public T get() {
		if (!cortexDeployed)
			throw new IllegalStateException("Cannot access '" + componentName + "' before cortex is deployed!");

		return delegate.get();
	}

	@Override
	public void onDeploy(Deployable deployable, DeployedUnit deployedUnit) {
		if ("cortex".equals(deployable.getExternalId())) {
			cortexDeployed = true;
			deployRegistry.removeListener(this);
		}
	}

	@Override
	public void onUndeploy(Deployable deployable, DeployedUnit deployedUnit) {
		// NO OP
	}

}
