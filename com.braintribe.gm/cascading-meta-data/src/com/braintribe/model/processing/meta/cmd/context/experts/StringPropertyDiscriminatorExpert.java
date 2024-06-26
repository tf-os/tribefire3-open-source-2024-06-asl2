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
package com.braintribe.model.processing.meta.cmd.context.experts;

import com.braintribe.model.meta.selector.StringPropertyDiscriminator;
import com.braintribe.model.processing.meta.cmd.context.SelectorContext;

public class StringPropertyDiscriminatorExpert extends SimplePropertyDiscriminatorExpert<StringPropertyDiscriminator, String> {

	public StringPropertyDiscriminatorExpert() {
		super(String.class);
	}

	@Override
	public boolean matches(StringPropertyDiscriminator selector, SelectorContext context) throws Exception {
		Object actualValue = getProperty(selector, context);

		/* null will not enter the if block since null instanceof X returns false */
		if (actualValue instanceof String || actualValue instanceof Enum<?>) {
			String discriminatorValue = selector.getDiscriminatorValue();
			return actualValue.toString().equals(discriminatorValue);
		}

		return false;
	}

}