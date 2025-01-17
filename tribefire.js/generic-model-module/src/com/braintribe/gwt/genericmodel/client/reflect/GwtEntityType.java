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
package com.braintribe.gwt.genericmodel.client.reflect;

import static com.braintribe.utils.lcd.CollectionTools2.asList;
import static com.braintribe.utils.lcd.CollectionTools2.mapBy;
import static com.braintribe.utils.lcd.CollectionTools2.newList;
import static java.util.Collections.emptyList;

import java.util.List;
import java.util.Map;

import com.braintribe.gwt.genericmodel.client.itw.CastableTypeMap;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.enhance.EnhancedEntity;
import com.braintribe.model.generic.enhance.FieldAccessingPropertyAccessInterceptor;
import com.braintribe.model.generic.enhance.SessionUnboundPropertyAccessInterceptor;
import com.braintribe.model.generic.reflection.EntityInitializer;
import com.braintribe.model.generic.reflection.EntityInitializerImpl;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.GmtsEnhancedEntityStub;
import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.generic.reflection.PropertyAccessInterceptor;
import com.braintribe.model.generic.reflection.TransientProperty;
import com.braintribe.model.generic.reflection.type.custom.AbstractEntityType;

/**
 * An extension for {@link EntityType} useful for implementation of {@link EnhancedEntity} in GWT.
 */
@SuppressWarnings("unusable-by-js")
public abstract class GwtEntityType<T extends GenericEntity> extends AbstractEntityType<T> {

	private boolean hasExplicitToString;
	private EntityInitializer[] initializers;
	private Property[] initializedProperties;
	private Object protoInstance;

	private CastableTypeMap castableTypeMap;

	public void setCastableTypeMap(CastableTypeMap castableTypeMap) {
		this.castableTypeMap = castableTypeMap;
	}

	public void setProtoInstance(Object protoInstance) {
		this.protoInstance = protoInstance;
	}

	public Object getProtoInstance() {
		return protoInstance;
	}

	public CastableTypeMap getCastableTypeMap() {
		return castableTypeMap;
	}

	public void setHasExplicitToString(boolean hasExplicitToString) {
		this.hasExplicitToString = hasExplicitToString;
	}

	public void setInitializedProperties(Property[] initializedProperties) {
		this.initializedProperties = initializedProperties;
	}

	public void setInitializers(EntityInitializer[] initializers) {
		this.initializers = initializers;
	}

	@Override
	protected EntityInitializer[] getInitializers() {
		if (initializers == null && initializedProperties != null) {
			initializers = new EntityInitializer[initializedProperties.length];
			int i = 0;
			for (Property p : initializedProperties)
				initializers[i++] = EntityInitializerImpl.newInstance(p, p.getInitializer());
		}
		return initializers;
	}

	/**
	 * Why do we need this in GWT only?
	 */
	public boolean hasExplicitToString() {
		return hasExplicitToString;
	}

	@Override
	public boolean isAssignableFrom(EntityType<?> entityType) {
		GwtEntityType<?> otherType = (GwtEntityType<?>) entityType;
		return isInstance(otherType.protoInstance);
	}

	@Override
	public Class<T> plainClass() {
		throw new UnsupportedOperationException("Method 'GwtEntityType.plainClass' is not supported in GWT!");
	}

	@Override
	public Class<T> enhancedClass() {
		throw new UnsupportedOperationException("Method 'GwtEntityType.enhancedClass' is not supported in GWT!");
	}

	@Override
	public T createPlainRaw() {
		return createRaw(FieldAccessingPropertyAccessInterceptor.INSTANCE);
	}

	@Override
	public T createRaw(PropertyAccessInterceptor pai) {
		T result = createRaw();
		((GmtsEnhancedEntityStub) result).pai = pai;
		return result;
	}

	public T jsCreate() {
		T result = createRaw(SessionUnboundPropertyAccessInterceptor.INSTANCE);
		return super.initialize(result);
	}

	/**
	 * Not that given array might be empty in case all the transient properties are inherited. But this is only called
	 * iff there is at least one transient property, declared or inherited.
	 */
	public void setDeclaredTransientProperties(TransientProperty[] tps) {
		List<TransientProperty> tpList = tps == null ? emptyList() : asList(tps);
		Map<String, TransientProperty> tpsByName = mapBy(tpList, TransientProperty::getName);

		/* In case we inherit the same transient property twice, we only take the first on. We assume it's the same type
		 * in both cases, cause otherwise the java interfaces themselves would not compile. */

		for (EntityType<?> superType : getSuperTypes())
			for (TransientProperty tp : superType.getTransientProperties())
				tpsByName.putIfAbsent(tp.getName(), tp);

		List<TransientProperty> allTps = newList(tpsByName.values());
		super.setTransientProperties(allTps);
	}

}
