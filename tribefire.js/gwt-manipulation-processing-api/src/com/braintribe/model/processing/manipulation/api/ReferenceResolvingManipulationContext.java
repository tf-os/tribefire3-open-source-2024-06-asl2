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
package com.braintribe.model.processing.manipulation.api;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.manipulation.ChangeValueManipulation;
import com.braintribe.model.generic.manipulation.Manipulation;
import com.braintribe.model.generic.manipulation.PropertyManipulation;
import com.braintribe.model.generic.value.EntityReference;
import com.braintribe.model.generic.value.PersistentEntityReference;

/**
 * An extension of {@link ManipulationExpositionContext} which is able to resolve references and therefore can provide
 * the actual instances this manipulation .
 */
public interface ReferenceResolvingManipulationContext extends ManipulationExpositionContext {

	/**
	 * @return instance corresponding to given {@link Manipulation} if the underlying reference has type
	 *         {@link PersistentEntityReference}, or <tt>null</tt> in other cases.
	 */
	<T extends GenericEntity> T getTargetInstance();

	/**
	 * @return in case of a {@link PropertyManipulation}, this returns the current value of the manipulated property.
	 *         The result value is something like {@code  getTargetProperty().getProperty(getTargetInstance())} (using
	 *         the {@link #getTargetProperty()} and {@link #getTargetInstance()} methods).
	 */
	<T> T getTargetPropertyValue();

	/**
	 * @return entity corresponding to given reference if it is a {@link PersistentEntityReference}, or <tt>null</tt>
	 *         otherwise. This may be e.g. used for resolving the entities referenced as values of various
	 *         {@link PropertyManipulation}s.
	 */
	GenericEntity resolveReference(EntityReference reference) throws ManipulationContextException;

	/**
	 * If the current manipulation is a {@link ChangeValueManipulation} which changes the <tt>id</tt> property of the
	 * owner, then the reference will be returned which will reference the owner from now on. In other case,
	 * <tt>null</tt> is returned. Also, the method returns the normalized reference, so any later call of
	 * {@link #getNormalizedTargetReference()} for manipulations using the same owner will return the same instance as
	 * this method..
	 */
	EntityReference getNormalizedNewReference();

}
