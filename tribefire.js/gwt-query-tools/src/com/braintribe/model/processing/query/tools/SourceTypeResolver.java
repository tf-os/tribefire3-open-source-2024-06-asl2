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
package com.braintribe.model.processing.query.tools;

import com.braintribe.model.generic.GMF;
import com.braintribe.model.generic.reflection.CollectionType;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.reflection.GenericModelTypeReflection;
import com.braintribe.model.processing.query.eval.api.RuntimeQueryEvaluationException;
import com.braintribe.model.query.From;
import com.braintribe.model.query.Join;
import com.braintribe.model.query.PropertyOperand;
import com.braintribe.model.query.Source;

/**
 * 
 */
public class SourceTypeResolver {

	private static final GenericModelTypeReflection typeReflection = GMF.getTypeReflection();

	/**
	 * Note that in case of joining with property of type {@code Set<SomeEntity>}, the resolved type is <tt>SomeEntity</tt>.
	 */
	public static <T extends GenericModelType> T resolveType(Source source) {
		return (T) resolveTypeHelper(source, true);
	}

	public static <T extends GenericModelType> T resolveType(Source source, boolean resolveCollectionType) {
		return (T) resolveTypeHelper(source, resolveCollectionType);
	}

	public static <T extends GenericModelType> T resolvePropertyType(PropertyOperand propertyOperand, boolean resolveCollectionType) {
		String propertyName = propertyOperand.getPropertyName();

		if (propertyName == null)
			return resolveType(propertyOperand.getSource(), resolveCollectionType);
		else
			return resolvePropertyType(propertyOperand.getSource(), propertyName, resolveCollectionType);
	}

	private static GenericModelType resolveTypeHelper(Source source, boolean resolveCollectionType) {
		if (source instanceof From) {
			return typeReflection.getEntityType(((From) source).getEntityTypeSignature());

		} else {
			Join join = (Join) source;
			return resolvePropertyTypeHelper(join.getSource(), join.getProperty(), resolveCollectionType);
		}
	}

	private static GenericModelType resolvePropertyTypeHelper(Source source, String propertyName, boolean resolveCollectionType) {
		EntityType<?> entityType = resolveType(source, true);
		GenericModelType propertyType = entityType.getProperty(propertyName).getType();

		if (resolveCollectionType) {
			switch (propertyType.getTypeCode()) {
				case listType:
				case mapType:
				case setType:
					return resolveCollectionElementType(propertyType);
				default:
					break;
			}
		}
		return propertyType;
	}

	public static <T extends GenericModelType> T resolvePropertyType(Source source, String propertyPath, boolean resolveCollectionType) {
		return (T) resolvePropertyPathTypeHelper(source, propertyPath, resolveCollectionType);
	}

	private static GenericModelType resolvePropertyPathTypeHelper(Source source, String propertyPath, boolean resolveCollectionType) {
		EntityType<?> entityType = resolveType(source, true);

		String[] propertyNames = propertyPath.split("\\.");
		int counter = 0;
		for (String propertyName : propertyNames) {
			boolean isLast = ++counter == propertyNames.length;
			GenericModelType propertyType = entityType.getProperty(propertyName).getType();

			if (!isLast) {
				if (!propertyType.isEntity()) {
					throw new RuntimeQueryEvaluationException("Illegal attempt to dereference a property path for a non entity ("
							+ buildPropertyChain(source) + "." + buildPropertyChain(propertyNames, counter) + ".[" + propertyNames[counter] + "])");
				}
				entityType = (EntityType<?>) propertyType;

			} else if (propertyType.isCollection() && resolveCollectionType) {
				return resolveCollectionElementType(propertyType);

			} else {
				return propertyType;
			}
		}

		throw new IllegalArgumentException("Cannot resolve property path for '" + buildPropertyChain(source) + "' and propertyPath: " + propertyPath);
	}

	public static GenericModelType resolveCollectionElementType(GenericModelType propertyType) {
		return ((CollectionType) propertyType).getCollectionElementType();
	}

	// ############################################
	// ## . . . . . . Helper methods . . . . . . ##
	// ############################################

	public static String buildPropertyChain(Source source) {
		if (source instanceof From) {
			return ((From) source).getEntityTypeSignature();
		} else {
			Join join = (Join) source;
			return buildPropertyChain(join.getSource()) + "." + join.getProperty();
		}
	}

	public static String buildPropertyChain(String[] properties, int counter) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < counter; i++) {
			if (i > 0) {
				sb.append(',');
			}
			sb.append(properties[i]);
		}

		return sb.toString();
	}

}
