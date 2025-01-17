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
package com.braintribe.gwt.metadataeditor.client.experts;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.meta.GmEntityType;
import com.braintribe.model.meta.GmMetaModel;
import com.braintribe.model.meta.data.MetaData;
import com.braintribe.model.meta.info.GmEntityTypeInfo;
import com.braintribe.model.meta.override.GmCustomTypeOverride;
import com.braintribe.model.meta.override.GmEntityTypeOverride;

public class DeclaredPropertyOverviewExpert extends AbstractBaseEditorExpert {

	private List<GenericEntity> alreadyAddedList = new ArrayList<>();

	@Override
	public void provide(GenericEntity value, GmMetaModel editingModel, GenericEntity editingEntity, CallbackMetaData callback) {
		
		try {
			if (value == null) {
				callback.onSuccess(new ArrayList<MetaData>());
				return;
			}
			
			//RVE - get PropertyMetaData
			alreadyAddedList.clear();
			
			List<MetaDataEditorExpertResultType> list = getMetaDataResultTypeList(value, editingModel);
			List<MetaData> listMetaData = new ArrayList<MetaData>();
			for (MetaDataEditorExpertResultType resultType : list)
				listMetaData.add(resultType.getMetaData());
			callback.onSuccess(listMetaData);
		} catch (Exception e) {
			callback.onFailure(e);
		}				
	}
	
	private void addFromGmEntityTypeInfo(GenericEntity value, GmMetaModel editingModel, List<MetaDataEditorExpertResultType> listResultType) {
		if (alreadyAddedList.contains(value)) 
			return;		

		alreadyAddedList.add(value);
		
		addMetaDataResultType(value,((GmEntityTypeInfo) value).getPropertyMetaData(), listResultType);
		
		if (editingModel != null && !editingModel.getTypeOverrides().isEmpty())
			for (GmCustomTypeOverride typeOverride : editingModel.getTypeOverrides()) {						
				if (typeOverride instanceof GmEntityTypeOverride && ((GmEntityTypeOverride) typeOverride).getEntityType().equals(value))
					addMetaDataResultType(typeOverride, ((GmEntityTypeOverride) typeOverride).getPropertyMetaData(), listResultType);
					//listMetaData.addAll(typeOverride.getMetaData());	
			}
		
		for (GmEntityType superTypeEntity : ((GmEntityType) value).getSuperTypes()) {
			addFromGmEntityTypeInfo(superTypeEntity, editingModel, listResultType);
		}			
	}

	private void addMetaDataResultType(GenericEntity value, Set<MetaData> listMetaData, List<MetaDataEditorExpertResultType> listResultType) {
		for (MetaData metaData : listMetaData) {
			MetaDataEditorExpertResultType resultType = new MetaDataEditorExpertResultType();
			resultType.setMetaData(metaData);
			resultType.setOwner(value);
			listResultType.add(resultType);
		}
	}
	
	private List<MetaDataEditorExpertResultType> getMetaDataResultTypeList(GenericEntity value, GmMetaModel editingModel) {
		//get MetaData
		List<MetaDataEditorExpertResultType> listResultType = new ArrayList<>();

		if (value instanceof GmEntityTypeInfo) {
			//GmEntityType and GmEntityTypeOverride versions
			addFromGmEntityTypeInfo(value, editingModel, listResultType);
		} 		return listResultType;
	}
	
	@Override
	public void provide(GenericEntity value, CallbackMetaData callback) {
		provide(value, null, null, callback);
	}

	@Override
	public void provide(GenericEntity value, CallbackEntityType callback) {
		//not used in Overview mode
	}
	@Override
	public void provide(GenericEntity value, CallbackExpertResultType callback) {
		provide(value, null, null, callback);		
	}

	@Override
	public void provide(GenericEntity value, GmMetaModel editingModel, GenericEntity editingEntity, CallbackExpertResultType callback) {
		try {
			if (value == null) {
				callback.onSuccess(new ArrayList<MetaDataEditorExpertResultType>());
				return;
			}
			
			alreadyAddedList.clear();
			
			List<MetaDataEditorExpertResultType> list = getMetaDataResultTypeList(value, editingModel);
			callback.onSuccess(list);
		} catch (Exception e) {
			callback.onFailure(e);
		}
	}
}
