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
package com.braintribe.model.processing.meta.editor;

import static com.braintribe.model.processing.meta.editor.BasicModelMetaDataEditor.add;
import static com.braintribe.model.processing.meta.editor.BasicModelMetaDataEditor.remove;

import java.util.function.Consumer;
import java.util.function.Predicate;

import com.braintribe.model.generic.reflection.GenericModelException;
import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.meta.GmEntityType;
import com.braintribe.model.meta.data.MetaData;
import com.braintribe.model.meta.info.GmEntityTypeInfo;
import com.braintribe.model.meta.info.GmPropertyInfo;
import com.braintribe.model.processing.meta.oracle.flat.FlatEntityType;
import com.braintribe.model.processing.meta.oracle.flat.FlatProperty;

/**
 * @author peter.gazdik
 */
@SuppressWarnings("unusable-by-js")
public class BasicEntityTypeMetaDataEditor implements EntityTypeMetaDataEditor {

	private final BasicModelMetaDataEditor modelMdEditor;
	private final FlatEntityType flatEntityType;

	public BasicEntityTypeMetaDataEditor(BasicModelMetaDataEditor modelMdEditor, FlatEntityType flatEntityType) {
		this.modelMdEditor = modelMdEditor;
		this.flatEntityType = flatEntityType;
	}

	@Override
	public GmEntityType getEntityType() {
		return flatEntityType.type;
	}

	@Override
	public EntityTypeMetaDataEditor configure(Consumer<GmEntityTypeInfo> consumer) {
		consumer.accept(acquireGmEntityTypeInfo());
		return this;
	}

	@Override
	public EntityTypeMetaDataEditor configure(String propertyName, Consumer<GmPropertyInfo> consumer) {
		consumer.accept(acquireGmPropertyInfo(propertyName));
		return this;
	}

	@Override
	public EntityTypeMetaDataEditor configure(Property property, Consumer<GmPropertyInfo> consumer) {
		configure(property.getName(), consumer);
		return this;
	}

	@Override
	public EntityTypeMetaDataEditor configure(GmPropertyInfo gmPropertyInfo, Consumer<GmPropertyInfo> consumer) {
		configure(gmPropertyInfo.relatedProperty().getName(), consumer);
		return this;
	}

	@Override
	public EntityTypeMetaDataEditor addMetaData(MetaData... mds) {
		add(acquireGmEntityTypeInfo().getMetaData(), mds);
		return this;
	}

	@Override
	public EntityTypeMetaDataEditor removeMetaData(Predicate<? super MetaData> filter) {
		remove(acquireGmEntityTypeInfo().getMetaData(), filter);
		return this;
	}

	@Override
	public EntityTypeMetaDataEditor addPropertyMetaData(MetaData... mds) {
		add(acquireGmEntityTypeInfo().getPropertyMetaData(), mds);
		return this;
	}

	@Override
	public EntityTypeMetaDataEditor removePropertyMetaData(Predicate<? super MetaData> filter) {
		remove(acquireGmEntityTypeInfo().getPropertyMetaData(), filter);
		return this;
	}

	@Override
	public EntityTypeMetaDataEditor addPropertyMetaData(String propertyName, MetaData... mds) {
		add(acquireGmPropertyInfo(propertyName).getMetaData(), mds);
		return this;
	}

	@Override
	public EntityTypeMetaDataEditor removePropertyMetaData(String propertyName, Predicate<? super MetaData> filter) {
		remove(acquireGmPropertyInfo(propertyName).getMetaData(), filter);
		return this;
	}
	@Override
	public EntityTypeMetaDataEditor addPropertyMetaData(Property property, MetaData... mds) {
		addPropertyMetaData(property.getName(), mds);
		return this;
	}

	@Override
	public EntityTypeMetaDataEditor removePropertyMetaData(Property property, Predicate<? super MetaData> filter) {
		removePropertyMetaData(property.getName(), filter);
		return this;
	}

	@Override
	public EntityTypeMetaDataEditor addPropertyMetaData(GmPropertyInfo gmPropertyInfo, MetaData... mds) {
		addPropertyMetaData(gmPropertyInfo.relatedProperty().getName(), mds);
		return this;
	}

	@Override
	public EntityTypeMetaDataEditor removePropertyMetaData(GmPropertyInfo gmPropertyInfo, Predicate<? super MetaData> filter) {
		removePropertyMetaData(gmPropertyInfo.relatedProperty().getName(), filter);
		return this;
	}

	protected GmEntityTypeInfo acquireGmEntityTypeInfo() {
		if (modelMdEditor.appendToDeclaration) {
			return flatEntityType.type;
		} else {
			return modelMdEditor.leafModel.acquireGmEntityTypeInfo(flatEntityType.type);
		}
	}

	protected GmPropertyInfo acquireGmPropertyInfo(String propertyName) {
		FlatProperty flatProperty = flatEntityType.acquireFlatProperties().get(propertyName);
		if (flatProperty == null) {
			throw new GenericModelException("Property '" + propertyName + "' not found for entity type: " + flatEntityType.type.getTypeSignature());
		}

		if (modelMdEditor.appendToDeclaration) {
			return flatProperty.gmProperty;
		} else {
			return modelMdEditor.leafModel.acquireGmPropertyInfo(flatEntityType.type, flatProperty.gmProperty);
		}
	}

}
