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
package com.braintribe.model.processing.impl;

import java.util.function.Consumer;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.processing.ConstraintViolation;
import com.braintribe.model.processing.PropertyValidationContext;
import com.braintribe.model.processing.meta.cmd.builders.ModelMdResolver;
import com.braintribe.model.processing.meta.cmd.builders.PropertyMdResolver;
import com.braintribe.model.processing.traversing.api.path.TraversingPropertyModelPathElement;

/**
 * Contains various information about what is to be validated as well as a metadata resolver
 * ({@link #getPropertyMdResolver()}. Constraint violations can be reported by
 * {@link #notifyConstraintViolation(String)}.
 * 
 * @author Neidhart.Orlich
 *
 */
public class PropertyValidationContextImpl extends ValidationContextImpl implements PropertyValidationContext {
	private final Property property;
	private final GenericModelType propertyType;
	private final PropertyMdResolver propertyMdResolver;
	private final GenericEntity entity;
	private final EntityType<GenericEntity> entityType;

	public PropertyValidationContextImpl(TraversingPropertyModelPathElement pathElement, ModelMdResolver mdResolver,
			Consumer<ConstraintViolation> constraintViolationConsumer) {
		super(pathElement, constraintViolationConsumer);

		this.entity = pathElement.getEntity();
		this.entityType = entity.entityType();
		this.property = pathElement.getProperty();
		this.propertyType = property.getType();
		this.propertyMdResolver = mdResolver.entity(entity).property(property);

	}

	@Override
	public Property getProperty() {
		return property;
	}

	@Override
	public Object getPropertyValue() {
		return getValue();
	}

	@Override
	public GenericModelType getPropertyType() {
		return propertyType;
	}

	@Override
	public PropertyMdResolver getPropertyMdResolver() {
		return propertyMdResolver;
	}

	@Override
	public GenericEntity getEntity() {
		return entity;
	}

	@Override
	public EntityType<GenericEntity> getEntityType() {
		return entityType;
	}

}
