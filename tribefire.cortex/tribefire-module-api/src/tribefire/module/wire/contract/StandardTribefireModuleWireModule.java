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

import com.braintribe.wire.api.context.WireContextBuilder;
import com.braintribe.wire.api.module.WireModule;

/**
 * Convenience interface for implementing tribefire module's {@link WireModule} simply by specifying the module's
 * {@link TribefireModuleContract} implementation.
 * <p>
 * Every TF module is loaded via it's WireModule implementation, which is a class that has the same name in every
 * module, namely {@code tribefire.module.wire.TribefireModuleWireModule}. If the standard structure of the wiring code
 * for tribefire modules is used, we just need to specify the module space class, i.e. module's implementation of
 * {@link TribefireModuleContract}.
 * 
 * <h2>Standard wiring code structure</h2>
 * 
 * <pre>
 * [src]
 *   [tribefire]
 *     [module]
 *       [wire]
 *         [moduleName]
 *         	 CustomWireModule.java
 *           [contract] (optional)
 *           	SomeCustomContract.java	(optional)
 *           [space]
 *             ModuleNameSpace.java	(TribefireModuleContract)
 *             SomeCustomSpace.java (optional)
 * </pre>
 * 
 * In either case, there are two different relationships to bind - the API's {@link TribefireModuleContract} has to be
 * bound to {@code ModuleNameSpace} and module's contracts are bound to the corresponding spaces by the name convention,
 * but still have to say in our module declaration that that's what we want.
 * <p>
 * In other words, if our module's {@link WireModule} uses this as it's super-interface, it only has to provide the
 * {@code ModuleNameSpace} class and from that both binding use-cases will be taken care of.
 * 
 * @author peter.gazdik
 */
public interface StandardTribefireModuleWireModule extends WireModule {

	@Override
	default void configureContext(WireContextBuilder<?> contextBuilder) {
		TribefireModules.bindModuleContract(contextBuilder, moduleSpaceClass());
	}

	Class<? extends TribefireModuleContract> moduleSpaceClass();

}
