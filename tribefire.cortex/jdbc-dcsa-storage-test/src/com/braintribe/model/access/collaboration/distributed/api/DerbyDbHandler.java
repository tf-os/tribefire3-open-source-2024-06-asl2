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
package com.braintribe.model.access.collaboration.distributed.api;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

import javax.sql.DataSource;

import com.braintribe.model.processing.lock.db.impl.DbLockManager;
import com.braintribe.processing.test.db.derby.DerbyServerControl;
import com.braintribe.util.network.NetworkTools;
import com.braintribe.utils.DateTools;
import com.braintribe.utils.FileTools;
import com.zaxxer.hikari.HikariDataSource;

public class DerbyDbHandler implements DbHandler {

	protected DataSource dataSource = null;
	protected DbLockManager dbLockManager;

	protected DerbyServerControl derbyServerControl = null;
	public static int DERBY_PORT = 1527;

	@Override
	public void initialize() throws Exception {
		deleteDatabaseFiles();

		if (!DerbyServerControl.checkDerbyServerPortAvailable(DERBY_PORT)) {
			print("Derby port " + DERBY_PORT + " is not available.");
		}

		derbyServerControl = new DerbyServerControl();
		derbyServerControl.setLogConnections(true);
		// derbyServerControl.setTraceDirectory(new File("log"));
		derbyServerControl.setPort(DERBY_PORT);
		derbyServerControl.setLogWriter(new PrintWriter(System.out, true));
		derbyServerControl.start();

		// Call this on startup so that the result is cached and does not interfere with other tests that rely on timing
		NetworkTools.getNetworkAddress().getHostAddress();

		dataSource();
		lockManager();
	}

	@Override
	public void destroy() throws Exception {

		if (derbyServerControl != null) {
			derbyServerControl.stop();
		}

		deleteDatabaseFiles();
		File logFile = new File("derby.log");
		if (logFile.exists()) {
			logFile.delete();
		}
	}

	@Override
	public DataSource dataSource() {
		if (dataSource == null) {
			HikariDataSource bean = new HikariDataSource();
			try {
				bean.setDriverClassName("org.apache.derby.jdbc.ClientDriver");
			} catch (Exception e) {
				throw new RuntimeException("Could not set driver class.", e);
			}
			bean.setJdbcUrl("jdbc:derby://localhost:" + DERBY_PORT + "/res/db/leadershiptests;create=true");
			bean.setUsername("cortex");
			bean.setPassword("cortex");
			dataSource = bean;
		}
		return dataSource;
	}

	@Override
	public DbLockManager lockManager() {
		if (dbLockManager == null) {
			DbLockManager bean = new DbLockManager();
			bean.setDataSource(dataSource());
			bean.postConstruct();
			dbLockManager = bean;
		}
		return dbLockManager;
	}

	protected static void deleteDatabaseFiles() throws IOException {
		File dbFolder = new File("res/db");
		if (dbFolder.exists() && dbFolder.isDirectory()) {
			FileTools.deleteDirectoryRecursively(dbFolder);
		}
	}
	private static void print(String text) {
		System.out.println(DateTools.encode(new Date(), DateTools.LEGACY_DATETIME_WITH_MS_FORMAT) + " [Master]: " + text);
	}

}
