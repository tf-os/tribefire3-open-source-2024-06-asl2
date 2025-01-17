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
package com.braintribe.gwt.gmview.codec.client;

import static com.braintribe.model.processing.session.api.common.GmSessions.getMetaData;

import java.util.Date;

import com.braintribe.codec.CodecException;
import com.braintribe.gwt.codec.date.client.ZonelessDateCodec;
import com.braintribe.gwt.codec.string.client.GwtDateCodec;
import com.braintribe.gwt.gmview.client.PropertyBean;
import com.braintribe.gwt.gmview.util.client.GMEMetadataUtil;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.meta.data.constraint.DateClipping;
import com.braintribe.model.meta.data.prompt.TimeZoneless;
import com.braintribe.model.processing.meta.cmd.builders.EntityMdResolver;
import com.braintribe.model.processing.meta.cmd.builders.ModelMdResolver;

/**
 * {@link GwtDateCodec} which checks the {@link DateClipping} metadata for date formats.
 * It must only be used as a renderer (no decode method available).
 * @author michel.docouto
 *
 */
public class MetaDataRelatedDateCodec extends GwtDateCodec implements PropertyRelatedCodec {
	
	private String propertyName;
	private GenericEntity parentEntity;
	private EntityType<?> parentEntityType;
	private ModelMdResolver modelMdResolver;
	private String useCase;
	private String defaultPattern;
	
	/**
	 * Configures the default pattern to be used, when there is no metadata.
	 */
	public void setDefaultPattern(String defaultPattern) {
		this.defaultPattern = defaultPattern;
	}

	@Override
	public void configurePropertyBean(PropertyBean propertyBean) {
		setFormatByString(defaultPattern);
		if (propertyBean != null) {
			this.propertyName = propertyBean.propertyName;
			this.parentEntity = propertyBean.parentEntity;
			this.parentEntityType = propertyBean.parentEntityType;
		} else {
			this.propertyName = null;
			this.parentEntity = null;
			this.parentEntityType = null;
		}
	}
	
	@Override
	public void configureModelMdResolver(ModelMdResolver modelMdResolver) {
		this.modelMdResolver = modelMdResolver;
	}
	
	@Override
	public void configureUseCase(String useCase) {
		this.useCase = useCase;
	}
	
	@Override
	public Date decode(String encodedValue) throws CodecException {
		throw new RuntimeException("The MetaDataRelatedDateCoded must only be used as a renderer.");
	}
	
	@Override
	public String encode(Date value) throws CodecException {
		if (propertyName != null)
			checkMetaData();
		
		Date theValue = value;
		if (isTimeZoneless())
			theValue = ZonelessDateCodec.INSTANCE.decode(value);
		
		String result = super.encode(theValue);
		configurePropertyBean(null);
		return result;
	}
	
	private boolean isTimeZoneless() {
		if (modelMdResolver == null || propertyName == null)
			return false;
		
		EntityMdResolver entityMdResolver;
		if (parentEntity != null)
			entityMdResolver = modelMdResolver.entity(parentEntity);
		else
			entityMdResolver = modelMdResolver.entityType(parentEntityType);
		
		return entityMdResolver.useCase(useCase).property(propertyName).is(TimeZoneless.T);
	}
	
	private void checkMetaData() {
		if (parentEntity != null)
			modelMdResolver = getMetaData(parentEntity);
			
		if (modelMdResolver == null) {
			setFormatByString(defaultPattern);
			return;
		}
		
		EntityMdResolver builder;
		if (parentEntity != null)
			builder = modelMdResolver.entity(parentEntity);
		else
			builder = modelMdResolver.entityType(parentEntityType);
		
		String pattern = GMEMetadataUtil.getDatePattern(builder.useCase(useCase).property(propertyName), defaultPattern);
		setFormatByString(pattern);
	}

}
