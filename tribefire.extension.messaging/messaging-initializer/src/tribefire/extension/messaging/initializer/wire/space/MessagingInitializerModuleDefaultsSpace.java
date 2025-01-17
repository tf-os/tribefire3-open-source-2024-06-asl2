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
package tribefire.extension.messaging.initializer.wire.space;

import static com.braintribe.utils.lcd.CollectionTools2.asList;

import com.braintribe.logging.Logger;
import com.braintribe.utils.StringTools;
import com.braintribe.wire.api.annotation.Import;
import com.braintribe.wire.api.annotation.Managed;

import tribefire.cortex.initializer.support.wire.space.AbstractInitializerSpace;
import tribefire.extension.messaging.initializer.wire.contract.ExistingInstancesContract;
import tribefire.extension.messaging.initializer.wire.contract.MessagingInitializerModuleDefaultsContract;
import tribefire.extension.messaging.initializer.wire.contract.RuntimePropertiesContract;
import tribefire.extension.messaging.model.InterceptionTarget;
import tribefire.extension.messaging.model.ResourceBinaryPersistence;
import tribefire.extension.messaging.model.conditions.types.TypeComparison;
import tribefire.extension.messaging.model.conditions.types.TypeOperator;
import tribefire.extension.messaging.model.deployment.event.ProducerEventConfiguration;
import tribefire.extension.messaging.model.deployment.event.rule.ProducerEventRule;
import tribefire.extension.messaging.model.deployment.event.rule.ProducerStandardEventRule;
import tribefire.extension.messaging.model.service.demo.ProduceDemoMessage;
import tribefire.extension.messaging.templates.api.MessagingTemplateContext;
import tribefire.extension.messaging.templates.wire.contract.MessagingTemplatesContract;

@Managed
public class MessagingInitializerModuleDefaultsSpace extends AbstractInitializerSpace implements MessagingInitializerModuleDefaultsContract {

	private static final Logger logger = Logger.getLogger(MessagingInitializerModuleDefaultsSpace.class);

	@Import
	private ExistingInstancesContract existingInstances;

	@Import
	private RuntimePropertiesContract properties;

	@Import
	private MessagingTemplatesContract messagingTemplates;

	@Override
	public void setupDefaultConfiguration() {
		MessagingTemplateContext defaultTemplateContext = defaultTemplateContext();
		logger.debug(() -> "Created messaging template context: \n" + StringTools.asciiBoxMessage(defaultTemplateContext.toString(), -1));

		messagingTemplates.setupMessaging(defaultTemplateContext);

	}

	// -----------------------------------------------------------------------
	// HELPERS
	// -----------------------------------------------------------------------

	@Managed
	private MessagingTemplateContext defaultTemplateContext() {
		//@formatter:off
		MessagingTemplateContext context = MessagingTemplateContext.builder()
				.setContext("$Default")
				.setEntityFactory(super::create)
				.setMessagingModule(existingInstances.module())
				.setLookupFunction(super::lookup)
				.setLookupExternalIdFunction(super::lookupExternalId)
				.setEventConfiguration(producerEventConfiguration())
				.setServiceModelDependency(existingInstances.serviceModel())
			.build();
		//@formatter:on
		return context;
	}

	@Managed
	private ProducerEventConfiguration producerEventConfiguration() {
		ProducerEventConfiguration bean = ProducerEventConfiguration.T.create();
		bean.setInterceptionType(existingInstances.genericEntityType());
		bean.setEventRules(asList(producerEventRule()));

		return bean;
	}

	@Managed
	private ProducerEventRule producerEventRule() {
		ProducerStandardEventRule bean = ProducerStandardEventRule.T.create();

		bean.setEndpointConfiguration(null);
		bean.setFilePersistenceStrategy(ResourceBinaryPersistence.NONE);
		bean.setInterceptionTarget(InterceptionTarget.REQUEST);
		bean.setName("Default Rule");
		bean.setRequestPropertyCondition(null);
		bean.setRequestTypeCondition(typeComparison());
		bean.setRuleEnabled(true);

		return bean;
	}

	@Managed
	private TypeComparison typeComparison() {
		TypeComparison bean = TypeComparison.T.create();
		bean.setOperator(TypeOperator.equal);
		bean.setTypeName(ProduceDemoMessage.T.getTypeName());
		return bean;
	}

}
