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
package com.braintribe.model.processing.query.stringifier.experts;

import java.util.Collection;
import java.util.function.Function;

import com.braintribe.model.processing.query.api.stringifier.QueryStringifierRuntimeException;
import com.braintribe.model.processing.query.api.stringifier.experts.Stringifier;
import com.braintribe.model.processing.query.api.stringifier.experts.paramter.FunctionParameterProvider;
import com.braintribe.model.processing.query.api.stringifier.experts.paramter.MultipleParameterProvider;
import com.braintribe.model.processing.query.api.stringifier.experts.paramter.SingleParameterProvider;
import com.braintribe.model.processing.query.stringifier.BasicQueryStringifierContext;

public class FunctionStringifier<F extends Object> implements Stringifier<F, BasicQueryStringifierContext> {
	private Function<F,String> functionNameSupplier;
	private FunctionParameterProvider parameterProvider;

	public FunctionStringifier() {
		// Nothing
	}

	public FunctionStringifier(String functionName, FunctionParameterProvider parameterProvider) {
		this();
		setFunctionName(functionName);
		setOperandProvider(parameterProvider);
	}

	public FunctionStringifier(Function<F,String> functionNameSupplier, FunctionParameterProvider parameterProvider) {
		setFunctionNameSupplier(functionNameSupplier);
		setOperandProvider(parameterProvider);
	}
	
	public void setFunctionName(String functionName) {
		this.functionNameSupplier = (v)->functionName;
	}

	public void setFunctionNameSupplier(Function<F, String> functionNameSupplier) {
		this.functionNameSupplier = functionNameSupplier;
	}
	
	public void setOperandProvider(FunctionParameterProvider parameterProvider) {
		this.parameterProvider = parameterProvider;
	}

	@Override
	public String stringify(F function, BasicQueryStringifierContext context) throws QueryStringifierRuntimeException {
		StringBuilder queryString = new StringBuilder();

		queryString.append(this.functionNameSupplier.apply(function));
		queryString.append("(");

		if (this.parameterProvider != null) {
			if (this.parameterProvider instanceof MultipleParameterProvider<?>) {
				MultipleParameterProvider<F> multipleProvider = (MultipleParameterProvider<F>) this.parameterProvider;

				Collection<?> parameters = multipleProvider.provideParameters(function);
				
				if (parameters.size() == 1) {
					appendSingleParameter(parameters.iterator().next(), context, queryString);
				} else {
					boolean first = true;
					for (Object parameter : parameters) {
						if (!first) {
							queryString.append(", ");
						}
						
						queryString.append(context.stringify(parameter));
						first = false;
					}
				}
			} else if (this.parameterProvider instanceof SingleParameterProvider<?>) {
				SingleParameterProvider<F> singleProvider = (SingleParameterProvider<F>) this.parameterProvider;
				appendSingleParameter(singleProvider.provideParameter(function), context, queryString);
			} else {
				throw new QueryStringifierRuntimeException("Unsuppored parameter provider: " + this.parameterProvider);
			}

		}

		queryString.append(")");
		return queryString.toString();
	}

	private void appendSingleParameter(Object singleParameter, BasicQueryStringifierContext context, StringBuilder queryString) {
		if (singleParameter instanceof Collection<?>) {
			appendSingleCollection(context, queryString, singleParameter);
		} else {
			queryString.append(context.stringify(singleParameter));
		}
	}

	private void appendSingleCollection(BasicQueryStringifierContext context, StringBuilder queryString, Object singleParameter) {
		Collection<?> collectionParameter = (Collection<?>) singleParameter;
		if (collectionParameter.size() > 0) {
			boolean first = true;
			for (final Object item : collectionParameter) {
				if (first == false) {
					// Add operand splitter
					queryString.append(", ");
				}

				// Add stringified operand
				queryString.append(context.stringify(item));
				first = false;
			}
		} else {
			queryString.append("()");
		}
	}
}
