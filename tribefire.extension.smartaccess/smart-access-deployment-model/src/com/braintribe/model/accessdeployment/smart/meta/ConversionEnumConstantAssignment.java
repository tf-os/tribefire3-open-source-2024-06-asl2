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
package com.braintribe.model.accessdeployment.smart.meta;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

/**
 * Mapping for an enum constant which is represented by a simple value in the delegate.
 */
public interface ConversionEnumConstantAssignment extends EnumConstantAssignment {

	EntityType<ConversionEnumConstantAssignment> T = EntityTypes.T(ConversionEnumConstantAssignment.class);

	// @formatter:off
	Object getDelegateValue();
	void setDelegateValue(Object delegateValue);
	// @formatter:on

}