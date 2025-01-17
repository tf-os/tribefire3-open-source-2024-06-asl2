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
package com.braintribe.model.processing.smart.query.planner.core.builder;

import java.util.List;
import java.util.Map;

import com.braintribe.model.accessdeployment.smart.meta.conversion.EnumToSimpleValue;
import com.braintribe.model.accessdeployment.smart.meta.conversion.EnumToString;
import com.braintribe.model.accessdeployment.smart.meta.conversion.SmartConversion;
import com.braintribe.model.meta.GmEnumConstant;
import com.braintribe.model.meta.GmEnumType;
import com.braintribe.model.processing.smart.query.planner.graph.EntitySourceNode;
import com.braintribe.model.processing.smart.query.planner.structure.adapter.DiscriminatedHierarchy;
import com.braintribe.model.queryplan.value.Value;
import com.braintribe.model.smartqueryplan.ScalarMapping;
import com.braintribe.model.smartqueryplan.value.CompositeDiscriminatorBasedSignature;
import com.braintribe.model.smartqueryplan.value.ConvertedValue;
import com.braintribe.model.smartqueryplan.value.SimpleDiscriminatorBasedSignature;
import com.braintribe.model.smartqueryplan.value.SmartEntitySignature;

/**
 * 
 * @author peter.gazdik
 */
public class SmartValueBuilder {

	public static SmartEntitySignature smartEntitySignature(EntitySourceNode sourceNode, Map<String, String> signatureMapping) {
		SmartEntitySignature result = SmartEntitySignature.T.createPlain();

		result.setTuplePosition(sourceNode.getDelegateSignaturePosition());
		result.setSignatureMapping(signatureMapping);

		return result;
	}

	public static SimpleDiscriminatorBasedSignature simpleDiscriminatorBasedSignature(int tuplePosition, DiscriminatedHierarchy dh) {
		SimpleDiscriminatorBasedSignature result = SimpleDiscriminatorBasedSignature.T.createPlain();

		result.setTuplePosition(tuplePosition);
		result.setSignatureMapping(dh.getSimpleSignatureMapping());

		return result;
	}

	public static CompositeDiscriminatorBasedSignature compositeDiscriminatorBasedSignature(List<Integer> tuplePositions,
			DiscriminatedHierarchy dh) {

		CompositeDiscriminatorBasedSignature result = CompositeDiscriminatorBasedSignature.T.createPlain();

		result.setTuplePositions(tuplePositions);
		result.setRules(dh.getCompositeSignatureRules());

		return result;
	}

	public static EnumToString identityEnumToStringConversion(GmEnumType gmEnumType) {
		EnumToString conversion = EnumToString.T.createPlain();
		conversion.setEnumType(gmEnumType);
		conversion.setInverse(true);

		return conversion;
	}

	public static EnumToSimpleValue enumToSimpleValueConversion(Map<GmEnumConstant, Object> valueMappings) {
		EnumToSimpleValue conversion = EnumToSimpleValue.T.createPlain();
		conversion.setValueMappings(valueMappings);
		conversion.setInverse(true);

		return conversion;
	}

	public static Value ensureConvertedValue(Value operand, SmartConversion conversion, boolean inverse) {
		return conversion == null ? operand : convertedValue(operand, conversion, inverse);
	}

	public static ConvertedValue convertedValue(Value operand, SmartConversion conversion) {
		return convertedValue(operand, conversion, false);
	}

	public static ConvertedValue convertedValue(Value operand, SmartConversion conversion, boolean inverse) {
		ConvertedValue result = ConvertedValue.T.createPlain();
		result.setOperand(operand);
		result.setConversion(conversion);
		result.setInverse(inverse);

		return result;
	}

	public static ScalarMapping scalarMapping(int tupleComponentIndex, Value value) {
		ScalarMapping result = ScalarMapping.T.createPlain();

		result.setTupleComponentIndex(tupleComponentIndex);
		result.setSourceValue(value);

		return result;
	}

}
