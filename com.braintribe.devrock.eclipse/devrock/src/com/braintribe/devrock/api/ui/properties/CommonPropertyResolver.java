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
package com.braintribe.devrock.api.ui.properties;

import com.braintribe.cfg.Configurable;
import com.braintribe.ve.api.VirtualEnvironment;
import com.braintribe.ve.impl.StandardEnvironment;

public class CommonPropertyResolver implements VirtualPropertyResolver {
	protected VirtualEnvironment virtualEnvironment = StandardEnvironment.INSTANCE;
	
	public CommonPropertyResolver() {
		super();
	}

	@Configurable
	public void setVirtualEnvironment(VirtualEnvironment virtualEnvironment) {
		this.virtualEnvironment = virtualEnvironment;
	}

	/**
	 * just checks if the expression contains ${..} somehow
	 * @param expression - the string to check 
	 * @return - true if a variable reference is in the string and false otherwise 
	 */
	protected boolean requiresEvaluation(String expression) {
		String extract = extract(expression);
		return !extract.equalsIgnoreCase(expression);
	}

	/**
	 * extracts the first variable in the expression 
	 * @param expression - the {@link String} to extract the variable from 
	 * @return - the first variable (minus the ${..} stuff)
	 */
	protected String extract(String expression) {
		int p = expression.indexOf( "${");
		if (p < 0)
			return expression;
		int q = expression.indexOf( "}", p+1);
		return expression.substring(p+2, q);	
	}

	/**
	 * replaces any occurrence of the variable by its value 
	 * @param variable - without ${..}, it will be added 
	 * @param value - the value of the variable
	 * @param expression - the expression to replace in 
	 * @return - the expression after the replace 
	 */
	protected String replace(String variable, String value, String expression) {
		return expression.replace( "${" + variable + "}", value);
	}

	/**
	 * splits an expression - typically a variable into two parts<br/>
	 * - the part before the first point<br/>
	 * - the remainder. 
	 * @param expression - such as env.* or settings.* or project.*
	 * @return - a tuple as an array 
	 */
	protected String [] split(String expression) {
		String [] tokens = new String[2];
		int p = expression.indexOf(".");
		if (p == 0) {
			tokens[1] = expression.substring(1);
		}
		else if (p > 0) {
			tokens[0] = expression.substring(0, p);
			tokens[1] = expression.substring( p+1);
		}
		else {
			tokens[0] = expression;
		}			
		return tokens;
	}

	/**
	 * looks up a system property, if there isn't any named like this, it throws an {@link PropertyResolvingException}
	 * @param key - the name of the property 
	 * @return - the value of the property  
	 */
	@Override
	public String getSystemProperty(String key) {
		String value = virtualEnvironment.getProperty(key);
		return value;
	}
	@Override
	public String getEnvironmentProperty( String key) {
		String keyToUse = key;
		if (keyToUse.startsWith( "env.")) {
			keyToUse = keyToUse.substring( "env.".length());
		}		
		String value = virtualEnvironment.getEnv(keyToUse);
		return value;
	}
	
	@Override
	public String resolve(String expression) {
		if (!requiresEvaluation(expression))
			return expression;
		String value = expression;
		do {
			String key = extract(value);
			String v = getEnvironmentProperty(key);
			if (v == null) {
				v = getSystemProperty(key);
			}
			value = replace( key, v, value);			
		} while ( requiresEvaluation( value));
		return value;
	}
	

}
