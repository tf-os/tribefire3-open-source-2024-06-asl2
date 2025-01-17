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
package com.braintribe.model.processing.check.jdbc;

import java.util.List;
import java.util.function.Supplier;

import com.braintribe.cfg.Configurable;
import com.braintribe.logging.Logger;
import com.braintribe.model.check.service.CheckRequest;
import com.braintribe.model.check.service.CheckResult;
import com.braintribe.model.deployment.Deployable;
import com.braintribe.model.deployment.DeploymentStatus;
import com.braintribe.model.deployment.database.pool.DatabaseConnectionPool;
import com.braintribe.model.processing.check.api.CheckProcessor;
import com.braintribe.model.processing.query.fluent.EntityQueryBuilder;
import com.braintribe.model.processing.service.api.ServiceRequestContext;
import com.braintribe.model.processing.session.api.persistence.PersistenceGmSession;
import com.braintribe.model.query.EntityQuery;

public class DatabaseConnectionsCheck extends ParallelDatabaseConnectionCheckProcessor implements CheckProcessor {

	private static Logger logger = Logger.getLogger(DatabaseConnectionsCheck.class);

	private Supplier<PersistenceGmSession> cortexSessionFactory;

	@Override
	public CheckResult check(ServiceRequestContext requestContext, CheckRequest checkRequest) {
		EntityQuery query = EntityQueryBuilder.from(DatabaseConnectionPool.T) //
				.where().property(Deployable.deploymentStatus).eq(DeploymentStatus.deployed) //
				.done();

		PersistenceGmSession session = cortexSessionFactory.get();
		List<DatabaseConnectionPool> cpList = session.query().entities(query).list();
		logger.debug(() -> "Found " + (cpList != null ? cpList.size() : "0") + " deployed connection pools to check.");

		return super.performCheck(cpList);
	}

	@Configurable
	public void setCortexSessionFactory(Supplier<PersistenceGmSession> cortexSessionFactory) {
		this.cortexSessionFactory = cortexSessionFactory;
	}

}
