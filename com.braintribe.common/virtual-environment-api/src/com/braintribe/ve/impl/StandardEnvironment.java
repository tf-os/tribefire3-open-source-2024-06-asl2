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
package com.braintribe.ve.impl;

import com.braintribe.ve.api.VirtualEnvironment;

/**
 * This {@link VirtualEnvironment} implementation directly passes the access to system properties to {@link System#getProperty(String)} and the access to environment variables to {@link System#getenv(String)}.
 * @author Dirk Scheffler
 *
 */
public class StandardEnvironment implements VirtualEnvironment {
	public static final StandardEnvironment INSTANCE = new StandardEnvironment();
	@Override
	public String getEnv(String name) {
		return System.getenv(name);
	}
	
	@Override
	public String getProperty(String name) {
		return System.getProperty(name);
	}

}
