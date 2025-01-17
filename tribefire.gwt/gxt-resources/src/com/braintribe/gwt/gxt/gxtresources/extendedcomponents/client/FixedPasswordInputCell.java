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
package com.braintribe.gwt.gxt.gxtresources.extendedcomponents.client;

import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.sencha.gxt.cell.core.client.form.PasswordInputCell;
import com.sencha.gxt.cell.core.client.form.ViewData;

/**
 * Added attributes for not showing auto completion suggestions for password fields.
 * @author michel.docouto
 *
 */
public class FixedPasswordInputCell extends PasswordInputCell {
	
	@Override
	public void render(Context context, String value, SafeHtmlBuilder sb) {
		ViewData viewData = checkViewData(context, value);
	    String s = (viewData != null) ? viewData.getCurrentValue() : value;

	    FieldAppearanceOptions options = new FieldAppearanceOptions(getWidth(), getHeight(), isReadOnly(), getEmptyText());
	    options.setName("password'  autocomplete='new-password"); //hack to add the parameter
	    options.setEmptyText(getEmptyText());
	    options.setDisabled(isDisabled());
	    getAppearance().render(sb, "password", s == null ? "" : s, options);
	}

}
