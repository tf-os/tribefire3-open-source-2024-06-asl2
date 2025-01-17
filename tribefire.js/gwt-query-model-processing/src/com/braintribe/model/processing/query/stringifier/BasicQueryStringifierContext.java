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
package com.braintribe.model.processing.query.stringifier;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.braintribe.model.processing.query.api.shortening.SignatureExpert;
import com.braintribe.model.processing.query.api.stringifier.experts.Alias;
import com.braintribe.model.processing.query.parser.QueryParser;
import com.braintribe.model.processing.query.shortening.Qualified;
import com.braintribe.model.query.From;
import com.braintribe.model.query.Join;
import com.braintribe.model.query.Source;
import com.braintribe.utils.lcd.StringTools;

public class BasicQueryStringifierContext extends BasicStringifierContext {
	private Set<String> keywords = new HashSet<String>(QueryParser.getKeywords());
	private SignatureExpert shortening = null;
	private final int maxAliasLength = 15;

	private String replaceAliasTag = "{ReplaceAlias}";
	private boolean doRemoveReplaceAliasTag = true;
	private boolean replaceAliasTagInUse = false;

	protected Map<Source, Alias> sourceAliasMapping = new HashMap<Source, Alias>();
	protected String defaultAliasName = null;
	protected String emptyAliasName = "<?>";
	private Set<String> usedVariables = new HashSet<>();

	public void setShortening(SignatureExpert shortening) {
		this.shortening = shortening;
	}

	public void setDefaultAliasName(String defaultAliasName) {
		this.defaultAliasName = defaultAliasName;
	}

	public String getReplaceAliasTag() {
		this.replaceAliasTagInUse = true;
		return this.replaceAliasTag;
	}

	public boolean isReplaceAliasInUse() {
		return this.replaceAliasTagInUse;
	}

	public void setDoRemoveReplaceAliasTag(boolean value) {
		if (this.replaceAliasTagInUse) {
			this.doRemoveReplaceAliasTag = value;
		}
	}

	public String getDefaultAliasName() {
		return this.defaultAliasName;
	}

	public String getEmptyAliasName() {
		return this.emptyAliasName;
	}

	public boolean isAliasRegisteredFor(Source source) {
		return (source != null) ? this.sourceAliasMapping.containsKey(source) : false;
	}

	public Alias acquireAlias(Source source) {
		if (source != null) {
			Alias alias = this.sourceAliasMapping.get(source);
			if (alias == null) {
				alias = registerAlias(source);
			}

			return alias;
		}

		return new Alias(this.defaultAliasName);
	}

	public Alias registerAlias(Source source) {
		Alias alias = null;
		String sourceAliasName = source.getName();

		if (sourceAliasName == null) {
			// No explicit alias set. Determine alias from type signature of source
			final String typeSignature = getTypeSignature(source, null);
			alias = new Alias(getFreeAlias(getBaseAliasName(typeSignature)));
		} else {
			// There's an explicit alias configured.
			alias = new Alias(getFreeAlias(sourceAliasName));
		}

		this.sourceAliasMapping.put(source, alias);
		return alias;
	}

	public SignatureExpert getShortening() {
		if (this.shortening == null) {
			this.shortening = new Qualified();
		}

		return this.shortening;
	}

	public String escapeKeywords(final String propertyName) {
		if (propertyName == null) {
			return null;
		}

		final String[] propertyNameParts = propertyName.split("\\.");
		for (int i = 0, l = propertyNameParts.length; i < l; i++) {
			if (this.keywords.contains(propertyNameParts[i])) {
				propertyNameParts[i] = "\"" + propertyNameParts[i] + "\"";
			}
		}

		return StringTools.createStringFromCollection(Arrays.asList(propertyNameParts), ".");
	}

	public String getFreeAliasNameForTypeSignature(String typeSignature) {
		return getFreeAlias(getBaseAliasName(typeSignature));
	}

	protected String getBaseAliasName(final String typeSignature) {
		if (typeSignature != null && typeSignature.length() > 0) {
			final int packageSplit = typeSignature.lastIndexOf(".");
			if (packageSplit < 0) {
				return typeSignature;
			}

			// Get simpleTypeName of full type signature
			final StringBuilder simpleTypeName = new StringBuilder(typeSignature.substring(packageSplit + 1));
			int simpleTypeNameLength = simpleTypeName.length();

			// Check if length of simpleTypeName is to long
			if (simpleTypeNameLength > this.maxAliasLength) {
				int shorteningPos = 0;
				for (int i = 0; i < simpleTypeNameLength; i++) {
					final char character = simpleTypeName.charAt(i);
					if (Character.isUpperCase(character) == true || i == 0) {
						simpleTypeName.setCharAt(shorteningPos, simpleTypeName.charAt(i));
						shorteningPos++;
					}
				}

				// Remove lower case characters to shorten simpleTypeName
				simpleTypeName.delete(shorteningPos, simpleTypeNameLength);
				simpleTypeNameLength = simpleTypeName.length();
			} else if (simpleTypeNameLength > 0) {
				// Make only the first letter lower case
				simpleTypeName.setCharAt(0, Character.toLowerCase(simpleTypeName.charAt(0)));
			}

			return simpleTypeName.toString();
		}

		return null;
	}

	public String getFreeAlias(final String aliasBase) {
		if (aliasBase == null) {
			return this.emptyAliasName;
		}

		// Define alias for source
		String alias = escapeKeywords(aliasBase);
		int counter = 2;

		// Search for a free alias for source
		while (isAliasInUse(alias) == true) {
			alias = escapeKeywords(aliasBase + counter);
			counter++;
		}

		return alias;
	}

	protected boolean isAliasInUse(final String alias) {
		for (final Alias aliasMap : this.sourceAliasMapping.values()) {
			if (aliasMap.getName().equals(alias)) {
				return true;
			}
		}

		return false;
	}

	public String getTypeSignature(Source source, String defaultSignature) {
		String signature = null;

		if (source instanceof From) {
			final From from = (From) source;
			signature = from.getEntityTypeSignature();
		} else if (source instanceof Join) {
			final Join join = (Join) source;
			signature = join.getProperty();
		}

		if (signature == null) {
			signature = defaultSignature;
		}

		return signature;
	}

	public void ReplaceAliasTags(StringBuilder queryString) {
		if (this.replaceAliasTagInUse) {
			// Find all replace alias tags
			final String aliasName = (this.doRemoveReplaceAliasTag ? "" : this.acquireAlias(null).getName());
			final int aliasTagLength = this.replaceAliasTag.length() + (this.doRemoveReplaceAliasTag ? 1 : 0);

			int index = queryString.indexOf(this.replaceAliasTag);
			while (index >= 0) {
				// Replace the tags
				queryString.replace(index, index + aliasTagLength, aliasName);
				index = queryString.indexOf(this.replaceAliasTag);
			}
		}
	}
	
	public void registerUsedVariable(String variable) {
		usedVariables.add(variable);
	}
	
	public boolean isVariableUsed(String variable) {
		return usedVariables.contains(variable);
	}
}
