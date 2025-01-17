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
package com.braintribe.model.processing.meta.oracle.flat;

import static com.braintribe.utils.lcd.CollectionTools2.newMap;
import static com.braintribe.utils.lcd.CollectionTools2.nullSafe;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.braintribe.model.meta.GmEntityType;
import com.braintribe.model.meta.GmMetaModel;
import com.braintribe.model.meta.GmProperty;
import com.braintribe.model.meta.info.GmEntityTypeInfo;
import com.braintribe.model.meta.info.GmPropertyInfo;
import com.braintribe.model.meta.override.GmEntityTypeOverride;
import com.braintribe.model.meta.override.GmPropertyOverride;
import com.braintribe.model.processing.meta.oracle.hierarchy.GraphInliner;
import com.braintribe.model.processing.meta.oracle.hierarchy.InlinedGraph;

/**
 * Generator for flat local properties - i.e. properties / property info of given entity type that does not contain
 * information from the super types, just whatever was declared on this level (with the {@link GmEntityType} and
 * {@link GmEntityTypeOverride}s).
 * 
 * @author peter.gazdik
 */
public class FlatPropertiesFactory_WithSuper {

	private final FlatEntityType flatEntityType;
	private final Map<String, FlatProperty> flatProperties;

	/** this type and all it's super-types */
	private final List<GmEntityType> hierarchyTypes;
	private final Map<GmEntityType, Integer> hierarchyIndex;

	public static Map<String, FlatProperty> buildFor(FlatEntityType flatEntityType) {
		return new FlatPropertiesFactory_WithSuper(flatEntityType).build();
	}

	FlatPropertiesFactory_WithSuper(FlatEntityType flatEntityType) {
		this.flatEntityType = flatEntityType;
		this.flatProperties = newMap();

		InlinedGraph<GmEntityType> graph = GraphInliner.inline(flatEntityType.type, GmEntityType::getSuperTypes);
		this.hierarchyTypes = graph.list;
		this.hierarchyIndex = graph.index;
	}

	private Map<String, FlatProperty> build() {
		Map<String, FlatCustomType<?, ?>> flatTypesMap = flatEntityType.flatModel.flatCustomTypes;

		// @formatter:off
		List<GmEntityTypeInfo> allInfos = hierarchyTypes.stream()
				.map(gmEntityType -> (FlatEntityType) flatTypesMap.get(gmEntityType.getTypeSignature()))
				.flatMap(t -> t.infos.stream())
				.collect(Collectors.toList());
		// @formatter:on

		allInfos.sort(new EntityInfoComparator());

		for (GmEntityTypeInfo entityTypeInfo : allInfos)
			visit(entityTypeInfo);

		return flatProperties;
	}

	// Models are sorted by their max distance from leaf model (so the opposite of RootModel) (MI)
	// entity infos are sorted by declaring models (MI)
	// properties in a hierarchy are sorted by max distance (PI)
	// property infos are sorted primarily by their MI, for same MI, by their PI

	private void visit(GmEntityTypeInfo gmEntityTypeInfo) {
		for (GmPropertyOverride gmPropertyOverride : nullSafe(gmEntityTypeInfo.getPropertyOverrides()))
			visitPropertyInfo(gmPropertyOverride);

		if (!(gmEntityTypeInfo instanceof GmEntityType))
			return;

		GmEntityType gmEntityType = (GmEntityType) gmEntityTypeInfo;
		for (GmProperty gmProperty : gmEntityType.getProperties())
			visitPropertyInfo(gmProperty);
	}

	private void visitPropertyInfo(GmPropertyInfo gmPropertyInfo) {
		FlatProperty flatProperty = acquireFlatProperty(gmPropertyInfo);
		flatProperty.infos.add(gmPropertyInfo);
	}

	private FlatProperty acquireFlatProperty(GmPropertyInfo info) {
		GmProperty gmProperty = info.relatedProperty();
		String propertyName = gmProperty.getName();

		FlatProperty result = flatProperties.get(propertyName);
		if (result == null) {
			result = new FlatProperty(gmProperty);
			flatProperties.put(propertyName, result);
		}

		return result;
	}

	class EntityInfoComparator implements Comparator<GmEntityTypeInfo> {
		private final Map<GmMetaModel, Integer> modelIndex = flatEntityType.flatModel.modelIndex;

		@Override
		public int compare(GmEntityTypeInfo i1, GmEntityTypeInfo i2) {
			if (i1 == i2)
				return 0;

			GmMetaModel m1 = i1.getDeclaringModel();
			GmMetaModel m2 = i2.getDeclaringModel();

			if (m1 != m2)
				return modelIndex.get(m1).compareTo(modelIndex.get(m2));
			else
				return hierarchyIndex.get(i1.addressedType()).compareTo(hierarchyIndex.get(i2.addressedType()));
		}
	}

}
