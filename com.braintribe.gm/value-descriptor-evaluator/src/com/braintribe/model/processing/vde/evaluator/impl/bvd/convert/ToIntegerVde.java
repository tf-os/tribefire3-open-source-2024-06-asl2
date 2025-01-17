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
package com.braintribe.model.processing.vde.evaluator.impl.bvd.convert;

import com.braintribe.model.bvd.convert.ToInteger;
import com.braintribe.model.processing.vde.evaluator.api.ValueDescriptorEvaluator;
import com.braintribe.model.processing.vde.evaluator.api.VdeContext;
import com.braintribe.model.processing.vde.evaluator.api.VdeResult;
import com.braintribe.model.processing.vde.evaluator.api.VdeRuntimeException;
import com.braintribe.model.processing.vde.evaluator.impl.VdeResultImpl;

/**
 * {@link ValueDescriptorEvaluator} for {@link ToInteger}
 * 
 */
public class ToIntegerVde implements ValueDescriptorEvaluator<ToInteger> {

	@Override
	public VdeResult evaluate(VdeContext context, ToInteger valueDescriptor) throws VdeRuntimeException {

		Object format = valueDescriptor.getFormat();
		if (format != null) {
			throw new VdeRuntimeException("Format is not supported for ToInteger yet, format:" + format);
		}

		Object result = null;
		Object operand = context.evaluate(valueDescriptor.getOperand());

		if (operand instanceof String) {
			
			result = Integer.parseInt((String) operand);
		} else if (operand instanceof Boolean) {

			if ((Boolean) operand) {
				result = Integer.valueOf(1);
			} else {
				result = Integer.valueOf(0);
			}

		} else if (operand instanceof Enum) {
			result = Integer.valueOf(((Enum<?>) operand).ordinal());
		} else {
			throw new VdeRuntimeException("Convert ToInteger is not applicable to:" + operand);
		}

		return new VdeResultImpl(result, false);
	}

}
