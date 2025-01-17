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
 * Utility class for TF module {@link WireModule wire modules}.
 * 
 * @see StandardTribefireModuleWireModule
 * 
 * @author peter.gazdik
 */
public class TribefireModules {

	/**
	 * Binds the {@link TribefireModuleContract}'s space, assuming the given {@code moduleSpaceClass} resides directly inside a package called
	 * "space".
	 * <p>
	 * This method actually binds given class directly to the {@link TribefireModuleContract}, and also binds all the contracts for the parent package
	 * of given space class via {@link WireContextBuilder#bindContracts(String)}.
	 * <p>
	 * Note that this method allows the space package to lie anywhere, doesn't have to be the standard {@code tribefire.module.wire.space} package as
	 * described in {@link StandardTribefireModuleWireModule}, although that's gonna be the case most of (if not all) the time.
	 * 
	 * @see StandardTribefireModuleWireModule
	 */
	public static void bindModuleContract(WireContextBuilder<?> contextBuilder, Class<? extends TribefireModuleContract> moduleSpaceClass) {
		contextBuilder.bindContracts(getParentPackageName(moduleSpaceClass));

		contextBuilder.bindContract(TribefireModuleContract.class, moduleSpaceClass);
	}

	private static String getParentPackageName(Class<?> moduleSpaceClass) {
		Package pckg = moduleSpaceClass.getPackage();
		if (pckg == null)
			throw new IllegalArgumentException("Module Space class has no package: " + moduleSpaceClass.getName());

		String pckgName = pckg.getName();
		int i = pckgName.lastIndexOf(".");
		if (i < 0)
			throw unexpectedPackageNameException(moduleSpaceClass);

		String hopefullyTheWordSpace = pckgName.substring(i + 1);
		if (!"space".equals(hopefullyTheWordSpace))
			throw unexpectedPackageNameException(moduleSpaceClass);

		return pckgName.substring(0, i);
	}

	private static IllegalArgumentException unexpectedPackageNameException(Class<?> moduleSpaceClass) {
		return new IllegalArgumentException("Invalid Module Space class package for class: '" + moduleSpaceClass.getName()
				+ "' . Expected structure: '${x.y.z...}.{moduleName}.space'");
	}

}
