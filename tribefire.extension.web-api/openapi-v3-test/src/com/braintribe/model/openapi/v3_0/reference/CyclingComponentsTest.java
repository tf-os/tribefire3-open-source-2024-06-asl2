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
package com.braintribe.model.openapi.v3_0.reference;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.braintribe.model.openapi.v3_0.OpenapiComponents;
import com.braintribe.model.openapi.v3_0.OpenapiDiscriminator;
import com.braintribe.model.openapi.v3_0.OpenapiParameter;
import com.braintribe.model.openapi.v3_0.OpenapiPath;
import com.braintribe.model.openapi.v3_0.OpenapiSchema;
import com.braintribe.model.openapi.v3_0.meta.OpenapiContact;
import com.braintribe.model.openapi.v3_0.reference.utils.AbstractComponentsTest;
import com.braintribe.model.openapi.v3_0.reference.utils.TestApiContext;

public class CyclingComponentsTest extends AbstractComponentsTest {
	@Test
	public void testSimple() {

		// One cycle with 4 elements:
		// 4 (fake) OpenapiSchemas of OpenapiComponents -> OpenapiParameter -> OpenapiPath -> OpenapiContact ->
		// OpenapiComponents
		OpenapiSchema schemaRef = schemaRef(rootContext, OpenapiComponents.T, c -> {
			OpenapiSchema ref = schemaRef(c, OpenapiParameter.T, c2 -> {
				OpenapiSchema ref2 = schemaRef(c2, OpenapiPath.T, c3 -> {
					OpenapiSchema ref3 = schemaRef(c3, OpenapiContact.T, c4 -> {
						OpenapiSchema ref4 = alreadyPresentSchemaRef(c4, OpenapiComponents.T);

						OpenapiSchema comp4 = OpenapiSchema.T.create();
						comp4.setItems(ref4);
						return comp4;
					});

					OpenapiSchema comp3 = OpenapiSchema.T.create();
					comp3.setItems(ref3);
					return comp3;
				});

				OpenapiSchema comp2 = OpenapiSchema.T.create();
				comp2.setItems(ref2);
				return comp2;
			});

			OpenapiSchema comp = OpenapiSchema.T.create();
			comp.setItems(ref);
			return comp;
		});

		assertCycle(schemaRef, rootContext);

	}

	public void assertCycle(OpenapiSchema schemaRef, TestApiContext context) {
		OpenapiSchema componentsComponent = getSchemaComponent(OpenapiComponents.T, context, schemaRef);
		OpenapiSchema parameterRef = componentsComponent.getItems();
		OpenapiSchema parameterComponent = getSchemaComponent(OpenapiParameter.T, context, parameterRef);
		OpenapiSchema pathRef = parameterComponent.getItems();
		OpenapiSchema pathComponent = getSchemaComponent(OpenapiPath.T, context, pathRef);
		OpenapiSchema contactRef = pathComponent.getItems();
		OpenapiSchema contactComponent = getSchemaComponent(OpenapiContact.T, context, contactRef);
		OpenapiSchema componentsRef = contactComponent.getItems();
		OpenapiSchema componentsComponent2 = getSchemaComponent(OpenapiComponents.T, context, componentsRef);

		assertThat(componentsComponent).isSameAs(componentsComponent2);
	}

	@Test
	public void testMultipleContexts() {

		TestApiContext childContext = rootContext.childContext("CHILD");

		// One cycle with 4 elements:
		// 4 (fake) OpenapiSchemas of OpenapiComponents -> OpenapiParameter -> OpenapiPath -> OpenapiContact ->
		// OpenapiComponents
		OpenapiSchema schemaRef = schemaRef(childContext, OpenapiComponents.T, c -> {
			OpenapiSchema ref = schemaRef(c, OpenapiParameter.T, c2 -> {
				OpenapiSchema ref2 = schemaRef(c2, OpenapiPath.T, c3 -> {

					OpenapiSchema ref3 = schemaRef(c3, OpenapiContact.T, c4 -> {
						OpenapiSchema ref4 = alreadyPresentSchemaRef(c4, OpenapiComponents.T);

						OpenapiSchema comp4 = OpenapiSchema.T.create();
						comp4.setItems(ref4);
						return comp4;
					});

					OpenapiSchema comp3 = OpenapiSchema.T.create();
					comp3.setItems(ref3);
					return comp3;
				});

				OpenapiSchema comp2 = OpenapiSchema.T.create();
				comp2.setItems(ref2);
				return comp2;
			});

			OpenapiSchema comp = OpenapiSchema.T.create();
			comp.setItems(ref);
			return comp;
		});

		assertCycle(schemaRef, rootContext);
	}

	@Test
	public void testMultipleContextsWithChanges() {

		TestApiContext childContext = rootContext.childContext("CHILD");

		// One cycle with 4 elements:
		// 4 (fake) OpenapiSchemas of OpenapiComponents -> OpenapiParameter -> OpenapiPath -> OpenapiContact ->
		// OpenapiComponents
		OpenapiSchema schemaRef = schemaRef(childContext, OpenapiComponents.T, c -> {
			OpenapiSchema ref = schemaRef(c, OpenapiParameter.T, c2 -> {
				OpenapiSchema ref2 = schemaRef(c2, OpenapiPath.T, c3 -> {
					OpenapiSchema ref3 = schemaRef(c3, OpenapiContact.T, c4 -> {
						OpenapiSchema ref4 = alreadyPresentSchemaRef(c4, OpenapiComponents.T);

						OpenapiSchema comp4 = OpenapiSchema.T.create();
						comp4.setItems(ref4);
						return comp4;
					});

					OpenapiSchema comp3 = OpenapiSchema.T.create();
					comp3.setItems(ref3);
					return comp3;
				});

				OpenapiSchema comp2 = OpenapiSchema.T.create();
				comp2.setItems(ref2);
				return comp2;
			});

			OpenapiSchema comp = OpenapiSchema.T.create();
			comp.setItems(ref);

			if (c == childContext) {
				comp.setDescription("Changed in child context");
			}

			return comp;
		});

		assertCycle(schemaRef, childContext);
	}

	@Test
	public void testMultipleContextsWithChanges2() {

		TestApiContext childContext = rootContext.childContext("CHILD");

		// One cycle with 4 elements:
		// 4 (fake) OpenapiSchemas of OpenapiComponents -> OpenapiParameter -> OpenapiPath -> OpenapiContact ->
		// OpenapiComponents
		OpenapiSchema schemaRef = schemaRef(childContext, OpenapiComponents.T, c -> {
			OpenapiSchema ref = schemaRef(c, OpenapiParameter.T, c2 -> {
				OpenapiSchema ref2 = schemaRef(c2, OpenapiPath.T, c3 -> {
					OpenapiSchema ref3 = schemaRef(c3, OpenapiContact.T, c4 -> {
						OpenapiSchema ref4 = alreadyPresentSchemaRef(c4, OpenapiComponents.T);

						OpenapiSchema comp4 = OpenapiSchema.T.create();
						comp4.setItems(ref4);
						if (c4 == childContext) {
							comp4.setDescription("Changed in child context");
						}
						return comp4;
					});

					OpenapiSchema comp3 = OpenapiSchema.T.create();
					comp3.setItems(ref3);
					return comp3;
				});

				OpenapiSchema comp2 = OpenapiSchema.T.create();
				comp2.setItems(ref2);
				return comp2;
			});

			OpenapiSchema comp = OpenapiSchema.T.create();
			comp.setItems(ref);

			return comp;
		});

		assertCycle(schemaRef, childContext);
	}

	@Test
	public void testMultipleContextsWithChanges3() {

		TestApiContext childContext = rootContext.childContext("CHILD");

		// One cycle with 4 elements:
		// 4 (fake) OpenapiSchemas of OpenapiComponents -> OpenapiParameter -> OpenapiPath -> OpenapiContact ->
		// OpenapiComponents
		OpenapiSchema schemaRef = schemaRef(childContext, OpenapiComponents.T, c -> {
			OpenapiSchema ref = schemaRef(c, OpenapiParameter.T, c2 -> {
				OpenapiSchema ref2 = schemaRef(c2, OpenapiPath.T, c3 -> {
					OpenapiSchema ref3 = schemaRef(c3, OpenapiContact.T, c4 -> {
						OpenapiSchema ref4 = alreadyPresentSchemaRef(c4, OpenapiComponents.T);

						OpenapiSchema comp4 = OpenapiSchema.T.create();
						comp4.setItems(ref4);

						return comp4;
					});

					OpenapiSchema comp3 = OpenapiSchema.T.create();
					comp3.setItems(ref3);
					if (c3 == childContext) {
						comp3.setDescription("Changed in child context");
					}
					return comp3;
				});

				OpenapiSchema comp2 = OpenapiSchema.T.create();
				comp2.setItems(ref2);
				return comp2;
			});

			OpenapiSchema comp = OpenapiSchema.T.create();
			comp.setItems(ref);

			return comp;
		});

		assertCycle(schemaRef, childContext);
	}

	@Test
	public void testClosureVariableIssue() {

		TestApiContext childContext = rootContext.childContext("CHILD");

		// One cycle with 4 elements:
		// 4 (fake) OpenapiSchemas of OpenapiComponents -> OpenapiParameter -> OpenapiPath -> OpenapiContact ->
		// OpenapiComponents
		OpenapiSchema schemaRef = schemaRef(childContext, OpenapiComponents.T, c -> {
			// This ref is created during the creation of the first schema but its java instance will be referenced
			// again
			// during the creation of child schemas.
			OpenapiSchema closureRef = schemaRef(c, OpenapiDiscriminator.T);

			OpenapiSchema ref = schemaRef(c, OpenapiParameter.T, c2 -> {
				OpenapiSchema ref2 = schemaRef(c2, OpenapiPath.T, c3 -> {
					OpenapiSchema ref3 = schemaRef(c3, OpenapiContact.T, c4 -> {

						OpenapiSchema comp4 = OpenapiSchema.T.create();

						// java-reference from further up the component-reference chain
						// This one must be from the same context
						comp4.setAdditionalProperties(closureRef);

						// assert that they are from the same context
						// TODO: maybe do this outside this factory lambda later
						assertThat(schemaRef(c4, OpenapiDiscriminator.T)).isSameAs(closureRef);

						OpenapiSchema ref4 = schemaRef(c4, OpenapiComponents.T);
						comp4.setItems(ref4);
						return comp4;
					});

					OpenapiSchema comp3 = OpenapiSchema.T.create();
					comp3.setItems(ref3);
					if (c3 == childContext) {
						comp3.setDescription("Changed in child context");
					}

					return comp3;
				});

				OpenapiSchema comp2 = OpenapiSchema.T.create();
				comp2.setItems(ref2);
				return comp2;
			});

			OpenapiSchema comp = OpenapiSchema.T.create();
			comp.setItems(ref);

			return comp;
		});

		assertCycle(schemaRef, childContext);
	}
}
