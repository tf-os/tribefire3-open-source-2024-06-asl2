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
package com.braintribe.product.rat.imp;

import com.braintribe.model.accessdeployment.smood.CollaborativeSmoodAccess;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.product.rat.imp.impl.deployable.CollaborativeSmoodAccessImp;

/**
 * An imp manages an instance of a certain type of {@link GenericEntity} on which it can perform typical operations. For
 * example a {@link CollaborativeSmoodAccessImp} manages an instance of {@link CollaborativeSmoodAccess} and can amongst other things deploy or
 * undeploy it. Imps also have a {@link #session() session}.
 * <p>
 * <b>Note:</b> An imp's instance must be attached to its session and persisted in this session's access.
 *
 * @param <T>
 *            type of the instance managed by this imp
 */
public interface Imp<T extends GenericEntity> extends HasSession {

	/**
	 * Returns instance managed by this imp.
	 */
	T get();

	/**
	 * Deletes the instance/entity managed by this imp from the access/session.
	 */
	void delete();

}
