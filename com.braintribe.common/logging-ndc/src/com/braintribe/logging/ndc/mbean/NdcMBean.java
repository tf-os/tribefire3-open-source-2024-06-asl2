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
package com.braintribe.logging.ndc.mbean;

import java.util.Deque;
import java.util.Map;

import javax.management.MBeanRegistration;

public interface NdcMBean extends MBeanRegistration {

	void pushContext(String context);
	void popContext();
	void removeContext();
	
	Deque<String> getNdc();
	
	void clearMdc();
	Object get(String key);
	void put(String key, String value);
	void remove(String key);

	Map<String,String> getMdc();
}
