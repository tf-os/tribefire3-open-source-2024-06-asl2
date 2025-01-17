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

import java.math.BigDecimal;
import java.math.MathContext;

import com.braintribe.model.bvd.convert.ToDecimal;
import com.braintribe.model.processing.vde.evaluator.api.ValueDescriptorEvaluator;
import com.braintribe.model.processing.vde.evaluator.api.VdeContext;
import com.braintribe.model.processing.vde.evaluator.api.VdeResult;
import com.braintribe.model.processing.vde.evaluator.api.VdeRuntimeException;
import com.braintribe.model.processing.vde.evaluator.impl.VdeResultImpl;

/**
 * {@link ValueDescriptorEvaluator} for {@link ToDecimal}
 * 
 */
public class ToDecimalVde implements ValueDescriptorEvaluator<ToDecimal> {

	@Override
	public VdeResult evaluate(VdeContext context, ToDecimal valueDescriptor) throws VdeRuntimeException {

		Object format = valueDescriptor.getFormat();

		Object result = null;

		Object operand = context.evaluate(valueDescriptor.getOperand());

		if (operand instanceof String) {

			if (format != null) {
				if (format instanceof String) {
					try {
						result = new BigDecimal((String) operand, new MathContext((String) format));
					} catch (Exception e) {
						throw new VdeRuntimeException(
								"Format for ToDecimal with String operand should follow MathContext format", e);
					}
				} else {
					throw new VdeRuntimeException("Convert ToDecimal with String operand is not applicable for format:"
							+ format);
				}

			} else {
				result = new BigDecimal((String) operand);
			}
		} else if (operand instanceof Boolean) {

			if (format != null) {
				throw new VdeRuntimeException("Convert ToDecimal with Boolean operand is not applicable for any format");
			}

			if ((Boolean) operand) {
				result = new BigDecimal(1);
			} else {
				result = new BigDecimal(0);
			}

		} else {
			throw new VdeRuntimeException("Convert ToDecimal is not applicable to:" + operand);
		}

		return new VdeResultImpl(result, false);
	}

}
