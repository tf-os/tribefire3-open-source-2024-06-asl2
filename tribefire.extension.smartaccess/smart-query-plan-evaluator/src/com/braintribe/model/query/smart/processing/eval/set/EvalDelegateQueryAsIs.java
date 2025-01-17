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
package com.braintribe.model.query.smart.processing.eval.set;

import static com.braintribe.utils.lcd.CollectionTools2.newList;

import java.util.Iterator;
import java.util.List;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.pr.criteria.CriterionType;
import com.braintribe.model.generic.reflection.BaseType;
import com.braintribe.model.generic.reflection.TraversingContext;
import com.braintribe.model.processing.query.eval.api.Tuple;
import com.braintribe.model.processing.query.eval.set.HasMoreAwareSet;
import com.braintribe.model.processing.query.eval.set.base.TransientGeneratorEvalTupleSet;
import com.braintribe.model.processing.query.eval.tuple.OneDimensionalTuple;
import com.braintribe.model.processing.smartquery.eval.api.SmartQueryEvaluationContext;
import com.braintribe.model.query.SelectQuery;
import com.braintribe.model.query.SelectQueryResult;
import com.braintribe.model.query.smart.processing.eval.wrapper.ListRecord2TupleAdapter;
import com.braintribe.model.record.ListRecord;
import com.braintribe.model.smartqueryplan.set.DelegateQueryAsIs;

public class EvalDelegateQueryAsIs extends TransientGeneratorEvalTupleSet implements HasMoreAwareSet {

	protected final DelegateQueryAsIs delegateQueryAsIs;
	protected final List<Tuple> result;
	protected final SmartQueryEvaluationContext smartContext;

	protected boolean hasMore;

	public EvalDelegateQueryAsIs(DelegateQueryAsIs delegateQueryAsIs, SmartQueryEvaluationContext context) {
		super(context);

		this.delegateQueryAsIs = delegateQueryAsIs;
		this.smartContext = context;

		this.hasMore = false;
		this.result = getResult();
	}

	@Override
	public boolean hasMore() {
		/* The only way for this to be true is that our delegate query returned 'hasMore'; In all other cases this set
		 * returns all the results there are, so even if there is pagination on top of this, it has to handle the
		 * hasMore by itself. */
		return hasMore;
	}

	private List<Tuple> getResult() {
			return runQuery(delegateQueryAsIs.getDelegateQuery());
	}

	private List<Tuple> runQuery(SelectQuery query) {
		SelectQueryResult queryResult = smartContext.runQuery(delegateQueryAsIs.getDelegateAccess(), query);
		adopt(queryResult);

		hasMore = queryResult.getHasMore();


		return asTupleList(queryResult.getResults());
	}

	private List<Tuple> asTupleList(List<Object> results) {
		List<Tuple> wrappedResult = newList();

		if ((results != null) && !results.isEmpty()) {
			if (results.get(0) instanceof ListRecord) {
				for (Object record: results)
					wrappedResult.add(new ListRecord2TupleAdapter((ListRecord) record));

			} else {
				for (Object record: results) {
					OneDimensionalTuple tupleRecord = new OneDimensionalTuple(0);
					tupleRecord.setValueDirectly(0, record);
					wrappedResult.add(tupleRecord);
				}
			}
		}

		return wrappedResult;
	}

	private void adopt(SelectQueryResult queryResult) {
		BaseType.INSTANCE.traverse(queryResult.getResults(), null, this::adoptIfEntityVisited);
	}

	private void adoptIfEntityVisited(TraversingContext ctx) {
		if (ctx.getCurrentCriterionType() == CriterionType.ENTITY) {
			GenericEntity entity = (GenericEntity) ctx.getObjectStack().peek();
			smartContext.getSession().attach(entity);
		}
	}
	
	
	@Override
	public Iterator<Tuple> iterator() {
		return result.iterator();
	}

}
