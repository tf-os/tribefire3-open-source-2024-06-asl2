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
package com.braintribe.model.processing.manipulation.basic.oracle;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.manipulation.AtomicManipulation;
import com.braintribe.model.generic.manipulation.EntityProperty;
import com.braintribe.model.generic.manipulation.LifecycleManipulation;
import com.braintribe.model.generic.manipulation.LocalEntityProperty;
import com.braintribe.model.generic.manipulation.ManipulationType;
import com.braintribe.model.generic.manipulation.Owner;
import com.braintribe.model.generic.manipulation.OwnerType;
import com.braintribe.model.generic.manipulation.PropertyManipulation;
import com.braintribe.model.generic.manipulation.VoidManipulation;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.generic.value.EntityReference;
import com.braintribe.model.processing.manipulation.api.AtomicManipulationOracle;
import com.braintribe.model.processing.manipulation.api.ManipulationOracle;

/**
 * @author peter.gazdik
 */
public class BasicAtomicManipulationOracle implements AtomicManipulationOracle {

	private final BasicManipulationOracle manipulationOracle;
	private final AtomicManipulation am;

	public BasicAtomicManipulationOracle(BasicManipulationOracle manipulationOracle, AtomicManipulation am) {
		this.manipulationOracle = manipulationOracle;
		this.am = am;
	}

	@Override
	public ManipulationOracle getManipulationOracle() {
		return manipulationOracle;
	}

	@Override
	public AtomicManipulation getManipulation() {
		return am;
	}

	@Override
	public boolean isProperty() {
		return am instanceof PropertyManipulation;
	}

	@Override
	public boolean isLifecycle() {
		return am instanceof LifecycleManipulation;
	}

	@Override
	public boolean isVoid() {
		return am instanceof VoidManipulation;
	}

	@Override
	public ManipulationType manipulationType() {
		return am.manipulationType();
	}

	@Override
	public String propertyName() {
		checkPropertyManipulation();
		return propertyM().getOwner().getPropertyName();
	}

	@Override
	public Property property() {
		checkPropertyManipulation();
		return propertyM().getOwner().property();
	}

	@Override
	public Owner owner() {
		checkPropertyManipulation();
		return propertyM().getOwner();
	}

	@Override
	public GenericEntity getManipulatedEntity() {
		GenericEntity result = findManipulatedEntity();
		if (result == null) {
			checkEntityRelated();
			return throwNoEntityFound();
		}

		return result;
	}

	@Override
	public GenericEntity findManipulatedEntity() {
		if (isProperty())
			return propertyM().getOwner().ownerEntity();
		if (isLifecycle())
			return lifecycleM().manipulatedEntity();

		return null;
	}

	@Override
	public String manipulatedEntitySignature() {
		if (isProperty())
			return ownerTypeSignature();
		if (isLifecycle())
			return lifecycleTypeSignature();

		return throwNotEntityRelated();
	}

	private String ownerTypeSignature() {
		Owner owner = getPropertyOwner();
		if (owner.ownerType() == OwnerType.LOCAL_ENTITY_PROPERTY)
			return ((LocalEntityProperty) owner).getEntity().entityType().getTypeSignature();
		else
			return ((EntityProperty) owner).getReference().getTypeSignature();
	}

	private String lifecycleTypeSignature() {
		GenericEntity ge = lifecycleM().manipulatedEntity();
		if (ge instanceof EntityReference)
			return ((EntityReference) ge).getTypeSignature();
		else
			return ge.entityType().getTypeSignature();
	}

	@Override
	public EntityType<?> manipulatedEntityType() {
		if (isProperty())
			return ownerType();
		if (isLifecycle())
			return lifecycleEntityType();

		return throwNotEntityRelated();
	}

	private EntityType<?> ownerType() {
		Owner owner = getPropertyOwner();
		if (owner.ownerType() == OwnerType.LOCAL_ENTITY_PROPERTY)
			return ((LocalEntityProperty) owner).getEntity().entityType();
		else
			return ((EntityProperty) owner).getReference().valueType();
	}

	private EntityType<?> lifecycleEntityType() {
		GenericEntity ge = lifecycleM().manipulatedEntity();
		if (ge instanceof EntityReference)
			return ((EntityReference) ge).valueType();
		else
			return ge.entityType();
	}

	private Owner getPropertyOwner() {
		checkPropertyManipulation();
		return propertyM().getOwner();
	}

	@Override
	public GenericEntity resolveManipulatedEntity() {
		// doing a shortcut, we should really have some way to tell if local or remote
		// on the other hand, the client should only call this with a remote stack
		GenericEntity entity = getManipulatedEntity();
		if (entity instanceof EntityReference)
			return manipulationOracle.resolve((EntityReference) entity);
		else
			return entity;
	}

	private PropertyManipulation propertyM() {
		return (PropertyManipulation) am;
	}

	private LifecycleManipulation lifecycleM() {
		return (LifecycleManipulation) am;
	}

	private void checkPropertyManipulation() {
		if (!isProperty())
			throw new IllegalStateException("Cannot provide propertyName for non-property manipulation: " + am);
	}

	private void checkEntityRelated() {
		if (!isProperty() && !isLifecycle())
			throwNotEntityRelated();
	}

	private <T> T throwNotEntityRelated() {
		throw new IllegalStateException(
				"The required operation can only be performed on a PropertyManipulation or a LifecycleManipulation, but not: " + am);
	}

	private GenericEntity throwNoEntityFound() {
		throw new IllegalStateException("Invalid manipulation - no manipulated entity set on: " + am);
	}

}
