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

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Objects;

import com.braintribe.model.deployment.Deployable;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.processing.deployment.api.DeployedComponent;
import com.braintribe.model.processing.deployment.api.DeployedUnit;
import com.braintribe.model.processing.deployment.api.DeploymentException;

public class ConfigurableDeployedUnit implements DeployedUnit {

	protected Map<EntityType<? extends Deployable>, DeployedComponent> bindingsMap = new IdentityHashMap<>();
	protected boolean proxyUnit = false;
	
	public void setProxyUnit(boolean proxyUnit) {
		this.proxyUnit = proxyUnit;
	}

	@Override
	public <C> C getComponent(EntityType<? extends Deployable> componentType) throws DeploymentException {
		Objects.requireNonNull(componentType, "componentType must not be null");
		
		C component = (C) getDeployedComponent(componentType).exposedImplementation();
		return component;
		
	}

	@Override
	public <C> C findComponent(EntityType<? extends Deployable> componentType) {
		Objects.requireNonNull(componentType, "componentType must not be null");
		
		DeployedComponent deployedComponent = findDeployedComponent(componentType);
		
		C component = (C) (deployedComponent != null? deployedComponent.exposedImplementation(): null);
		return component;
	}
	
	@Override
	public DeployedComponent getDeployedComponent(EntityType<? extends Deployable> componentType) throws DeploymentException {
		
		Objects.requireNonNull(componentType, "componentType must not be null");
		
		DeployedComponent component = findDeployedComponent(componentType);
		
		if (component == null) {
			throw new DeploymentException("Component not found for type "+componentType.getTypeSignature());
		}
		
		return component;
	}
	
	@Override
	public DeployedComponent findDeployedComponent(EntityType<? extends Deployable> componentType) {
		
		Objects.requireNonNull(componentType, "componentType must not be null");
		
		return bindingsMap.get(componentType);
	}
	

	@Override
	public Map<EntityType<? extends Deployable>, DeployedComponent> getComponents() {
		return bindingsMap;
	}

	public void putDeployedComponent(EntityType<? extends Deployable> componentType, DeployedComponent component) {
		bindingsMap.put(componentType, component);
	}
	
	public void put(EntityType<? extends Deployable> componentType, Object component) {
		bindingsMap.put(componentType, new ConfigurableDeployedComponent(null, component, component));
	}
	
}
