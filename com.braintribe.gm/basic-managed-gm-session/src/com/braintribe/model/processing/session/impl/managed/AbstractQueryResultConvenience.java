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
package com.braintribe.model.processing.session.impl.managed;

import static com.braintribe.utils.lcd.CollectionTools2.newMap;

import java.util.List;
import java.util.Map;

import com.braintribe.exception.Exceptions;
import com.braintribe.logging.Logger;
import com.braintribe.model.generic.pr.criteria.TraversingCriterion;
import com.braintribe.model.generic.session.exception.GmSessionException;
import com.braintribe.model.generic.session.exception.GmSessionRuntimeException;
import com.braintribe.model.processing.session.api.managed.QueryResultConvenience;
import com.braintribe.model.processing.template.evaluation.TemplateEvaluation;
import com.braintribe.model.processing.template.evaluation.TemplateEvaluationException;
import com.braintribe.model.query.EntityQueryResult;
import com.braintribe.model.query.PropertyQueryResult;
import com.braintribe.model.query.Query;
import com.braintribe.model.query.QueryResult;
import com.braintribe.model.query.SelectQueryResult;
import com.braintribe.model.template.Template;

public abstract class AbstractQueryResultConvenience<Q extends Query, R extends QueryResult, C extends QueryResultConvenience> implements QueryResultConvenience {
	
	private static Logger log = Logger.getLogger(AbstractDelegatingQueryResultConvenience.class);
	
	private Q query;
	private final Map<String, Object> variables = newMap();
	
	public AbstractQueryResultConvenience(Q query) {
		this.query = query;
	}
	
	public Q getQuery() {
		return query;
	}
	
	@Override
	public <E> E first() throws GmSessionException {
		List<E> list = list();
		
		return list.isEmpty() ? null : list.get(0);
	}
	
	@Override
	public <E> E unique() throws GmSessionException {
		List<E> list = list();
		switch (list.size()) {
			case 0:
				return null;
			case 1:
				return list.get(0);
			default:
				throw new GmSessionException("Unique query returned " + list.size() + " results. Query: " + query.stringify() + ", result: " + list);
		}
	}
	
	@Override
	public <E> List<E> list() throws GmSessionException {
		QueryResult qr = result();
		
		if (qr instanceof SelectQueryResult)
			return (List<E>)((SelectQueryResult) qr).getResults();

		if (qr instanceof EntityQueryResult)
			return (List<E>)((EntityQueryResult) qr).getEntities();

		if (qr instanceof PropertyQueryResult)
			return (List<E>)((PropertyQueryResult) qr).getPropertyValue();
		
		throw new GmSessionException("Unsupported query result: " + qr);
	}
	
	@Override
	public <E> E value() throws GmSessionException {
		QueryResult result = result();

		if (result instanceof SelectQueryResult)
			return (E) ((SelectQueryResult) result).getResults();

		if (result instanceof EntityQueryResult)
			return (E) ((EntityQueryResult) result).getEntities();

		if (result instanceof PropertyQueryResult)
			return (E) ((PropertyQueryResult) result).getPropertyValue();
		
		throw new GmSessionException("Unsupported query result: "+result);
	}
	
	@Override
	public R result() {
		try {
			return resultInternal(resolveQuery());

		} catch (RuntimeException e) {
			throw Exceptions.unchecked(e, "Error while evaluating query: " + query.stringify());
		}
	}
	
	private Q resolveQuery() {
		if (!variables.isEmpty())
			resolveQueryTemplate();

		return query;
	}

	private void resolveQueryTemplate() {
		log.trace(() -> "Evaluating variables of query: " + query);

		TemplateEvaluation templateEvaluation = createQueryTemplateEvaluation();
		
		try {
			query = templateEvaluation.evaluateTemplate(false);

			log.trace(() -> "Successfully evaluated variables of query: " + query.stringify());

		} catch (TemplateEvaluationException e) {
			throw new GmSessionRuntimeException("Error while evaluating query variables.",e);
		}
	}

	private TemplateEvaluation createQueryTemplateEvaluation() {
		Template template = Template.T.create();
		template.setPrototype(query);
		
		TemplateEvaluation result = new TemplateEvaluation();
		result.setTemplate(template);
		result.setValueDescriptorValues(variables);

		return result;
	}

	protected abstract R resultInternal(Q query) throws GmSessionException;
	
	@Override
	public C setVariable(String name, Object value) {
		variables.put(name, value);
		return self();
	}

	
	@Override
	public C setTraversingCriterion(TraversingCriterion traversingCriterion) {
		query.setTraversingCriterion(traversingCriterion);
		return self();
	}

	protected C self() {
		return (C) this;
	}
}
