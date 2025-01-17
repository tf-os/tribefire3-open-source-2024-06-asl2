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
package com.braintribe.model.access.smart;


import static com.braintribe.utils.lcd.CollectionTools2.newMap;

import java.util.Map;

import com.braintribe.model.accessdeployment.smart.meta.conversion.BooleanToString;
import com.braintribe.model.accessdeployment.smart.meta.conversion.DateToString;
import com.braintribe.model.accessdeployment.smart.meta.conversion.DecimalToString;
import com.braintribe.model.accessdeployment.smart.meta.conversion.DoubleToString;
import com.braintribe.model.accessdeployment.smart.meta.conversion.EnumToSimpleValue;
import com.braintribe.model.accessdeployment.smart.meta.conversion.EnumToString;
import com.braintribe.model.accessdeployment.smart.meta.conversion.IntegerToString;
import com.braintribe.model.accessdeployment.smart.meta.conversion.LongToString;
import com.braintribe.model.accessdeployment.smart.meta.conversion.SmartConversion;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.processing.query.eval.api.function.QueryFunctionExpert;
import com.braintribe.model.processing.query.eval.context.function.AsStringExpert;
import com.braintribe.model.processing.query.eval.context.function.LocalizeExpert;
import com.braintribe.model.processing.smartquery.eval.api.SmartConversionExpert;
import com.braintribe.model.query.functions.EntitySignature;
import com.braintribe.model.query.functions.Localize;
import com.braintribe.model.query.functions.QueryFunction;
import com.braintribe.model.query.functions.value.AsString;
import com.braintribe.model.query.smart.processing.eval.context.conversion.BooleanToStringExpert;
import com.braintribe.model.query.smart.processing.eval.context.conversion.DateToStringExpert;
import com.braintribe.model.query.smart.processing.eval.context.conversion.DecimalToStringExpert;
import com.braintribe.model.query.smart.processing.eval.context.conversion.DoubleToStringExpert;
import com.braintribe.model.query.smart.processing.eval.context.conversion.EnumToSimpleValueExpert;
import com.braintribe.model.query.smart.processing.eval.context.conversion.EnumToStringExpert;
import com.braintribe.model.query.smart.processing.eval.context.conversion.IntegerToStringExpert;
import com.braintribe.model.query.smart.processing.eval.context.conversion.LongToStringExpert;
import com.braintribe.model.query.smart.processing.eval.context.function.AssembleEntityExpert;
import com.braintribe.model.query.smart.processing.eval.context.function.DiscriminatorValueExpert;
import com.braintribe.model.query.smart.processing.eval.context.function.ResolveDelegatePropertyExpert;
import com.braintribe.model.query.smart.processing.eval.context.function.ResolveIdExpert;
import com.braintribe.model.query.smart.processing.eval.context.function.SmartEntitySignatureExpert;
import com.braintribe.model.smartqueryplan.functions.AssembleEntity;
import com.braintribe.model.smartqueryplan.functions.DiscriminatorValue;
import com.braintribe.model.smartqueryplan.queryfunctions.ResolveDelegateProperty;
import com.braintribe.model.smartqueryplan.queryfunctions.ResolveId;

/**
 * 
 */
class ConfigurationTools {

	private static final Map<EntityType<? extends QueryFunction>, QueryFunctionExpert<?>> DEFAULT_FUNCTION_EXPERTS = newMap();

	static {
		DEFAULT_FUNCTION_EXPERTS.put(Localize.T, LocalizeExpert.INSTANCE);
		DEFAULT_FUNCTION_EXPERTS.put(AssembleEntity.T, AssembleEntityExpert.INSTANCE);
		DEFAULT_FUNCTION_EXPERTS.put(DiscriminatorValue.T, DiscriminatorValueExpert.INSTANCE);
		DEFAULT_FUNCTION_EXPERTS.put(AsString.T, AsStringExpert.INSTANCE);
		DEFAULT_FUNCTION_EXPERTS.put(ResolveId.T, ResolveIdExpert.INSTANCE);
		DEFAULT_FUNCTION_EXPERTS.put(ResolveDelegateProperty.T, ResolveDelegatePropertyExpert.INSTANCE);
		DEFAULT_FUNCTION_EXPERTS.put(EntitySignature.T, SmartEntitySignatureExpert.INSTANCE);
	}

	static Map<EntityType<? extends QueryFunction>, QueryFunctionExpert<?>> functionExperts(
			Map<EntityType<? extends QueryFunction>, QueryFunctionExpert<?>> configuredExperts) {

		Map<EntityType<? extends QueryFunction>, QueryFunctionExpert<?>> result = newMap();
		result.putAll(DEFAULT_FUNCTION_EXPERTS);
		if (configuredExperts != null)
			result.putAll(configuredExperts);

		return result;
	}

	private static final Map<EntityType<? extends SmartConversion>, SmartConversionExpert<?>> DEFAULT_CONVERSION_EXPERTS = newMap();

	static {
		DEFAULT_CONVERSION_EXPERTS.put(BooleanToString.T, BooleanToStringExpert.INSTANCE);
		DEFAULT_CONVERSION_EXPERTS.put(DateToString.T, DateToStringExpert.INSTANCE);
		DEFAULT_CONVERSION_EXPERTS.put(DecimalToString.T, DecimalToStringExpert.INSTANCE);
		DEFAULT_CONVERSION_EXPERTS.put(DoubleToString.T, DoubleToStringExpert.INSTANCE);
		DEFAULT_CONVERSION_EXPERTS.put(EnumToString.T, EnumToStringExpert.INSTANCE);
		DEFAULT_CONVERSION_EXPERTS.put(EnumToSimpleValue.T, EnumToSimpleValueExpert.INSTANCE);
		DEFAULT_CONVERSION_EXPERTS.put(IntegerToString.T, IntegerToStringExpert.INSTANCE);
		DEFAULT_CONVERSION_EXPERTS.put(LongToString.T, LongToStringExpert.INSTANCE);
	}

	static Map<EntityType<? extends SmartConversion>, SmartConversionExpert<?>> conversionExperts(
			Map<EntityType<? extends SmartConversion>, SmartConversionExpert<?>> configuredExperts) {

		Map<EntityType<? extends SmartConversion>, SmartConversionExpert<?>> result = newMap();
		result.putAll(DEFAULT_CONVERSION_EXPERTS);
		if (configuredExperts != null)
			result.putAll(configuredExperts);

		return result;
	}

}
