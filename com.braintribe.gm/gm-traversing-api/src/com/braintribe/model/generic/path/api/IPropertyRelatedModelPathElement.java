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
package com.braintribe.model.generic.path.api;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.Property;

/**
 * This {@link IModelPathElement} collects common properties of all property based elements such as:
 * 
 * <ul>
 * <li>{@link IPropertyModelPathElement}</li>
 * <li>{@link IListItemModelPathElement}</li>
 * <li>{@link ISetItemModelPathElement}</li>
 * <li>{@link IMapKeyModelPathElement}</li>
 * <li>{@link IMapValueModelPathElement}</li>
 * <li></li>
 * </ul>
 * 
 * @author dirk.scheffler
 * @author pit.steinlin
 * @author peter.gazdik
 */
public interface IPropertyRelatedModelPathElement extends IModelPathElement, HasEntityPropertyInfo {

	/**
	 * 
	 * @return the {@link Property} which holds the value returned by {@link #getValue()}
	 */
	@Override
	Property getProperty();

	/**
	 * 
	 * @return the {@link GenericEntity} which is the owner of the property value returned by {@link #getValue()}
	 */
	@Override
	<T extends GenericEntity> T getEntity();

	/**
	 * 
	 * @return the {@link GenericEntity} which is the owner of the property value returned by {@link #getValue()}
	 */
	@Override
	<T extends GenericEntity> EntityType<T> getEntityType();

	@Override
	default boolean isPropertyRelated() {
		return true;
	}

}
