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
package com.braintribe.cartridge.common.processing;

import static com.braintribe.utils.lcd.CollectionTools2.nullSafe;

import java.util.List;
import java.util.regex.Matcher;

import javax.sql.DataSource;

import com.braintribe.cfg.Required;
import com.braintribe.exception.Exceptions;
import com.braintribe.logging.Logger;
import com.braintribe.util.jdbc.dialect.JdbcDialectAutoSense;
import com.braintribe.utils.StringTools;

/**
 * This class holds the list of {@link DialectMapping}s. It's main purpose is to increase the expressiveness of the configuration (for EM).
 * 
 * @author michael.lafite
 * 
 * @param <D>
 *            The dialect type
 */
public class DialectAutoSense<D> {

	private static Logger logger = Logger.getLogger(DialectAutoSense.class);

	private List<DialectMapping> dialectMappings;

	public List<DialectMapping> getDialectMappings() {
		return this.dialectMappings;
	}

	@Required
	public void setDialectMappings(List<DialectMapping> dialectMappings) {
		this.dialectMappings = dialectMappings;
	}

	public Class<? extends D> senseDialect(DataSource connectionPool) {
		try {
			return autoSenseDialect(JdbcDialectAutoSense.getProductNameAndVersion(connectionPool));
		} catch (RuntimeException e) {
			throw Exceptions.contextualize(e, "Could not autosense dialect.");
		}
	}

	private Class<? extends D> autoSenseDialect(String productNameAndVersion) {
		if (StringTools.isEmpty(productNameAndVersion))
			throw new RuntimeException("The auto-sense feature of the connection pool does not work. Could not determine the database product name.");

		for (DialectMapping mapping : nullSafe(dialectMappings)) {
			Matcher m = mapping.getProductMatcher().matcher(productNameAndVersion);
			if (m.matches()) {
				String variant = mapping.getVariant();
				String dialect = mapping.getDialect();

				logger.debug("Auto-sensed variant " + variant + " with dialect " + dialect + " for: " + productNameAndVersion);

				try {
					return (Class<? extends D>) this.getClass().getClassLoader().loadClass(dialect);
				} catch (Exception e) {
					throw new IllegalStateException("Cannot load dialect class " + dialect, e);
				}
			}
		}

		throw new RuntimeException(
				"The auto-sense feature of the connection pool does not work. No pattern for database " + productNameAndVersion + " configured.");
	}

}
