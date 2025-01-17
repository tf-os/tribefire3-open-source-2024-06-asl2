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
package com.braintribe.model.processing.deployment.hibernate.test.mapping.xmlusage.access;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;

import com.braintribe.logging.Logger;
import com.braintribe.model.access.hibernate.EnhancerHidingInterceptor;
import com.braintribe.model.access.hibernate.HibernateAccess;
import com.braintribe.model.access.hibernate.HibernateAccessInitializationContext;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.processing.IdGenerator;
import com.braintribe.model.processing.core.expert.api.GmExpertDefinition;
import com.braintribe.model.processing.core.expert.impl.ConfigurableGmExpertDefinition;
import com.braintribe.model.processing.core.expert.impl.ConfigurableGmExpertRegistry;
import com.braintribe.utils.CommonTools;
import com.braintribe.utils.lcd.NullSafe;

/**
 * @author peter.gazdik
 */
public class HibernateAccessTestTools {

	private static final Logger logger = Logger.getLogger(HibernateAccessTestTools.class);
	
	public static HibernateAccess newHibernateAccess(final HibernateAccessInitializationContext initializationContext) {

		boolean trace = logger.isTraceEnabled();
		boolean debug = logger.isDebugEnabled();

		if (debug) {
			logger.debug("Initializing new HibernateAccess... " + CommonTools.getParametersString(initializationContext, initializationContext));
		}

		final Configuration configuration = new Configuration();
		if (initializationContext.getInterceptor() == null) {
			configuration.setInterceptor(new EnhancerHidingInterceptor());
		} else {
			configuration.setInterceptor(initializationContext.getInterceptor());
		}

		for (final File mappingsFolder : NullSafe.iterable(initializationContext.getHibernateMappingsFolders())) {
			if (debug) {
				logger.debug("Adding mappings folder " + mappingsFolder + " ...");
			}
			configuration.addDirectory(mappingsFolder);
		}

		for (final URL mappingsResourceUrl : NullSafe.iterable(initializationContext.getHibernateMappingsResources())) {
			if (debug) {
				logger.debug("Adding mappings resource " + mappingsResourceUrl + " ...");
			}
			configuration.addURL(mappingsResourceUrl);
		}

		final Map<String, String> properties = new HashMap<String, String>();
		properties.putAll(initializationContext.getHibernateConfigurationProperties());

		// required to avoid "No CurrentSessionContext configured!" problem
		properties.put(Environment.CURRENT_SESSION_CONTEXT_CLASS, "thread");
		// create/update database tables
		properties.put(Environment.HBM2DDL_AUTO, "update");

		for (final Entry<String, String> propertyEntry : properties.entrySet()) {
			if (trace) {
				logger.trace("Adding property: " + propertyEntry.getKey() + "=" + propertyEntry.getValue());
			}
			configuration.setProperty(propertyEntry.getKey(), propertyEntry.getValue());
		}

		StandardServiceRegistryBuilder ssrb = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties());
		final SessionFactory hibernateSessionFactory = configuration.buildSessionFactory(ssrb.build());

		final HibernateAccess hibernateAccess = new HibernateAccess();
		hibernateAccess.setModelName(initializationContext.getModelName());
		hibernateAccess.setHibernateSessionFactory(hibernateSessionFactory);
		ConfigurableGmExpertRegistry newRegistry = new ConfigurableGmExpertRegistry();
		List<GmExpertDefinition> expertDefinitions = new ArrayList<GmExpertDefinition>();

		for (final Entry<Class<? extends GenericEntity>, IdGenerator> entry : NullSafe.entrySet(initializationContext.getIdGenerators())) {
			if (trace) {
				logger.trace("Registering " + IdGenerator.class.getSimpleName() + " " + entry.getValue() + " for entity type "
						+ entry.getKey().getName() + ".");
			}

			ConfigurableGmExpertDefinition gmExpertDefinition = new ConfigurableGmExpertDefinition();
			gmExpertDefinition.setDenotationType(entry.getKey());
			gmExpertDefinition.setExpertType(IdGenerator.class);
			gmExpertDefinition.setExpert(entry.getValue());

			expertDefinitions.add(gmExpertDefinition);
		}

		newRegistry.setExpertDefinitions(expertDefinitions);

		hibernateAccess.setExpertRegistry(newRegistry);

		if (debug) {
			logger.debug("Successfully initialized HibernateAccess.");
		}
		return hibernateAccess;
	}
}
