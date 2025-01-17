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
package com.braintribe.utils.collection.impl;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

import com.braintribe.common.attribute.AttributeContext;
import com.braintribe.common.attribute.AttributeContextBuilder;
import com.braintribe.common.attribute.TypeSafeAttribute;
import com.braintribe.common.attribute.TypeSafeAttributeEntry;
import com.braintribe.common.attribute.TypeSafeAttribution;

import jsinterop.annotations.JsConstructor;
import jsinterop.annotations.JsIgnore;
import jsinterop.annotations.JsType;
import jsinterop.context.JsInteropNamespaces;

@JsType(namespace = JsInteropNamespaces.attr)
public class MapAttributeContext extends AbstractMap<Class<? extends TypeSafeAttribute<?>>, Object> implements AttributeContext, TypeSafeAttribution {
	
	private static class UndefinedValue {
		@Override
		public String toString() {
			return "<undefined>";
		}
	}
	
	private static final UndefinedValue undefined = new UndefinedValue();

	private final AttributeContext parent;
	private final Map<Class<? extends TypeSafeAttribute<?>>, Object> attributes = new HashMap<>();

	private Map<Class<? extends TypeSafeAttribute<?>>, Object> accumulatedAttributes;
	private boolean sealed;
	
	@JsConstructor
	public MapAttributeContext(AttributeContext parent) {
		this.parent = parent;
	}
	
	@JsIgnore
	public MapAttributeContext() {
		this(null);
	}

	public Map<Class<? extends TypeSafeAttribute<?>>, Object> asMap() {
		if (accumulatedAttributes == null) {
			seal();
			accumulatedAttributes = new IdentityHashMap<>();
			transferAttributes(accumulatedAttributes);
		}

		return accumulatedAttributes;
	}
	
	public void seal() {
		sealed = true;
	}
	
	protected void checkMutability() {
		if (sealed)
			throw new IllegalStateException("AttributeContext was sealed and is therefore immutable");
	}
	
	@Override
	public <A extends TypeSafeAttribute<? super V>, V> void setAttribute(Class<A> attribute, V value) {
		checkMutability();
		
		Object nullSafeValue = value != null? value: undefined;
		
		attributes.put(attribute, nullSafeValue);
	}
	
	@Override
	public Stream<TypeSafeAttributeEntry> streamAttributes() {
		return asMap().entrySet().stream().map(e -> TypeSafeAttributeEntry.of((Class<TypeSafeAttribute<Object>>)e.getKey(), e.getValue()));
	}

	@Override
	public <A extends TypeSafeAttribute<V>, V> Optional<V> findAttribute(Class<A> attribute) {
		return Optional.ofNullable((V)findAttribute_(attribute));
	}
	
	private Object findAttribute_(Class<? extends TypeSafeAttribute<?>> attribute) {
		Object value = accumulatedAttributes != null?
			accumulatedAttributes.get(attribute): 
			attributes.computeIfAbsent(attribute, this::findParentAttribute);
			
		if (value == undefined)
			value = null;
		
		return value;
	}
	
	private Object findParentAttribute(Class<? extends TypeSafeAttribute<?>> attribute) {
		return parent != null? parent.findAttribute((Class<TypeSafeAttribute<Object>>)attribute).orElse(null): null;
	}

	@Override
	public void transferAttributes(Map<Class<? extends TypeSafeAttribute<?>>, Object> target) {
		if (parent != null)
			parent.transferAttributes(target);

		for (Map.Entry<Class<? extends TypeSafeAttribute<?>>, Object> entry: attributes.entrySet()) {
			Object value = entry.getValue();
			if (value == undefined)
				value = null;
			
			target.put(entry.getKey(), value);
		}
	}

	@Override
	public AttributeContextBuilder derive() {
		return new AttributeContextBuilderImpl(new MapAttributeContext(MapAttributeContext.this));
	}

	@JsIgnore
	protected <T extends AttributeContextBuilderImpl> T derive(Function<AttributeContext, MapAttributeContext> constructor, Function<MapAttributeContext, T> builderConstructor) {
		MapAttributeContext newContext = constructor.apply(MapAttributeContext.this);
		
		if (newContext == this)
			throw new IllegalStateException("AttributeContext derivation should not lead to an identical context.");
			
		return builderConstructor.apply(newContext);
	}
	
	@Override
	public AttributeContext parent() {
		return parent;
	}

	@Override
	public Set<Entry<Class<? extends TypeSafeAttribute<?>>, Object>> entrySet() {
		return asMap().entrySet();
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();

		builder.append('{');
		
		boolean first = true;
		
		for (Map.Entry<Class<? extends TypeSafeAttribute<?>>, Object> entry: asMap().entrySet()) {
			if (first)
				first = false;
			else
				builder.append(", ");

			builder.append(entry.getKey().getSimpleName());
			builder.append(": ");
			builder.append(entry.getValue());
		}
		
		builder.append('}');
		
		return builder.toString();
	}
	
}
