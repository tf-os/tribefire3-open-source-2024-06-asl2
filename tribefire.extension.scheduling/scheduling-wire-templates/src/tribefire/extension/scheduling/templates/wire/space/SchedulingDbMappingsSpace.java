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
package tribefire.extension.scheduling.templates.wire.space;

import com.braintribe.model.accessdeployment.hibernate.meta.EntityMapping;
import com.braintribe.model.accessdeployment.hibernate.meta.PropertyMapping;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.meta.data.constraint.MaxLength;
import com.braintribe.model.processing.meta.editor.EntityTypeMetaDataEditor;
import com.braintribe.model.processing.meta.editor.ModelMetaDataEditor;
import com.braintribe.wire.api.annotation.Import;
import com.braintribe.wire.api.annotation.Managed;
import com.braintribe.wire.api.annotation.Scope;
import com.braintribe.wire.api.context.WireContext;
import com.braintribe.wire.api.scope.InstanceConfiguration;
import com.braintribe.wire.api.scope.InstanceQualification;

import tribefire.cortex.initializer.support.wire.space.AbstractInitializerSpace;
import tribefire.extension.scheduling.templates.util.NameShortening;
import tribefire.extension.scheduling.templates.wire.contract.SchedulingDbMappingsContract;

@Managed
public class SchedulingDbMappingsSpace extends AbstractInitializerSpace implements SchedulingDbMappingsContract {

	@Import
	private WireContext<?> wireContext;

	@Managed
	@Override
	public EntityMapping forceMapping() {
		EntityMapping bean = create(EntityMapping.T);
		bean.setForceMapping(true);
		return bean;
	}

	@Managed
	@Override
	public PropertyMapping clobProperty() {
		PropertyMapping bean = create(PropertyMapping.T);
		bean.setType("materialized_clob");
		return bean;
	}

	@Managed
	@Override
	public MaxLength maxLen2k() {
		MaxLength bean = create(MaxLength.T);
		bean.setLength(2000);
		return bean;
	}

	@Managed
	@Override
	public MaxLength maxLen4k() {
		MaxLength bean = create(MaxLength.T);
		bean.setLength(4000);
		return bean;
	}

	@Managed
	@Override
	public MaxLength maxLen10Meg() {
		MaxLength bean = create(MaxLength.T);
		bean.setLength(10_000_000);
		return bean;
	}

	@Override
	@Managed
	public MaxLength maxLen2GigProperty() {
		MaxLength bean = create(MaxLength.T);
		bean.setLength(Integer.MAX_VALUE);
		return bean;
	}

	@Override
	@Managed
	public MaxLength maxLen1k() {
		MaxLength maxLength = create(MaxLength.T);
		maxLength.setLength(1024);
		return maxLength;
	}

	@Override
	@Managed
	public EntityMapping entityUnmapped() {
		EntityMapping bean = create(EntityMapping.T);

		bean.setMapToDb(false);

		return bean;
	}

	// Helpers

	@Managed(Scope.prototype)
	private PropertyMapping lookupIndex(EntityType<?> entityType, String propertyName) {
		PropertyMapping bean = create(PropertyMapping.T);
		// global id
		InstanceConfiguration currentInstance = wireContext.currentInstancePath().lastElement().config();
		InstanceQualification instanceQualification = currentInstance.qualification();
		String prefix = initializerSupport.initializerId();

		String propertySignature = entityType.getTypeSignature() + "/" + propertyName;

		String indexName = NameShortening.shorten(propertySignature, propertyName);

		bean.setGlobalId("wire://" + prefix + "/" + instanceQualification.space().getClass().getSimpleName() + "/" + instanceQualification.name()
				+ "/" + indexName);

		bean.setIndex(indexName);
		return bean;
	}

	@Override
	public void applyIndex(ModelMetaDataEditor editor, EntityType<?> entityType, String propertyName) {
		editor.onEntityType(entityType).addPropertyMetaData(propertyName, lookupIndex(entityType, propertyName));
	}

	@Override
	public void applyIndices(ModelMetaDataEditor editor, EntityType<?> entityType, String... propertyNames) {
		EntityTypeMetaDataEditor onEntityType = editor.onEntityType(entityType);

		for (String propertyName : propertyNames) {
			onEntityType.addPropertyMetaData(propertyName, lookupIndex(entityType, propertyName));
		}
	}

}
