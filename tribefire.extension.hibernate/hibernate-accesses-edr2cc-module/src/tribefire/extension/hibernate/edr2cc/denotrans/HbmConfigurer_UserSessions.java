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
package tribefire.extension.hibernate.edr2cc.denotrans;

import com.braintribe.gm.model.usersession.PersistenceUserSession;
import com.braintribe.model.generic.GenericEntity;

/**
 * @author peter.gazdik
 */
class HbmConfigurer_UserSessions extends AbstractHbmConfigurer {

	public static void run(HibernateAccessEdr2ccEnricher enricher) {
		new HbmConfigurer_UserSessions(enricher).run();
	}

	private HbmConfigurer_UserSessions(HibernateAccessEdr2ccEnricher enricher) {
		super(enricher, "hbm:edr2cc/user-sessions/");
	}

	private void run() {
		mdEditor.onEntityType(PersistenceUserSession.T) //
				.addPropertyMetaData(screamingCamelCaseConversion()) //
				.addMetaData(entityMapping("TF_US_PERSISTENCE_USER_SESSION", PersistenceUserSession.T)) //
				.addPropertyMetaData(GenericEntity.id, stringTypeSpecification()) //
				.addPropertyMetaData(PersistenceUserSession.expiryDate,
						index("IDX_EXPIRY_DATE", propMapping("EXPIRY_DATE", "PersistenceUserSession/expiryDate")));
	}

}
