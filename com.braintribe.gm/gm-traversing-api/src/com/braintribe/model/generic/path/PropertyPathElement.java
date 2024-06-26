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
package com.braintribe.model.generic.path;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.path.api.IPropertyModelPathElement;
import com.braintribe.model.generic.reflection.Property;

@SuppressWarnings("unusable-by-js")
public class PropertyPathElement extends PropertyRelatedModelPathElement implements IPropertyModelPathElement {

	public PropertyPathElement(GenericEntity entity, Property property, Object value) {
		super(entity, property, property.getType().getActualType(value), value);
	}

	@Override
	public ModelPathElementType getPathElementType() {
		return ModelPathElementType.Property;
	}

	@Override
	public String toString() {
		return super.toString();
	}

	@Override
	public PropertyPathElement copy() {
		return new PropertyPathElement(getEntity(), getProperty(), getValue());
	}

	@Override
	public com.braintribe.model.generic.path.api.ModelPathElementType getElementType() {
		return com.braintribe.model.generic.path.api.ModelPathElementType.Property;
	}
}