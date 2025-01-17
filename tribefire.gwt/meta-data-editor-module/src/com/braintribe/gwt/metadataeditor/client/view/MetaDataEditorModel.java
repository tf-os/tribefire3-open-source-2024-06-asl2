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
package com.braintribe.gwt.metadataeditor.client.view;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.meta.data.MetaData;
import com.braintribe.model.meta.data.Predicate;
import com.braintribe.model.meta.selector.MetaDataSelector;

public class MetaDataEditorModel {

	private final String name;
	private final MetaData metadata;
	private final EntityType<?> entityType;
	private final Property property;
	private int childLevel = 0;
	//private Boolean isVisible = true;
	private Object propertyValue;
//	private MetaDataSelector selector;
	private Boolean isVisible = true;
	private GenericEntity owner;

	public MetaDataEditorModel(int id, MetaData metadata, EntityType<?> entityType, Property property, GenericEntity owner) {
		this.name = (metadata != null) ? metadata.getClass().getName() + "#" + id : (entityType != null) ? entityType.getClass().getName() + "#" + id : "" + id;
		this.metadata = metadata;
		this.entityType = entityType;
		this.property = property;
		this.propertyValue = property != null ? property.get(metadata)  : null;
		this.owner = owner;
	}

	/*
	public MetaDataEditorModel(int id, EntityType<?> entityType, Property property) {
		this.name = (entityType != null) ? entityType.getClass().getName() + "#" + id : "" + id;
		this.metadata = null;
		this.entityType = entityType;
		this.property = property;
		this.propertyValue =  null;
	}
	*/

	public MetaDataEditorModel getModel() {
		return this;
	}

	public String getName() {
		return this.name;
	}

	public Object getValue() {
		    if (this.entityType != null) {		    	
		    	return this.entityType;
		    } else {
		    	return this.metadata;
		    }
	}

	public MetaData getMetaData() {
		return this.metadata;
	}
	
	public EntityType<?> getEntityTypeValue() {
		return this.entityType;
	}
	
	public Property getProperty() {
		return this.property;
	}

	public GenericModelType getType() {
		if (this.entityType != null) {
			return this.entityType;			
		} else {
			return this.metadata.entityType();
		}
	}

	public void setChildLevel(int childLevel) {
		this.childLevel = childLevel;
	}
	
	public int getChildLevel() {
		return this.childLevel;
	}
	
	public MetaDataSelector getSelector() {
//		if (selector == null) {
//			ViewSituationSelector temp = ViewSituationSelector.T.create();
//			temp.setId(System.currentTimeMillis());
//			temp.setConflictPriority(1D);
//			selector = temp; 
//		}
//		return selector;
		if (this.metadata != null)
			return this.metadata.getSelector();
		
		return null;
	}

	public void setSelector(MetaDataSelector selector) {
		if (this.metadata != null)
			this.metadata.setSelector(selector);
	}

	public Double getConflictPriority() {
		if (this.metadata != null)
			return this.metadata.getConflictPriority();
		
		return null;
	}

	public void setConflictPriority(Double conflictPriority) {
		if (this.metadata != null)
			this.metadata.setConflictPriority(conflictPriority);
	}

	/*
	public void setIsVisible(Boolean isVisible) {
		this.isVisible = isVisible;
	}
	
	public Boolean getIsVisible() {
		return this.isVisible;
	}
	*/
	
	/*
	public <X> void setPropertyValue(X value) {
		this.propertyValue = value;
	}
	*/
	
	public <X> X getPropertyValue() {
		if (this.metadata != null) {
			this.propertyValue = this.property != null ? this.property.get(this.metadata)  : null;
			return (X) this.propertyValue;
		} else {
			return null;
		}
	}

	public boolean refersTo(Object object) {
		if (this.metadata == object || this.entityType == object) {
			return true;
		} 
		
		return false;
	}
	
	public void setIsVisible(Boolean isVisible) {
		this.isVisible = isVisible;
	}
	
	public Boolean getIsVisible() {
		return this.isVisible;
	}	
	
	public Boolean isPredicateMetaData() {
		if (getEntityTypeValue() != null)
			if (Predicate.T.isAssignableFrom(getEntityTypeValue()))
				return true;
		
		return false;
	}
	
	public Boolean isAssignablePropertyType() {
		if (property != null)
			return property.getType().isEntity();
		
		return false;
	}

	public GenericEntity getOwner() {
		return owner;
	}

	public void setOwner(GenericEntity owner) {
		this.owner = owner;
	}
	
	public MetaDataEditorModel getDeclaredModel() {
		return this;
	}
	public MetaDataEditorModel getDeclaredOwner() {
		return this;
	}
	
	// @Override
	// public int compareTo(MetaDataEditorModel model) {
	// return model == null ? 0 : Integer.compare(id, model.id);
	// }

}
