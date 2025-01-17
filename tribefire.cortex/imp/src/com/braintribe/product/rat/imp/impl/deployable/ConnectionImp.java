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
package com.braintribe.product.rat.imp.impl.deployable;

import com.braintribe.model.deployment.database.connector.GenericDatabaseConnectionDescriptor;
import com.braintribe.model.deployment.database.connector.MssqlConnectionDescriptor;
import com.braintribe.model.deployment.database.connector.MssqlDriver;
import com.braintribe.model.deployment.database.connector.MssqlVersion;
import com.braintribe.model.deployment.database.connector.OracleConnectionDescriptor;
import com.braintribe.model.deployment.database.connector.OracleVersion;
import com.braintribe.model.deployment.database.pool.ConfiguredDatabaseConnectionPool;
import com.braintribe.model.generic.session.exception.GmSessionException;
import com.braintribe.model.processing.session.api.persistence.PersistenceGmSession;
import com.braintribe.utils.lcd.CommonTools;

/**
 * A {@link BasicDeployableImp} specialized in {@link ConfiguredDatabaseConnectionPool}
 */
public class ConnectionImp<I extends ConfiguredDatabaseConnectionPool> extends BasicDeployableImp<I> {

	public ConnectionImp(PersistenceGmSession session, I connector) {
		super(session, connector);
	}

	public ConnectionImp<I> addOracleDescriptor(String user, String password, String dbName, String host, String service, int port,
			OracleVersion version) throws GmSessionException {

		logger.info("Adding oracle descriptor " + CommonTools.getParametersString("User", user, "Password", password) + " and "
				+ CommonTools.getParametersString("Service", service, "DB", dbName, "Port", port));

		OracleConnectionDescriptor oracleConnectionDescriptor = session().create(OracleConnectionDescriptor.T);
		oracleConnectionDescriptor.setUser(user);
		oracleConnectionDescriptor.setPassword(password);
		oracleConnectionDescriptor.setSid(service);
		oracleConnectionDescriptor.setHost(host);
		oracleConnectionDescriptor.setPort(port);
		oracleConnectionDescriptor.setVersion(version);
		this.instance.setConnectionDescriptor(oracleConnectionDescriptor);

		return this;
	}

	public ConnectionImp<I> addMssqlDescriptor(String user, String password, String dbName, MssqlDriver driver, String host, String instance,
			int port, MssqlVersion version) throws GmSessionException {
		logger.info("Adding mssql descriptor " + CommonTools.getParametersString("User", user, "Password", password) + " and "
				+ CommonTools.getParametersString("Instance", instance, "DB", dbName, "Port", port));

		MssqlConnectionDescriptor mssqlConnectionDescriptor = session().create(MssqlConnectionDescriptor.T);
		mssqlConnectionDescriptor.setUser(user);
		mssqlConnectionDescriptor.setPassword(password);
		mssqlConnectionDescriptor.setDatabase(dbName);
		mssqlConnectionDescriptor.setDriver(driver);
		mssqlConnectionDescriptor.setHost(host);
		mssqlConnectionDescriptor.setInstance(instance);
		mssqlConnectionDescriptor.setPort(port);
		mssqlConnectionDescriptor.setVersion(version);
		this.instance.setConnectionDescriptor(mssqlConnectionDescriptor);
		return this;
	}

	public ConnectionImp<I> addGenericDescriptor(String user, String password, String url, String driver) throws GmSessionException {
		logger.info("Adding oracle descriptor " + CommonTools.getParametersString("User", user, "Password", password) + " and "
				+ CommonTools.getParametersString("Url", url, "Driver", driver));

		GenericDatabaseConnectionDescriptor jdbcConnectionDescriptor = session().create(GenericDatabaseConnectionDescriptor.T);
		jdbcConnectionDescriptor.setUser(user);
		jdbcConnectionDescriptor.setPassword(password);
		jdbcConnectionDescriptor.setUrl(url);
		jdbcConnectionDescriptor.setDriver(driver);
		instance.setConnectionDescriptor(jdbcConnectionDescriptor);

		return this;
	}

}
