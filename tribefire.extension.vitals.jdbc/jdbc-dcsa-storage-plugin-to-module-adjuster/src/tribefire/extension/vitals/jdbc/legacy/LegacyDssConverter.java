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
package tribefire.extension.vitals.jdbc.legacy;

import java.util.function.Function;

import com.braintribe.model.csa.PlugableDcsaSharedStorage;
import com.braintribe.model.dcsadeployment.DcsaSharedStorage;
import com.braintribe.model.deployment.Deployable;
import com.braintribe.model.deployment.database.connector.GenericDatabaseConnectionDescriptor;
import com.braintribe.model.deployment.database.pool.DatabaseConnectionPool;
import com.braintribe.model.deployment.database.pool.HikariCpConnectionPool;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.plugin.jdbc.JdbcPlugableDcsaSharedStorage;

import tribefire.extension.jdbc.dcsa.model.deployment.JdbcDcsaSharedStorage;

/**
 * @author peter.gazdik
 */
public class LegacyDssConverter implements Function<PlugableDcsaSharedStorage, Deployable> {

	private static final String CONNECTION_POOL_EXTERNAL_ID = "dcsa.main.connectionPool.legacy-originated";

	@Override
	public DcsaSharedStorage apply(PlugableDcsaSharedStorage legacySharedStorage) {
		if (!(legacySharedStorage instanceof JdbcPlugableDcsaSharedStorage))
			throw new IllegalArgumentException(
					"Cannot convert legacy DCSA shared storage of type: " + legacySharedStorage.entityType().getTypeSignature()
							+ ". Only supported type is: " + JdbcPlugableDcsaSharedStorage.T.getTypeSignature());

		JdbcPlugableDcsaSharedStorage legacy = (JdbcPlugableDcsaSharedStorage) legacySharedStorage;

		JdbcDcsaSharedStorage result = JdbcDcsaSharedStorage.T.create();
		result.setGlobalId(legacy.getGlobalId());
		result.setName("JDBC DCSA Shared Storage");
		result.setExternalId("jdbc.dcsa.shared.storage.main");
		result.setProject(legacy.getProject());
		result.setAutoUpdateSchema(legacy.getAutoUpdateSchema());
		result.setConnectionPool(getConnectionPool(legacy));
		result.setParallelFetchThreads(legacy.getParallelFetchThreads());

		return result;
	}

	private DatabaseConnectionPool getConnectionPool(JdbcPlugableDcsaSharedStorage legacy) {
		DatabaseConnectionPool result = legacy.getDatabaseConnectionPool();
		return result != null ? cloneAndChangeExternalId(result) : buildConnectionPool(legacy);
	}

	private DatabaseConnectionPool cloneAndChangeExternalId(DatabaseConnectionPool cp) {
		EntityType<? extends DatabaseConnectionPool> et = cp.entityType();

		DatabaseConnectionPool result = et.create();

		for (Property p : et.getProperties()) {
			Object value = p.get(cp);
			p.set(result, value);
		}

		result.setExternalId(CONNECTION_POOL_EXTERNAL_ID);

		return result;
	}

	private DatabaseConnectionPool buildConnectionPool(JdbcPlugableDcsaSharedStorage legacy) {
		GenericDatabaseConnectionDescriptor cd = GenericDatabaseConnectionDescriptor.T.create();
		cd.setUser(legacy.getUsername());
		cd.setPassword(legacy.getPassword());
		cd.setUrl(legacy.getUrl());
		cd.setDriver(legacy.getDriver());

		HikariCpConnectionPool result = HikariCpConnectionPool.T.create();
		/* Stupid externalId you say? We have to be careful here - if the DataSource in the configuration.json (resolved via bindId) had the same
		 * externalId, it would lead to an error due to an attempt to register some metric internally with the same name twice. */
		result.setExternalId(CONNECTION_POOL_EXTERNAL_ID);
		result.setName("The DCSA Connection Pool");
		result.setConnectionDescriptor(cd);
		// since reading DCSA entries can be slow, we use more than the usual default (which is 3) 
		result.setMaxPoolSize(15);

		return result;
	}

}
