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
package com.braintribe.model.access.hibernate.schema.auto;

import java.util.function.Supplier;

import com.braintribe.cfg.Configurable;
import com.braintribe.cfg.Required;
import com.braintribe.logging.Logger;
import com.braintribe.utils.DigestGenerator;
import com.braintribe.utils.lcd.CommonTools;
import com.braintribe.utils.lcd.NullSafe;

/**
 * Context provider supplied by JDBC functionality
 * 
 *
 */
public class SimpleDbSchemaUpdateContextProvider implements Supplier<String> {

	private static final Logger logger = Logger.getLogger(SimpleDbSchemaUpdateContextProvider.class);

	private static final String SEP = "_";

	private Supplier<String> connectionUrlSupplier;
	private Supplier<String> schemaSupplier;
	private String tableNamePrefix;

	private String context;

	@Override
	public String get() {
		if (context == null)
			context = computeContext();

		return context;
	}

	private String computeContext() {
		NullSafe.nonNull(connectionUrlSupplier, "connectionUrlSupplier");
		NullSafe.nonNull(schemaSupplier, "schemaSupplier");

		String result;
		if (CommonTools.isEmpty(tableNamePrefix))
			result = connectionUrlSupplier.get() + SEP + schemaSupplier.get();
		else
			result = connectionUrlSupplier.get() + SEP + schemaSupplier.get() + SEP + tableNamePrefix;

		return truncateId(result);
	}

	protected String truncateId(String id) {
		if (id == null) {
			throw new IllegalArgumentException("The identifier of the lock must not be null.");
		}
		if (id.length() > 240) {
			String md5;
			try {
				md5 = DigestGenerator.stringDigestAsString(id, "MD5");
			} catch (Exception e) {
				logger.error("Could not generate an MD5 sum of ID " + id, e);
				md5 = "";
			}
			String cutId = id.substring(0, 200);
			String newId = cutId.concat("#").concat(md5);
			return newId;
		}
		return id;
	}

	// -----------------------------------------------------------------------
	// GETTER & SETTER
	// -----------------------------------------------------------------------

	@Required
	public void setConnectionUrlSupplier(Supplier<String> connectionUrlSupplier) {
		this.connectionUrlSupplier = connectionUrlSupplier;
	}

	@Required
	public void setSchemaSupplier(Supplier<String> schemaSupplier) {
		this.schemaSupplier = schemaSupplier;
	}

	@Configurable
	public void setTableNamePrefix(String tableNamePrefix) {
		this.tableNamePrefix = tableNamePrefix;
	}
}
