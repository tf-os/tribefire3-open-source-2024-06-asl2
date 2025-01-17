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
package com.braintribe.model.access.hibernate.base.tools;

import static com.braintribe.utils.lcd.CollectionTools2.newList;

import java.io.File;
import java.util.List;
import java.util.function.Supplier;

import javax.sql.DataSource;

import org.hibernate.SessionFactory;

import com.braintribe.model.access.hibernate.HibernateAccess;
import com.braintribe.model.meta.GmMetaModel;
import com.braintribe.model.processing.deployment.hibernate.HibernateMappingsDirectorySupplier;
import com.braintribe.persistence.hibernate.GmAwareHibernateSessionFactoryBean;
import com.braintribe.persistence.hibernate.HibernateSessionFactoryBean;

/**
 * @author peter.gazdik
 */
public class HibernateAccessSetupHelper {

	private static final List<AutoCloseable> closeables = newList();

	public static HibernateAccess hibernateAccess(String accessId, Supplier<GmMetaModel> modelSupplier, DataSource dataSource) throws Exception {
		return hibernateAccess(accessId, modelSupplier, hibernateSessionFactory(modelSupplier, dataSource));
	}

	public static HibernateAccess hibernateAccess(String accessId, Supplier<GmMetaModel> modelSupplier, SessionFactory hibernateSessionFactory) {
		HibernateAccess bean = new HibernateAccess();
		bean.setAccessId(accessId);
		bean.setModelSupplier(modelSupplier);
		bean.setHibernateSessionFactory(hibernateSessionFactory);

		return bean;
	}

	public static SessionFactory hibernateSessionFactory(Supplier<GmMetaModel> modelSupplier, DataSource dataSource) {
		return hibernateSessionFactoryBean(modelSupplier, dataSource).getObject();
	}

	public static HibernateSessionFactoryBean hibernateSessionFactoryBean(Supplier<GmMetaModel> modelSupplier, DataSource dataSource) {
		HibernateSessionFactoryBean sessionFactory = new GmAwareHibernateSessionFactoryBean();
		sessionFactory.setShowSql(true);
		sessionFactory.setMappingDirectoryLocations(mappingsFolder(modelSupplier));
		sessionFactory.setDataSource(dataSource);
		sessionFactory.setDefaultBatchFetchSize(30);

		closeables.add(sessionFactory::closeFactory);

		return sessionFactory;
	}

	private static File mappingsFolder(Supplier<GmMetaModel> modelSupplier) {
		HibernateMappingsDirectorySupplier bean = new HibernateMappingsDirectorySupplier();
		bean.setMetaModel(modelSupplier.get());

		return bean.get();
	}

	public static DataSource dataSource_H2(String dbName) {
		org.h2.jdbcx.JdbcDataSource bean = new org.h2.jdbcx.JdbcDataSource();

		// DB_CLOSE_DELAY -> https://stackoverflow.com/questions/5763747/h2-in-memory-database-table-not-found
		bean.setUrl("jdbc:h2:mem:" + dbName + ";DB_CLOSE_DELAY=-1");
		bean.setUser("sa");
		bean.setPassword(""); // null causes NPE

		return bean;
	}

	public static void close() {
		for (AutoCloseable closeable : closeables) {
			try {
				closeable.close();

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
