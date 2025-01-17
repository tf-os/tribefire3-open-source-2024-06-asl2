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
package com.braintribe.model.processing.web.rest;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.reflection.Property;

/**
 * This interface is used by the HttpRequestEntityDecoder when properties of type "object" when decoding
 * HttpServletRequest.
 * 
 *
 */
@FunctionalInterface
public interface PropertyTypeResolver {

	/**
	 * Resolves the actual type of the given property.
	 * 
	 * @param entityType
	 *            the entityType, never {@code null}
	 * @param property
	 *            the the property to resolve the type for, never {@code null}
	 * 
	 * @return the type, must not be {@code null}
	 */
	GenericModelType resolvePropertyType(EntityType<?> entityType, Property property);
}
