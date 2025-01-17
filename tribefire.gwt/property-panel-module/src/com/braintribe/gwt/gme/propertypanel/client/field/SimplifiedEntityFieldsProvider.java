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
package com.braintribe.gwt.gme.propertypanel.client.field;

import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import com.braintribe.gwt.genericmodelgxtsupport.client.PropertyFieldContext;
import com.braintribe.gwt.gmview.util.client.GMEUtil;
import com.braintribe.gwt.ioc.client.Required;
import com.braintribe.model.generic.GMF;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.sencha.gxt.widget.core.client.form.Field;

/**
 * This Function is responsible for providing field providers based on the given Class.
 * @author michel.docouto
 *
 */
public class SimplifiedEntityFieldsProvider implements Function<PropertyFieldContext, Supplier<? extends Field<?>>> {
	
	private Map<Class<?>, Supplier<? extends Field<?>>> fieldsProvidersMap;
	private Supplier<SimplifiedEntityField> simplifiedEntityFieldProvider;
	private Supplier<QuickAccessTriggerField> quickAccessTriggerFieldSupplier;
	
	/**
	 * Configures the fields providers map.
	 */
	@Required
	public void setFieldsProvidersMap(Map<Class<?>, Supplier< ? extends Field<?>>> fieldsProvidersMap) {
		this.fieldsProvidersMap = fieldsProvidersMap;
	}
	
	/**
	 * Configures the simplified entity field provider.
	 */
	@Required
	public void setSimplifiedEntityFieldProvider(Supplier<SimplifiedEntityField> simplifiedEntityFieldProvider) {
		this.simplifiedEntityFieldProvider = simplifiedEntityFieldProvider;
	}
	
	/**
	 * Configures the required field used when editing an entity collection.
	 */
	@Required
	public void setQuickAccessTriggerFieldSupplier(Supplier<QuickAccessTriggerField> quickAccessTriggerFieldSupplier) {
		this.quickAccessTriggerFieldSupplier = quickAccessTriggerFieldSupplier;
	}

	@Override
	public Supplier<? extends Field<?>> apply(PropertyFieldContext propertyFieldContext) {
		GenericModelType modelType = propertyFieldContext.getModelType();
		Class<?> clazz = modelType.getJavaType();
		Supplier<? extends Field<?>> fieldProvider = fieldsProvidersMap.get(clazz);
		if (fieldProvider == null) {
			//RVE - we need check also subClass, also subClass can show in same type of field
		    for (Class<?> mapClazz : fieldsProvidersMap.keySet()) {
		        if (GMF.getTypeReflection().getType(mapClazz).isAssignableFrom(GMF.getTypeReflection().getType(clazz))) {
		        	fieldProvider = fieldsProvidersMap.get(mapClazz);
		        	break;
		        }
		    }
		}
		
		if (fieldProvider != null)
			return fieldProvider;
		
		if (propertyFieldContext.isHandlingCollection() && modelType.isEntity())
			return prepareCollectionEntityField(modelType);
		
		if (modelType.isEntity())
			return () -> {
				SimplifiedEntityField simplifiedEntityField = simplifiedEntityFieldProvider.get();
				simplifiedEntityField.setTypeCondition(GMEUtil.prepareTypeCondition(modelType));
				return simplifiedEntityField;
			};
		
		return null;
	}

	private Supplier<? extends Field<?>> prepareCollectionEntityField(GenericModelType modelType) {
		return () -> {
			QuickAccessTriggerField quickAccessTriggerField = quickAccessTriggerFieldSupplier.get();
			quickAccessTriggerField.setTypeCondition(GMEUtil.prepareTypeCondition(modelType));
			return quickAccessTriggerField;
		};
	}

}
