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
package com.braintribe.model.processing.vde.evaluator.impl.arithmetic.divide;

import com.braintribe.model.bvd.math.Divide;
import com.braintribe.model.processing.vde.evaluator.api.arithmetic.ArithmeticEvalExpert;

/**
 * Expert for {@link Divide} that operates on first operand of type Number and
 * second operand of type Number, where internally they are converted to
 * Integer
 * 
 */
public class IntegerNumericDivide implements ArithmeticEvalExpert<Number, Number> {

	private static IntegerNumericDivide instance = null;

	protected IntegerNumericDivide() {
		// empty
	}

	public static IntegerNumericDivide getInstance() {
		if (instance == null) {
			instance = new IntegerNumericDivide();
		}
		return instance;
	}
	
	@Override
	public Object evaluate(Number firstOperand, Number secondOperand) {
		Integer firstValue = firstOperand.intValue();
		Integer secondValue = secondOperand.intValue();
		return firstValue / secondValue;
	}

}
