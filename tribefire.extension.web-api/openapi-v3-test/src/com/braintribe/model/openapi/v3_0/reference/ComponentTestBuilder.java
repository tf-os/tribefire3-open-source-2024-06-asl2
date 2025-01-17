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

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.openapi.v3_0.OpenapiComponents;
import com.braintribe.model.openapi.v3_0.OpenapiSchema;
import com.braintribe.model.openapi.v3_0.reference.utils.AbstractComponentsTest;
import com.braintribe.model.openapi.v3_0.reference.utils.TestApiContext;

public class ComponentTestBuilder { // TODO: extend from common superclass
	private static class SchemaNode {
		final EntityType<?> schemaType;
		final SchemaNode parentNode;
		final Map<TestApiContext, String> changesForContexts = new HashMap<>();

		boolean alreadyExisting;

		SchemaNode itemNode;
		SchemaNode addPropNode;

		public SchemaNode(EntityType<?> schemaType, SchemaNode parentNode) {
			this.schemaType = schemaType;
			this.parentNode = parentNode;
		}

		public OpenapiSchema buildRef(TestApiContext context) {

			return context.schema(schemaType).ensure(c -> {
				if (alreadyExisting) {
					throw new IllegalStateException("Component was expected to already exist but factory was called");
				}

				OpenapiSchema schemaComponent = OpenapiSchema.T.create();
				schemaComponent.setDescription(schemaType.getTypeSignature());

				if (itemNode != null) {
					OpenapiSchema itemNodeRef = itemNode.buildRef(c);
					schemaComponent.setItems(itemNodeRef);
				}

				if (addPropNode != null) {
					OpenapiSchema addPropRef = addPropNode.buildRef(c);
					schemaComponent.setAdditionalProperties(addPropRef);
				}

				if (changesForContexts.containsKey(c)) {
					String change = changesForContexts.get(c);
					schemaComponent.setDefault("Changed for context " + change);
				}

				return schemaComponent;
			}).getRef();
		}

	}

	private final SchemaNode currentNode;
	private final AbstractComponentsTest test;

	private ComponentTestBuilder(SchemaNode rootNode, AbstractComponentsTest test) {
		this.currentNode = rootNode;
		this.test = test;
	}

	ComponentTestBuilder(AbstractComponentsTest test) {
		this(new SchemaNode(OpenapiComponents.T, null), test);
	}

	public ComponentTestBuilder addAlreadyExisting(EntityType<?> schemaType) {
		ComponentTestBuilder added = add(schemaType);
		added.currentNode.alreadyExisting = true;

		return added;
	}

	public ComponentTestBuilder add(EntityType<?> schemaType) {
		SchemaNode childNode = new SchemaNode(schemaType, currentNode);

		currentNode.itemNode = childNode;
		return new ComponentTestBuilder(childNode, test);
	}

	public ComponentTestBuilder addBranch(EntityType<?> schemaType) {
		SchemaNode childNode = new SchemaNode(schemaType, currentNode);

		currentNode.addPropNode = childNode;
		return new ComponentTestBuilder(childNode, test);
	}

	public ComponentTestBuilder changeForContexts(String change, TestApiContext... contexts) {
		Stream.of(contexts).forEach(c -> currentNode.changesForContexts.put(c, change));
		return this;
	}

	public ComponentTestBuilder changeForContext(TestApiContext context) {
		currentNode.changesForContexts.put(context, context.getKeySuffix());

		return this;
	}

	public OpenapiSchema buildRef(TestApiContext context) {
		return currentNode.buildRef(context);
	}

}
