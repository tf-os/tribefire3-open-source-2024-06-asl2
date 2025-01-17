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
package tribefire.extension.messaging.model.deployment.event.rule;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.annotation.Abstract;
import com.braintribe.model.generic.annotation.Initializer;
import com.braintribe.model.generic.annotation.SelectiveInformation;
import com.braintribe.model.generic.annotation.meta.Description;
import com.braintribe.model.generic.annotation.meta.Mandatory;
import com.braintribe.model.generic.annotation.meta.Name;
import com.braintribe.model.generic.annotation.meta.Priority;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.service.api.ServiceRequest;

import tribefire.extension.messaging.model.ResourceBinaryPersistence;
import tribefire.extension.messaging.model.InterceptionTarget;
import tribefire.extension.messaging.model.comparison.TypesProperties;
import tribefire.extension.messaging.model.conditions.properties.PropertyCondition;
import tribefire.extension.messaging.model.conditions.types.TypeCondition;

@Abstract
@SelectiveInformation("Producer Event Rule: ${name} - ${ruleEnabled}")
public interface ProducerEventRule extends EventRule {
	EntityType<ProducerEventRule> T = EntityTypes.T(ProducerEventRule.class);

	String requestTypeCondition = "requestTypeCondition";
	String requestPropertyCondition = "requestPropertyCondition";
	String filePersistenceStrategy = "filePersistenceStrategy";
	String interceptionTarget = "interceptionTarget";
	String fieldsToInclude = "fieldsToInclude";
	String requiresUserInfo = "requiresUserInfo";

	@Name("Fields to include")
	@Description("Fields to be extracted from entry to be compared (Diff)/sent (Other) in the message")
	List<TypesProperties> getFieldsToInclude();
	void setFieldsToInclude(List<TypesProperties> fieldsToInclude);

	@Name("Request Type Condition")
	@Description("The condition if an event should be sent for a particular request type.")
	@Priority(1.8d)
	TypeCondition getRequestTypeCondition();
	void setRequestTypeCondition(TypeCondition requestTypeCondition);

	@Name("Request Property Condition")
	@Description("The condition if an event should be sent for a particular request type.")
	@Priority(1.6d)
	PropertyCondition getRequestPropertyCondition();
	void setRequestPropertyCondition(PropertyCondition requestPropertyCondition);

	@Name("File persistence strategy")
	@Description("File persistence strategy to be used for current endpoint")
	@Mandatory
	@Initializer("enum(tribefire.extension.messaging.model.ResourceBinaryPersistence,NONE)")
	@Priority(1.7d)
    ResourceBinaryPersistence getFilePersistenceStrategy();
	void setFilePersistenceStrategy(ResourceBinaryPersistence filePersistenceStrategy);

	@Name("Interception Target")
	@Description("Target of interception request/response or both")
	@Mandatory
	@Initializer("enum(tribefire.extension.messaging.model.InterceptionTarget,REQUEST)")
	@Priority(1.9d)
	InterceptionTarget getInterceptionTarget();
	void setInterceptionTarget(InterceptionTarget interceptionTarget);

	@Name("Requires user info")
	@Description("Defines whether user info needs to be attached to messages")
	@Initializer("true")
	Boolean getRequiresUserInfo();
	void setRequiresUserInfo(Boolean requiresUserInfo);

	default boolean requiresDiff() {
		return this.getInterceptionTarget() == InterceptionTarget.DIFF;
	}

	default boolean appliesTo(ServiceRequest request) {
		boolean type = Optional.ofNullable(this.getRequestTypeCondition()).map(r -> r.matches(request)).orElse(true);
		boolean prop = Optional.ofNullable(this.getRequestPropertyCondition()).map(a -> a.matches(request)).orElse(true);

		return type && prop;
	}

	default boolean requiresResponse() {
		return requiresTarget(InterceptionTarget.RESPONSE);
	}

	default boolean requiresRequest() {
		return requiresTarget(InterceptionTarget.REQUEST);
	}

	default boolean requiresTarget(final InterceptionTarget target) {
		//@formatter:off
		return Optional.ofNullable(this.getInterceptionTarget())
				       .map(t -> Set.of(target, InterceptionTarget.BOTH).contains(t))
				       .orElse(false);
		//@formatter:on
	}

	default Set<String> getPropertiesToIncludeByType(GenericEntity entity){
		String ts = entity.entityType().getTypeSignature();
		//@formatter:off
		return this.getFieldsToInclude().stream()
				       .filter(e->e.getEntityType().getTypeSignature().equals(ts))
				       .findFirst()
				       .map(TypesProperties::getProperties)
				       .orElseGet(HashSet::new);
		//@formatter:on
	}

	default boolean requiresUserInfo() {
		return this.getRequiresUserInfo();
	}
}
