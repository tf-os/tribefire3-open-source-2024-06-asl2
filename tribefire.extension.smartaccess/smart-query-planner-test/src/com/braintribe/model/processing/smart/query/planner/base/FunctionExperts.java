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
package com.braintribe.model.processing.smart.query.planner.base;

import static com.braintribe.utils.lcd.CollectionTools2.newMap;

import java.util.Map;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.processing.query.eval.api.function.QueryFunctionExpert;
import com.braintribe.model.processing.query.eval.context.function.AsStringExpert;
import com.braintribe.model.processing.query.eval.context.function.LocalizeExpert;
import com.braintribe.model.query.functions.EntitySignature;
import com.braintribe.model.query.functions.Localize;
import com.braintribe.model.query.functions.QueryFunction;
import com.braintribe.model.query.functions.value.AsString;
import com.braintribe.model.query.smart.processing.eval.context.function.AssembleEntityExpert;
import com.braintribe.model.query.smart.processing.eval.context.function.ResolveDelegatePropertyExpert;
import com.braintribe.model.query.smart.processing.eval.context.function.ResolveIdExpert;
import com.braintribe.model.query.smart.processing.eval.context.function.SmartEntitySignatureExpert;
import com.braintribe.model.smartqueryplan.functions.AssembleEntity;
import com.braintribe.model.smartqueryplan.queryfunctions.ResolveDelegateProperty;
import com.braintribe.model.smartqueryplan.queryfunctions.ResolveId;

/**
 * 
 */
public class FunctionExperts {

	public static final Map<EntityType<? extends QueryFunction>, QueryFunctionExpert<?>> DEFAULT_FUNCTION_EXPERTS = newMap();

	static {
		DEFAULT_FUNCTION_EXPERTS.put(Localize.T, LocalizeExpert.INSTANCE);
		DEFAULT_FUNCTION_EXPERTS.put(AssembleEntity.T, AssembleEntityExpert.INSTANCE);
		DEFAULT_FUNCTION_EXPERTS.put(AsString.T, AsStringExpert.INSTANCE);
		DEFAULT_FUNCTION_EXPERTS.put(ResolveId.T, ResolveIdExpert.INSTANCE);
		DEFAULT_FUNCTION_EXPERTS.put(ResolveDelegateProperty.T, ResolveDelegatePropertyExpert.INSTANCE);
		DEFAULT_FUNCTION_EXPERTS.put(EntitySignature.T, SmartEntitySignatureExpert.INSTANCE);
	}

}
