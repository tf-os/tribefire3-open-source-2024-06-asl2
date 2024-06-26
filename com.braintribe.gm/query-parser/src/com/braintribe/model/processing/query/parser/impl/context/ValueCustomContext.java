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
package com.braintribe.model.processing.query.parser.impl.context;

/**
 * Parent of all the different variations of {@link CustomContext}. In the
 * parser listeners, most of the "value" tokens are cast to this type to provide
 * generality.
 * 
 * @param <R>
 *            Type of object that is wrapped
 */
public class ValueCustomContext<R extends Object> extends CustomContext<R> {

	public ValueCustomContext(R returnValue) {
		super(returnValue);
	}

}