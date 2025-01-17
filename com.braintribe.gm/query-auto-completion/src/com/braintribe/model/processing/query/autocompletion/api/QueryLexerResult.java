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
package com.braintribe.model.processing.query.autocompletion.api;

import com.braintribe.model.generic.StandardIdentifiable;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.meta.GmType;
import com.braintribe.model.processing.query.autocompletion.impl.lexer.container.QueryLexerToken;

/**
 * The actual result information of the current token -> from the QueryAutocompletionLexer
 */
public interface QueryLexerResult extends StandardIdentifiable {
	
	final EntityType<QueryLexerResult> T = EntityTypes.T(QueryLexerResult.class);

	boolean getInString();

	void setInString(boolean value);

	boolean getInEscapeKeyword();

	void setInEscapeKeyword(boolean value);

	int getBracketScope();

	void setBracketScope(int value);

	QueryLexerToken getQueryToken();

	void setQueryToken(QueryLexerToken value);

	GmType getAliasType();

	void setAliasType(GmType value);

	String getFilterString();

	void setFilterString(String value);
	
	GmType getPropertyType();
	
	void setPropertyType(GmType gmType);
	
	String getKeywordMatch();
	
	void setKeywordMatch(String keywordMatch);
	
	String getNumberString();
	
	void setNumberString(String numberString);
}
