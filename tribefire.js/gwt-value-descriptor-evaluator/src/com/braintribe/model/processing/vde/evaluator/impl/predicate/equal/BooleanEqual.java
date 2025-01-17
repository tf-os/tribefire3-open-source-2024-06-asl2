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
package com.braintribe.model.processing.vde.evaluator.impl.predicate.equal;

import com.braintribe.model.bvd.predicate.Equal;
import com.braintribe.model.processing.vde.evaluator.api.VdeRuntimeException;
import com.braintribe.model.processing.vde.evaluator.api.predicate.PredicateEvalExpert;

/**
 * Expert for {@link Equal} that operates on left hand side operand of type
 * Boolean and right hand side operand of type Boolean
 * 
 */
public class BooleanEqual implements PredicateEvalExpert<Boolean, Boolean> {

	private static BooleanEqual instance = null;

	protected BooleanEqual() {
		// empty
	}

	public static BooleanEqual getInstance() {
		if (instance == null) {
			instance = new BooleanEqual();
		}
		return instance;
	}

	@Override
	public Object evaluate(Boolean leftOperand, Boolean rightOperand) throws VdeRuntimeException {
		return leftOperand.equals(rightOperand);
	}

}
