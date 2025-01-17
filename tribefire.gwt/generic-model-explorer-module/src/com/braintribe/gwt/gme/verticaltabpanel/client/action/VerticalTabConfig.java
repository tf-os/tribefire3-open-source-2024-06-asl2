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
package com.braintribe.gwt.gme.verticaltabpanel.client.action;

//============================================================================
//Braintribe IT-Technologies GmbH - www.braintribe.com
//Copyright Braintribe IT-Technologies GmbH, Austria, 2002-2015 - All Rights Reserved
//It is strictly forbidden to copy, modify, distribute or use this code without written permission
//To this file the Braintribe License Agreement applies.
//============================================================================


import com.braintribe.gwt.gme.verticaltabpanel.client.LocalizedText;

public enum VerticalTabConfig {
	
			ACTION_FOLDER_EXPLORER("$explorer", "Explorer", "", "folder"),
			ACTION_HOME("$homeConstellation", LocalizedText.INSTANCE.home(), "$explorer", "action"),
			ACTION_CHANGES("$changesConstellation", LocalizedText.INSTANCE.changes(), "$explorer", "action"),
			ACTION_CLIPBOARD("$clipboardConstellation", LocalizedText.INSTANCE.clipboard(), "$explorer", "action"),
			ACTION_NOTIFICATIONS("$notificationsConstellation", LocalizedText.INSTANCE.notifications(), "$explorer", "action"),
			ACTION_VALIDATION("$validationConstellation", LocalizedText.INSTANCE.validation(), "$explorer", "action"),
			ACTION_FOLDER_SELECTION("$selection", "Selection Dialog", "", "folder"),
			ACTION_WORKBENCH_SELECTION("$workbenchConstellation", LocalizedText.INSTANCE.workbench(), "$selection", "action"),
			ACTION_CLIPBOARD_SELECTION("$clipboardConstellation", LocalizedText.INSTANCE.clipboard(), "$selection", "action"),
			ACTION_CHANGES_SELECTION("$changesConstellation", LocalizedText.INSTANCE.changes(), "$selection", "action"),
			ACTION_QUICK_ACCESS_SELECTION("$quickAccessConstellation", LocalizedText.INSTANCE.quickAccess(), "$selection", "action"),
			ACTION_HOME_SELECTION("$homeConstellation", LocalizedText.INSTANCE.home(), "$selection", "action"),
			ACTION_EXPERT_UI_SELECTION("$expertUI", LocalizedText.INSTANCE.expertUI(), "$selection", "action"),
			;
	
	VerticalTabConfig(String name, String displayName, String parentFolder, String kind) {
		this.name = name;
		this.displayName = displayName;
		this.parentFolder = parentFolder;
		this.kind = kind;
	}
	
	private String name;
	private String displayName;
	private String parentFolder;
	private String kind;
	
	public String getName() {
		return name;
	}
	
	public String getDisplayName() {
		return displayName;
	}
	
	public String getParentFolder() {
		return parentFolder;
	}

	public String getKind() {
		return kind;
	}
}


