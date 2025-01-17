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
package com.braintribe.model.processing.smood.manipulation;

import static com.braintribe.model.generic.manipulation.DeleteMode.ignoreReferences;
import static com.braintribe.model.generic.manipulation.util.ManipulationBuilder.compound;
import static com.braintribe.model.generic.manipulation.util.ManipulationBuilder.delete;
import static com.braintribe.model.generic.manipulation.util.ManipulationBuilder.manifestation;
import static com.braintribe.model.processing.manipulation.basic.tools.ManipulationTools.createInverse;

import java.util.List;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.manipulation.CompoundManipulation;
import com.braintribe.model.generic.manipulation.DeleteManipulation;
import com.braintribe.model.generic.manipulation.DeleteMode;
import com.braintribe.model.generic.manipulation.Manipulation;
import com.braintribe.model.generic.manipulation.util.ManipulationBuilder;
import com.braintribe.model.generic.reflection.GmReflectionTools;
import com.braintribe.model.generic.tracking.StandardManipulationCollector;
import com.braintribe.model.processing.detachrefs.DirectDetacher;
import com.braintribe.model.processing.findrefs.meta.OptimizedPolymorphicPropertyReferenceAnalyzer;
import com.braintribe.model.processing.manipulator.api.ManipulatorContext;
import com.braintribe.model.processing.manipulator.api.ReferenceDetacher;
import com.braintribe.model.processing.manipulator.expert.basic.AbstractDeleteManipulator;
import com.braintribe.model.processing.session.api.notifying.NotifyingGmSession;
import com.braintribe.model.processing.session.api.persistence.PersistenceGmSession;
import com.braintribe.model.processing.smood.Smood;
import com.braintribe.model.processing.smood.SmoodManipulatorContext;

/**
 * 
 */
public class SmoodDeleteManipulator extends AbstractDeleteManipulator<StandardManipulationCollector> {

	private final Smood smood;
	private final ReferenceDetacher referenceDetacher;

	public SmoodDeleteManipulator(Smood smood, DeleteMode deleteMode) {
		this.smood = smood;
		this.referenceDetacher = detacher(smood, deleteMode);

		if (deleteMode != DeleteMode.ignoreReferences)
			this.setPropertyReferenceAnalyzer(new OptimizedPolymorphicPropertyReferenceAnalyzer(smood.getSafeModelOracle()));
	}

	@Override
	public void apply(DeleteManipulation manipulation, ManipulatorContext context) {
		if (isIgnoreReferencesButShouldProtectConsistency(manipulation, context)) {
			manipulation = GmReflectionTools.makeShallowCopy(manipulation);
			manipulation.setDeleteMode(DeleteMode.failIfReferenced);
		}

		super.apply(manipulation, context);
	}

	/**
	 * In this case we replace ignoreReferences with failIfReferenced to protect referential integrity. This is similar to how ignoreReferences in
	 * HibernateAccess would still fail if foreign key constraint violation occurred. We do not do this with local requests, as there we assume the
	 * user knows what he's doing.
	 */
	private boolean isIgnoreReferencesButShouldProtectConsistency(DeleteManipulation m, ManipulatorContext c) {
		return m.getDeleteMode() == ignoreReferences && ((SmoodManipulatorContext) c).getCheckRefereesOnDelete(); 
	}

	private static ReferenceDetacher detacher(Smood smood, DeleteMode deleteMode) {
		if (deleteMode == DeleteMode.ignoreReferences)
			return null;
		else
			return new DirectDetacher(smood, smood.getCmdResolver()); // This returns null on GMML initialization - WTF?
	}

	@Override
	protected StandardManipulationCollector onBeforeDelete() {
		return new StandardManipulationCollector();
	}

	@Override
	protected void onBeforeDetach(StandardManipulationCollector smc) {
		NotifyingGmSession session = smood.getGmSession();
		session.listeners().add(smc);

		if (session instanceof PersistenceGmSession) {
			PersistenceGmSession persistenceSession = (PersistenceGmSession) session;
			persistenceSession.suspendHistory();
		}
	}

	@Override
	protected void onAfterDetach(StandardManipulationCollector smc) {
		NotifyingGmSession session = smood.getGmSession();
		session.listeners().remove(smc);

		if (session instanceof PersistenceGmSession) {
			PersistenceGmSession persistenceSession = (PersistenceGmSession) session;
			persistenceSession.resumeHistory();
		}
	}

	@Override
	protected ReferenceDetacher getReferenceDetacher() {
		return referenceDetacher;
	}

	@Override
	protected void deleteActualEntity(GenericEntity entity, DeleteMode deleteMode, StandardManipulationCollector deleteContext) {
		DeleteManipulation dm = createDeleteManipulation(entity, deleteMode, deleteContext);
		smood.getGmSession().noticeManipulation(dm);
		entity.detach();
	}

	private static DeleteManipulation createDeleteManipulation(GenericEntity entity, DeleteMode deleteMode, StandardManipulationCollector context) {
		Manipulation manifestation = manifestation(entity);
		Manipulation reattach = createReattachManipulation(context);

		CompoundManipulation inverse = compound(manifestation, reattach);
		DeleteManipulation delete = delete(entity, deleteMode);

		delete.linkInverse(inverse);

		return delete;
	}

	private static Manipulation createReattachManipulation(StandardManipulationCollector deleteContext) {
		List<Manipulation> manipulations = deleteContext.getManipulations();
		switch (manipulations.size()) {
			case 0:
				return ManipulationBuilder.voidManipulation();
			case 1:
				return manipulations.get(0).getInverseManipulation();
			default:
				CompoundManipulation attachManipulation = compound(manipulations);

				return createInverse(attachManipulation);
		}
	}
}
