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
package com.braintribe.model.processing.meta.cmd.builders;

import java.util.List;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.meta.data.MetaData;
import com.braintribe.model.processing.meta.cmd.CascadingMetaDataException;
import com.braintribe.model.processing.meta.cmd.context.ExtendedSelectorContext;
import com.braintribe.model.processing.meta.cmd.extended.ConstantMdDescriptor;
import com.braintribe.model.processing.meta.cmd.extended.EntityMdDescriptor;
import com.braintribe.model.processing.meta.cmd.extended.EnumMdDescriptor;
import com.braintribe.model.processing.meta.cmd.extended.MdDescriptor;
import com.braintribe.model.processing.meta.cmd.extended.ModelMdDescriptor;
import com.braintribe.model.processing.meta.cmd.extended.PropertyMdDescriptor;
import com.braintribe.model.processing.meta.cmd.resolvers.ConstantMdAggregator;
import com.braintribe.model.processing.meta.cmd.resolvers.EntityMdAggregator;
import com.braintribe.model.processing.meta.cmd.resolvers.EnumMdAggregator;
import com.braintribe.model.processing.meta.cmd.resolvers.MdAggregator;
import com.braintribe.model.processing.meta.cmd.resolvers.ModelMdAggregator;
import com.braintribe.model.processing.meta.cmd.resolvers.PropertyMdAggregator;
import com.braintribe.model.processing.meta.cmd.result.ConstantMdResult;
import com.braintribe.model.processing.meta.cmd.result.EntityMdResult;
import com.braintribe.model.processing.meta.cmd.result.EnumMdResult;
import com.braintribe.model.processing.meta.cmd.result.MdResult;
import com.braintribe.model.processing.meta.cmd.result.ModelMdResult;
import com.braintribe.model.processing.meta.cmd.result.PropertyMdResult;

/**
 * @author peter.gazdik
 */
public abstract class MdResultImpl<M extends MetaData, D extends MdDescriptor> implements MdResult<M, D> {

	private final EntityType<M> metaDataType;
	private final MdAggregator mdAggregator;
	private final ExtendedSelectorContext selectorContext;

	public MdResultImpl(EntityType<M> metaDataType, MdAggregator mdAggregator, ExtendedSelectorContext selectorContext) {
		this.metaDataType = metaDataType;
		this.mdAggregator = mdAggregator;
		this.selectorContext = selectorContext;
	}

	@Override
	public final M exclusive() throws CascadingMetaDataException {
		return (M) mdAggregator.exclusive(metaDataType, selectorContext);
	}

	@Override
	public final D exclusiveExtended() throws CascadingMetaDataException {
		return (D) mdAggregator.exclusiveExtended(metaDataType, selectorContext);
	}

	@Override
	public final List<M> list() throws CascadingMetaDataException {
		return (List<M>) mdAggregator.list(metaDataType, selectorContext);
	}

	@Override
	public final List<D> listExtended() throws CascadingMetaDataException {
		return (List<D>) (List<?>) mdAggregator.listExtended(metaDataType, selectorContext);
	}

	// Model
	public static class ModelMdResultImpl<M extends MetaData> extends MdResultImpl<M, ModelMdDescriptor> implements ModelMdResult<M> {
		public ModelMdResultImpl(EntityType<M> metaDataType, ModelMdAggregator mdAggregator, ExtendedSelectorContext selectorContext) {
			super(metaDataType, mdAggregator, selectorContext);
		}
	}

	// Entity
	public static class EntityMdResultImpl<M extends MetaData> extends MdResultImpl<M, EntityMdDescriptor> implements EntityMdResult<M> {
		public EntityMdResultImpl(EntityType<M> metaDataType, EntityMdAggregator mdAggregator, ExtendedSelectorContext selectorContext) {
			super(metaDataType, mdAggregator, selectorContext);
		}
	}

	// Property
	public static class PropertyMdResultImpl<M extends MetaData> extends MdResultImpl<M, PropertyMdDescriptor> implements PropertyMdResult<M> {
		public PropertyMdResultImpl(EntityType<M> metaDataType, PropertyMdAggregator mdAggregator, ExtendedSelectorContext selectorContext) {
			super(metaDataType, mdAggregator, selectorContext);
		}
	}

	// Enum
	public static class EnumMdResultImpl<M extends MetaData> extends MdResultImpl<M, EnumMdDescriptor> implements EnumMdResult<M> {
		public EnumMdResultImpl(EntityType<M> metaDataType, EnumMdAggregator mdAggregator, ExtendedSelectorContext selectorContext) {
			super(metaDataType, mdAggregator, selectorContext);
		}
	}

	// Constant
	public static class ConstantMdResultImpl<M extends MetaData> extends MdResultImpl<M, ConstantMdDescriptor> implements ConstantMdResult<M> {
		public ConstantMdResultImpl(EntityType<M> metaDataType, ConstantMdAggregator mdAggregator, ExtendedSelectorContext selectorContext) {
			super(metaDataType, mdAggregator, selectorContext);
		}
	}

}
