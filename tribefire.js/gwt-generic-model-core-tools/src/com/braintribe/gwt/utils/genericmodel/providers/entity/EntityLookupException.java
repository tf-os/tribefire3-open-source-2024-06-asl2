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
package com.braintribe.gwt.utils.genericmodel.providers.entity;

/**
 * An exception thrown by {@link EntityProvider}s, if an error occurs while looking up the entity.
 *
 * @author michael.lafite
 */
@SuppressWarnings("serial")
public class EntityLookupException extends EntityProviderException {

	public EntityLookupException(final String message) {
		super(message);
	}

	public EntityLookupException(final String message, final Throwable cause) {
		super(message, cause);
	}
}
