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
package tribefire.platform.impl.initializer;

import java.io.File;
import java.io.InputStream;

import com.braintribe.cfg.Required;
import com.braintribe.codec.marshaller.api.DecodingLenience;
import com.braintribe.codec.marshaller.api.GmDeserializationOptions;
import com.braintribe.codec.marshaller.yaml.YamlMarshaller;
import com.braintribe.logging.Logger;
import com.braintribe.model.processing.session.api.collaboration.ManipulationPersistenceException;
import com.braintribe.model.processing.session.api.collaboration.PersistenceInitializationContext;
import com.braintribe.model.processing.session.api.collaboration.SimplePersistenceInitializer;
import com.braintribe.model.processing.session.api.managed.ManagedGmSession;
import com.braintribe.utils.FileTools;

import tribefire.descriptor.model.UxModuleDescriptor;
import tribefire.descriptor.model.UxModulesDescriptor;
import tribefire.extension.js.model.deployment.UxModule;

public class UxModulesCortexInitializer extends SimplePersistenceInitializer {

	private final static Logger logger = Logger.getLogger(UxModulesCortexInitializer.class);

	private File modulesFolder;

	@Required
	public void setModulesFolder(File modulesFolder) {
		this.modulesFolder = modulesFolder;
	}

	@Override
	public void initializeData(PersistenceInitializationContext context) throws ManipulationPersistenceException {
		ManagedGmSession session = context.getSession();

		UxModulesDescriptor modulesDescriptor = readUxModuleDescriptors(modulesFolder);
		if (modulesDescriptor == null)
			return;

		for (UxModuleDescriptor md : modulesDescriptor.getUxModules()) {
			UxModule uxModule = session.create(UxModule.T);

			uxModule.setName(md.getArtifactId() + "-" + md.getVersion());

			uxModule.setGlobalId(UxModule.GLOBAL_ID_PREFIX + md.getGroupId() + ":" + md.getArtifactId());
			uxModule.setPath("js-libraries/" + md.getGroupId() + "." + md.getArtifactId() + "-" + md.getVersion() + "/index.js");

			// TODO add support for dependencies

			logger.info("Initialized UxModule " + md.getGroupId() + ":" + md.getArtifactId() + "#" + md.getVersion());
		}
	}

	private UxModulesDescriptor readUxModuleDescriptors(File modulesFolder) {
		File file = new File(modulesFolder, "ux-modules.yaml");
		if (!file.exists())
			return null;

		return FileTools.read(file).fromInputStream(this::unmarshallFromYaml);
	}

	private <T> T unmarshallFromYaml(InputStream in) {
		DecodingLenience marshallerLenience = new DecodingLenience().propertyLenient(true);

		GmDeserializationOptions moduleDescriptorYamlOptions = GmDeserializationOptions.deriveDefaults() //
				.setInferredRootType(UxModulesDescriptor.T) //
				.setDecodingLenience(marshallerLenience) //
				.build();

		return (T) new YamlMarshaller().unmarshall(in, moduleDescriptorYamlOptions);
	}

}
