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
package com.braintribe.model.processing.email.search;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class SearchTermLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.5", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, T__8=9, 
		T__9=10, T__10=11, T__11=12, T__12=13, NOT=14, AND=15, OR=16, EQ=17, LT=18, 
		LE=19, GT=20, GE=21, NE=22, STRING=23, DIGIT=24, SPACE=25;
	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] ruleNames = {
		"T__0", "T__1", "T__2", "T__3", "T__4", "T__5", "T__6", "T__7", "T__8", 
		"T__9", "T__10", "T__11", "T__12", "NOT", "AND", "OR", "EQ", "LT", "LE", 
		"GT", "GE", "NE", "STRING", "DIGIT", "SPACE"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "'('", "')'", "'from'", "'to'", "'cc'", "'bcc'", "'unread'", "'older'", 
		"'younger'", "'recvdate'", "'sentdate'", "'body'", "'subject'", "'not'", 
		"'and'", "'or'", "'='", "'<'", "'<='", "'>'", "'>='", "'!='"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, null, null, null, null, null, null, null, null, null, null, 
		null, null, "NOT", "AND", "OR", "EQ", "LT", "LE", "GT", "GE", "NE", "STRING", 
		"DIGIT", "SPACE"
	};
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}


	public SearchTermLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "SearchTerm.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\2\33\u00a8\b\1\4\2"+
		"\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4"+
		"\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22"+
		"\t\22\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31"+
		"\t\31\4\32\t\32\3\2\3\2\3\3\3\3\3\4\3\4\3\4\3\4\3\4\3\5\3\5\3\5\3\6\3"+
		"\6\3\6\3\7\3\7\3\7\3\7\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\t\3\t\3\t\3\t\3\t"+
		"\3\t\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\13\3\13\3\13\3\13\3\13\3\13\3\13"+
		"\3\13\3\13\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\r\3\r\3\r\3\r\3\r\3\16"+
		"\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\17\3\17\3\17\3\17\3\20\3\20\3\20"+
		"\3\20\3\21\3\21\3\21\3\22\3\22\3\23\3\23\3\24\3\24\3\24\3\25\3\25\3\26"+
		"\3\26\3\26\3\27\3\27\3\27\3\30\3\30\7\30\u0099\n\30\f\30\16\30\u009c\13"+
		"\30\3\30\3\30\3\31\3\31\3\32\6\32\u00a3\n\32\r\32\16\32\u00a4\3\32\3\32"+
		"\2\2\33\3\3\5\4\7\5\t\6\13\7\r\b\17\t\21\n\23\13\25\f\27\r\31\16\33\17"+
		"\35\20\37\21!\22#\23%\24\'\25)\26+\27-\30/\31\61\32\63\33\3\2\5\3\2$$"+
		"\3\2\62;\5\2\13\f\17\17\"\"\u00a9\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2"+
		"\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3"+
		"\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2"+
		"\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2\2\2\2)\3\2\2"+
		"\2\2+\3\2\2\2\2-\3\2\2\2\2/\3\2\2\2\2\61\3\2\2\2\2\63\3\2\2\2\3\65\3\2"+
		"\2\2\5\67\3\2\2\2\79\3\2\2\2\t>\3\2\2\2\13A\3\2\2\2\rD\3\2\2\2\17H\3\2"+
		"\2\2\21O\3\2\2\2\23U\3\2\2\2\25]\3\2\2\2\27f\3\2\2\2\31o\3\2\2\2\33t\3"+
		"\2\2\2\35|\3\2\2\2\37\u0080\3\2\2\2!\u0084\3\2\2\2#\u0087\3\2\2\2%\u0089"+
		"\3\2\2\2\'\u008b\3\2\2\2)\u008e\3\2\2\2+\u0090\3\2\2\2-\u0093\3\2\2\2"+
		"/\u0096\3\2\2\2\61\u009f\3\2\2\2\63\u00a2\3\2\2\2\65\66\7*\2\2\66\4\3"+
		"\2\2\2\678\7+\2\28\6\3\2\2\29:\7h\2\2:;\7t\2\2;<\7q\2\2<=\7o\2\2=\b\3"+
		"\2\2\2>?\7v\2\2?@\7q\2\2@\n\3\2\2\2AB\7e\2\2BC\7e\2\2C\f\3\2\2\2DE\7d"+
		"\2\2EF\7e\2\2FG\7e\2\2G\16\3\2\2\2HI\7w\2\2IJ\7p\2\2JK\7t\2\2KL\7g\2\2"+
		"LM\7c\2\2MN\7f\2\2N\20\3\2\2\2OP\7q\2\2PQ\7n\2\2QR\7f\2\2RS\7g\2\2ST\7"+
		"t\2\2T\22\3\2\2\2UV\7{\2\2VW\7q\2\2WX\7w\2\2XY\7p\2\2YZ\7i\2\2Z[\7g\2"+
		"\2[\\\7t\2\2\\\24\3\2\2\2]^\7t\2\2^_\7g\2\2_`\7e\2\2`a\7x\2\2ab\7f\2\2"+
		"bc\7c\2\2cd\7v\2\2de\7g\2\2e\26\3\2\2\2fg\7u\2\2gh\7g\2\2hi\7p\2\2ij\7"+
		"v\2\2jk\7f\2\2kl\7c\2\2lm\7v\2\2mn\7g\2\2n\30\3\2\2\2op\7d\2\2pq\7q\2"+
		"\2qr\7f\2\2rs\7{\2\2s\32\3\2\2\2tu\7u\2\2uv\7w\2\2vw\7d\2\2wx\7l\2\2x"+
		"y\7g\2\2yz\7e\2\2z{\7v\2\2{\34\3\2\2\2|}\7p\2\2}~\7q\2\2~\177\7v\2\2\177"+
		"\36\3\2\2\2\u0080\u0081\7c\2\2\u0081\u0082\7p\2\2\u0082\u0083\7f\2\2\u0083"+
		" \3\2\2\2\u0084\u0085\7q\2\2\u0085\u0086\7t\2\2\u0086\"\3\2\2\2\u0087"+
		"\u0088\7?\2\2\u0088$\3\2\2\2\u0089\u008a\7>\2\2\u008a&\3\2\2\2\u008b\u008c"+
		"\7>\2\2\u008c\u008d\7?\2\2\u008d(\3\2\2\2\u008e\u008f\7@\2\2\u008f*\3"+
		"\2\2\2\u0090\u0091\7@\2\2\u0091\u0092\7?\2\2\u0092,\3\2\2\2\u0093\u0094"+
		"\7#\2\2\u0094\u0095\7?\2\2\u0095.\3\2\2\2\u0096\u009a\7$\2\2\u0097\u0099"+
		"\n\2\2\2\u0098\u0097\3\2\2\2\u0099\u009c\3\2\2\2\u009a\u0098\3\2\2\2\u009a"+
		"\u009b\3\2\2\2\u009b\u009d\3\2\2\2\u009c\u009a\3\2\2\2\u009d\u009e\7$"+
		"\2\2\u009e\60\3\2\2\2\u009f\u00a0\t\3\2\2\u00a0\62\3\2\2\2\u00a1\u00a3"+
		"\t\4\2\2\u00a2\u00a1\3\2\2\2\u00a3\u00a4\3\2\2\2\u00a4\u00a2\3\2\2\2\u00a4"+
		"\u00a5\3\2\2\2\u00a5\u00a6\3\2\2\2\u00a6\u00a7\b\32\2\2\u00a7\64\3\2\2"+
		"\2\5\2\u009a\u00a4\3\2\3\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}