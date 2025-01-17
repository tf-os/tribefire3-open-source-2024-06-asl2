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
package com.braintribe.model.io.metamodel.testbase;

import static com.braintribe.utils.lcd.CollectionTools2.asList;
import static com.braintribe.utils.lcd.CollectionTools2.newList;
import static com.braintribe.utils.lcd.CollectionTools2.newMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.braintribe.model.meta.GmEntityType;
import com.braintribe.model.meta.GmProperty;
import com.braintribe.model.meta.GmType;
import com.braintribe.model.meta.data.MetaData;

/**
 * 
 */
public class EntityTypeBuilder {

	private final GmEntityType type;
	private final Map<String, GmProperty> propertyByName = newMap();

	public EntityTypeBuilder(String typeSignature) {
		type = GmEntityType.T.create("type:" + typeSignature);
		type.setTypeSignature(typeSignature);
	}

	public EntityTypeBuilder addAncestors(GmEntityType... gmEntityTypes) {
		for (GmEntityType et : gmEntityTypes) {
			addAncestor(et);
		}

		return this;
	}

	public EntityTypeBuilder addAncestor(GmEntityType gmEntType) {
		List<GmEntityType> superTypes = type.getSuperTypes();
		if (superTypes == null) {
			superTypes = new LinkedList<GmEntityType>();
			type.setSuperTypes(superTypes);
		}

		superTypes.add(gmEntType);

		return this;
	}

	public EntityTypeBuilder setIsAbstract() {
		type.setIsAbstract(true);
		return this;
	}

	public EntityTypeBuilder setProperties(Object... args) {
		if (args.length % 2 == 1) {
			throw new RuntimeException("Problem in test. Expecting even number of args for properties");
		}

		List<GmProperty> properties = new ArrayList<GmProperty>();

		int i = 0;
		while (i < args.length) {
			String propName = (String) args[i++];
			GmType propType = (GmType) args[i++];

			properties.add(createGmProperty(propName, propType));
		}

		type.setProperties(properties);

		return this;
	}

	public EntityTypeBuilder addProperty(GmProperty gmProperty) {
		List<GmProperty> properties = type.getProperties();
		if (properties == null) {
			properties = newList();
			type.setProperties(properties);
		}

		gmProperty.setDeclaringType(type);
		properties.add(gmProperty);

		return this;
	}

	public EntityTypeBuilder setInitializer(String propName, Object initializer) {
		propertyByName.get(propName).setInitializer(initializer);
		return this;
	}

	public EntityTypeBuilder addMd(MetaData... md) {
		type.getMetaData().addAll(Arrays.asList(md));
		return this;
	}

	public EntityTypeBuilder addPropertyMd(String propName, MetaData... md) {
		propertyByName.get(propName).getMetaData().addAll(asList(md));
		return this;
	}

	public EntityTypeBuilder setEvaluatesTo(GmType evaluatesToType) {
		type.setEvaluatesTo(evaluatesToType);
		return this;
	}

	private GmProperty createGmProperty(String propName, GmType propType) {
		GmProperty gmProp = GmProperty.T.create("property:" + type.getTypeSignature() + "/" + propName);
		gmProp.setName(propName);
		gmProp.setDeclaringType(type);
		gmProp.setType(propType);

		propertyByName.put(propName, gmProp);

		return gmProp;
	}

	public GmEntityType create() {
		return type;
	}

}
