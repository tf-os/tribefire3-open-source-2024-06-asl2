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
package com.braintribe.gwt.gme.headerbar.client.action;

import com.braintribe.gwt.gme.constellation.client.LocalizedText;

public enum HeaderBarConfig {
	
	        TF_LOGO("tb_Logo", LocalizedText.INSTANCE.tfLogo(), "", "icon", "$logo.png"),
	        TF_TITLE("$title", LocalizedText.INSTANCE.tfTitle(), "", "text", ""),
	        QUICK_ACCESS("$quickAccess-slot", LocalizedText.INSTANCE.quickAccess(), "", "widget", ""),
			GLOBAL_SLOT("$globalState-slot", LocalizedText.INSTANCE.globalSlot(), "", "widget", ""),
			//LINK_EXPLORER("linkExplorer", LocalizedText.INSTANCE.explorer(), "", "link", "../tribefire-explorer"),
			//LINK_SERVICES("linkServices", LocalizedText.INSTANCE.services(), "", "link", "../tribefire-services"),
			LINK_DYNAMIC_SLOT("$dynamicLink-slot", LocalizedText.INSTANCE.dynamicLinkSlot(), "", "widget", ""),
			//LINK_REPOSITORY("linkRepository", LocalizedText.INSTANCE.repository(), "", "link", "../tribefire-repository"),
			//LINK_DOCUMENTATION("linkDocumentation", LocalizedText.INSTANCE.documentation(), "$dynamicLink-slot", "link", "https://documentation.tribefire.com"),
			//LINK_LABS("linkLabs", LocalizedText.INSTANCE.labs(), "", "link", "http://codelabs.tribefire.com"),
			NOTIFICATION_SLOT("$notification-slot", LocalizedText.INSTANCE.notificationSlot(), "", "widget", ""),
			GLOBAL_ACTIONS_SLOT("$globalActions-slot", LocalizedText.INSTANCE.globalActionsSlot(), "", "widget", ""),
			MENU_SETTINGS("$settingsMenu", LocalizedText.INSTANCE.settings(), "", "menu", "$menuSettings.png"),
			ACTION_SWITCH_TO("$switchTo", LocalizedText.INSTANCE.switchTo(), "$settingsMenu", "action", ""),
			ACTION_RELOAD("$reloadSession", LocalizedText.INSTANCE.reload(), "$settingsMenu", "action", ""),
			ACTION_SHOW_SETTINGS("$showSettings", LocalizedText.INSTANCE.settings(), "$settingsMenu", "action", ""),
			ACTION_SHOW_UI_THEME("$uiTheme", LocalizedText.INSTANCE.uiTheme(), "$settingsMenu", "action", ""),
			ACTION_SHOW_LOG("$showLog", LocalizedText.INSTANCE.showLog(), "$settingsMenu", "action", ""),
			//ACTION_PERSIST_ACTION("$persistActionGroup", LocalizedText.INSTANCE.persistActionGroup(), "$settingsMenu", "action", ""),
			//ACTION_PERSIST_HEADERBAR("$persistHeaderBar", LocalizedText.INSTANCE.persistHeaderBar(), "$settingsMenu", "action", ""),
			ACTION_SHOW_ASSET_MANAGEMENT_DIALOG("$showAssetManagementDialog", LocalizedText.INSTANCE.showAssetManagementDialog(), "$settingsMenu", "action", ""),
			ACTION_SHOW_ABOUT("$showAbout", LocalizedText.INSTANCE.about(), "$settingsMenu", "action", ""),
			MENU_USER_PROFILE("$userMenu", LocalizedText.INSTANCE.userProfile(), "", "menu", "$menuUser.png"),
			ACTION_PROFILE("$showUserProfile", LocalizedText.INSTANCE.profile(), "$userMenu", "action", ""),
			ACTION_SIGN_OUT("$showLogout", LocalizedText.INSTANCE.signOut(), "$userMenu", "action", ""),		
			;
	
	HeaderBarConfig(String name, String displayName, String parentFolder, String kind, String resourceLink) {
		this.name = name;
		this.displayName = displayName;
		this.parentFolder = parentFolder;
		this.kind = kind;
		this.resourceLink = resourceLink;
	}
	
	private String name;
	private String displayName;
	private String parentFolder;
	private String kind;
	private String resourceLink;
	
	public String getName() {
		return this.name;
	}
	
	public String getDisplayName() {
		return this.displayName;
	}
	
	public String getParentFolder() {
		return this.parentFolder;
	}

	public String getKind() {
		return this.kind;
	}

	public String getResourceLink() {
		return this.resourceLink;
	}

}
