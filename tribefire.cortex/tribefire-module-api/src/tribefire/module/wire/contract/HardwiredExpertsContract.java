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
package tribefire.module.wire.contract;

import java.util.function.Predicate;

import com.braintribe.model.deployment.HardwiredDeployable;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.session.GmSession;
import com.braintribe.model.meta.data.MetaData;
import com.braintribe.model.meta.selector.MetaDataSelector;
import com.braintribe.model.processing.meta.cmd.CmdResolver;
import com.braintribe.model.processing.meta.cmd.context.experts.SelectorExpert;
import com.braintribe.model.processing.service.api.ServiceProcessor;
import com.braintribe.model.processing.session.api.managed.ModelAccessoryFactory;
import com.braintribe.model.service.api.InternalPushRequest;
import com.braintribe.model.service.api.result.PushResponse;
import com.braintribe.wire.api.space.WireSpace;

import tribefire.module.api.DenotationTransformerRegistry;

/**
 * Contract offering various ways to extend the platform with experts that are not reflected in cortex as {@link HardwiredDeployable}s.
 * <p>
 * <b>All the methods must be called within {@link TribefireModuleContract#bindHardwired()} method.</b>
 * 
 * @see #bindMetaDataSelectorExpert(EntityType, SelectorExpert)
 * 
 * @author peter.gazdik
 */
public interface HardwiredExpertsContract extends WireSpace {

	/**
	 * Binds a custom {@link SelectorExpert} for given {@link MetaDataSelector} type.
	 * <p>
	 * Every registered expert is passed to a {@link CmdResolver MD resolver} which is managed by the platform itself. This includes all the
	 * {@link GmSession sessions} and {@link ModelAccessoryFactory model accessory factories} used internally and/or offered via contracts to modules,
	 * as well as custom resolvers built via {@link ModelApiContract#newCmdResolver}.
	 * <p>
	 * For this reason we do not recommend instantiating your own {@link CmdResolver} other than, as mentioned, via {@link ModelApiContract}.
	 * 
	 * @see MetaData
	 * @see MetaDataSelector
	 * @see CmdResolver
	 */
	<S extends MetaDataSelector> void bindMetaDataSelectorExpert(EntityType<? extends S> entityType, SelectorExpert<S> expert);

	void addPushHandler(ServiceProcessor<InternalPushRequest, PushResponse> handler);

	/**
	 * Extends given perspective with all given domains.
	 * <p>
	 * If no such perspective exists yet, it will be created.
	 * 
	 * @see ModelAccessoryFactory#forPerspective(String)
	 */
	void extendModelPerspective(String perspective, String... metaDataDomains); // additive

	/**
	 * Extends given domain with all the meta-data that pass given test (i.e. where given predicate evaluates to <tt>true</tt>).
	 * <p>
	 * If no such domain exists yet, it will be created.
	 * 
	 * @see ModelAccessoryFactory#forPerspective(String)
	 */
	void extendMetaDataDomain(String domain, Predicate<MetaData> test);

	DenotationTransformerRegistry denotationTransformationRegistry();

}
