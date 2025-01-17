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
package com.braintribe.model.processing.deployment.api;

import java.util.Map;

import com.braintribe.model.deployment.Deployable;
import com.braintribe.model.generic.reflection.EntityType;

/**
 * <p>
 * Provides access to bundled components resulted from one deployment operation.
 * 
 * @author dirk.scheffler
 */
public interface DeployedUnit {

	/**
	 * <p>
	 * Returns the component matching the given {@code componentType}.
	 * 
	 * <p>
	 * Unlike {@link #findComponent(EntityType)}, this method throws a {@link DeploymentException} if no component is
	 * found matching the given {@code componentType}.
	 * 
	 * @param componentType
	 *            The {@code componentType} for which a component is to be returned.
	 * @return The bound value of the bundled component within the unit matching the given {@code componentType}.
	 * @throws DeploymentException
	 *             If no component is found matching the given {@code componentType}.
	 */
	<C> C getComponent(EntityType<? extends Deployable> componentType) throws DeploymentException;

	/**
	 * <p>
	 * Returns the component matching the given {@code componentType}.
	 * 
	 * <p>
	 * Unlike {@link #getComponent(EntityType)}, this method returns {@code null} if no component is found matching the
	 * given {@code componentType}.
	 * 
	 * @param componentType
	 *            The {@code componentType} for which a component is to be returned.
	 * @return The bound value of the bundled component within the unit matching the given {@code componentType}.
	 */
	<C> C findComponent(EntityType<? extends Deployable> componentType);
	
	/**
	 * <p>
	 * Returns the component matching the given {@code componentType}.
	 * 
	 * <p>
	 * Unlike {@link #findDeployedComponent(EntityType)}, this method throws a {@link DeploymentException} if no component is
	 * found matching the given {@code componentType}.
	 * 
	 * @param componentType
	 *            The {@code componentType} for which a component is to be returned.
	 * @return The bundled {@link DeployedComponent} within the unit matching the given {@code componentType}.
	 * @throws DeploymentException
	 *             If no component is found matching the given {@code componentType}.
	 */
	DeployedComponent getDeployedComponent(EntityType<? extends Deployable> componentType);
	
	/**
	 * <p>
	 * Returns the component matching the given {@code componentType}.
	 * 
	 * <p>
	 * Unlike {@link #getDeployedComponent(EntityType)}, this method returns {@code null} if no component is found matching the
	 * given {@code componentType}.
	 * 
	 * @param componentType
	 *            The {@code componentType} for which a component is to be returned.
	 * @return The bundled {@link DeployedComponent} within the unit matching the given {@code componentType}.
	 */
	DeployedComponent findDeployedComponent(EntityType<? extends Deployable> componentType);

	/**
	 * <p>
	 * Returns a map of components bundled within this {@link DeployedUnit}, keyed by {@code componentType}.
	 * 
	 * @return A map of components bundled within this {@link DeployedUnit}.
	 */
	Map<EntityType<? extends Deployable>, DeployedComponent> getComponents();

}
