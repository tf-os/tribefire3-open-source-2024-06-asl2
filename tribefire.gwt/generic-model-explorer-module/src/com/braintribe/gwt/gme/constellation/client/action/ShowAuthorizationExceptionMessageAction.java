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
package com.braintribe.gwt.gme.constellation.client.action;

import com.braintribe.gwt.action.client.Action;
import com.braintribe.gwt.action.client.TriggerInfo;
import com.braintribe.gwt.gme.constellation.client.LocalizedText;
import com.braintribe.gwt.gme.notification.client.ConfirmationDialog;
import com.braintribe.gwt.gme.notification.client.NotificationView;
import com.braintribe.model.notification.Level;

/**
 * Action which shows an dialog with a nice message for the AuthorizationException.
 * @author michel.docouto
 *
 */
public class ShowAuthorizationExceptionMessageAction extends Action {

	@Override
	public void perform(TriggerInfo triggerInfo) {
		ConfirmationDialog dialog = NotificationView.showConfirmationDialog(Level.ERROR, LocalizedText.INSTANCE.accessDenied());
		dialog.setCancelButtonVisible(false);
		dialog.setOkButtonVisible(false);
		dialog.setClosable(false);
		dialog.setOnEsc(false);
		dialog.show();
	}

}
