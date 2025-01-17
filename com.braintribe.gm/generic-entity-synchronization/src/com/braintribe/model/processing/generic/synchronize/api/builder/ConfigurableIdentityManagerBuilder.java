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
package com.braintribe.model.processing.generic.synchronize.api.builder;

import java.util.Collection;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.processing.generic.synchronize.api.GenericEntitySynchronization;
import com.braintribe.model.processing.generic.synchronize.api.IdentityManager;

/**
 * A generic {@link IdentityManager} implementation that can be configured to
 * individual needs. Basically it provides support to specify the type which
 * this {@link IdentityManager} should be responsible for and properties that
 * should be used to lookup existing entities.
 */
public interface ConfigurableIdentityManagerBuilder<S extends GenericEntitySynchronization, B extends ConfigurableIdentityManagerBuilder<S,B>> extends QueryingIdentiyManagerBuilder<S, B> {

	/**
	 * If set, null values of properties will be synchronized. By default this
	 * behavior is disabled.
	 */
	B transferNullValues();

	/**
	 * Adds a property (name) of the responsible type that should be included
	 * during synchronization.
	 * <br />
	 * If includedProperties are specified only those properties are respected 
	 * during synchronization (except they are also specified in excludedProperties)
	 */
	B addIncludedProperty(String property);

	/**
	 * Same as {@link #addIncludedProperty(String)} but let you specify multiple
	 * properties in one step.
	 */
	B addIncludedProperties(Collection<String> properties);

	/**
	 * Same as {@link #addIncludedProperty(String)} but let you specify multiple
	 * properties in one step.
	 */
	B addIncludedProperties(String... properties);

	/**
	 * Adds a property (name) of the responsible type that should be excluded
	 * from the synchronization.
	 * <br />	
	 * Properties specified in excludedProperties are not respected during synchronization
	 * and thus remain untouched in the target.<br /> Excluded properties are stronger then
	 * included properties which means specifying a property in both collections will result
	 * into an excluded property. 
	 */
	B addExcludedProperty(String property);

	/**
	 * Same as {@link #addExcludedProperty(String)} but let you specify multiple
	 * properties in one step.
	 */
	B addExcludedProperties(Collection<String> properties);

	/**
	 * Same as {@link #addExcludedProperty(String)} but let you specify multiple
	 * properties in one step.
	 */
	B addExcludedProperties(String... properties);

	/**
	 * Defines the {@link EntityType} for which this {@link IdentityManager} is responsible for.
	 */
	B responsibleFor(EntityType<? extends GenericEntity> type);

	/**
	 * Convenience way to specify the responsible type for {@link #responsibleFor(EntityType)}
	 */
	B responsibleFor(String typeSignature);

	/**
	 * Convenience way to specify the responsible type for {@link #responsibleFor(EntityType)}
	 */
	B responsibleFor(Class<? extends GenericEntity> typeClass);
}
