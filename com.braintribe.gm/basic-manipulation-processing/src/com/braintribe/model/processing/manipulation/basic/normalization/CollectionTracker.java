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
package com.braintribe.model.processing.manipulation.basic.normalization;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.braintribe.cc.lcd.CodingSet;
import com.braintribe.model.generic.manipulation.AddManipulation;
import com.braintribe.model.generic.manipulation.AtomicManipulation;
import com.braintribe.model.generic.manipulation.ChangeValueManipulation;
import com.braintribe.model.generic.manipulation.ClearCollectionManipulation;
import com.braintribe.model.generic.manipulation.Owner;
import com.braintribe.model.generic.manipulation.RemoveManipulation;
import com.braintribe.model.generic.value.EntityReference;

/**
 * 
 */
abstract class CollectionTracker {

	protected final Owner owner;
	protected final String propertySignature;

	public CollectionTracker(Owner owner, String propertySignature) {
		this.owner = owner;
		this.propertySignature = propertySignature;
	}

	/**
	 * Note that if there is a {@link ClearCollectionManipulation}, it is always the first manipulation for given property, that is assured by the
	 * {@link SimpleManipulationNormalizer}.
	 */
	public abstract void onClearCollection();

	/**
	 * Note that if there is a {@link ChangeValueManipulation}, it is always the first manipulation for given property, that is assured by the
	 * {@link SimpleManipulationNormalizer}.
	 */
	public abstract void onChangeValue(ChangeValueManipulation m);

	public abstract void onBulkInsert(AddManipulation m);

	public abstract void onBulkRemove(RemoveManipulation m);

	public abstract void appendAggregateManipulations(List<AtomicManipulation> manipulations, Set<EntityReference> entitiesToDelete);

	protected static <E> E cast(Object o) {
		return (E) o;
	}

	protected <E> E getTypeSafeCvmValue(Class<E> expectedType, ChangeValueManipulation m) {
		Object value = m.getNewValue();
		if (value == null)
			return null;

		if (!expectedType.isInstance(value))
			throw new IllegalArgumentException("ChangeValueManipulation with an incompatible value found! Property " + owner.getPropertyName()
					+ " of " + owner.ownerEntity() + "  is of type '" + propertySignature + "', but the value was of type '"
					+ value.getClass().getSimpleName() + "'. Actual value: " + value);

		return (E) value;
	}

	protected <T> Set<T> ensureComparable(Collection<T> set) {
		Set<T> s = CodingSet.create(ElementHashingComparator.INSTANCE);
		s.addAll(set);

		return s;
	}

}
