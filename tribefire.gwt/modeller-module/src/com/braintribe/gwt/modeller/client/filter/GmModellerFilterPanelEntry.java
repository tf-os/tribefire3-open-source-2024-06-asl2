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
package com.braintribe.gwt.modeller.client.filter;

import java.util.List;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.manipulation.Manipulation;
import com.braintribe.model.generic.tracking.ManipulationListener;
import com.braintribe.model.modellerfilter.RelationshipFilter;
import com.braintribe.model.processing.session.api.persistence.PersistenceGmSession;
import com.braintribe.model.processing.session.api.transaction.NestedTransaction;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IntegerBox;
import com.google.gwt.user.client.ui.Label;

public class GmModellerFilterPanelEntry extends FlowPanel implements ManipulationListener{
	
	private PropertyContext context;
	
	private CheckBox selectionCheckbox;
	private IntegerBox valueTextbox;
	private Label nameLabel;
	private Button deleteButton;
	
	private PersistenceGmSession session;
	private GenericEntity parentEntity;
	private String propertyName;
	
	public GmModellerFilterPanelEntry(PersistenceGmSession session, GenericEntity parentEntity, PropertyContext context) {
		this.session = session;
		this.context = context;	
		this.propertyName = context.propertyName;
		this.parentEntity = parentEntity;
		
		if(propertyName != null)
			this.session.listeners().entity(parentEntity).property(propertyName).add(this);
		
		if(context.isBoolean) {
			add(getSelectionCheckbox());
			add(getNameLabel());			
		}else{
			add(getNameLabel());
			add(getValueTextbox());			
		}
		
		if(context.deletable)
			add(getDeleteButton());
		
		getNameLabel().setTitle(context.desc);
		getNameLabel().setText(context.desc);
		
		addStyleName("gmModelerFilterPanelEntry");
		adapt();
	}
	
	public void dispose() {
		this.session.listeners().entity(parentEntity).property(propertyName).remove(this);
	}
	
	private void adapt() {
		
		Object value = parentEntity.entityType().getProperty(propertyName).get(parentEntity);
		
		if(context.isOperand) {
			value = parentEntity.entityType().getProperty(propertyName).get(parentEntity);
			List<RelationshipFilter> operands = (List<RelationshipFilter>) value;
			getSelectionCheckbox().setValue(operands.contains(context.filter), false);
		}else {
			if(context.isBoolean) {
				getSelectionCheckbox().setValue((Boolean) value, false); 
			}else {
				getValueTextbox().setValue((Integer) value, false); 
			}
		}
		
	}
	
	private void changeValue() {
		Object value = null;
		if(context.isOperand) {
			NestedTransaction nt = this.session.getTransaction().beginNestedTransaction();
			boolean add = getSelectionCheckbox().getValue();
			List<RelationshipFilter> operands = (List<RelationshipFilter>) parentEntity.entityType().getProperty("operands").get(parentEntity);
			List<RelationshipFilter> inactive = (List<RelationshipFilter>) parentEntity.entityType().getProperty("inactiveOperands").get(parentEntity);
			if(add) {
				inactive.remove(context.filter);
				operands.add(context.filter);
			}
			else {
				inactive.add(context.filter);
				operands.remove(context.filter);
			}
			nt.commit();
		}else {
			
			if(context.isBoolean) {
				value = getSelectionCheckbox().getValue();
			}else {
				value = getValueTextbox().getValue();
			}
			
			if(value != null) {
				parentEntity.entityType().getProperty(propertyName).set(parentEntity, value);
			}else
				adapt();
		}
	}
	
	@Override
	public void noticeManipulation(Manipulation manipulation) {
		adapt();
	}	
	
	public CheckBox getSelectionCheckbox() {
		if(selectionCheckbox == null) {
			selectionCheckbox = new CheckBox();
			selectionCheckbox.addStyleName("gmModellerDefaultCheckBox");
			selectionCheckbox.addValueChangeHandler(event -> changeValue());
		}
		return selectionCheckbox;
	}
	
	public IntegerBox getValueTextbox() {
		if(valueTextbox == null) {
			valueTextbox = new IntegerBox();
			valueTextbox.addChangeHandler(event -> changeValue());
		}
		return valueTextbox;
	}
	
	public Label getNameLabel() {
		if(nameLabel == null) {
			nameLabel = new Label();
		}
		return nameLabel;
	}
	
	public Button getDeleteButton() {
		if(deleteButton == null) {
			deleteButton = new Button("x");
			deleteButton.addStyleName("gmModelerFilterPanelButton");
			deleteButton.addClickHandler(event -> {
				if(context.isOperand) {
					NestedTransaction nt = session.getTransaction().beginNestedTransaction();
					//boolean add = getSelectionCheckbox().getValue();
					List<RelationshipFilter> operands = (List<RelationshipFilter>) parentEntity.entityType().getProperty("operands").get(parentEntity);
					List<RelationshipFilter> inactive = (List<RelationshipFilter>) parentEntity.entityType().getProperty("inactiveOperands").get(parentEntity);
					operands.remove(context.filter);
					inactive.remove(context.filter);
					//session.delete(context.filter);
					nt.commit();
				}
			});
		}
		return deleteButton;
	}

}
