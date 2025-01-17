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
package com.braintribe.model.processing.meta.cmd;

import static com.braintribe.utils.lcd.CollectionTools2.newMap;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.meta.data.MetaData;
import com.braintribe.model.meta.selector.MetaDataSelector;
import com.braintribe.model.processing.meta.cmd.context.SelectorContextAspect;
import com.braintribe.model.processing.meta.cmd.context.experts.SelectorExpert;
import com.braintribe.model.processing.meta.oracle.ModelOracle;

/**
 * 
 */
public class ResolutionContextBuilder implements CmdResolverBuilder {

	private final ResolutionContextInfo rci;

	private final Map<EntityType<? extends MetaDataSelector>, SelectorExpert<?>> experts = newMap();
	private final Map<Class<? extends SelectorContextAspect<?>>, Object> aspectValues = newMap();
	private final Map<Class<? extends SelectorContextAspect<?>>, Supplier<?>> dynamicAspectValueProviders = newMap();
	private Set<MetaData> defaultMetaData = Collections.emptySet();
	private Supplier<?> sessionProvider;
	private int maxSessionCacheSize = 50;

	public ResolutionContextBuilder(ModelOracle modelOracle) {
		this.rci = new ResolutionContextInfo(modelOracle);
	}

	@Override
	public <T extends MetaDataSelector> ResolutionContextBuilder addExpert(EntityType<? extends T> entityType, SelectorExpert<T> expert) {
		if (expert != null)
			experts.put(entityType, expert);
		return this;
	}

	@Override
	public ResolutionContextBuilder addExperts(Map<EntityType<? extends MetaDataSelector>, SelectorExpert<?>> experts) {
		if (experts != null)
			this.experts.putAll(experts);
		return this;
	}

	@Override
	public <T, A extends SelectorContextAspect<T>> ResolutionContextBuilder addStaticAspect(Class<A> aspect, T value) {
		if (value != null)
			aspectValues.put(aspect, value);

		return this;
	}

	@Override
	public <T, A extends SelectorContextAspect<T>> ResolutionContextBuilder addDynamicAspectProvider(Class<A> clazz, Supplier<T> provider) {
		if (provider != null)
			dynamicAspectValueProviders.put(clazz, provider);

		return this;
	}

	@Override
	public ResolutionContextBuilder addDynamicAspectProviders(Map<Class<? extends SelectorContextAspect<?>>, Supplier<?>> providers) {
		if (providers != null)
			dynamicAspectValueProviders.putAll(providers);

		return this;
	}

	@Override
	public ResolutionContextBuilder setSessionProvider(Supplier<?> sessionProvider) {
		this.sessionProvider = sessionProvider;
		return this;
	}

	@Override
	public ResolutionContextBuilder setMaxSessionCacheSize(int maxSessionCacheSize) {
		this.maxSessionCacheSize = maxSessionCacheSize;
		return this;
	}

	@Override
	public ResolutionContextBuilder setDefaultMetaData(Set<MetaData> defaultMetaData) {
		this.defaultMetaData = defaultMetaData;
		return this;
	}

	public ResolutionContextInfo build() {
		rci.setStaticAspects(aspectValues);
		rci.setExperts(experts);
		rci.setDynamicAspectValueProviders(dynamicAspectValueProviders);
		rci.setSessionProvider(sessionProvider);
		rci.setMaxSessionCacheSize(maxSessionCacheSize);
		rci.setDefaultMetaData(defaultMetaData);

		return rci;
	}

	@Override
	public CmdResolver done() {
		return new CmdResolverImpl(build());
	}

}
