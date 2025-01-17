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
package com.braintribe.gwt.gme.constellation.client.gima.field;

import com.braintribe.gwt.genericmodelgxtsupport.client.field.LocalizedValuesDialog;
import com.braintribe.gwt.gme.constellation.client.GIMADialog;
import com.braintribe.gwt.gme.constellation.client.LocalizedText;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.grid.editing.AbstractGridEditing;

/**
 * Extension of the {@link LocalizedValuesDialog} prepared for being used within a {@link GIMADialog}.
 * @author michel.docouto
 *
 */
public class GIMALocalizedValuesDialog extends LocalizedValuesDialog {
	
	private GIMALocalizedValuesView gimaView;
	private GIMADialog gimaDialog;
	
	@Override
	public void show() {
		AbstractGridEditing<?> parentGridEditing = getParentGridEditing();
		gimaDialog = parentGridEditing == null ? null : getParentGimaDialog(parentGridEditing.getEditableGrid());
		
		if (gimaDialog == null || !gimaDialog.isVisible()) {
			addToolBar();
			super.show();
			return;
		}
		
		removeToolBar();
		
		gimaDialog.addEntityFieldView(LocalizedText.INSTANCE.localization(), prepareGIMAView(gimaDialog));
		gridView.refresh(true);
	}
	
	@Override
	public void applyChanges() {
		if (gimaDialog != null)
			fireEvent(new HideEvent());
		else
			GIMALocalizedValuesDialog.super.applyChanges();
	}
	
	@Override
	public void cancelChanges() {
		if (gimaDialog != null)
			cancelChanges = true;
		else
			GIMALocalizedValuesDialog.super.cancelChanges();
	}

	private GIMALocalizedValuesView prepareGIMAView(GIMADialog gimaDialog) {
		if (gimaView != null) {
			Widget widget = getView();
			if (widget.getParent() != gimaView)
				gimaView.add(getView());
			gimaView.configureGimaDialog(gimaDialog);
			return gimaView;
		}
		
		gimaView = new GIMALocalizedValuesView(this, gimaDialog);
		return gimaView;
	}

	private GIMADialog getParentGimaDialog(Widget widget) {
		if (widget == null)
			return null;
		
		if (widget instanceof GIMADialog)
			return (GIMADialog) widget;
		
		return getParentGimaDialog(widget.getParent());
	}

}
