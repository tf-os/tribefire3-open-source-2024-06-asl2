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
package com.braintribe.model.processing.manipulation.basic.oracle;

import static com.braintribe.utils.lcd.CollectionTools2.asMap;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import java.util.Map.Entry;

import org.junit.Test;

import com.braintribe.model.generic.commons.EntRefHashingComparator;
import com.braintribe.model.generic.manipulation.AtomicManipulation;
import com.braintribe.model.generic.manipulation.ChangeValueManipulation;
import com.braintribe.model.generic.manipulation.EntityProperty;
import com.braintribe.model.generic.value.EntityReference;
import com.braintribe.model.generic.value.PersistentEntityReference;
import com.braintribe.model.processing.manipulation.basic.oracle.base.ManipulationToolsTestBase;
import com.braintribe.model.processing.manipulation.testdata.manipulation.TestEntity;

/**
 * @author peter.gazdik
 */
public class InternalReferenceResolverTests extends ManipulationToolsTestBase {

	private Map<EntityReference, PersistentEntityReference> result;

	private TestEntity entity;
	private EntityReference originalRef;

	@Test
	public void noEffectOnEmptyStack() throws Exception {
		manipulation = VOID_MANIPULATION;
		inducedManipulation = VOID_MANIPULATION;

		assertResolvedToEmpty();
	}

	@Test
	public void onlyPersistedEntities_NoEffect() throws Exception {
		initPersistedEntity();

		record(() -> {
			entity.setName("hell");
		});

		assertResolvedToEmpty();
	}

	@Test
	public void persisted_IdChange_Error() throws Exception {
		initPersistedEntity();

		record(() -> {
			entity.setId(2);
		});

		assertResolvesRefs();
	}

	@Test
	public void idPartitionInduced() throws Exception {
		record(() -> {
			initEntity(null, null);
		});

		recordInduced(() -> {
			entity.setId(1l);
			entity.setPartition("partition");
		});

		assertResolvesRefs();
	}

	@Test
	public void idExplicit_PartitionInduced() throws Exception {
		record(() -> {
			initEntity(1l, null);
		});

		recordInduced(() -> {
			entity.setPartition("partition");
		});

		assertResolvesRefs();
	}

	@Test
	public void partitionExplicit_IdInduced() throws Exception {
		record(() -> {
			initEntity(null, "partition");
		});

		recordInduced(() -> {
			entity.setId(1l);
		});

		assertResolvesRefs();
	}

	private void initPersistedEntity() {
		initEntity(1, "part");
	}

	private void initEntity(Object id, String partition) {
		entity = newTestEntity(id, partition);
		originalRef = entity.reference();
	}

	// #############################################
	// ## . . . . . . . . Helpers . . . . . . . . ##
	// #############################################

	protected void endRegular_BeginInduced() {
		this.session.getTransaction().getManipulationsDone().add(VOID_MANIPULATION);
	}

	/** Records manipulations as well as the original and changed entity references. */
	protected void record(Runnable r) {
		manipulation = md.track(s -> r.run(), session);
	}

	protected void recordInduced(Runnable r) {
		boolean mustPurgePartition = entity.getPartition() == null;

		inducedManipulation = md.track(s -> r.run(), session);

		if (mustPurgePartition)
			inducedManipulation.stream().forEach(this::purgePartition);
	}

	private void purgePartition(AtomicManipulation am) {
		ChangeValueManipulation cvm = (ChangeValueManipulation) am;
		EntityProperty owner = (EntityProperty) cvm.getOwner();

		owner.getReference().setRefPartition(null);
	}

	private TestEntity newTestEntity(Object id, String partition) {
		TestEntity entity = session.create(TestEntity.T);
		if (id != null)
			entity.setId(id);
		if (partition != null)
			entity.setPartition(partition);

		return entity;
	}

	private void assertResolvedToEmpty() {
		resolve();
		assertThat(result).isEmpty();
	}

	private void assertResolvesRefs() {
		resolve();
		assertMaps();
	}

	private void resolve() {
		result = InternalReferenceResolver.resolve(manipulation, inducedManipulation);
	}

	private void assertMaps() {
		Map<EntityReference, EntityReference> expectedMap = asMap(originalRef, entity.reference());

		for (Entry<EntityReference, EntityReference> entry : expectedMap.entrySet()) {
			EntityReference originalR = entry.getKey();
			EntityReference newR = entry.getValue();

			PersistentEntityReference resultNewR = result.get(originalR);

			assertThat(resultNewR).as("No reference found for: " + originalR).isNotNull();
			assertThat(EntRefHashingComparator.INSTANCE.compare(newR, resultNewR)).isTrue();
		}
	}
}
