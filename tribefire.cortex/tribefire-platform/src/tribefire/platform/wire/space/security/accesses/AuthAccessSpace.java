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
package tribefire.platform.wire.space.security.accesses;

import com.braintribe.logging.Logger;
import com.braintribe.model.access.IncrementalAccess;
import com.braintribe.model.access.collaboration.CollaborativeSmoodAccess;
import com.braintribe.model.cortex.deployment.CortexConfiguration;
import com.braintribe.model.processing.bootstrapping.TribefireRuntime;
import com.braintribe.model.processing.deployment.api.SchrodingerBean;
import com.braintribe.model.processing.tfconstants.TribefireConstants;
import com.braintribe.wire.api.annotation.Import;
import com.braintribe.wire.api.annotation.Managed;
import com.braintribe.wire.api.context.WireContextConfiguration;

import tribefire.platform.impl.security.EnsureAdminUserWorker;
import tribefire.platform.wire.space.SchrodingerBeansSpace;
import tribefire.platform.wire.space.common.EnvironmentSpace;
import tribefire.platform.wire.space.common.MarshallingSpace;
import tribefire.platform.wire.space.cortex.accesses.SchrodingerBeanSystemAccessSpaceBase;
import tribefire.platform.wire.space.cortex.accesses.SystemAccessCommonsSpace;
import tribefire.platform.wire.space.cortex.accesses.TribefireProductModels;
import tribefire.platform.wire.space.cortex.accesses.TribefireProductModels.TribefireProductModel;
import tribefire.platform.wire.space.cortex.services.WorkerSpace;

@Managed
public class AuthAccessSpace extends SchrodingerBeanSystemAccessSpaceBase {

	private static Logger logger = Logger.getLogger(AuthAccessSpace.class);

	private static final String id = TribefireConstants.ACCESS_AUTH;
	private static final String name = TribefireConstants.ACCESS_AUTH_NAME;
	private static final TribefireProductModel model = TribefireProductModels.authAccessModel;
	private static final String modelName = model.modelName;

	// @formatter:off
	@Import	private EnvironmentSpace environment;
	@Import	private MarshallingSpace marshalling;
	@Import	private SystemAccessCommonsSpace systemAccessCommons;
	@Import	private SchrodingerBeansSpace schrodingerBeans;
	@Import	private WorkerSpace worker;
	// @formatter:on

	@Override
	public void onLoaded(WireContextConfiguration configuration) {
		super.onLoaded(configuration);

		String ensureString = TribefireRuntime.getProperty("TRIBEFIRE_AUTH_ADMIN_ENSURE");
		if (ensureString != null && ensureString.equalsIgnoreCase("false")) {
			logger.debug(() -> "Ensuring the admin user is actively deactivated.");
			return;
		}

		EnsureAdminUserWorker ensureAdminWorker = new EnsureAdminUserWorker();
		ensureAdminWorker.setAuthSessionSupplier(sessionProvider());
		ensureAdminWorker.setJsonMarshaller(marshalling.jsonMarshaller());
		ensureAdminWorker.setEnvironmentDenotations(environment.environmentDenotations());

		worker.manager().deploy(ensureAdminWorker);
	}

	// @formatter:off
	@Override public String id() { return id; }
	@Override public String name() { return name; }
	@Override public String modelName() { return modelName; }
	// @formatter:on

	@Override
	@Managed
	public IncrementalAccess access() {
		return accessSchrodingerBean().proxy();
	}

	@Managed
	public SchrodingerBean<IncrementalAccess> accessSchrodingerBean() {
		return schrodingerBeans.newBean("AuthAccess", CortexConfiguration::getAuthenticationAccess, binders.incrementalAccess());
	}

	public CollaborativeSmoodAccess defaultAccess() {
		return systemAccessCommons.collaborativeSmoodAccess(id());
	}

}
