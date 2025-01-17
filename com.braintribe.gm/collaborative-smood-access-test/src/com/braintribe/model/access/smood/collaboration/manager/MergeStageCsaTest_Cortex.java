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
package com.braintribe.model.access.smood.collaboration.manager;

import org.junit.Test;

import com.braintribe.model.access.collaboration.CollaborativeAccessManager;
import com.braintribe.model.access.collaboration.persistence.BufferedManipulationAppender;
import com.braintribe.model.access.smood.collaboration.manager.model.StagedEntity;
import com.braintribe.model.cortexapi.access.collaboration.MergeCollaborativeStage;
import com.braintribe.model.generic.value.PreliminaryEntityReference;
import com.braintribe.model.meta.GmMetaModel;
import com.braintribe.model.processing.query.tools.PreparedQueries;
import com.braintribe.testing.junit.assertions.assertj.core.api.Assertions;

/**
 * @see MergeCollaborativeStage
 * @see CollaborativeAccessManager
 *
 * @author peter.gazdik
 */
public class MergeStageCsaTest_Cortex extends MergeStageCsaTest_Common {

	/**
	 * Bug: DEVCX-1346
	 * 
	 * Basically we create entities and their IDs are assigned later. So we can actually achieve, that during the merge,
	 * we get entity in one bulk, but it's id assignment in the next bulk. That means that the manipulation uses
	 * {@link PreliminaryEntityReference} in the first bulk, and the corresponding variable associated with this entity
	 * (and this this reference) has to be preserved till next bulk. There was special treatment implemented for this
	 * purpose.
	 * 
	 * Note that after the last bulk these references have to be removed, cause new manipulations might come re-using
	 * the preliminary IDs, but the variable names have to be stored. This case is actually covered by
	 * {@link #mergeSimpleDataMultipleTimes()} above.
	 */
	@Test
	public void mergePreliminaryEntities() {
		// Trunk
		for (int i = 0; i < BufferedManipulationAppender.LIMIT; i++)
			session.create(StagedEntity.T);

		session.commit();

		// Merge
		mergeStage(trunkStageName, oldStageName);

		// Assertions
		assertOldDataFileContains("$0", "$1", "$2", "$99", "$100");
		assertOldDataFileNotContains("$101");

		assertOldOnly_DataOnly();
	}

	@Test
	public void mergeSimpleModel() {
		// Trunk
		GmMetaModel stage1ModelElement = session.create(GmMetaModel.T);
		stage1ModelElement.setName("model1");
		session.commit();

		// Merge
		mergeStage(trunkStageName, oldStageName);

		// Assertions
		assertOldOnly_ModelAndData();
		assertEntityStage(stage1ModelElement, oldStageName);

		// Re-deploy
		redeploy();

		// More assertions
		GmMetaModel model = session.query().select(PreparedQueries.modelByName("model1")).unique();
		assertEntityStage(model, oldStageName);
	}

	/**
	 * There was a bug that the variables map was not purged after merge, so a follow-up manipulation would only be the
	 * CVM (so no lookup of the entity), thus re-deploying would fail as such a man file is invalid.
	 */
	@Test
	public void appendWorksAfterMerge() {
		// Trunk
		StagedEntity entity = session.create(StagedEntity.T);
		session.commit();

		// Merge
		mergeStage(trunkStageName, oldStageName);
	
		// Additional Manipulations
		entity = session.query().entity(entity).require();
		entity.setName("entityX");
		session.commit();

		// Re-deploy
		redeploy();

		// Check the entity can be found
		entity = session.query().select(queryStagedEntityByName("entityX")).unique();
		Assertions.assertThat(entity).as("Entity was not persisted correctly.").isNotNull();
	}

}
