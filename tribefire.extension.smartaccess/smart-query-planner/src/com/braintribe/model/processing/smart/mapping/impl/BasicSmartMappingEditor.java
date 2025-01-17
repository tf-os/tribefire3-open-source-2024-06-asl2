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
package com.braintribe.model.processing.smart.mapping.impl;

import static com.braintribe.utils.lcd.CollectionTools2.newMap;

import java.util.Map;
import java.util.StringJoiner;
import java.util.function.Function;

import com.braintribe.model.accessdeployment.smart.meta.AsIs;
import com.braintribe.model.accessdeployment.smart.meta.SmartUnmapped;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.meta.data.MetaData;
import com.braintribe.model.meta.selector.UseCaseSelector;
import com.braintribe.model.processing.meta.editor.ModelMetaDataEditor;
import com.braintribe.model.processing.meta.oracle.ModelOracle;
import com.braintribe.model.processing.smart.mapping.api.SmartEntityTypeMappingEditor;
import com.braintribe.model.processing.smart.mapping.api.SmartMappingEditor;

/**
 * @author peter.gazdik
 */
public class BasicSmartMappingEditor implements SmartMappingEditor {

	protected final String globalIdPrefix;
	protected final ModelOracle lookupOracle;
	protected final ModelMetaDataEditor mdEditor;
	protected final Function<EntityType<?>, GenericEntity> entityFactory;
	protected final Function<String, GenericEntity> entityLookup;

	private final Map<String, UseCaseSelector> useCases = newMap();
	private final Map<String, AsIs> asIses = newMap();
	private final Map<String, SmartUnmapped> unmappeds = newMap();
	private final Map<String, SmartEntityTypeMappingEditor> entityTypeEditors = newMap();

	private boolean lastAcquireWasCreate;

	public static SmartMappingEditorBuilder newInstance() {
		return new BasicSmartMappingEditorBuilder();
	}

	/* package */ BasicSmartMappingEditor(BasicSmartMappingEditorBuilder configuration) {
		this.globalIdPrefix = configuration.globalIdPrefix();
		this.lookupOracle = configuration.lookupOracle();
		this.mdEditor = configuration.smartModelMdEditor();
		this.entityFactory = configuration.entityFactory();
		this.entityLookup = configuration.entityLookup();
	}

	@Override
	public SmartEntityTypeMappingEditor onEntityType(EntityType<?> entityType) {
		return onEntityType(entityType.getTypeSignature());
	}

	@Override
	public SmartEntityTypeMappingEditor onEntityType(String typeSignature) {
		return onEntityType(typeSignature, typeSignature);
	}

	@Override
	public SmartEntityTypeMappingEditor onEntityType(EntityType<?> entityType, String globalIdPart) {
		return onEntityType(entityType.getTypeSignature(), globalIdPart);
	}

	@Override
	public SmartEntityTypeMappingEditor onEntityType(String typeSignature, String globalIdPart) {
		return entityTypeEditors.computeIfAbsent(typeSignature, this::newSmartEntityTypeMappingEditor) //
				.forDelegate(null) //
				.withGlobalIdPart(globalIdPart);
	}

	private SmartEntityTypeMappingEditor newSmartEntityTypeMappingEditor(String typeSignature) {
		return new BasicSmartEntityTypeMappingEditor(this, mdEditor.onEntityType(typeSignature));
	}

	protected UseCaseSelector acquireDelegateSelector(String accessId) {
		return accessId == null ? null : useCases.computeIfAbsent(accessId, this::findDelegateSelector);
	}

	private UseCaseSelector findDelegateSelector(String accessId) {
		String globalId = "smart.map.access:" + accessId;

		UseCaseSelector result = acquireEntity(UseCaseSelector.T, globalId);
		if (lastAcquireWasCreate)
			result.setUseCase(accessId);

		return result;
	}

	public AsIs acquireAsIs(String accessId) {
		return asIses.computeIfAbsent(accessId, this::findAsIs);
	}

	private AsIs findAsIs(String accessId) {
		String globalId = accessId == null ? "global:asIs" : "asIs:" + accessId;
		return newMapping(AsIs.T, globalId, accessId);

	}

	public SmartUnmapped acquireUnmapped(String accessId) {
		return unmappeds.computeIfAbsent(accessId, this::findUnmapped);
	}

	private SmartUnmapped findUnmapped(String accessId) {
		String globalId = accessId == null ? "global:unmapped" : "unmapped:" + accessId;
		return newMapping(SmartUnmapped.T, globalId, accessId);
	}

	protected <T extends GenericEntity> T acquireEntity(EntityType<T> entityType, String globalId) {
		T result = (T) entityLookup.apply(globalId);

		if (result != null) {
			lastAcquireWasCreate = false;

		} else {
			lastAcquireWasCreate = true;
			result = newEntity(entityType, globalId);
		}

		return result;
	}

	protected <T extends MetaData> T newMapping(EntityType<T> entityType, String globalId, String accessId) {
		T result = newEntity(entityType, globalId);
		result.setSelector(acquireDelegateSelector(accessId));

		return result;
	}

	protected <T extends GenericEntity> T newEntity(EntityType<T> entityType, String globalId) {
		T result = (T) entityFactory.apply(entityType);
		result.setGlobalId(globalId);
		return result;
	}

	protected String globalId(String... parts) {
		StringJoiner sj = new StringJoiner(":");

		sj.add(globalIdPrefix);
		for (String part : parts)
			sj.add(part);

		return sj.toString();
	}

}
