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

import java.util.Objects;

import com.braintribe.cfg.Required;
import com.braintribe.logging.Logger;
import com.braintribe.model.deployment.DeploymentStatus;
import com.braintribe.model.deployment.HardwiredDeployable;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.CloningContext;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.StandardCloningContext;
import com.braintribe.model.generic.session.exception.GmSessionException;
import com.braintribe.model.generic.session.exception.GmSessionRuntimeException;
import com.braintribe.model.meta.GmMetaModel;
import com.braintribe.model.processing.query.tools.PreparedQueries;
import com.braintribe.model.processing.session.api.collaboration.ManipulationPersistenceException;
import com.braintribe.model.processing.session.api.collaboration.PersistenceInitializationContext;
import com.braintribe.model.processing.session.api.collaboration.SimplePersistenceInitializer;
import com.braintribe.model.processing.session.api.managed.ManagedGmSession;
import com.braintribe.model.query.SelectQuery;

import tribefire.platform.impl.deployment.HardwiredDenotationBinding;

public class HardwiredDeployableInitializer extends SimplePersistenceInitializer {

	protected static Logger logger = Logger.getLogger(HardwiredDeployableInitializer.class);


	private HardwiredDenotationBinding hardwiredDenotationBinding;
	
	@Required
	public void setHardwiredDenotationBinding(HardwiredDenotationBinding hardwiredDenotationBinding) {
		this.hardwiredDenotationBinding = hardwiredDenotationBinding;
	}

	@Override
	public void initializeData(PersistenceInitializationContext context) throws ManipulationPersistenceException {
		logger.info("Start synchronization of hardwired deployables.");

		CloningContext cc = new ModelResolvingCloningContext(context.getSession());

		hardwiredDenotationBinding.deployableStream() //
				.forEach(hd -> importHardwiredDeployable(hd, cc));
	}

	private void importHardwiredDeployable(HardwiredDeployable hd, CloningContext cc) {
		HardwiredDeployable clonedHd = hd.clone(cc);

		clonedHd.setAutoDeploy(true);
		clonedHd.setDeploymentStatus(DeploymentStatus.deployed);

		if (hd.getGlobalId() == null)
			clonedHd.setGlobalId("hardwired:" + hd.getExternalId());
	}

	public static class ModelResolvingCloningContext extends StandardCloningContext {
		private final ManagedGmSession session;

		public ModelResolvingCloningContext(ManagedGmSession session) {
			this.session = session;
		}

		@Override
		public <T> T getAssociated(GenericEntity entity) {
			T result = super.getAssociated(entity);

			if (result == null && entity instanceof GmMetaModel) {
				result = (T) findAssociatedModelInSession((GmMetaModel) entity);
				registerAsVisited(entity, result);
			}

			return result;
		}

		private GmMetaModel findAssociatedModelInSession(GmMetaModel metaModel) {
			String metaModelName = metaModel.getName();
			Objects.requireNonNull(metaModelName, "metaModel name");

			SelectQuery query = PreparedQueries.modelByName(metaModelName);

			GmMetaModel model = null;
			try {
				model = session.query().select(query).unique();

			} catch (GmSessionException e) {
				throw new GmSessionRuntimeException(e.getMessage(), e);
			}

			if (model == null)
				throw new GmSessionRuntimeException("Meta model not found: " + metaModelName);

			return model;
		}

		@Override
		public GenericEntity supplyRawClone(EntityType<? extends GenericEntity> entityType, GenericEntity instanceToBeCloned) {
			return session.create(entityType);
		}
	}

}
