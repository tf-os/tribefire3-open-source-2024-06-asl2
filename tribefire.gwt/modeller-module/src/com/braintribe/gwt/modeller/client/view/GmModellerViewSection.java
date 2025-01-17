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
package com.braintribe.gwt.modeller.client.view;

import com.braintribe.model.generic.manipulation.Manipulation;
import com.braintribe.model.generic.tracking.ManipulationListener;
import com.braintribe.model.modellerfilter.view.ModellerView;
import com.braintribe.model.processing.session.api.persistence.PersistenceGmSession;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextBox;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

public class GmModellerViewSection extends FlowPanel implements ManipulationListener{
	
	private GmModellerViewPanel parentPanel;
	private ModellerView view;
	
	private RadioButton isDefaultCheckBox;
	private TextBox viewNameTextBox;
	
	private TextButton showViewButton;
	private TextButton deleteViewButton;
	
	private boolean readOnly = false;
		
	public GmModellerViewSection(GmModellerViewPanel parentPanel, PersistenceGmSession session, ModellerView view, boolean readOnly) {
		this.parentPanel = parentPanel;
		this.view = view;
		this.readOnly = readOnly;
		
		addStyleName("gmModellerViewSection");
		
		add(getIsDefaultCheckBox());
		add(getViewNameTextBox());
		
		add(getShowViewButton());
		
		add(getDeleteViewButton());
			
		session.listeners().entity(view).add(this);
	}
	
	public CheckBox getIsDefaultCheckBox() {
		if(isDefaultCheckBox == null){
			isDefaultCheckBox = new RadioButton("isDefault");
			isDefaultCheckBox.setEnabled(!readOnly);
			isDefaultCheckBox.setStyleName("gmModellerRadioButton");
			isDefaultCheckBox.setValue(parentPanel.isDefaultView(view));
			
			isDefaultCheckBox.addValueChangeHandler(new ValueChangeHandler<Boolean>() {				
				@Override
				public void onValueChange(ValueChangeEvent<Boolean> event) {
					if(event.getValue())
						parentPanel.setDefaultView(view);
					parentPanel.adaptSections();
				}
			});
		}
		return isDefaultCheckBox;
	}
	
	public TextBox getViewNameTextBox() {
		if(viewNameTextBox == null){
			viewNameTextBox = new TextBox();
			viewNameTextBox.setEnabled(!readOnly);
			viewNameTextBox.addStyleName("gmModellerViewName");
			
			viewNameTextBox.setText(view.getName());	
			if(parentPanel.isCurrentView(view))
				viewNameTextBox.getElement().getStyle().setProperty("fontWeight", "bold");
			else
				viewNameTextBox.getElement().getStyle().setProperty("fontWeight", "normal");
			
			viewNameTextBox.addKeyUpHandler(new KeyUpHandler() {
				
				@Override
				public void onKeyUp(KeyUpEvent event) {
					if(event.getNativeKeyCode() == KeyCodes.KEY_ENTER)
						viewNameTextBox.setFocus(false);
				}
			});
			
			viewNameTextBox.addChangeHandler(new ChangeHandler() {
				
				@Override
				public void onChange(ChangeEvent event) {
					String value = viewNameTextBox.getValue();
					if(value != null && !value.equals(""))
						view.setName(value);
					else
						viewNameTextBox.setValue(view.getName(), false);
				}
			});
		}
		return viewNameTextBox;
	}
	
	public TextButton getShowViewButton() {
		if(showViewButton == null) {
			showViewButton = new TextButton();
			showViewButton.setText("Show");
			showViewButton.addSelectHandler(new SelectHandler() {
				
				@Override
				public void onSelect(SelectEvent event) {
					parentPanel.setCurrentView(view, true);
					parentPanel.showFilterPanel();	
				}
			});
		}
		return showViewButton;
	}
	
	public TextButton getDeleteViewButton() {
		if(deleteViewButton == null){
			deleteViewButton = new TextButton("x");
			deleteViewButton.setVisible(!parentPanel.isDefaultView(view) && !readOnly);
			deleteViewButton.addSelectHandler(new SelectHandler() {
				
				@Override
				public void onSelect(SelectEvent event) {
					parentPanel.removeView(view);
					parentPanel.refresh();
				}
			});
		}
		return deleteViewButton;
	}
	
	@Override
	public void noticeManipulation(Manipulation manipulation) {
		adapt();	
	}
	
	public void adapt(){
		getDeleteViewButton().setVisible(!parentPanel.isDefaultView(view));
		getViewNameTextBox().setValue(view.getName(), false);
		
		if(parentPanel.isCurrentView(view))
			getViewNameTextBox().getElement().getStyle().setProperty("fontWeight", "bold");
		else
			getViewNameTextBox().getElement().getStyle().setProperty("fontWeight", "normal");
		
		getIsDefaultCheckBox().setValue(parentPanel.isDefaultView(view), false);
	}

}
