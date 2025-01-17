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
package com.braintribe.model.processing.query.parser.impl.listener.error;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

import com.braintribe.model.processing.query.parser.api.GmqlParsingError;
import com.braintribe.model.processing.query.parser.api.GmqlSyntacticParsingError;
import com.braintribe.model.processing.query.parser.impl.GmqlQueryParserImpl;
import com.braintribe.model.processing.query.parser.impl.autogenerated.GmqlLexer;
import com.braintribe.model.processing.query.parser.impl.autogenerated.GmqlParser;

/**
 * An error listener implementation that collects all different kinds of errors that might occur during parsing.
 * 
 * It is instantiated and registered to {@link GmqlParser} in {@link GmqlQueryParserImpl}.
 * 
 */
public class GmqlErrorListener extends BaseErrorListener {

	GmqlLexer lexer;
	List<GmqlParsingError> errorList;

	public GmqlErrorListener(GmqlLexer lexer) {
		this.lexer = lexer;
		this.errorList = new ArrayList<GmqlParsingError>();
	}

	public List<GmqlParsingError> getErrorList() {
		return this.errorList;
	}

	@Override
	public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine,
			String msg, RecognitionException e) {
		List<String> stack = ((Parser) recognizer).getRuleInvocationStack();
		Collections.reverse(stack);
		if (offendingSymbol instanceof CommonToken) {
			CommonToken token = (CommonToken) offendingSymbol;
			String symbolicName = "";
			int tokenId = token.getType() - 1;
			String[] ruleNames = this.lexer.getRuleNames();
			if (tokenId >= 0 && tokenId < ruleNames.length) {
				symbolicName = ruleNames[tokenId];
			}
			GmqlSyntacticParsingError parsingError = getParsingError(line, charPositionInLine, msg, symbolicName, stack);
			this.errorList.add(parsingError);
		} else {
			System.err.println("offendingSymbol" + offendingSymbol + " is not of type CommonToken, check with the queryParserDeveloper");
		}
	}

	private static GmqlSyntacticParsingError getParsingError(int line, int charPositionInLine, String msg, Object offendingToken,
			List<String> stack) {
		GmqlSyntacticParsingError error = GmqlSyntacticParsingError.T.create();
		error.setLineNumber(line);
		error.setCharacterPostionInLine(charPositionInLine);
		error.setMessage(msg);
		error.setOffendingToken(offendingToken);
		error.setRuleInvocationStack(stack);
		return error;
	}
}
