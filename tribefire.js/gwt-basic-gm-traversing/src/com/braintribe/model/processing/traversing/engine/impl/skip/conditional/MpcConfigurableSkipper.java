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
package com.braintribe.model.processing.traversing.engine.impl.skip.conditional;

import com.braintribe.model.mpc.ModelPathCondition;
import com.braintribe.model.processing.mpc.MPC;
import com.braintribe.model.processing.mpc.evaluator.api.MpcEvaluatorRuntimeException;
import com.braintribe.model.processing.traversing.api.GmTraversingContext;
import com.braintribe.model.processing.traversing.api.GmTraversingException;
import com.braintribe.model.processing.traversing.api.GmTraversingSkippingCriteria;
import com.braintribe.model.processing.traversing.api.SkipUseCase;
import com.braintribe.model.processing.traversing.api.path.TraversingModelPathElement;
import com.braintribe.model.processing.traversing.impl.visitors.GmTraversingVisitorAdapter;

/**
 * A {link ConditionalSkipper} that requires {@link MPC} to evaluate if
 * condition.
 *
 */
public class MpcConfigurableSkipper extends GmTraversingVisitorAdapter implements ConditionalSkipper {

	private ModelPathCondition condition;
	private GmTraversingSkippingCriteria skippingCriteria;
	private SkipUseCase skipUseCase;

	public ModelPathCondition getCondition() {
		return condition;
	}

	public void setCondition(ModelPathCondition condition) {
		this.condition = condition;
	}

	@Override
	public void onElementEnter(GmTraversingContext context, TraversingModelPathElement pathElement) throws GmTraversingException {

		try {
			boolean match = MPC.matches(getCondition(), pathElement);

			if (match) {
				switch (getSkippingCriteria()) {
					case skipAll:
						context.skipAll(getSkipUseCase());
						break;
					case skipDescendants:
						context.skipDescendants(getSkipUseCase());
						break;
					case skipWalkFrame:
						context.skipWalkFrame(getSkipUseCase());
						break;
				}
			}

		} catch (MpcEvaluatorRuntimeException e) {
			throw new GmTraversingException("Evaluation of MPC at MpcConfigurableSkipper failed", e);
		}
	}

	@Override
	public SkipUseCase getSkipUseCase() {
		return skipUseCase;
	}

	@Override
	public void setSkipUseCase(SkipUseCase skipUseCase) {
		this.skipUseCase = skipUseCase;
	}

	@Override
	public GmTraversingSkippingCriteria getSkippingCriteria() {
		return skippingCriteria;
	}

	@Override
	public void setSkippingCriteria(GmTraversingSkippingCriteria skippingCriteria) {
		this.skippingCriteria = skippingCriteria;

	}
}
