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
package com.braintribe.model.processing.deployment.hibernate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.braintribe.model.dbs.DbColumn;
import com.braintribe.model.dbs.DbTable;
import com.braintribe.model.generic.session.exception.GmSessionException;
import com.braintribe.model.meta.GmEntityType;
import com.braintribe.model.meta.GmMetaModel;
import com.braintribe.model.meta.GmProperty;
import com.braintribe.model.meta.GmSimpleType;
import com.braintribe.model.meta.GmType;
import com.braintribe.model.processing.query.fluent.EntityQueryBuilder;
import com.braintribe.model.processing.session.api.persistence.PersistenceGmSession;
import com.braintribe.model.query.EntityQuery;

/**
 * 
 */
class GmTypeRegistry {

	private static GmMetaModel metaModel;
	private final Map<String, GmEntityType> entities = new HashMap<String, GmEntityType>();
	private final Map<String, GmProperty> properties = new HashMap<String, GmProperty>();
	private final Map<String, GmSimpleType> simpleTypes = new HashMap<String, GmSimpleType>();

	public GmTypeRegistry(GmMetaModel metaModel, PersistenceGmSession session) throws GmSessionException {
		GmTypeRegistry.metaModel = metaModel;
		for (GmType gmType: metaModel.getTypes()) {
			if (gmType.isGmEntity()) {
				registerEntity((GmEntityType) gmType);
			}
		}
		
		EntityQuery query = EntityQueryBuilder.from(GmSimpleType.class).done();
		List<GmSimpleType> gmSimpleTypes = session.query().entities(query).list();
		
		for (GmSimpleType gmSimpleType : gmSimpleTypes) {
			simpleTypes.put(gmSimpleType.getTypeSignature(), gmSimpleType);
		}
	}

	public GmSimpleType getGmSimpleType(String signature) {
		return simpleTypes.get(signature);
	}

	public GmEntityType getGmEntityType(String signature) {
		return entities.get(signature);
	}

	public void registerEntity(GmEntityType gmEntityType) {
		entities.put(gmEntityType.getTypeSignature(), gmEntityType);
		
		if (gmEntityType.getProperties() == null) {
			return;
		}
		
		for (GmProperty gmProperty : gmEntityType.getProperties()) {
			registerProperty(gmProperty);
		}
	}

	public GmProperty getProperty(DbColumn dbColumn) {
		return properties.get(getPropertySignature(dbColumn));
	}

	public void registerProperty(GmProperty gmProperty) {
		properties.put(getPropertySignature(gmProperty), gmProperty);
	}

	private static String getEntitySignatureFrom(DbTable dbTable) {
		return DbTableProcessingUtils.getEntitySignatureFrom(metaModel, dbTable);
	}

	private static String getPropertySignature(GmProperty gmProperty) {
		return gmProperty.getDeclaringType().getTypeSignature() + "#" + gmProperty.getName();
	}

	private static String getPropertySignature(DbColumn dbColumn) {
		return getEntitySignatureFrom(dbColumn.getOwner()) + "#" + getPropertyNameFrom(dbColumn);
	}

	private static String getPropertyNameFrom(DbColumn dbColumn) {
		return DbTableProcessingUtils.getPropertyName(dbColumn);
	}

}
