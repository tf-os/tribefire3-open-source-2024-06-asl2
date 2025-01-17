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
package com.braintribe.model.modellerfilter.view;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.annotation.SelectiveInformation;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.meta.GmMetaModel;
import com.braintribe.model.meta.GmType;

 @SelectiveInformation("${name} - ${user}")
public interface ModellerView extends GenericEntity {

	EntityType<ModellerView> T = EntityTypes.T(ModellerView.class);
	
	public void setIncludesFilterContext(IncludesFilterContext includesFilterContext);
	public IncludesFilterContext getIncludesFilterContext();
	
	public void setExcludesFilterContext(ExcludesFilterContext excludesFilterContext);
	public ExcludesFilterContext getExcludesFilterContext();
	
	public void setRelationshipKindFilterContext(RelationshipKindFilterContext relationshipKindFilterContext);
	public RelationshipKindFilterContext getRelationshipKindFilterContext();
	
	public void setMetaModel(GmMetaModel metaModel);
	public GmMetaModel getMetaModel();
	
	public void setFocusedType(GmType gmType);
	public GmType getFocusedType();
	
	public void setName(String name);
	public String getName();
	
	public void setUser(String user);
	public String getUser();
	
	public void setSettings(ModellerSettings settings);
	public ModellerSettings getSettings();
	
}
