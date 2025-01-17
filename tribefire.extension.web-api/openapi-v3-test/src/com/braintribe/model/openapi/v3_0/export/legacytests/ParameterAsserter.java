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
package com.braintribe.model.openapi.v3_0.export.legacytests;

import static com.braintribe.model.openapi.v3_0.export.legacytests.AbstractOpenapiProcessorTest.getReferenced;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.Assert;

import com.braintribe.model.openapi.v3_0.OpenapiMediaType;
import com.braintribe.model.openapi.v3_0.OpenapiOperation;
import com.braintribe.model.openapi.v3_0.OpenapiParameter;
import com.braintribe.model.openapi.v3_0.OpenapiRequestBody;
import com.braintribe.model.openapi.v3_0.OpenapiSchema;
import com.braintribe.model.openapi.v3_0.OpenapiType;
import com.braintribe.model.openapi.v3_0.export.legacytests.ParameterAssertion.SimpleParameterAssertion;

public class ParameterAsserter {
	private final List<ParameterAssertion<?>> assertions = new ArrayList<>();
	private final Map<String, SchemaAsserter> requestBodies = new HashMap<>();

	public static ParameterAsserter start() {
		return new ParameterAsserter();
	}

	public ParameterAsserter expectEndpoint(String name, OpenapiType type, Object defaultValue) {
		return expect("endpoint." + name, "query", type, false, defaultValue);
	}

	public ParameterAsserter expect(String name, String in, OpenapiType type, boolean required, Object defaultValue) {
		SimpleParameterAssertion assertion = new SimpleParameterAssertion(name, in, type, required, defaultValue);
		assertions.add(assertion);

		return this;
	}

	public ParameterAsserter expectRequestBody(String mime, SchemaAsserter bodySchema) {
		requestBodies.put(mime, bodySchema);
		return this;
	}

	// public ParameterAsserter expectSerializedRequest() {
	// assertions.add(new SimpleParameterAssertion("serialized-request", "formData", OpenapiType.STRING, false, null));
	// return this;
	// }

	public ParameterAsserter expect(List<? extends ParameterAssertion<?>> assertions) {
		this.assertions.addAll(assertions);
		return this;
	}

	public void in(OpenapiOperation operation) {
		Assert.assertNotNull(operation);

		List<OpenapiParameter> actual = operation.getParameters();

		assertThat(actual).hasSameSizeAs(assertions);

		Iterator<OpenapiParameter> actualIt = actual.iterator();
		Iterator<ParameterAssertion<?>> assertionsIt = assertions.iterator();

		while (actualIt.hasNext()) {
			OpenapiParameter actualParam = getReferenced(actualIt.next());
			ParameterAssertion<?> paramAssertion = assertionsIt.next();

			paramAssertion.test(actualParam);
		}

		OpenapiRequestBody requestBody = operation.getRequestBody();

		if (!requestBodies.isEmpty()) {
			assertThat(requestBody).as("Request body expected but not found").isNotNull();

			Map<String, OpenapiMediaType> content = getReferenced(requestBody).getContent();

			assertThat(content).containsOnlyKeys(requestBodies.keySet().toArray(new String[requestBodies.size()]));

			requestBodies.forEach((k, v) -> assertMediaTypeSchema(content, k, v));
		} else {
			assertThat(requestBody).as("No request body expected but found one").isNull();
		}
	}

	private void assertMediaTypeSchema(Map<String, OpenapiMediaType> content, String k, SchemaAsserter v) {
		if (v == null) {
			System.out.println("Skipping detailed assertions for the " + k + " schema.");
			return;
		}
		System.out.println("Asserting schema for " + k);
		
		List<String> flatMimeTypes = Arrays.asList(AbstractOpenapiProcessorTest.MULTIPART_FORM_DATA, AbstractOpenapiProcessorTest.URLENCODED);
		OpenapiSchema schema = content.get(k).getSchema();
		
		if (!flatMimeTypes.contains(k))
			schema = AbstractOpenapiProcessorTest.unwrap(schema);
		
		v.test(schema);
	}

}
