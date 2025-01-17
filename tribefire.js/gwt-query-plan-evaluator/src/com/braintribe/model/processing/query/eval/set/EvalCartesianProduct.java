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
package com.braintribe.model.processing.query.eval.set;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.braintribe.model.processing.query.eval.api.EvalTupleSet;
import com.braintribe.model.processing.query.eval.api.QueryEvaluationContext;
import com.braintribe.model.processing.query.eval.api.Tuple;
import com.braintribe.model.processing.query.eval.set.base.TransientGeneratorEvalTupleSet;
import com.braintribe.model.processing.query.eval.tools.QueryEvaluationTools;
import com.braintribe.model.queryplan.set.CartesianProduct;

/**
 * 
 */
public class EvalCartesianProduct extends TransientGeneratorEvalTupleSet {

	protected final List<EvalTupleSet> operands;

	public EvalCartesianProduct(CartesianProduct cartesianProduct, QueryEvaluationContext context) {
		super(context);
		this.operands = QueryEvaluationTools.resolveTupleSets(cartesianProduct.getOperands(), context);
	}

	@Override
	public Iterator<Tuple> iterator() {
		return new CartesianProductIterator();
	}

	protected class CartesianProductIterator extends AbstractTupleIterator {

		protected List<Iterator<Tuple>> iterators;
		protected List<Tuple> nexts;

		public CartesianProductIterator() {
			iterators = new ArrayList<Iterator<Tuple>>(operands.size());
			nexts = new ArrayList<Tuple>(operands.size());

			for (EvalTupleSet operand: operands) {
				Iterator<Tuple> it = operand.iterator();

				if (!it.hasNext()) {
					return;
				}

				iterators.add(it);
				nexts.add(it.next());
			}

			next = singletonTuple;
			buildNext();
		}

		@Override
		protected void prepareNextValue() {
			if (moveIterators()) {
				buildNext();
			} else {
				next = null;
			}
		}

		protected boolean moveIterators() {
			return moveIterators(iterators.size() - 1);
		}

		private boolean moveIterators(int position) {
			if (position < 0) {
				return false;
			}

			Iterator<Tuple> it = iterators.get(position);
			if (it.hasNext()) {
				nexts.set(position, it.next());
				return true;
			}

			if (!moveIterators(position - 1)) {
				return false;
			}

			it = cast(operands.get(position).iterator());

			iterators.set(position, it);
			nexts.set(position, it.next());

			return true;
		}

		/* this code assumes the iterator provides at least two elements, because otherwise the Cartesian product would
		 * make no sense */
		private void buildNext() {
			Iterator<Tuple> it = nexts.iterator();

			/* Note that we set the AbstractTupleIterator.tuple = singletonTuple in the constructor, so this is dealing
			 * with the right tuple instance */
			singletonTuple.acceptAllValuesFrom(it.next());

			do {
				singletonTuple.acceptValuesFrom(it.next());

			} while (it.hasNext());
		}

	}

}
