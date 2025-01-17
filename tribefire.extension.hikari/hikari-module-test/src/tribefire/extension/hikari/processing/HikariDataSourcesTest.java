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
package tribefire.extension.hikari.processing;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Statement;

import javax.sql.DataSource;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.braintribe.model.deployment.database.connector.GenericDatabaseConnectionDescriptor;
import com.braintribe.model.deployment.database.pool.HikariCpConnectionPool;
import com.braintribe.processing.test.db.derby.DerbyServerControl;
import com.braintribe.utils.FileTools;
import com.braintribe.utils.IOTools;
import com.zaxxer.hikari.HikariDataSource;

/**
 * @author peter.gazdik
 */
public class HikariDataSourcesTest {

	protected static DerbyServerControl derbyServerControl = null;

	public static int DERBY_PORT = 1527;

	@BeforeClass
	public static void initializeDatabase() throws Exception {
		deleteDatabaseFiles();

		if (!DerbyServerControl.checkDerbyServerPortAvailable(DERBY_PORT))
			throw new IllegalStateException("Derby port " + DERBY_PORT + " is not available.");

		derbyServerControl = new DerbyServerControl();
		derbyServerControl.setLogConnections(true);
		derbyServerControl.setPort(DERBY_PORT);
		derbyServerControl.setLogWriter(new PrintWriter(System.out, true));
		derbyServerControl.start();
	}

	@AfterClass
	public static void destroyDatabase() throws Exception {
		if (derbyServerControl != null)
			derbyServerControl.stop();

		deleteDatabaseFiles();
		File logFile = new File("derby.log");
		if (logFile.exists())
			logFile.delete();
	}

	private static void deleteDatabaseFiles() throws IOException {
		File dbFolder = new File("res/db");
		if (dbFolder.exists() && dbFolder.isDirectory()) {
			FileTools.deleteDirectoryRecursively(dbFolder);
		}
	}

	@Test
	public void testHikariCpConnection() throws Exception {
		HikariDataSources pds = new HikariDataSources();

		GenericDatabaseConnectionDescriptor connectionDescriptor = GenericDatabaseConnectionDescriptor.T.create();
		connectionDescriptor.setDriver("org.apache.derby.jdbc.ClientDriver");
		connectionDescriptor.setUrl("jdbc:derby://localhost:" + DERBY_PORT + "/res/db/hikaritest;create=true");
		connectionDescriptor.setUser("cortex");
		connectionDescriptor.setPassword("cortex");

		HikariCpConnectionPool cp = HikariCpConnectionPool.T.create();
		cp.setConnectionDescriptor(connectionDescriptor);
		cp.setExternalId("test.cp");

		HikariDataSource dataSource = pds.dataSource(cp);
		try {
			testDataSource(dataSource);
		} finally {
			IOTools.closeCloseable(dataSource, null);
		}
	}

	private void testDataSource(final DataSource dataSource) throws Exception {
		Connection con = dataSource.getConnection();
		try {
			Statement st = con.createStatement();
			try {
				st.execute("create table test (id varchar(250) primary key, content varchar(255))");
			} finally {
				IOTools.closeCloseable(st, null);
			}
		} finally {
			IOTools.closeCloseable(con, null);
		}
	}
}
