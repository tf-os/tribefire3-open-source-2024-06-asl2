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
package com.braintribe.tribefire.jinni.support.request.completion;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.jinni.meta.FileName;
import com.braintribe.model.jinni.meta.FolderName;
import com.braintribe.model.meta.GmEnumType;
import com.braintribe.model.meta.GmLinearCollectionType;
import com.braintribe.model.meta.GmMapType;
import com.braintribe.model.meta.GmType;
import com.braintribe.model.processing.meta.cmd.builders.PropertyMdResolver;
import com.braintribe.model.resource.FileResource;
import com.braintribe.model.resource.Resource;
import com.braintribe.utils.lcd.NullSafe;

/**
 * @author peter.gazdik
 */
/* package */ class KnownType {

	public static final String FILE_TYPE = "file";
	public static final String FOLDER_TYPE = "folder";

	public static KnownType IGNORED_TYPE = new KnownType("ignored", null, null);

	public final String valueType;
	public final String keyType;
	public final String collectionType;

	public KnownType(String valueType, String keyType, String collectionType) {
		this.valueType = NullSafe.get(valueType, "");
		this.keyType = NullSafe.get(keyType, "");
		this.collectionType = NullSafe.get(collectionType, "");
	}

	public static KnownType resolveKnownType(GmType type, PropertyMdResolver propertyMdResolver, EnumsRegistry enumsRegistry) {
		return new KnownTypeResolver(type, propertyMdResolver, enumsRegistry).resolve();
	}

	private static class KnownTypeResolver {

		private final GmType type;
		private final PropertyMdResolver propertyMdResolver;
		private final EnumsRegistry enumsRegistry;

		public KnownTypeResolver(GmType type, PropertyMdResolver propertyMdResolver, EnumsRegistry enumsRegistry) {
			this.type = type;
			this.propertyMdResolver = propertyMdResolver;
			this.enumsRegistry = enumsRegistry;
		}

		public KnownType resolve() {
			switch (type.typeKind()) {
				case LIST:
				case SET:
					return KnownType.ofCollection(typeOf(((GmLinearCollectionType) type).getElementType()));
				case MAP: {
					GmMapType mapType = (GmMapType) type;
					return KnownType.ofMap(typeOf(mapType.getKeyType()), typeOf(mapType.getValueType()));
				}
				default:
					return KnownType.of(typeOf(type));
			}
		}

		private String typeOf(GmType type) {
			switch (type.typeKind()) {
				case BOOLEAN:
					return "boolean";

				case STRING: {
					if (propertyMdResolver.is(FileName.T))
						return FILE_TYPE;
					if (propertyMdResolver.is(FolderName.T))
						return FOLDER_TYPE;
					break;
				}

				case ENUM:
					return enumsRegistry.acquireShortIdentifier((GmEnumType) type);

				case ENTITY: {
					EntityType<?> et = type.reflectionType();
					if (et == Resource.T || et == FileResource.T)
						return FILE_TYPE;
					break;
				}

				default:
					break;
			}

			return null;
		}

	}

	private static KnownType of(String type) {
		return type == null ? IGNORED_TYPE : new KnownType(type, null, null);
	}

	private static KnownType ofCollection(String elementType) {
		return elementType == null ? IGNORED_TYPE : new KnownType(elementType, null, "linear");
	}

	private static KnownType ofMap(String keyType, String valueType) {
		return keyType == null && valueType == null ? IGNORED_TYPE : new KnownType(valueType, keyType, "map");
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;

		if (!(o instanceof KnownType))
			return false;

		KnownType kt = (KnownType) o;

		return valueType.equals(kt.valueType) && //
				keyType.equals(kt.keyType) && //
				collectionType.equals(kt.collectionType);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		return prime * prime * collectionType.hashCode() + //
				prime * keyType.hashCode() + //
				valueType.hashCode();
	}

}
