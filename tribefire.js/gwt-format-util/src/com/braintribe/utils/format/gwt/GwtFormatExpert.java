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
package com.braintribe.utils.format.gwt;

import com.braintribe.utils.format.api.CustomDateFormat;
import com.braintribe.utils.format.api.CustomNumberFormat;
import com.braintribe.utils.format.api.FormatExpert;

public class GwtFormatExpert implements FormatExpert {

	private final static CustomDateFormat dateFormat = new GwtDateFormat();
	private final static CustomNumberFormat numberFormat = new GwtNumberFormat();

	@Override
	public CustomDateFormat getDateFormat() {
		return dateFormat;
	}

	@Override
	public CustomNumberFormat getNumberFormat() {
		return numberFormat;
	}
}
