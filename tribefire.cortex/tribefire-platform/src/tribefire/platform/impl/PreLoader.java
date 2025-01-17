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
package tribefire.platform.impl;

import java.io.File;

import com.braintribe.logging.Logger;
import com.braintribe.model.generic.GMF;
import com.braintribe.model.processing.cortex.priming.CortexModelsPersistenceInitializer;
import com.braintribe.wire.api.context.WireContext;

import tribefire.platform.wire.space.system.PreLoadingSpace;

/**
 * This is used to optimize startup by executing certain necessary tasks in a parallel threads.
 * <p>
 * Note that this is just an optimization and the system must work perfectly fine even if this class was removed.
 * <p>
 * The point is to start with certain longer-lasting tasks sooner, in a parallel thread, while the main thread is
 * loading the portions of the application before these tasks.
 * <p>
 * To maximize utility, some tasks are triggered even before the application's {@link WireContext} is initialized, by
 * {@link #startStaticPreLoading()};
 * <p>
 * The task(s) that require some wiring are then triggered from {@link PreLoadingSpace} via
 * 
 * @author peter.gazdik
 */
public class PreLoader {

	private static final Logger log = Logger.getLogger(PreLoader.class);

	// ################################################
	// ## . . . . . . static pre-loading . . . . . . ##
	// ################################################

	public static void startStaticPreLoading() {
		startThread(PreLoader::staticPreLoading, "models-pre-loader");
	}

	private static void staticPreLoading() {
		log.info("Pre-loading started [static]");

		preLoadModels();
	}

	// To make profiling easier; This is a stand-alone task that needs to be done anyway at some point.
	private static void preLoadModels() {
		long start = System.currentTimeMillis();
		int models = GMF.getTypeReflection().getPackagedModels().size();
		log.info("Pre-loading " + models + " model declarations took " + (System.currentTimeMillis() - start) + " ms");
	}

	// ################################################
	// ## . . . . . . wired pre-loading . . . . . . .##
	// ################################################

	private File cortexStorageBase;

	public void setCortexStorageBase(File cortexStorageBase) {
		this.cortexStorageBase = cortexStorageBase;
	}

	public void startWiredPreLoading() {
		startThread(this::wiredPreLoading, "cortex-models-pre-loader");
	}

	private void wiredPreLoading() {
		log.info("Pre-loading started [wired]");

		preLoadCortexGmModels();
	}

	private void preLoadCortexGmModels() {
		long start = System.currentTimeMillis();
		CortexModelsPersistenceInitializer.preLoadGmModels(cortexStorageBase);
		log.info("Pre-loading cortex meta models took " + (System.currentTimeMillis() - start) + " ms");
	}

	private static void startThread(Runnable r, String name) {
		Thread t = Thread.ofVirtual().unstarted(r);
		t.setName(name);
		t.setDaemon(true);
		t.start();
	}

}
