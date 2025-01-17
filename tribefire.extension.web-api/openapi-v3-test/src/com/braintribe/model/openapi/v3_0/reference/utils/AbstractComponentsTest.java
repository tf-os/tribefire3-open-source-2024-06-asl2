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
package com.braintribe.model.openapi.v3_0.reference.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import java.util.Set;
import java.util.function.Function;

import org.junit.Before;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.openapi.v3_0.JsonReferencable;
import com.braintribe.model.openapi.v3_0.OpenapiSchema;
import com.braintribe.utils.CollectionTools;

public abstract class AbstractComponentsTest {
	protected TestComponents testComponents;
	protected TestApiContext rootContext;

	protected final Set<GenericModelType> rootContextSupportsTypes = CollectionTools.getSet(OpenapiSchema.T);

	@Before
	public void init() {
		rootContext = TestApiContext.create("ROOT", AbstractComponentsTest::isOpenapiEntity);
		testComponents = rootContext.components();
	}

	public static boolean isOpenapiEntity(GenericModelType type) {
		return type.getTypeSignature().contains("Openapi");
	}

	protected OpenapiSchema getSchemaComponent(EntityType<?> type, TestApiContext context, JsonReferencable ref) {
		String prefix = "#/components/schemas/";
		String refString = ref.get$ref();

		assertThat(refString).startsWith(prefix);

		String refKey = refString.substring(prefix.length());

		String keySuffix = context.getKeySuffix();
		assertThat(refKey).endsWith(keySuffix);

		String typePartOfKey = refKey.substring(0, refKey.length() - keySuffix.length());
		assertThat(typePartOfKey).isEqualTo(type.getTypeSignature());

		OpenapiSchema openapiSchema = testComponents.schemaComponents.get(refKey);

		assertThat(openapiSchema).isNotNull();

		return openapiSchema;
	}

	protected OpenapiSchema getSchemaComponentFromRef(JsonReferencable ref) {
		String prefix = "#/components/schemas/";
		String refKey = ref.get$ref().substring(prefix.length());

		return testComponents.schemaComponents.get(refKey);
	}

	private static void assertIsReference(JsonReferencable ref) {

		EntityType<GenericEntity> entityType = ref.entityType();

		for (Property property : entityType.getProperties()) {
			if (property.getName().equals("$ref")) {
				assertThat(!property.isEmptyValue(ref));
			} else {
				assertThat(property.isEmptyValue(ref));
			}
		}
	}

	protected static OpenapiSchema schemaRef(TestApiContext context, EntityType<?> type, Function<TestApiContext, OpenapiSchema> factory) {
		OpenapiSchema ref = context.schema(type).ensure(factory).getRef();

		if (ref == null) {
			// schema not present in context
			return null;
		}

		assertIsReference(ref);

		return ref;
	}

	protected static OpenapiSchema alreadyPresentSchemaRef(TestApiContext context, EntityType<?> type) {
		return schemaRef(context, type, c -> {
			fail("Factory should not have been called because schema is already expected to be present.");
			return null;
		});
	}

	protected static OpenapiSchema schemaRef(TestApiContext context, EntityType<?> type) {
		return schemaRef(context, type, schemaFactory(type));
	}

	protected static Function<TestApiContext, OpenapiSchema> schemaFactory(EntityType<?> type) {
		return c -> {
			OpenapiSchema schema = OpenapiSchema.T.create();
			schema.setDescription(type.getTypeSignature());
			return schema;
		};
	}

}