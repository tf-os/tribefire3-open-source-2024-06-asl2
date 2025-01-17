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
package com.braintribe.model.processing.deployment.hibernate.test.mapping.xmlusage;

import org.hibernate.dialect.DerbyTenFiveDialect;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.Oracle10gDialect;

import com.braintribe.processing.test.db.derby.DerbyServerControl;

/**
 * <p>
 * Central point of database control/configuration for tests/labs.
 * 
 */
public abstract class DatabaseTest {

	public static boolean useInternallyManagedDerbyInstance = true;
	
	protected static String driver = "oracle.jdbc.OracleDriver";
	protected static String user = "braintribe";
	protected static String password = "1234";
	protected static String url = "jdbc:oracle:thin:@localhost:1521:xe";
	protected static Class<? extends Dialect> dialect = Oracle10gDialect.class;

	private static final String derbyDriver = "org.apache.derby.jdbc.ClientDriver";
	private static final String derbyUser = "cortex";
	private static final String derbyPassword = "cortex";
	private static final String derbyUrl = "jdbc:derby://localhost:1527/res/db/hibernateAccessTest;create=true";
	private static final Class<? extends Dialect> derbyDialect = DerbyTenFiveDialect.class;
	

	private static DerbyServerControl derbyServerControl = null;

	public static void initializeDatabaseContext() throws Exception {
		
		if (useInternallyManagedDerbyInstance) {
			driver = derbyDriver;
			user = derbyUser;
			password = derbyPassword;
			url = derbyUrl;
			dialect = derbyDialect;
			derbyServerControl = new DerbyServerControl();
			derbyServerControl.start();
		}


	}

	public static void destroyDatabaseContext() throws Exception {
		if (derbyServerControl != null) {
			derbyServerControl.stop();
		}
	}

}
