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
package com.braintribe.model.accessdeployment.hibernate;

import com.braintribe.model.generic.base.EnumBase;
import com.braintribe.model.generic.reflection.EnumType;
import com.braintribe.model.generic.reflection.EnumTypes;

/**
 * Enum representing dialects from org.hibernate.dialect package. For each constant there exists a class with name
 * "org.hibernate.dialect.${constant}".
 * <p>
 * This list was created based on classes listed on
 * <a href="http://docs.jboss.org/hibernate/orm/5.2/javadocs/org/hibernate/dialect/package-tree.html">this hibernate documentation</a>.
 */
public enum HibernateDialect implements EnumBase {

	Cache71Dialect,
	CUBRIDDialect,
	DataDirectOracle9Dialect,
	DB2390Dialect,
	DB2400Dialect,
	DB297Dialect,
	DB2Dialect,
	DerbyDialect,
	DerbyTenFiveDialect,
	DerbyTenSevenDialect,
	DerbyTenSixDialect,
	FirebirdDialect,
	FrontBaseDialect,
	H2Dialect,
	HANAColumnStoreDialect,
	HANARowStoreDialect,
	HSQLDialect,
	Informix10Dialect,
	InformixDialect,
	Ingres10Dialect,
	Ingres9Dialect,
	IngresDialect,
	InterbaseDialect,
	JDataStoreDialect,
	MariaDB53Dialect,
	MariaDBDialect,
	MckoiDialect,
	MimerSQLDialect,
	MySQL55Dialect,
	MySQL57Dialect,
	MySQL57InnoDBDialect,
	MySQL5Dialect,
	MySQL5InnoDBDialect,
	MySQLDialect,
	MySQLInnoDBDialect,
	MySQLMyISAMDialect,
	Oracle10gDialect,
	Oracle12cDialect,
	Oracle8iDialect,
	Oracle9Dialect,
	Oracle9iDialect,
	OracleDialect,
	PointbaseDialect,
	PostgresPlusDialect,
	PostgreSQL81Dialect,
	PostgreSQL82Dialect,
	PostgreSQL91Dialect,
	PostgreSQL92Dialect,
	PostgreSQL93Dialect,
	PostgreSQL94Dialect,
	PostgreSQL95Dialect,
	PostgreSQL9Dialect,
	PostgreSQLDialect,
	ProgressDialect,
	RDMSOS2200Dialect,
	SAPDBDialect,
	SQLServer2005Dialect,
	SQLServer2008Dialect,
	SQLServer2012Dialect,
	SQLServerDialect,
	Sybase11Dialect,
	SybaseAnywhereDialect,
	SybaseASE157Dialect,
	SybaseASE15Dialect,
	SybaseDialect,
	Teradata14Dialect,
	TeradataDialect,
	TimesTenDialect;

	public static final EnumType T = EnumTypes.T(HibernateDialect.class);

	@Override
	public EnumType type() {
		return T;
	}

}
