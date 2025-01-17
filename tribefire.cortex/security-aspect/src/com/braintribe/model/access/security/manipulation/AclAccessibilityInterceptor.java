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
package com.braintribe.model.access.security.manipulation;

import static com.braintribe.model.acl.AclPermission.GRANT;
import static com.braintribe.utils.lcd.CollectionTools2.newSet;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.braintribe.model.accessapi.ManipulationRequest;
import com.braintribe.model.accessapi.ManipulationResponse;
import com.braintribe.model.acl.Acl;
import com.braintribe.model.acl.AclEntry;
import com.braintribe.model.acl.AclOperation;
import com.braintribe.model.generic.manipulation.AtomicManipulation;
import com.braintribe.model.generic.manipulation.Manipulation;
import com.braintribe.model.generic.manipulation.Owner;
import com.braintribe.model.generic.manipulation.PropertyManipulation;
import com.braintribe.model.generic.value.EntityReference;
import com.braintribe.model.processing.aop.api.context.AroundContext;
import com.braintribe.model.processing.aop.api.interceptor.AroundInterceptor;
import com.braintribe.model.processing.aop.api.interceptor.InterceptionException;
import com.braintribe.model.processing.manipulation.api.ManipulationOracle;
import com.braintribe.model.processing.manipulation.basic.oracle.BasicManipulationOracle;
import com.braintribe.model.processing.session.api.persistence.PersistenceGmSession;
import com.braintribe.model.processing.session.impl.persistence.PersistenceSessionReferenceResolver;

/**
 * This interceptor is responsible for keeping {@link Acl#getAccessibility()} up to date, when changes are performed on
 * {@link Acl} or {@link AclEntry}
 * 
 * @author peter.gazdik
 */
public class AclAccessibilityInterceptor implements AroundInterceptor<ManipulationRequest, ManipulationResponse> {

	@Override
	public ManipulationResponse run(AroundContext<ManipulationRequest, ManipulationResponse> context) throws InterceptionException {
		ManipulationResponse response = context.proceed();

		AclAccessibilityKeeper keeper = new AclAccessibilityKeeper(context, response);
		keeper.updateAccessibility();

		return response;
	}

	private static class AclAccessibilityKeeper {

		private final Manipulation manipulation;
		private final PersistenceGmSession session;
		private final ManipulationOracle manipulationOracle;

		private List<EntityReference> touchedAclRefs;
		private List<Acl> acls;

		public AclAccessibilityKeeper(AroundContext<ManipulationRequest, ManipulationResponse> context, ManipulationResponse response) {
			manipulation = context.getRequest().getManipulation();
			session = context.getSystemSession();

			manipulationOracle = manipulationOracle(response);
		}

		private ManipulationOracle manipulationOracle(ManipulationResponse response) {
			BasicManipulationOracle result = new BasicManipulationOracle(manipulation, new PersistenceSessionReferenceResolver(session));
			result.setInducedManipulation(response.getInducedManipulation());
			return result;
		}

		public void updateAccessibility() {
			collectTouchAclReferences();
			resolveAclRefs();
			updateAclAccessibilities();
		}

		private void collectTouchAclReferences() {
			touchedAclRefs = manipulation.stream() //
					.map(this::extractRelevantAclEntityIfPossible) //
					.filter(ref -> ref != null) //
					.collect(Collectors.toList());
		}

		/**
		 * We only care about manipulations touching the {@link Acl#getEntries() entries} property.
		 */
		private EntityReference extractRelevantAclEntityIfPossible(AtomicManipulation am) {
			if (!(am instanceof PropertyManipulation))
				return null;

			Owner owner = ((PropertyManipulation) am).getOwner();

			EntityReference ref = (EntityReference) owner.ownerEntity();
			if (!Acl.T.getTypeSignature().equals(ref.getTypeSignature()))
				return null;

			if (!"entries".equals(owner.getPropertyName()))
				return null;

			return ref;
		}

		private void resolveAclRefs() {
			manipulationOracle.resolveAll(touchedAclRefs);
			acls = manipulationOracle.getAllResolved(touchedAclRefs);
		}

		private void updateAclAccessibilities() {
			if (acls.isEmpty())
				return; // to prevent commit

			for (Acl acl : acls)
				acl.setAccessibility(accessibilityFor(acl));

			session.commit();
		}

		private Set<String> accessibilityFor(Acl acl) {
			Set<String> result = newSet();

			for (AclEntry aclEntry : acl.getEntries())
				if (AclOperation.READ.name().equals(aclEntry.operation()))
					if (aclEntry.getPermission() == GRANT)
						result.add(aclEntry.getRole());
					else
						result.add("!" + aclEntry.getRole());

			return result;
		}

	}

}
