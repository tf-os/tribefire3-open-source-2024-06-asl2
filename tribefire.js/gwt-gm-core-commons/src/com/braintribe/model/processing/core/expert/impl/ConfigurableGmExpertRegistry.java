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
package com.braintribe.model.processing.core.expert.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.braintribe.model.generic.GMF;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.reflection.GenericModelTypeReflection;
import com.braintribe.model.generic.reflection.TypeCode;
import com.braintribe.model.processing.core.expert.api.GmExpertBuilder;
import com.braintribe.model.processing.core.expert.api.GmExpertDefinition;
import com.braintribe.model.processing.core.expert.api.GmExpertRegistry;
import com.braintribe.model.processing.core.expert.api.GmExpertRegistryAware;
import com.braintribe.model.processing.core.expert.api.GmExpertRegistryException;


public class ConfigurableGmExpertRegistry implements GmExpertRegistry {
	private final Map<GmExpertKey, GmExpertDefinition> expertMap = new HashMap<GmExpertKey, GmExpertDefinition>();

	private final GenericModelTypeReflection genericModelTypeReflection = GMF.getTypeReflection();
	
	public void setExpertDefinitions(List<GmExpertDefinition> expertDefinitions) {
		for (GmExpertDefinition expertDefinition : expertDefinitions) {
			add(expertDefinition);
		}
	}

	public <T> ConfigurableGmExpertRegistry add(Class<? super T> expertType, Class<?> denotationType, T expert) {
		ConfigurableGmExpertDefinition definition = new ConfigurableGmExpertDefinition();
		definition.setExpertType(expertType);
		definition.setDenotationType(denotationType);
		definition.setExpert(expert);
		add(definition);
		return this;
	}

	public <T> ConfigurableGmExpertRegistry add(Class<? super T> expertType, Map<Class<?>, T> experts) {
		if (experts != null) {
			for (Map.Entry<Class<?>, T> expert : experts.entrySet()) {
				add(expertType, expert.getKey(), expert.getValue());
			}

		}
		return this;
	}

	protected void add(GmExpertDefinition expertDefinition) {
		GmExpertKey expertKey = new GmExpertKey(expertDefinition.expertType(), expertDefinition.denotationType());
		expertMap.put(expertKey, expertDefinition);
		initializeExpert(expertDefinition.expert());
	}
	
	protected void initializeExpert(Object expert) {
		if (expert instanceof GmExpertRegistryAware) {
			((GmExpertRegistryAware) expert).initExpertRegistry(this);
		}
	}
	

	@Override
	public <T> GmExpertBuilder<T> getExpert(Class<T> expertClass) {
		return new GmExpertBuilderImpl<T>(expertClass, true);
	}
	
	@Override
	public <T> GmExpertBuilder<T> findExpert(Class<T> expertClass) {
		return new GmExpertBuilderImpl<T>(expertClass, false);
	}
	
	
	protected GmExpertDefinition findExpertDefinition(GmExpertKey expertKey) {
		GmExpertDefinition expertDefinition = null;
		synchronized(expertMap) {
			expertDefinition = expertMap.get(expertKey);
		}
		
		if (expertDefinition == null) {
			
			GenericModelType denotationType = expertKey.getDenotationType();
			
			if (denotationType.getTypeCode() == TypeCode.entityType) {
				EntityType<?> entityDenotationType = (EntityType<?>) denotationType; 
				for (EntityType<?> denotationSuperType: entityDenotationType.getSuperTypes()) {
					GmExpertKey superExpertKey = new GmExpertKey(expertKey.getExpertClass(), denotationSuperType);
					expertDefinition = findExpertDefinition(superExpertKey); 
					if (expertDefinition != null) {
						synchronized(expertMap) {
							expertMap.put(superExpertKey, expertDefinition);
						}
						break;
					}
				}
			}
		}
		
		return expertDefinition;
	}
	
	private class GmExpertBuilderImpl<T> implements GmExpertBuilder<T> {
		
		private final Class<T> expertClass;
		private final boolean expertRequired;
		
		public GmExpertBuilderImpl(Class<T> expertClass, boolean expertRequired) {
			this.expertClass = expertClass;
			this.expertRequired = expertRequired;
		}
		
		@Override
		public <R extends T> R  forInstance(GenericEntity instance) throws GmExpertRegistryException {
			return forType(instance.entityType());
		}
		
		@Override
		public <R extends T> R  forType(Class<?> clazz)	throws GmExpertRegistryException {
			GenericModelType type = genericModelTypeReflection.getType(clazz);
			return forType(type);
		}
		
		@Override
		public <R extends T> R  forType(GenericModelType type) throws GmExpertRegistryException {
			GmExpertDefinition expertDefinition = findExpertDefinition(new GmExpertKey(expertClass, type));
			R expert = null;
			if (expertDefinition != null) {
				expert = (R)expertDefinition.expert();
			}
			if (expertRequired && expert == null ) {
				throw new GmExpertRegistryException("No expert found for type: "+type.getTypeSignature());
			}
			return expert;
		}
		
		@Override
		public <R extends T> R  forType(String typeSignature) throws GmExpertRegistryException {
			return forType(genericModelTypeReflection.getEntityType(typeSignature));
		}
		
		
	}
}
