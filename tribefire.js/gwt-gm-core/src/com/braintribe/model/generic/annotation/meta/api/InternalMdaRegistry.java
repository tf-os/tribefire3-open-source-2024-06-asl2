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
package com.braintribe.model.generic.annotation.meta.api;

import static com.braintribe.utils.lcd.CollectionTools2.newIdentityMap;

import java.lang.annotation.Annotation;
import java.util.IdentityHashMap;
import java.util.Map;

import com.braintribe.model.generic.annotation.meta.api.RepeatableMdaHandler.RepeatableAggregatorMdaHandler;
import com.braintribe.model.generic.annotation.meta.handlers.AliasMdaHandler;
import com.braintribe.model.generic.annotation.meta.handlers.CompoundUniqueMdaHandler;
import com.braintribe.model.generic.annotation.meta.handlers.DescriptionMdaHandler;
import com.braintribe.model.generic.annotation.meta.handlers.NameMdaHandler;
import com.braintribe.model.generic.annotation.meta.handlers.PlaceholderMdaHandler;
import com.braintribe.model.generic.annotation.meta.handlers.PredicateMdaHandlers;
import com.braintribe.model.generic.annotation.meta.handlers.SimpleMdaHandlers;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.meta.data.MetaData;

/**
 * @author peter.gazdik
 */
/* package */ class InternalMdaRegistry implements MdaRegistry {

	public Map<Class<? extends Annotation>, MdaHandler<?, ?>> annoToHandler = new IdentityHashMap<>();
	public Map<Class<? extends Annotation>, MdaHandler<?, ?>> protoAnnoToHandler = new IdentityHashMap<>();

	public Map<EntityType<? extends MetaData>, MdaHandler<?, ?>> mdTypeToHandler = null;

	{
		// alphabetical order
		register( //
				PredicateMdaHandlers.CONFIDENTIAL, //
				PredicateMdaHandlers.DEPRECATED, //
				PredicateMdaHandlers.EMPHASIZED, //
				PredicateMdaHandlers.MANDATORY, //
				PredicateMdaHandlers.NON_DELETABLE, //
				PredicateMdaHandlers.SINGLETON, //
				PredicateMdaHandlers.UNIQUE, //
				PredicateMdaHandlers.UNMODIFIABLE //
		);

		register( //
				SimpleMdaHandlers.BIDIRECTIONAL, //
				SimpleMdaHandlers.COLOR, //
				SimpleMdaHandlers.INDEX, //
				SimpleMdaHandlers.MAX, //
				SimpleMdaHandlers.MAX_LENGTH, //
				SimpleMdaHandlers.MIN, //
				SimpleMdaHandlers.MIN_LENGTH, //
				SimpleMdaHandlers.PATTERN, //
				SimpleMdaHandlers.POSITIONAL_ARGUMENTS, //
				SimpleMdaHandlers.PRIORITY, //
				SimpleMdaHandlers.SELECTIVE_INFORMATION, //
				SimpleMdaHandlers.TYPE_SPECIFICATION //
		);

		registerRepeatable( //
				AliasMdaHandler.INSTANCE, //
				CompoundUniqueMdaHandler.INSTANCE, //
				DescriptionMdaHandler.INSTANCE, //
				NameMdaHandler.INSTANCE, //
				PlaceholderMdaHandler.INSTANCE //
		);

		/* We have to be careful here. We are using regular analyzer that creates instances of Confidential even in proto analysis. This could be a
		 * problem if the basic types (meta-model/EagerMetaModelTypes) already needed this entityType, as the T literal would be still null when
		 * accessed in the analyzer and later an NPE would happen. The fire-proof solution would be to create a ProtoConfidential class and use that
		 * in the proto case (similar to ProtoGmTypes). But for now, as I don't think we'll ever need the Confidential MD for these base types, it
		 * seems safe. */
		registerProto( //
				PredicateMdaHandlers.CONFIDENTIAL //
		);
	}

	@Override
	public Map<Class<? extends Annotation>, MdaHandler<?, ?>> protoAnnoToHandler() {
		return protoAnnoToHandler;
	}

	@Override
	public Map<Class<? extends Annotation>, MdaHandler<?, ?>> annoToHandler() {
		return annoToHandler;
	}

	public void registerRepeatable(RepeatableMdaHandler<?, ?, ?>... handlers) {
		register(handlers);

		for (RepeatableMdaHandler<?, ?, ?> handler : handlers)
			register(handler.aggregatorHandler());
	}

	public void register(MdaHandler<?, ?>... handlers) {
		register(annoToHandler, handlers);
	}

	private void registerProto(MdaHandler<?, ?>... handlers) {
		register(protoAnnoToHandler, handlers);
	}

	private void register(Map<Class<? extends Annotation>, MdaHandler<?, ?>> map, MdaHandler<?, ?>[] handlers) {
		for (MdaHandler<?, ?> handler : handlers)
			register(map, handler);
	}

	private void register(Map<Class<? extends Annotation>, MdaHandler<?, ?>> map, MdaHandler<?, ?> handler) {
		register(map, handler, handler.annotationClass());
	}

	private void register(Map<Class<? extends Annotation>, MdaHandler<?, ?>> map, MdaHandler<?, ?> handler, Class<? extends Annotation> annoClass) {
		MdaHandler<?, ?> other = map.putIfAbsent(annoClass, handler);

		if (other != null)
			throw new IllegalStateException("Multiple mappings for annotation '" + annoClass.getName() + "'. Handler '" + handler + "' converts to: "
					+ handler.metaDataClass().getName() + " and '" + other + "' converts to: " + other.metaDataClass().getName());
	}

	@Override
	public Map<EntityType<? extends MetaData>, MdaHandler<?, ?>> mdTypeToHandler() {
		if (mdTypeToHandler == null)
			fillMdTypeToHandler();

		return mdTypeToHandler;
	}

	private final String MD_TYPE_TO_HANDLER_LOCK = new String("MD_TYPE_TO_HANDLER_LOCK");

	private void fillMdTypeToHandler() {
		synchronized (MD_TYPE_TO_HANDLER_LOCK) {
			if (mdTypeToHandler != null)
				return;

			Map<EntityType<? extends MetaData>, MdaHandler<?, ?>> map = newIdentityMap();

			for (MdaHandler<?, ?> handler : annoToHandler.values())
				if (!(handler instanceof RepeatableAggregatorMdaHandler<?, ?>))
					map.put(handler.metaDataType(), handler);

			mdTypeToHandler = map;
		}
	}

}
