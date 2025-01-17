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
package tribefire.cortex.module.loading;

import static com.braintribe.utils.lcd.CollectionTools2.newMap;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

import com.braintribe.cfg.Required;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.meta.data.MetaData;
import com.braintribe.model.meta.selector.MetaDataSelector;
import com.braintribe.model.processing.accessory.impl.MdPerspectiveRegistry;
import com.braintribe.model.processing.meta.cmd.context.experts.SelectorExpert;
import com.braintribe.model.processing.service.api.ServiceProcessor;
import com.braintribe.model.service.api.InternalPushRequest;
import com.braintribe.model.service.api.result.PushResponse;

import tribefire.module.api.DenotationTransformerRegistry;
import tribefire.module.wire.contract.HardwiredExpertsContract;

/**
 * @author peter.gazdik
 */
public class PlatformHardwiredExpertsRegistry implements HardwiredExpertsContract {

	// Set by ModuleLoader
	public ModulesCortexInitializer cortexInitializer;

	public Map<EntityType<? extends MetaDataSelector>, SelectorExpert<?>> mdSelectorExperts;

	private Consumer<ServiceProcessor<InternalPushRequest, PushResponse>> pushHandlerAdder;

	private MdPerspectiveRegistry mdPerspectiveRegistry;

	private DenotationTransformerRegistry transformerRegistry;

	@Required
	public void setDenotationTransformerRegistry(DenotationTransformerRegistry transformerRegistry) {
		this.transformerRegistry = transformerRegistry;
	}

	@Required
	public void setPushHandlerAdder(Consumer<ServiceProcessor<InternalPushRequest, PushResponse>> pushHandlerAdder) {
		this.pushHandlerAdder = pushHandlerAdder;
	}

	@Required
	public void setMdPerspectiveRegistry(MdPerspectiveRegistry mdPerspectiveRegistry) {
		this.mdPerspectiveRegistry = mdPerspectiveRegistry;
	}

	// ###########################################################
	// ## . . . . . . . . . Hardwired binding . . . . . . . . . ##
	// ###########################################################

	@Override
	public DenotationTransformerRegistry denotationTransformationRegistry() {
		return transformerRegistry;
	}

	@Override
	public <S extends MetaDataSelector> void bindMetaDataSelectorExpert(EntityType<? extends S> selectorType, SelectorExpert<S> expert) {
		cortexInitializer.ensureInCortex(selectorType);

		if (mdSelectorExperts == null)
			mdSelectorExperts = newMap();

		mdSelectorExperts.put(selectorType, expert);
	}

	@Override
	public void addPushHandler(ServiceProcessor<InternalPushRequest, PushResponse> handler) {
		this.pushHandlerAdder.accept(handler);
	}

	@Override
	public void extendModelPerspective(String perspective, String... metaDataDomains) {
		mdPerspectiveRegistry.extendModelPerspective(perspective, metaDataDomains);
	}

	@Override
	public void extendMetaDataDomain(String domain, Predicate<MetaData> test) {
		mdPerspectiveRegistry.extendMdDomain(domain, test);
	}

}
