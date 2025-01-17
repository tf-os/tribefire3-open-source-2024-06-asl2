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
package com.braintribe.model.deployment.database.pool;

import java.util.List;

import com.braintribe.model.deployment.database.JdbcTransactionIsolationLevel;
import com.braintribe.model.generic.annotation.Initializer;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

public interface HikariCpConnectionPool extends ConfiguredDatabaseConnectionPool, DatabaseConnectionInfoProvider {

	EntityType<HikariCpConnectionPool> T = EntityTypes.T(HikariCpConnectionPool.class);

	@Initializer("60000")
	Integer getCheckoutTimeout();
	void setCheckoutTimeout(Integer checkoutTimeout);

	@Initializer("3")
	Integer getMaxPoolSize();
	void setMaxPoolSize(Integer maxPoolSize);

	@Initializer("1")
	Integer getMinPoolSize();
	void setMinPoolSize(Integer minPoolSize);

	String getPreferredTestQuery();
	void setPreferredTestQuery(String preferredTestQuery);

	Integer getMaxStatements();
	void setMaxStatements(Integer maxStatements);

	@Initializer("60000")
	Integer getMaxIdleTime();
	void setMaxIdleTime(Integer maxIdleTime);

	@Initializer("10")
	Integer getLoginTimeout();
	void setLoginTimeout(Integer loginTimeout);

	String getDataSourceName();
	void setDataSourceName(String dataSourceName);

	List<String> getInitialStatements();
	void setInitialStatements(List<String> initialStatements);

	@Initializer("true")
	boolean getEnableJmx();
	void setEnableJmx(boolean enableJmx);

	@Initializer("true")
	boolean getEnableMetrics();
	void setEnableMetrics(boolean enableMetrics);

	@Initializer("60000")
	Integer getValidationTimeout();
	void setValidationTimeout(Integer validationTimeout);

	Integer getInitializationFailTimeout();
	void setInitializationFailTimeout(Integer initializationFailTimeout);

	String getSchema();
	void setSchema(String schema);

	JdbcTransactionIsolationLevel getTransactionIsolationLevel();
	void setTransactionIsolationLevel(JdbcTransactionIsolationLevel transactionIsolationLevel);

	/* Integer getAcquireIncrement(); void setAcquireIncrement(Integer acquireIncrement);
	 * 
	 * Integer getAcquireRetryAttempts(); void setAcquireRetryAttempts(Integer acquireRetryAttempts);
	 * 
	 * Integer getAcquireRetryDelay(); void setAcquireRetryDelay(Integer acquireRetryDelay);
	 * 
	 * Boolean getAutoCommitOnClose(); void setAutoCommitOnClose(Boolean autoCommitOnClose);
	 * 
	 * String getAutomaticTestTable(); void setAutomaticTestTable(String automaticTestTable);
	 * 
	 * Boolean getBreakAfterAcquireFailure(); void setBreakAfterAcquireFailure(Boolean breakAfterAcquireFailure);
	 * 
	 * 
	 * String getConnectionCustomizerClassName(); void setConnectionCustomizerClassName(String
	 * connectionCustomizerClassName);
	 * 
	 * String getConnectionTesterClassName(); void setConnectionTesterClassName(String connectionTesterClassName);
	 * 
	 * Boolean getDebugUnreturnedConnectionStackTraces(); void setDebugUnreturnedConnectionStackTraces(Boolean
	 * debugUnreturnedConnectionStackTraces);
	 * 
	 * String getPoolDescription(); void setPoolDescription(String description);
	 * 
	 * Boolean getForceIgnoreUnresolvedTransactions(); void setForceIgnoreUnresolvedTransactions(Boolean
	 * forceIgnoreUnresolvedTransactions);
	 * 
	 * Integer getIdleConnectionTestPeriod(); void setIdleConnectionTestPeriod(Integer idleConnectionTestPeriod);
	 * 
	 * Integer getInitialPoolSize(); void setInitialPoolSize(Integer initialPoolSize);
	 * 
	 * Integer getMaxAdministrativeTaskTime(); void setMaxAdministrativeTaskTime(Integer maxAdministrativeTaskTime);
	 * 
	 * Integer getMaxConnectionAge(); void setMaxConnectionAge(Integer maxConnectionAge);
	 * 
	 * 
	 * Integer getMaxIdleTimeExcessConnections(); void setMaxIdleTimeExcessConnections(Integer
	 * maxIdleTimeExcessConnections);
	 * 
	 * 
	 * 
	 * Integer getMaxStatementsPerConnection(); void setMaxStatementsPerConnection(Integer maxStatementsPerConnection);
	 * 
	 * 
	 * String getOverrideDefaultPassword(); void setOverrideDefaultPassword(String overrideDefaultPassword);
	 * 
	 * String getOverrideDefaultUser(); void setOverrideDefaultUser(String overrideDefaultUser);
	 * 
	 * 
	 * Integer getPropertyCycle(); void setPropertyCycle(Integer propertyCycle);
	 * 
	 * Boolean getTestConnectionOnCheckin(); void setTestConnectionOnCheckin(Boolean testConnectionOnCheckin);
	 * 
	 * Boolean getTestConnectionOnCheckout(); void setTestConnectionOnCheckout(Boolean testConnectionOnCheckout);
	 * 
	 * Integer getUnreturnedConnectionTimeout(); void setUnreturnedConnectionTimeout(Integer
	 * unreturnedConnectionTimeout);
	 * 
	 * Boolean getUsesTraditionalReflectiveProxies(); void setUsesTraditionalReflectiveProxies(Boolean
	 * usesTraditionalReflectiveProxies);
	 * 
	 * 
	 * 
	 * String getFactoryClassLocation(); void setFactoryClassLocation(String factoryClassLocation);
	 * 
	 * String getIdentityToken(); void setIdentityToken(String identityToken);
	 * 
	 * Integer getNumHelperThreads(); void setNumHelperThreads(Integer numHelperThreads); */

	// @formatter:on

}
