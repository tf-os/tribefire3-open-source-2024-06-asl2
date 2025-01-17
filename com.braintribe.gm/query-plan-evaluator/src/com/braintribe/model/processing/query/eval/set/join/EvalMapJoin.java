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
package com.braintribe.model.processing.query.eval.set.join;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

import com.braintribe.model.processing.query.eval.api.QueryEvaluationContext;
import com.braintribe.model.processing.query.eval.api.Tuple;
import com.braintribe.model.processing.query.eval.tuple.ArrayBasedTuple;
import com.braintribe.model.queryplan.set.join.JoinedMapKey;
import com.braintribe.model.queryplan.set.join.MapJoin;

/**
 * 
 */
public class EvalMapJoin extends AbstractEvalPropertyJoin {

	protected final JoinedMapKey mapKey;

	public EvalMapJoin(MapJoin join, QueryEvaluationContext context) {
		super(join, context);

		this.mapKey = join.getMapKey();
	}

	@Override
	public Iterator<Tuple> iterator() {
		return new MapIterator();
	}

	protected class MapIterator extends AbstractPropertyJoinIterator {
		private Iterator<Map.Entry<Object, Object>> entriesIterator;

		@Override
		protected void onNewOperandTuple$LeftInner(ArrayBasedTuple tuple) {
			Map<Object, Object> m = context.resolveValue(tuple, valueProperty);
			m = m != null ? m : Collections.emptyMap();
			entriesIterator = m.entrySet().iterator();
		}

		@Override
		protected boolean hasNextJoinedValue$LeftInner() {
			return entriesIterator.hasNext();
		}

		@Override
		protected void setNextJoinedValue$LeftInner(ArrayBasedTuple tuple) {
			Map.Entry<?, ?> nextEntry = entriesIterator.next();

			tuple.setValueDirectly(mapKey.getIndex(), nextEntry.getKey());
			setRightValue(tuple, nextEntry.getValue());
		}

		@Override
		protected void setNextJoinedValueAsVoid(ArrayBasedTuple tuple) {
			tuple.setValueDirectly(mapKey.getIndex(), null);
			tuple.setValueDirectly(componentPosition, null);
		}
	}

}
