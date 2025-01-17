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
package com.braintribe.model.processing.management;

import java.util.Objects;

import com.braintribe.cfg.Configurable;
import com.braintribe.logging.Logger;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.meta.GmMetaModel;
import com.braintribe.model.meta.GmType;
import com.braintribe.model.processing.session.api.persistence.PersistenceGmSession;

public class MetaModelFixer {
	private static final Logger logger = Logger.getLogger(MetaModelFixer.class);
	private PersistenceGmSession gmSession;

	@Configurable
	public void setGmSession(PersistenceGmSession gmSession) {
		this.gmSession = gmSession;
	}

	public void ensureAll(GmMetaModel model) {
		Objects.requireNonNull(model);
		ensureEssentialTypes(model);
		ensureDeclaringModel(model);
	}

	public void ensureEssentialTypes(GmMetaModel model) {
		Objects.requireNonNull(model);
		ensureBaseType(model);
		ensureSimpleTypes(model);

	}

	public void ensureDeclaringModel(GmMetaModel model) {
		try {

		} catch (Exception e) {
			throw new MetaModelSyncException(
					"Error while detecting declaring model for types of model: " + model.getName(), e);
		}
	}

	protected void ensureSimpleTypes(GmMetaModel gmMetaModel) {
	}

	protected void ensureBaseType(GmMetaModel gmMetaModel) {
	}

	private <T extends GmType> T acquireType(EntityType<T> et, String typeSignature) throws MetaModelSyncException {
		return null;
	}

}
