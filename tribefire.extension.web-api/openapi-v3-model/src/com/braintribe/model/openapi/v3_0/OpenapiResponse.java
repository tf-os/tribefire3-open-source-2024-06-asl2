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
package com.braintribe.model.openapi.v3_0;

import java.util.Map;

import com.braintribe.model.generic.annotation.meta.Mandatory;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

/**
 * See https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.3.md#responseObject
 *
 */
public interface OpenapiResponse extends JsonReferencable {

	EntityType<OpenapiResponse> T = EntityTypes.T(OpenapiResponse.class);

	@Mandatory
	String getDescription();
	void setDescription(String description);

	Map<String, OpenapiHeader> getHeaders();
	void setHeaders(Map<String, OpenapiHeader> headers);

	Map<String, OpenapiMediaType> getContent();
	void setContent(Map<String, OpenapiMediaType> content);

}
