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
package com.braintribe.gwt.gme.constellation.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Messages;

public interface LocalizedText extends Messages {
	public static final LocalizedText INSTANCE = GWT.create(LocalizedText.class);
	
	String about();
	String accessDenied();
	String add();
	String addDescription();
	String addFinish();
	String addFinishDescription();
	String advanced();
	String advancedCommit();
	String apply();
	String applyAll();
	String applyAllDescription();
	String applyDescription();
	String assign();
	String back();
	String backTypeDescription();
	String backValidationDescription();
	String cancel();
	String cancelAll();
	String cancelAllGimaDescription();
	String cancelDescription();
	String cancelGimaDescription();
	String changes();
	String checkingAssets();
	String choose();
	String chooseAccess();
	String chooseAction();
	String chooseType(String type);
	String clearClipboard();
	String clearNotifications();
	String clipboard();
	String close();
	String comment();
	String commit();
	String commitAndRun();
	String condensation();
	String condensationLocal();
	String condenseBy(String property);
	String create();
	String createAndAdd();
	String createAndAssign();
	String createNew();
	String creatingHeaderBar();
	String data();
	String deploy();
	String detailed();
	String details();
	String dynamicLinkSlot();
	String entityProperty(String entity, String property);
	String entryCount(int count);
	String errorApplyingManipulations();
	String errorDuringAutoCommit();
	String errorEnsuringModelTypes();
	String errorFetchingAccesses();
	String errorGettingAccessIds();
	String errorGettingModelEnvironment();
	String errorInstantiatingEntity();
	String errorOpeningExternalLink();
	String errorPerformingEntityQuery();
	String errorPerformingPropertyQuery();
	String errorPerformingSelectQuery();
	String errorQueryingHomeConstellation();
	String errorRedoing();
	String errorRollingEditionBack();
	String errorUndoing();
	String errorUndoingInstantiation();
	String execute();
	String executedUiCommand(String name);
	String filterType();
	String filterTypeDescription();
	String flat();
	String finish();
	String finishDescription();
	String globalActionsSlot();
	String globalSlot();
	String groupId();
	String headerBarCreated();	
	String hidePropertyPanel();
	String home();
	String information();
	String install();
	String invalidQueryMessage();
	String loadingActions();
	String loadingHomeElements();
	String loadingMetaData();
	String loadingPredecessorInfo();
	String loadingPreview();
	String localization();
	String management();
	String merge();
	String modelViewer();
	String moveEntityDown();
	String moveEntityUp();
	String myAsset();
	String name();
	String newEntry();
	String newType(String type);
	String newWindow();
	String noAccessesToDisplay();
	String noChanges();
	String noClipboardItems();
	String none();
	String noNewActionToBeAdded();
	String notAllowedAccess();
	String nothingToCloseOrMerge();
	String notificationSlot();
	String ok();
	String openSubFolders();
	String operation();
	String options();
	String packaging();
	String packagingInfo(String version);
	String packagingInfoDescription();
	String packagingInfoFor(String artifact);
	String packagingInfoNotAvailable(String version);
	String performAll(String action);
	String performingEntityQuery();
	String performingPropertyQuery();
	String performingSelectQuery();
	String persistHeaderBar();
	String platformAssetManagement();
	String profile();
	String quickAccess();
	String redo();
	String redoDescription();
	String reload();
	String removeEntity();
	String removeFromChanges();
	String removeFromClipboard();
	String removeNotification();
	String run();
	String save();
	String saveSuccess();
	String saving();
	String select();
	String selectedValues();
	String selectTypeDescription();
	String servicesAvailable();
	String servicesNotAvailable();
	String sessionBeforeExpired();
	String sessionExpired();
	String settings();
	String showAssetManagementDialog();
	String showLog();
	String showPropertyPanel();
	String signOut();
	String simple();
	String switchTo();
	String tfLogo();
	String tfTitle();
	String thisWindow();
	String transfer();
	String tribefireRelease(String version);
	String typeForFilteringActions();
	String uiTheme();
	String uncondensationLocal();
	String uncondense();
	String undo();
	String undoDescription();
	String userProfile();
	String upload();
	String validating();
	String validationError();
	String validationLog();
	String version(String version);
	String versionLabel();
	String view();
	String viewAndEdit(String entityType);
	String welcome(String user);
	String whereTo();
	String workbench();

}
