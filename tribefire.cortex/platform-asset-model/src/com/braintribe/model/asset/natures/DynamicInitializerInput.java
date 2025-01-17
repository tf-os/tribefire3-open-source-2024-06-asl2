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
package com.braintribe.model.asset.natures;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

/**
 * Dynamic initializer is a mechanism to bring custom smood initializer types into Collaborative Smood Access (CSA).
 * <p>
 * The actual implementation is bound from a module and is identified by that module's name (i.e. the module name is written into the access'
 * config.json). This implementation is then used for every single asset of this nature ({@link DynamicInitializerInput}), meaning the bound expert is
 * called with a file denoting a folder in the input asset as a parameter.
 * <p>
 * <b>IMPORTANT:</b> This means when setting up a project, there must be an entry in the corresponding config.json for every
 * {@link DynamicInitializerInput}, with the correct name of the module that binds the expert. The name of the module is determined by examining the
 * dependencies of the input asset, as this asset is expected to have exactly one direct module dependency - the module that binds the actual
 * initializer implementation.
 * <p>
 * The input for that implementation is given by the content of the "resources" folder of the input asset (which is copied into the corresponding
 * access' data folder).
 * 
 * <p>
 * <b>Example:</b> In this example we will simply prepare a new initializer type which simply creates a new model in cortex based on a text file
 * containing the name, version and a list of model dependencies.
 * 
 * <pre>
 *  model-creation-configuring-module
 *  	// This module binds an initializer, that expects a single file called model-description.txt in it's input folder.
 *  
 *  	ModelCreationConfiguringModuleSpace {
 *  
 *  		public void bindInitializers(InitializerBindingBuilder bindings) {
 *				bindings.bindDynamicInitializerFactory(this::newModelCreatingInitializer);
 *  		}
 *  
 *			private DataInitializer newRemotesInitializer(File inputFolder) {
 *				return ctx -> new ModelCreatingInitializer(ctx, inputFolder).run();
 *			}
 *
 *  		private class ModelCreatingInitializer {
 *  			...
 *  			public ModelCreatingInitializer(PersistenceInitializationContext context, File inputFolder) {
 *  				this.context = context;
 *  				this.inputFolder = inputFolder;
 *  			}
 *  			public void run() {
 *  				// create a new model based on the model-description.txt inside of inputFolder.
 *  				...
 *  			}
 *  		}
 *  	}
 *  	
 *  my-model-configuration // asset of type DynamicInitializerInput
 *  	resources
 *  		model-dependencies.txt >
 *  			model1,model2,model3
 * 		asset.man >
 * 			$nature = !com.braintribe.model.asset.natures.DynamicInitializerInput()
 * 			.accessIds=['cortex']
 *		pom.xml >
 * 			&lt;dependencies>
 *				&lt;dependency>
 *					&lt;groupId>my.extension&lt;/groupId>
 *					&lt;artifactId>model-creation-configuring-module&lt;/artifactId>
 *					&lt;version>1.0&lt;/version>
 *					&lt;classifier>asset&lt;/classifier>
 *					&lt;type>man&lt;/type>
 *					&lt;?tag asset?>
 *				&lt;/dependency>
 *				...
 *			&lt;/dependencies>
 * </pre>
 * 
 * <p>
 * After Jinni is run for a project which contains "my-model-configuration", the cortex storage will contain the following entries
 * 
 * <pre>
 * data
 * 	my.extension_my-model-configuration#1.0
 * 		model.dependencies.txt
 *	config.json >
 *		{"_type": "com.braintribe.model.csa.DynamicInitializer", "_id": "23",
 *   		"moduleId": my.extension:model-creation-configuring-module"
 *  	},
 * </pre>
 */
public interface DynamicInitializerInput extends ConfigurableStoragePriming {

	EntityType<DynamicInitializerInput> T = EntityTypes.T(DynamicInitializerInput.class);

}
