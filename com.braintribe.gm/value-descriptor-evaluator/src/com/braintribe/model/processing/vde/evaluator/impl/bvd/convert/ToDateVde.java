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

import java.util.Date;

import com.braintribe.model.bvd.convert.ToDate;
import com.braintribe.model.processing.vde.evaluator.api.ValueDescriptorEvaluator;
import com.braintribe.model.processing.vde.evaluator.api.VdeContext;
import com.braintribe.model.processing.vde.evaluator.api.VdeResult;
import com.braintribe.model.processing.vde.evaluator.api.VdeRuntimeException;
import com.braintribe.model.processing.vde.evaluator.impl.VdeResultImpl;
import com.braintribe.utils.format.lcd.FormatTool;

/**
 * {@link ValueDescriptorEvaluator} for {@link ToDate}
 * 
 */
public class ToDateVde implements ValueDescriptorEvaluator<ToDate> {

	@Override
	public VdeResult evaluate(VdeContext context, ToDate valueDescriptor) throws VdeRuntimeException {

		Object operand = context.evaluate(valueDescriptor.getOperand());
		Object format = valueDescriptor.getFormat();
		Object result = null;
		if (operand instanceof String) {
			if (format instanceof String) {
				try {
					result =  FormatTool.getExpert().getDateFormat().parseDate((String) operand, (String) format);
				} catch (Exception e) {
					throw new VdeRuntimeException("Convert to Date with String operand expects a format compatible with SimpleDateFormat", e);
				}
			} else {
				throw new VdeRuntimeException("Convert ToDate with String operand is not applicable for format:" + format);
			}
		} else if (operand instanceof Long) {
			if (format != null) {
				throw new VdeRuntimeException("Convert ToDate with String operand is not applicable for any format");
			}
			result = new Date((Long) operand);
		} else {
			throw new VdeRuntimeException("Convert ToDate is not applicable to:" + operand);
		}

		return new VdeResultImpl(result, false);
	}

}
