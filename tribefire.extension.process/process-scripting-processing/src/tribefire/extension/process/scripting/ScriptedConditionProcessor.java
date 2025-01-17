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
package tribefire.extension.process.scripting;

import java.util.HashMap;
import java.util.Map;

import tribefire.extension.process.api.ConditionProcessor;
import tribefire.extension.process.api.ConditionProcessorContext;
import tribefire.extension.process.model.data.Process;
import tribefire.extension.scripting.common.CommonScriptedProcessor;

public class ScriptedConditionProcessor extends CommonScriptedProcessor implements ConditionProcessor<Process> {

	@Override
	public boolean matches(ConditionProcessorContext<Process> context) {
		Map<String, Object> bindings = new HashMap<>();
		bindings.put("$context", context);

		Object result = processReasonedScripted(bindings).get();
		
		if (result instanceof Boolean)
			return (boolean) result;
		
		return result != null; 
	}

}
