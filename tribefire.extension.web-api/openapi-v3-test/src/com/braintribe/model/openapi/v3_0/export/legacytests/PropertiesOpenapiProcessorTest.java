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

import static com.braintribe.model.openapi.v3_0.export.AbstractOpenapiProcessor.ALL_MEDIA_TYPES_RANGE;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Assert;
import org.junit.Test;

import com.braintribe.model.openapi.v3_0.OpenApi;
import com.braintribe.model.openapi.v3_0.OpenapiOperation;
import com.braintribe.model.openapi.v3_0.OpenapiPath;
import com.braintribe.model.openapi.v3_0.OpenapiResponse;
import com.braintribe.model.openapi.v3_0.OpenapiSchema;
import com.braintribe.model.openapi.v3_0.OpenapiType;
import com.braintribe.model.openapi.v3_0.api.OpenapiPropertiesRequest;
import com.braintribe.model.openapi.v3_0.export.legacytests.wire.PropertiesOpenapiProcessorTestWireModule;
import com.braintribe.model.openapi.v3_0.export.legacytests.wire.contract.PropertiesOpenapiProcessorTestContract;
import com.braintribe.model.user.Group;
import com.braintribe.wire.api.module.WireTerminalModule;

public class PropertiesOpenapiProcessorTest extends AbstractOpenapiProcessorTest<PropertiesOpenapiProcessorTestContract> {

	private static final String PATH_USER__PROPERTIES = "/com.braintribe.model.user.User/{id}/";

	@Override
	protected WireTerminalModule<PropertiesOpenapiProcessorTestContract> module() {
		return PropertiesOpenapiProcessorTestWireModule.INSTANCE;
	}

	@Test
	public void testSwaggerPropertiesPath() {
		OpenapiPropertiesRequest request = OpenapiPropertiesRequest.T.create();
		request.setAccessId("test.access");

		OpenApi api = runProcessor(request);
		OpenapiPath path = getPath(api, PATH_USER__PROPERTIES + "firstName");

		// get
		assertIdAndPartition(path.getGet(), true, false);
		assertEndpointParameters(path.getGet());

		assertSimpleResponseBody(path.getGet(), SchemaAsserter.stringSchema());
		// Assert.assertEquals("string", schema.getType());

		// delete
		assertIdAndPartition(path.getDelete(), true, false);
		assertEndpointParameters(path.getDelete());
		assertSimpleResponseBody(path.getDelete(), SchemaAsserter.booleanSchema());
		// Assert.assertEquals("string", schema1.getType());

		// post
		Assert.assertNull(path.getPost());

		// put
		assertIdAndPartition(path.getPut(), true, false);
		assertEndpointParameters(path.getPut());
		getBodyParameter(path.getPut(), "firstName");
	}

	private void assertSimpleResponseBody(OpenapiOperation operation, SchemaAsserter schemaAsserter) {
		OpenapiResponse response = getReferenced(operation.getResponses().get("200"));
		Assert.assertNotNull(response);

		if (schemaAsserter == null) {
			Assert.assertNotNull(getSchemaFromContent(response.getContent()));
		} else {
			OpenapiSchema schema = getSchemaFromContent(response.getContent(), schemaAsserter.isSimpleType());
			schemaAsserter.test(schema);
		}
	}

	@Test
	public void testSwaggerEntityPropertiyArrayPath() {
		OpenapiPropertiesRequest request = OpenapiPropertiesRequest.T.create();
		request.setAccessId("test.access");

		OpenApi api = runProcessor(request);
		OpenapiPath path = getPath(api, PATH_USER__PROPERTIES + "groups");

		// get
		assertIdAndPartition(path.getGet(), true, false);
		assertEndpointParameters(path.getGet());

		OpenapiResponse response = getReferenced(path.getGet().getResponses().get("200"));
		Assert.assertNotNull(response);
		assertThat(response.getContent()).containsOnlyKeys("application/json", ALL_MEDIA_TYPES_RANGE);
		OpenapiSchema arraySchema = response.getContent().get(ALL_MEDIA_TYPES_RANGE).getSchema();

		assertThat(arraySchema.getType()).isEqualTo(OpenapiType.ARRAY);
		OpenapiSchema items = getReferenced(arraySchema.getItems());
		assertThat(items.getTitle()).isEqualTo(Group.T.getTypeSignature());
		// Assert.assertEquals("array", schema.getType());

		// delete
		assertIdAndPartition(path.getDelete(), true, false);
		assertEndpointParameters(path.getDelete());
		assertSimpleResponseBody(path.getDelete(), SchemaAsserter.booleanSchema());

		// post
		assertIdAndPartition(path.getPut(), true, false);
		assertEndpointParameters(path.getPut());
		getBodyParameter(path.getPut(), "groups");

		// put
		assertIdAndPartition(path.getPut(), true, false);
		assertEndpointParameters(path.getPut());
		getBodyParameter(path.getPut(), "groups");
	}

	@Test
	public void testSwaggerEntityPropertiyObjectPath() {
		OpenapiPropertiesRequest request = OpenapiPropertiesRequest.T.create();
		request.setAccessId("test.access");

		OpenApi api = runProcessor(request);
		OpenapiPath path = getPath(api, PATH_USER__PROPERTIES + "picture");

		// get
		assertIdAndPartition(path.getGet(), true, false);
		assertEndpointParameters(path.getGet());
		assertSimpleResponseBody(path.getGet(), null);

		// delete
		assertIdAndPartition(path.getDelete(), true, false);
		assertEndpointParameters(path.getDelete());
		assertSimpleResponseBody(path.getDelete(), SchemaAsserter.booleanSchema());

		// post
		assertIdAndPartition(path.getPut(), true, false);
		assertEndpointParameters(path.getPut());
		OpenapiSchema s = getBodyParameter(path.getPut(), "picture");
		assertSchemaProperty(getReferenced(s), "name", null, OpenapiType.STRING, true);

		// put
		assertIdAndPartition(path.getPut(), true, false);
		assertEndpointParameters(path.getPut());
		OpenapiSchema s1 = getBodyParameter(path.getPut(), "picture");
		assertSchemaProperty(getReferenced(s1), "name", null, OpenapiType.STRING, true);
	}

	private OpenApi runProcessor(OpenapiPropertiesRequest request) {
		// processor.setEvaluator(new TestSwaggerV2Evaluator());
		OpenApi result = request.eval(evaluator).get();
		components = result.getComponents();

		Assert.assertEquals("3.0.1", result.getOpenapi());
		// Assert.assertEquals("/tribefire-services/rest/v2/"+ request.getAccessId(), result.getBasePath());
		Assert.assertNotNull(result.getInfo());

		return result;
	}

}
