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
package com.braintribe.model.access.hibernate.schema;

import java.sql.Connection;
import java.util.function.Supplier;

import javax.sql.DataSource;

import com.braintribe.cfg.Configurable;
import com.braintribe.cfg.Required;
import com.braintribe.exception.Exceptions;

/**
 * URL provider supplied by JDBC functionality
 * 
 *
 */
public class SimpleConnectionUrlProvider implements Supplier<String> {

	private DataSource dataSource;

	private String url;

	@Override
	public String get() {
		if (url == null) {
			try (Connection connection = dataSource.getConnection()) {
				url = connection.getMetaData().getURL();
			} catch (Exception e) {
				throw Exceptions.unchecked(e, "Could not get 'ConnectionUrl'");
			}
		}
		return url;
	}

	// -----------------------------------------------------------------------
	// GETTER & SETTER
	// -----------------------------------------------------------------------

	@Configurable
	@Required
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

}
