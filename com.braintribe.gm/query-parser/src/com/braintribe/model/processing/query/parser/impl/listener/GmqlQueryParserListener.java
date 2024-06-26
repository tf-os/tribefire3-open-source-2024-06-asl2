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
package com.braintribe.model.processing.query.parser.impl.listener;

import com.braintribe.model.processing.query.parser.impl.autogenerated.GmqlParser.QueryContext;
import com.braintribe.model.processing.query.parser.impl.autogenerated.GmqlParser.QueryTailOrderByContext;
import com.braintribe.model.processing.query.parser.impl.autogenerated.GmqlParser.QueryTailPaginationContext;
import com.braintribe.model.processing.query.parser.impl.autogenerated.GmqlParser.QueryTailWhereContext;
import com.braintribe.model.processing.query.parser.impl.autogenerated.GmqlParser.WhereConditionContext;
import com.braintribe.model.processing.query.parser.impl.context.ObjectCustomContext;
import com.braintribe.model.processing.query.parser.impl.context.OrderingCustomContext;
import com.braintribe.model.processing.query.parser.impl.context.QueryCustomContext;
import com.braintribe.model.processing.query.parser.impl.context.ValueCustomContext;
import com.braintribe.model.processing.query.parser.impl.context.basetype.DefaultCustomContext;
import com.braintribe.model.query.Ordering;
import com.braintribe.model.query.Paging;
import com.braintribe.model.query.Query;
import com.braintribe.model.query.Restriction;
import com.braintribe.model.query.conditions.Condition;

public abstract class GmqlQueryParserListener extends GmqlDateFunctionParserListener {

	@Override
	public void exitQuery(QueryContext ctx) {
		propagateChildResult(ctx);
	}

	@Override
	public void exitWhereCondition(WhereConditionContext ctx) {
		setValue(ctx, takeValue(ctx.booleanValueExpression()));
	}

	@Override
	public void exitQueryTailOrderBy(QueryTailOrderByContext ctx) {
		Query query = ((QueryCustomContext) getValue(ctx.getParent()).cast()).getReturnValue();
		if (ctx.orderBy() != null) {
			Ordering orderBy = ((OrderingCustomContext) takeValue(ctx.orderBy()).cast()).getReturnValue();
			query.setOrdering(orderBy);
		}
		setValue(ctx, new DefaultCustomContext(""));
	}

	@Override
	public void exitQueryTailWhere(QueryTailWhereContext ctx) {
		Query query = ((QueryCustomContext) getValue(ctx.getParent()).cast()).getReturnValue();
		Object condition = null;
		if (ctx.whereCondition() != null) {
			condition = ((ValueCustomContext<?>) takeValue(ctx.whereCondition()).cast()).getReturnValue();
		}
		Restriction restriction = query.getRestriction();
		if (restriction == null) {
			query.setRestriction($.restriction((Condition) condition, null));
		} else {
			restriction.setCondition((Condition) condition);
		}
		setValue(ctx, new DefaultCustomContext(""));
	}

	@Override
	public void exitQueryTailPagination(QueryTailPaginationContext ctx) {
		Query query = ((QueryCustomContext) getValue(ctx.getParent()).cast()).getReturnValue();
		Object pagination = null;
		if (ctx.pagination() != null) {
			pagination = ((ObjectCustomContext) takeValue(ctx.pagination()).cast()).getReturnValue();
		}
		Restriction restriction = query.getRestriction();
		if (restriction == null) {
			query.setRestriction($.restriction(null, (Paging) pagination));
		} else {
			restriction.setPaging((Paging) pagination);
		}
		setValue(ctx, new DefaultCustomContext(""));
	}
}