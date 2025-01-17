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

import static com.braintribe.utils.lcd.CollectionTools2.newConcurrentMap;
import static com.braintribe.utils.lcd.CollectionTools2.newList;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import com.braintribe.logging.Logger;
import com.braintribe.model.csa.DynamicInitializer;
import com.braintribe.model.processing.session.api.collaboration.AbstractPersistenceInitializer;
import com.braintribe.model.processing.session.api.collaboration.DataInitializer;
import com.braintribe.model.processing.session.api.collaboration.ManipulationPersistenceException;
import com.braintribe.model.processing.session.api.collaboration.PersistenceInitializationContext;
import com.braintribe.model.processing.session.api.collaboration.PersistenceInitializer;
import com.braintribe.model.smoodstorage.stages.PersistenceStage;
import com.braintribe.model.smoodstorage.stages.StaticStage;
import com.braintribe.utils.FileTools;

import tribefire.descriptor.model.ModuleDescriptor;
import tribefire.module.api.InitializerBindingBuilder;

/**
 * Registry for binding initializers. Every module one instance which contains the initializers and dynamic initializer factories bound from that
 * module.
 * 
 * @author peter.gazdik
 */
/* package */ class ModuleInitializersRegistry implements InitializerBindingBuilder {

	private static final Logger log = Logger.getLogger(ModuleInitializersRegistry.class);

	public final PersistenceStage stage;
	private final ModuleDescriptor moduleDescriptor;
	private final ModuleLoader moduleLoader;

	private final Map<String, ModuleInitializer> accessIdToInitializer = newConcurrentMap();

	/** Initializer for all the accesses this module declares it initializes. */
	private final List<DataInitializer> universalInitializers = newList();

	public ModuleInitializersRegistry(ModuleDescriptor md, ModuleLoader moduleLoader) {
		this.moduleDescriptor = md;
		this.moduleLoader = moduleLoader;
		this.stage = StaticStage.forName(md.getArtifactId());
	}

	// ##########################################################
	// ## . . . . . . . . bind DataInitialiers . . . . . . . . ##
	// ##########################################################

	@Override
	public void bind(DataInitializer initializer) {
		moduleLoader.cortexInitializer.onBindInitializer(moduleDescriptor);
		universalInitializers.add(initializer);
	}

	@Override
	public void bind(String accessId, DataInitializer initializer) {
		moduleLoader.cortexInitializer.onBindInitializer(moduleDescriptor);
		acquireInitializer(accessId).dataInitializers.add(initializer);
	}

	public boolean bindsCortex() {
		if (accessIdToInitializer.containsKey("cortex"))
			return true;

		Set<String> accessIds = moduleDescriptor.getAccessIds();
		if (accessIds == null)
			return false;

		return accessIds.contains("cortex");
	}

	private ModuleInitializer acquireInitializer(String accessId) {
		return accessIdToInitializer.computeIfAbsent(accessId, aId -> new ModuleInitializer());
	}

	/**
	 * Returns a {@link PersistenceInitializer} for given acessId, which consists of all the {@link DataInitializer} relevant for given access. This
	 * is a union of initializers bound generally, without specifying any accessId, and those bound for the specific access only.
	 */
	public PersistenceInitializer getInitializer(String accessId) {
		return accessIdToInitializer.computeIfAbsent(accessId, this::getFallbackInitializer);
	}

	private ModuleInitializer getFallbackInitializer(String accessId) {
		if (universalInitializers.isEmpty())
			log.warn("Access initializers misconfiguration? No initializers bound for module: " + stage.getName() + ", access: " + accessId
					+ " (by the corresponding TribefireModuleContract implementation), but it seems such entry exists in the access' configuration."
					+ " This could happen if the module's asset.man file wasn't in syc with what the module is really binding.");

		return new ModuleInitializer();
	}

	private class ModuleInitializer extends AbstractPersistenceInitializer {

		private final List<DataInitializer> dataInitializers = newList();

		@Override
		public PersistenceStage getPersistenceStage() {
			return stage;
		}

		@Override
		public void initializeData(PersistenceInitializationContext context) throws ManipulationPersistenceException {
			init(context, universalInitializers);
			init(context, dataInitializers);
		}

		protected void init(PersistenceInitializationContext context, List<DataInitializer> dataInitializers) {
			for (DataInitializer di : dataInitializers)
				di.initialize(context);
		}
	}

	// ##########################################################
	// ## . . . . . . . bind DynamicInitialiers . . . . . . . .##
	// ##########################################################

	private Function<File, DataInitializer> initializerFactory;

	@Override
	public void bindDynamicInitializerFactory(Function<File, DataInitializer> initializerFactory) {
		if (this.initializerFactory != null)
			throw illegalStateException("Cannot bind two different dynamic initializer factories in one module.");

		this.initializerFactory = initializerFactory;
	}

	public PersistenceInitializer resolveDynamicInitializer(String accessId, DynamicInitializer di) {
		File file = resolveInputfolder(accessId, di);

		Function<File, DataInitializer> factory = resolveDiFactory(di);

		DataInitializer dataInitializer = factory.apply(file);

		return new DynamicPersitenceInitializer(di, dataInitializer);
	}

	private File resolveInputfolder(String accessId, DynamicInitializer di) {
		File dataFolder = moduleLoader.accessStorageResolver.apply(accessId);
		String stageFolderName = FileTools.replaceIllegalCharactersInFileName(di.getName(), "_");

		return new File(dataFolder, stageFolderName);
	}

	private Function<File, DataInitializer> resolveDiFactory(DynamicInitializer di) {
		if (initializerFactory == null)
			throw illegalStateException("No dynamic initializer factory found in module: " + di.getName());

		return initializerFactory;
	}

	private IllegalStateException illegalStateException(String msg) {
		return new IllegalStateException("Error in module '" + moduleDescriptor.name() + "'. " + msg);
	}

	private static class DynamicPersitenceInitializer extends AbstractPersistenceInitializer {

		private final PersistenceStage stage;
		private final DataInitializer dataInitializer;

		public DynamicPersitenceInitializer(DynamicInitializer di, DataInitializer dataInitializer) {
			this.dataInitializer = dataInitializer;
			this.stage = StaticStage.forName(di.getName());
		}

		@Override
		public PersistenceStage getPersistenceStage() {
			return stage;
		}

		@Override
		public void initializeData(PersistenceInitializationContext context) throws ManipulationPersistenceException {
			dataInitializer.initialize(context);
		}

	}
}
