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
package com.braintribe.model.processing.manipulation.basic;

import java.util.Map;

import com.braintribe.cc.lcd.CodingMap;
import com.braintribe.model.generic.GMF;
import com.braintribe.model.generic.commons.EntRefHashingComparator;
import com.braintribe.model.generic.manipulation.AtomicManipulation;
import com.braintribe.model.generic.manipulation.DeleteManipulation;
import com.braintribe.model.generic.manipulation.EntityProperty;
import com.braintribe.model.generic.manipulation.InstantiationManipulation;
import com.braintribe.model.generic.manipulation.ManifestationManipulation;
import com.braintribe.model.generic.manipulation.ManipulationType;
import com.braintribe.model.generic.manipulation.Owner;
import com.braintribe.model.generic.manipulation.PropertyManipulation;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.GenericModelTypeReflection;
import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.generic.value.EntityReference;
import com.braintribe.model.processing.manipulation.api.ManipulationExpositionContext;

/**
 * This only works with REMOTE manipulations.
 */
public class BasicMutableManipulationContext implements ManipulationExpositionContext {

	private static final GenericModelTypeReflection typeReflection = GMF.getTypeReflection();

	protected AtomicManipulation currentManipulation;
	protected ManipulationType currentManipulationType;
	protected EntityType<?> targetEntityType;
	protected Property targetProperty;
	protected EntityProperty targetEntityProperty;
	protected EntityReference targetEntityReference;

	private final Map<EntityReference, EntityReference> normalizedReferenceMap = CodingMap.create(EntRefHashingComparator.INSTANCE);

	@Override
	public <T extends AtomicManipulation> T getCurrentManipulation() {
		return (T) currentManipulation;
	}

	@Override
	public ManipulationType getCurrentManipulationType() {
		return currentManipulationType;
	}

	public void setCurrentManipulation(AtomicManipulation currentManipulation) {
		setCurrentManipulationSafe(currentManipulation);
	}

	/**
	 * Implementation of the interface method {@link #setCurrentManipulation(AtomicManipulation)}, but without the <code>throws</code>
	 * clause - since this implementation does not throw the exception, code working with this implementation directly make take advantage
	 * of that (it is not forced to handle the exception). For inheritance purposes however, we keep the original method with the
	 * <code>throws</code> clause, in case a subclass wants to throw that exception.
	 */
	public void setCurrentManipulationSafe(AtomicManipulation currentManipulation) {
		this.currentManipulation = currentManipulation;

		loadManipulationData();
	}

	private void loadManipulationData() {
		currentManipulationType = currentManipulation.manipulationType();
		targetEntityProperty = targetProperty();
		targetEntityReference = targetEntityReference();
		targetProperty = null;
		targetEntityType = null;
	}

	private EntityProperty targetProperty() {
		if (!(currentManipulation instanceof PropertyManipulation))
			return null;

		Owner owner = ((PropertyManipulation) currentManipulation).getOwner();

		return (EntityProperty) owner;
	}

	private EntityReference targetEntityReference() {
		if (currentManipulation instanceof PropertyManipulation) {
			return targetEntityProperty.getReference();
		}

		AtomicManipulation m = currentManipulation;

		switch (currentManipulationType) {
			case DELETE:
				return (EntityReference) ((DeleteManipulation) m).getEntity();
			case INSTANTIATION:
				return (EntityReference) ((InstantiationManipulation) m).getEntity();
			case MANIFESTATION:
				return (EntityReference) ((ManifestationManipulation) m).getEntity();
			default:
				throw new RuntimeException("Unknown AtomicManipulation type: " + currentManipulationType);
		}
	}

	@Override
	public Property getTargetProperty() {
		if (targetProperty == null) {
			targetProperty = targetEntityProperty == null ? null : getTargetEntityType().getProperty(getTargetPropertyName());
		}

		return targetProperty;
	}

	@Override
	public String getTargetPropertyName() {
		return targetEntityProperty == null ? null : targetEntityProperty.getPropertyName();
	}

	@Override
	public EntityReference getTargetReference() {
		return targetEntityReference;
	}

	@Override
	public EntityReference getNormalizedTargetReference() {
		return acquireNormalizedReference(targetEntityReference);
	}

	@Override
	public EntityReference getNormalizeReference(EntityReference entityReference) {
		return acquireNormalizedReference(entityReference);
	}

	private EntityReference acquireNormalizedReference(EntityReference reference) {
		EntityReference result = normalizedReferenceMap.get(reference);

		if (result == null) {
			result = reference;
			normalizedReferenceMap.put(result, result);
		}

		return result;
	}

	@Override
	public EntityType<?> getTargetEntityType() {
		if (targetEntityType == null) {
			targetEntityType = typeReflection.getEntityType(getTargetSignature());
		}

		return targetEntityType;
	}

	@Override
	public String getTargetSignature() {
		return targetEntityReference.getTypeSignature();
	}

}
