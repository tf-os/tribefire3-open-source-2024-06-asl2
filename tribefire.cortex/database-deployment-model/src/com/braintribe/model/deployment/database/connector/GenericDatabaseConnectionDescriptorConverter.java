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
package com.braintribe.model.deployment.database.connector;

import java.util.Map.Entry;

/**
 * @author peter.gazdik
 */
/* package */ class GenericDatabaseConnectionDescriptorConverter {

	public static GenericDatabaseConnectionDescriptor convert(DatabaseConnectionDescriptor connector) {

		if (connector instanceof GenericDatabaseConnectionDescriptor)
			return (GenericDatabaseConnectionDescriptor) connector;

		if (connector instanceof MssqlConnectionDescriptor)
			return convert((MssqlConnectionDescriptor) connector);

		if (connector instanceof OracleConnectionDescriptor)
			return convert((OracleConnectionDescriptor) connector);

		throw new IllegalArgumentException("Unknown connector type: " + connector);

	}

	// ###################################
	// ## . . . . . Oracle. . . . . . . ##
	// ###################################

	private static GenericDatabaseConnectionDescriptor convert(OracleConnectionDescriptor oracleConnectionDescriptor) {

		GenericDatabaseConnectionDescriptor connector = GenericDatabaseConnectionDescriptor.T.create();
		connector.setUser(oracleConnectionDescriptor.getUser());
		connector.setPassword(oracleConnectionDescriptor.getPassword());

		if (OracleVersion.Oracle8i == oracleConnectionDescriptor.getVersion()) {
			connector.setDriver("oracle.jdbc.driver.OracleDriver");
		} else {
			connector.setDriver("oracle.jdbc.OracleDriver");
		}

		String url = "jdbc:oracle:thin:@" + oracleConnectionDescriptor.getHost() + ":" + oracleConnectionDescriptor.getPort() + ":"
				+ oracleConnectionDescriptor.getSid();

		connector.setUrl(url);

		return connector;

	}

	// ###################################
	// ## . . . . . . MsSql . . . . . . ##
	// ###################################

	private static GenericDatabaseConnectionDescriptor convert(MssqlConnectionDescriptor mssqlConnectionDescriptor) {

		GenericDatabaseConnectionDescriptor connector = GenericDatabaseConnectionDescriptor.T.create();

		MssqlDriver mssqlDriver = mssqlConnectionDescriptor.getDriver();
		mssqlDriver = mssqlDriver == null ? MssqlDriver.MicrosoftJdbc4Driver : mssqlDriver;
		switch (mssqlDriver) {
			case MicrosoftJdbc4Driver:
				connector.setDriver("com.microsoft.sqlserver.jdbc.SQLServerDriver");
				connector.setUrl(buildStandardMssqlUrl(mssqlConnectionDescriptor));
				break;
			case Jtds:
				connector.setDriver("net.sourceforge.jtds.jdbc.Driver");
				connector.setUrl(buildJtdsMssqlUrl(mssqlConnectionDescriptor));
				break;
			default:
				throw new IllegalArgumentException("Unsupported driver for MssqlConnectionDescriptor: " + connector);
		}

		connector.setUser(mssqlConnectionDescriptor.getUser());
		connector.setPassword(mssqlConnectionDescriptor.getPassword());

		return connector;

	}

	private static String buildStandardMssqlUrl(MssqlConnectionDescriptor connector) {
		String url = "jdbc:sqlserver://" + connector.getHost();
		url = concatIfOk(url, connector.getInstance(), "\\" + connector.getInstance());
		url = concatIfOk(url, connector.getPort(), ":" + connector.getPort());
		url = concatIfOk(url, connector.getDatabase(), ";database=" + connector.getDatabase());
		url = addProps(url, connector);
		return url;
	}

	private static String buildJtdsMssqlUrl(MssqlConnectionDescriptor connector) {
		String url = "jdbc:jtds:sqlserver://" + connector.getHost();
		url = concatIfOk(url, connector.getPort(), ":" + connector.getPort());
		url = concatIfOk(url, connector.getDatabase(), "/" + connector.getDatabase());
		url = concatIfOk(url, connector.getInstance(), ";instance=" + connector.getInstance());
		url = addProps(url, connector);
		return url;
	}

	private static String addProps(String url, MssqlConnectionDescriptor connector) {
		for (Entry<String, String> entry : connector.getProperties().entrySet()) {
			url += ";" + entry.getKey() + "=" + entry.getValue();
		}
		return url;
	}

	private static String concatIfOk(String original, Object condidtion, String appendix) {
		if (condidtion != null) {
			return original + appendix;
		}
		return original;
	}

}
