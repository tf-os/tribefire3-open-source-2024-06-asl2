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
package com.braintribe.model.generic.value;


import com.braintribe.gm.model.reason.Reason;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

public interface UnsatisfiedValue extends ValueDescriptor {
	
	EntityType<UnsatisfiedValue> T = EntityTypes.T(UnsatisfiedValue.class);
	
	String why = "why";
	String value = "value";
	String hasValue = "hasValue";
	
	Reason getWhy();
	void setWhy(Reason whyUnsatisfied);
	
	Object getValue();
	void setValue(Object value);
	
	boolean getHasValue();
	void setHasValue(boolean hasValue);

	
	static UnsatisfiedValue create(Reason why) {
		UnsatisfiedValue unsatisfiedVd = UnsatisfiedValue.T.create();
		unsatisfiedVd.setWhy(why);
		return unsatisfiedVd;
	}
	
	static UnsatisfiedValue create(Reason why, Object value) {
		UnsatisfiedValue unsatisfiedVd = UnsatisfiedValue.T.create();
		unsatisfiedVd.setWhy(why);
		unsatisfiedVd.setValue(value);
		unsatisfiedVd.setHasValue(true);
		return unsatisfiedVd;
	}
}