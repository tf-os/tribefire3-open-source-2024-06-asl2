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
package com.braintribe.gwt.action.adapter.gxt.client;

import com.braintribe.gwt.action.client.Action;
import com.braintribe.gwt.action.client.ActionGroup;
import com.braintribe.gwt.action.client.ActionOrGroup;
import com.braintribe.gwt.action.client.TriggerInfo;
import com.braintribe.gwt.ioc.client.DisposableBean;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ImageResource;
import com.sencha.gxt.widget.core.client.button.SplitButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

public class SplitButtonActionGroupAdapter implements DisposableBean {
	
	private ActionGroup action;
	private SplitButton button;
	private ImageResource lastIcon;
	private boolean noText = false;
	private boolean ignoreIcon = false;
	private PropertyListenerImpl actionListener = new PropertyListenerImpl();
	private SelectHandler selectHandler;
	private HandlerRegistration handlerRegistration;
	
	private class PropertyListenerImpl implements Action.PropertyListener {
		@Override
		public void propertyChanged(ActionOrGroup source, String property) {
			updateButton();
		}
	}
	
	private class SelectHandlerImpl implements SelectHandler {
		@Override
		public void onSelect(SelectEvent event) {
			TriggerInfo triggerInfo = new TriggerInfo();
			triggerInfo.put(TriggerInfo.PROPERTY_COMPONENTEVENT, event);
			triggerInfo.setWidget(button);
			if (action.getGroupAction() != null)
				action.getGroupAction().perform(triggerInfo);
			else
				button.showMenu();
		}
	}
	
	/**
	 * Use {@link SplitButtonActionGroupAdapter#linkActionToButton(boolean, ActionGroup, SplitButton)} instead.
	 */
	private SplitButtonActionGroupAdapter(boolean ignoreIcon, ActionGroup action, SplitButton button) {
		this.button = button;
		this.action = action;
		this.ignoreIcon = ignoreIcon;

		action.addPropertyListener(actionListener);
		handlerRegistration = button.addSelectHandler(getSelectionHandler());
		updateButton();
	}
	
	/**
	 * Use {@link SplitButtonActionGroupAdapter#linkActionToButton(ActionGroup, SplitButton, boolean)} instead.
	 */
	private SplitButtonActionGroupAdapter(ActionGroup action, SplitButton button, boolean noText) {
		this.button = button;
		this.action = action;
		this.noText = noText;

		action.addPropertyListener(actionListener);
		handlerRegistration = button.addSelectHandler(getSelectionHandler());
		updateButton();
	}
	
	/**
	 * Use {@link SplitButtonActionGroupAdapter#linkActionToButton(ActionGroup, SplitButton)} instead.
	 */
	private SplitButtonActionGroupAdapter(ActionGroup action, SplitButton button) {
		this(action, button, false);
	}
	
	protected SelectHandler getSelectionHandler() {
		if (selectHandler == null)
			selectHandler = new SelectHandlerImpl();
		return selectHandler;
	}
	
	public SplitButton getButton() {
		return button;
	}

	public void updateButton() {
		String id = action.getId();
		
		if (id != null)
			button.setId(id);
		
		String name = action.getName();
		if (name == null) name = "";
		
		if (!noText && !name.equals(button.getText())) {
			button.setText(name);
		}
		
		button.setEnabled(action.getEnabled());
		
		if (!ignoreIcon) {
			ImageResource icon = action.getIcon();
			if (icon != null) {
				if (icon != lastIcon) {
					button.setIcon(icon);
					lastIcon = icon;
				}
			} 
		}
		
		if (action.getTooltip() != null) {
			button.setToolTip(action.getTooltip());
		} else if (noText) {
			button.setToolTip(action.getName());
		}
		
		button.setVisible(!action.getHidden());
	}
	
	@Override
	public void disposeBean() throws Exception {
		action.removePropertyListener(actionListener);
		handlerRegistration.removeHandler();
	}
	
	/**
	 * Links an {@link Action} to a {@link SplitButton}.
	 * @param noText - If true, use no text in the button (only use toolTip). Defaults to false.
	 */
	public static SplitButtonActionGroupAdapter linkActionToButton(ActionGroup action, SplitButton button, boolean noText) {
		return new SplitButtonActionGroupAdapter(action, button, noText);
	}
	
	/**
	 * Links an {@link Action} to a {@link SplitButton}.
	 */
	public static SplitButtonActionGroupAdapter linkActionToButton(ActionGroup action, SplitButton button) {
		return new SplitButtonActionGroupAdapter(action, button);
	}
	
	/**
	 * Links an {@link Action} to a {@link SplitButton}.
	 * @param ignoreIcon - If true, this adapter won't handle icon changes. Defaults to false.
	 */
	public static SplitButtonActionGroupAdapter linkActionToButton(boolean ignoreIcon, ActionGroup action, SplitButton button) {
		return new SplitButtonActionGroupAdapter(ignoreIcon, action, button);
	}

}
