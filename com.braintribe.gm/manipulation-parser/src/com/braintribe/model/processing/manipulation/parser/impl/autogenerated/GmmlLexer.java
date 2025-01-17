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
package com.braintribe.model.processing.manipulation.parser.impl.autogenerated;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.RuntimeMetaData;
import org.antlr.v4.runtime.Vocabulary;
import org.antlr.v4.runtime.VocabularyImpl;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.LexerATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class GmmlLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.5", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		Boolean=1, Null=2, Colon=3, Colon_Colon=4, EQ=5, PLUS=6, MINUS=7, MINUS_MINUS=8, 
		LB=9, RB=10, LSB=11, RSB=12, LCB=13, RCB=14, COMMA=15, Dot=16, EXCLAMATION=17, 
		StandardIdentifier=18, DateFunction=19, DateOffset=20, TimeZoneOffset=21, 
		DecimalLiteral=22, FloatLiteral=23, DoubleLiteral=24, LongBase10Literal=25, 
		LongBase16Literal=26, IntegerBase10Literal=27, IntegerBase16Literal=28, 
		StringOpen=29, WS=30, Comment=31, LineComment=32, ANY=33, UnicodeEscape=34, 
		EscB=35, EscT=36, EscN=37, EscF=38, EscR=39, EscSQ=40, EscBS=41, PlainContent=42, 
		StringClose=43;
	public static final int InsideString = 1;
	public static String[] modeNames = {
		"DEFAULT_MODE", "InsideString"
	};

	public static final String[] ruleNames = {
		"Boolean", "Null", "Colon", "Colon_Colon", "EQ", "PLUS", "MINUS", "MINUS_MINUS", 
		"LB", "RB", "LSB", "RSB", "LCB", "RCB", "COMMA", "Dot", "EXCLAMATION", 
		"StandardIdentifier", "IdentifierFirstCharacter", "IdentifierCharacter", 
		"Char", "UnderScore", "DollarSign", "DateFunction", "DateOffset", "TimeZoneOffset", 
		"YearFragment", "MonthFragment", "DayFragment", "HourFragment", "MinuteFragment", 
		"SecondFragment", "MilliSecondFragment", "ZoneFragment", "DecimalLiteral", 
		"FloatLiteral", "DoubleLiteral", "FloatingPointSpecial", "FloatingPointLiteral", 
		"Exponent", "LongBase10Literal", "LongBase16Literal", "IntegerBase10Literal", 
		"IntegerBase16Literal", "HexDigit", "Digit", "ZeroDigit", "PositiveDigit", 
		"PlusOrMinus", "LongSuffix", "FloatSuffix", "DoubleSuffix", "DecimalSuffix", 
		"ExponentIndicator", "HexInfix", "StringOpen", "SingleQuote", "WS", "Comment", 
		"LineComment", "ANY", "BackSlash", "UnicodeEscape", "EscB", "EscT", "EscN", 
		"EscF", "EscR", "EscSQ", "EscBS", "PlainContent", "StringClose"
	};

	private static final String[] _LITERAL_NAMES = {
		null, null, "'null'", "':'", "'::'", "'='", "'+'", "'-'", "'--'", "'('", 
		"')'", "'['", "']'", "'{'", "'}'", "','", "'.'", "'!'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, "Boolean", "Null", "Colon", "Colon_Colon", "EQ", "PLUS", "MINUS", 
		"MINUS_MINUS", "LB", "RB", "LSB", "RSB", "LCB", "RCB", "COMMA", "Dot", 
		"EXCLAMATION", "StandardIdentifier", "DateFunction", "DateOffset", "TimeZoneOffset", 
		"DecimalLiteral", "FloatLiteral", "DoubleLiteral", "LongBase10Literal", 
		"LongBase16Literal", "IntegerBase10Literal", "IntegerBase16Literal", "StringOpen", 
		"WS", "Comment", "LineComment", "ANY", "UnicodeEscape", "EscB", "EscT", 
		"EscN", "EscF", "EscR", "EscSQ", "EscBS", "PlainContent", "StringClose"
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


	public GmmlLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "GmmlLexer.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\2-\u01fd\b\1\b\1\4"+
		"\2\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n"+
		"\4\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22"+
		"\t\22\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31"+
		"\t\31\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t"+
		" \4!\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t"+
		"+\4,\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\4\63\t\63\4\64"+
		"\t\64\4\65\t\65\4\66\t\66\4\67\t\67\48\t8\49\t9\4:\t:\4;\t;\4<\t<\4=\t"+
		"=\4>\t>\4?\t?\4@\t@\4A\tA\4B\tB\4C\tC\4D\tD\4E\tE\4F\tF\4G\tG\4H\tH\4"+
		"I\tI\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\5\2\u009e\n\2\3\3\3\3\3\3\3\3"+
		"\3\3\3\4\3\4\3\5\3\5\3\5\3\6\3\6\3\7\3\7\3\b\3\b\3\t\3\t\3\t\3\n\3\n\3"+
		"\13\3\13\3\f\3\f\3\r\3\r\3\16\3\16\3\17\3\17\3\20\3\20\3\21\3\21\3\22"+
		"\3\22\3\23\3\23\7\23\u00c7\n\23\f\23\16\23\u00ca\13\23\3\24\3\24\3\24"+
		"\5\24\u00cf\n\24\3\25\3\25\5\25\u00d3\n\25\3\26\3\26\3\27\3\27\3\30\3"+
		"\30\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\32\3\32\3\32\3\32\3\32\3\32\3"+
		"\32\5\32\u00e9\n\32\3\33\3\33\3\34\6\34\u00ee\n\34\r\34\16\34\u00ef\3"+
		"\34\3\34\3\35\6\35\u00f5\n\35\r\35\16\35\u00f6\3\35\3\35\3\36\6\36\u00fc"+
		"\n\36\r\36\16\36\u00fd\3\36\3\36\3\37\6\37\u0103\n\37\r\37\16\37\u0104"+
		"\3\37\3\37\3 \6 \u010a\n \r \16 \u010b\3 \3 \3!\6!\u0111\n!\r!\16!\u0112"+
		"\3!\3!\3\"\6\"\u0118\n\"\r\"\16\"\u0119\3\"\3\"\3#\5#\u011f\n#\3#\3#\3"+
		"#\3#\3#\3#\3$\3$\5$\u0129\n$\3$\3$\3%\3%\3%\5%\u0130\n%\3%\3%\3&\3&\5"+
		"&\u0136\n&\3&\3&\3&\3&\3&\3&\5&\u013e\n&\3\'\3\'\3\'\3\'\3\'\3\'\3\'\3"+
		"\'\3\'\3\'\3\'\3\'\3\'\3\'\5\'\u014e\n\'\3(\5(\u0151\n(\3(\6(\u0154\n"+
		"(\r(\16(\u0155\3(\3(\7(\u015a\n(\f(\16(\u015d\13(\3(\5(\u0160\n(\3(\3"+
		"(\6(\u0164\n(\r(\16(\u0165\3(\5(\u0169\n(\3(\6(\u016c\n(\r(\16(\u016d"+
		"\3(\3(\5(\u0172\n(\3)\3)\5)\u0176\n)\3)\6)\u0179\n)\r)\16)\u017a\3*\3"+
		"*\3*\3+\3+\3+\3,\5,\u0184\n,\3,\6,\u0187\n,\r,\16,\u0188\3-\5-\u018c\n"+
		"-\3-\3-\3-\6-\u0191\n-\r-\16-\u0192\3.\3.\5.\u0197\n.\3/\3/\5/\u019b\n"+
		"/\3\60\3\60\3\61\3\61\3\62\3\62\3\63\3\63\3\64\3\64\3\65\3\65\3\66\3\66"+
		"\3\67\3\67\38\38\39\39\39\39\3:\3:\3;\6;\u01b6\n;\r;\16;\u01b7\3;\3;\3"+
		"<\3<\3<\3<\7<\u01c0\n<\f<\16<\u01c3\13<\3<\3<\3<\3<\3<\3=\3=\3=\3=\7="+
		"\u01ce\n=\f=\16=\u01d1\13=\3=\3=\3>\3>\3?\3?\3@\3@\3@\3@\3@\3@\3@\3A\3"+
		"A\3A\3B\3B\3B\3C\3C\3C\3D\3D\3D\3E\3E\3E\3F\3F\3F\3G\3G\3G\3H\6H\u01f6"+
		"\nH\rH\16H\u01f7\3I\3I\3I\3I\3\u01c1\2J\4\3\6\4\b\5\n\6\f\7\16\b\20\t"+
		"\22\n\24\13\26\f\30\r\32\16\34\17\36\20 \21\"\22$\23&\24(\2*\2,\2.\2\60"+
		"\2\62\25\64\26\66\278\2:\2<\2>\2@\2B\2D\2F\2H\30J\31L\32N\2P\2R\2T\33"+
		"V\34X\35Z\36\\\2^\2`\2b\2d\2f\2h\2j\2l\2n\2p\2r\37t\2v x!z\"|#~\2\u0080"+
		"$\u0082%\u0084&\u0086\'\u0088(\u008a)\u008c*\u008e+\u0090,\u0092-\4\2"+
		"\3\16\4\2C\\c|\4\2CHch\4\2--//\4\2NNnn\4\2HHhh\4\2FFff\4\2DDdd\4\2GGg"+
		"g\4\2ZZzz\5\2\13\f\16\17\"\"\4\2\f\f\17\17\4\2))^^\u020d\2\4\3\2\2\2\2"+
		"\6\3\2\2\2\2\b\3\2\2\2\2\n\3\2\2\2\2\f\3\2\2\2\2\16\3\2\2\2\2\20\3\2\2"+
		"\2\2\22\3\2\2\2\2\24\3\2\2\2\2\26\3\2\2\2\2\30\3\2\2\2\2\32\3\2\2\2\2"+
		"\34\3\2\2\2\2\36\3\2\2\2\2 \3\2\2\2\2\"\3\2\2\2\2$\3\2\2\2\2&\3\2\2\2"+
		"\2\62\3\2\2\2\2\64\3\2\2\2\2\66\3\2\2\2\2H\3\2\2\2\2J\3\2\2\2\2L\3\2\2"+
		"\2\2T\3\2\2\2\2V\3\2\2\2\2X\3\2\2\2\2Z\3\2\2\2\2r\3\2\2\2\2v\3\2\2\2\2"+
		"x\3\2\2\2\2z\3\2\2\2\2|\3\2\2\2\3\u0080\3\2\2\2\3\u0082\3\2\2\2\3\u0084"+
		"\3\2\2\2\3\u0086\3\2\2\2\3\u0088\3\2\2\2\3\u008a\3\2\2\2\3\u008c\3\2\2"+
		"\2\3\u008e\3\2\2\2\3\u0090\3\2\2\2\3\u0092\3\2\2\2\4\u009d\3\2\2\2\6\u009f"+
		"\3\2\2\2\b\u00a4\3\2\2\2\n\u00a6\3\2\2\2\f\u00a9\3\2\2\2\16\u00ab\3\2"+
		"\2\2\20\u00ad\3\2\2\2\22\u00af\3\2\2\2\24\u00b2\3\2\2\2\26\u00b4\3\2\2"+
		"\2\30\u00b6\3\2\2\2\32\u00b8\3\2\2\2\34\u00ba\3\2\2\2\36\u00bc\3\2\2\2"+
		" \u00be\3\2\2\2\"\u00c0\3\2\2\2$\u00c2\3\2\2\2&\u00c4\3\2\2\2(\u00ce\3"+
		"\2\2\2*\u00d2\3\2\2\2,\u00d4\3\2\2\2.\u00d6\3\2\2\2\60\u00d8\3\2\2\2\62"+
		"\u00da\3\2\2\2\64\u00e8\3\2\2\2\66\u00ea\3\2\2\28\u00ed\3\2\2\2:\u00f4"+
		"\3\2\2\2<\u00fb\3\2\2\2>\u0102\3\2\2\2@\u0109\3\2\2\2B\u0110\3\2\2\2D"+
		"\u0117\3\2\2\2F\u011e\3\2\2\2H\u0128\3\2\2\2J\u012f\3\2\2\2L\u013d\3\2"+
		"\2\2N\u014d\3\2\2\2P\u0150\3\2\2\2R\u0173\3\2\2\2T\u017c\3\2\2\2V\u017f"+
		"\3\2\2\2X\u0183\3\2\2\2Z\u018b\3\2\2\2\\\u0196\3\2\2\2^\u019a\3\2\2\2"+
		"`\u019c\3\2\2\2b\u019e\3\2\2\2d\u01a0\3\2\2\2f\u01a2\3\2\2\2h\u01a4\3"+
		"\2\2\2j\u01a6\3\2\2\2l\u01a8\3\2\2\2n\u01aa\3\2\2\2p\u01ac\3\2\2\2r\u01ae"+
		"\3\2\2\2t\u01b2\3\2\2\2v\u01b5\3\2\2\2x\u01bb\3\2\2\2z\u01c9\3\2\2\2|"+
		"\u01d4\3\2\2\2~\u01d6\3\2\2\2\u0080\u01d8\3\2\2\2\u0082\u01df\3\2\2\2"+
		"\u0084\u01e2\3\2\2\2\u0086\u01e5\3\2\2\2\u0088\u01e8\3\2\2\2\u008a\u01eb"+
		"\3\2\2\2\u008c\u01ee\3\2\2\2\u008e\u01f1\3\2\2\2\u0090\u01f5\3\2\2\2\u0092"+
		"\u01f9\3\2\2\2\u0094\u0095\7v\2\2\u0095\u0096\7t\2\2\u0096\u0097\7w\2"+
		"\2\u0097\u009e\7g\2\2\u0098\u0099\7h\2\2\u0099\u009a\7c\2\2\u009a\u009b"+
		"\7n\2\2\u009b\u009c\7u\2\2\u009c\u009e\7g\2\2\u009d\u0094\3\2\2\2\u009d"+
		"\u0098\3\2\2\2\u009e\5\3\2\2\2\u009f\u00a0\7p\2\2\u00a0\u00a1\7w\2\2\u00a1"+
		"\u00a2\7n\2\2\u00a2\u00a3\7n\2\2\u00a3\7\3\2\2\2\u00a4\u00a5\7<\2\2\u00a5"+
		"\t\3\2\2\2\u00a6\u00a7\7<\2\2\u00a7\u00a8\7<\2\2\u00a8\13\3\2\2\2\u00a9"+
		"\u00aa\7?\2\2\u00aa\r\3\2\2\2\u00ab\u00ac\7-\2\2\u00ac\17\3\2\2\2\u00ad"+
		"\u00ae\7/\2\2\u00ae\21\3\2\2\2\u00af\u00b0\7/\2\2\u00b0\u00b1\7/\2\2\u00b1"+
		"\23\3\2\2\2\u00b2\u00b3\7*\2\2\u00b3\25\3\2\2\2\u00b4\u00b5\7+\2\2\u00b5"+
		"\27\3\2\2\2\u00b6\u00b7\7]\2\2\u00b7\31\3\2\2\2\u00b8\u00b9\7_\2\2\u00b9"+
		"\33\3\2\2\2\u00ba\u00bb\7}\2\2\u00bb\35\3\2\2\2\u00bc\u00bd\7\177\2\2"+
		"\u00bd\37\3\2\2\2\u00be\u00bf\7.\2\2\u00bf!\3\2\2\2\u00c0\u00c1\7\60\2"+
		"\2\u00c1#\3\2\2\2\u00c2\u00c3\7#\2\2\u00c3%\3\2\2\2\u00c4\u00c8\5(\24"+
		"\2\u00c5\u00c7\5*\25\2\u00c6\u00c5\3\2\2\2\u00c7\u00ca\3\2\2\2\u00c8\u00c6"+
		"\3\2\2\2\u00c8\u00c9\3\2\2\2\u00c9\'\3\2\2\2\u00ca\u00c8\3\2\2\2\u00cb"+
		"\u00cf\5,\26\2\u00cc\u00cf\5.\27\2\u00cd\u00cf\5\60\30\2\u00ce\u00cb\3"+
		"\2\2\2\u00ce\u00cc\3\2\2\2\u00ce\u00cd\3\2\2\2\u00cf)\3\2\2\2\u00d0\u00d3"+
		"\5(\24\2\u00d1\u00d3\5^/\2\u00d2\u00d0\3\2\2\2\u00d2\u00d1\3\2\2\2\u00d3"+
		"+\3\2\2\2\u00d4\u00d5\t\2\2\2\u00d5-\3\2\2\2\u00d6\u00d7\7a\2\2\u00d7"+
		"/\3\2\2\2\u00d8\u00d9\7&\2\2\u00d9\61\3\2\2\2\u00da\u00db\7f\2\2\u00db"+
		"\u00dc\7c\2\2\u00dc\u00dd\7v\2\2\u00dd\u00de\7g\2\2\u00de\u00df\3\2\2"+
		"\2\u00df\u00e0\5\24\n\2\u00e0\63\3\2\2\2\u00e1\u00e9\58\34\2\u00e2\u00e9"+
		"\5:\35\2\u00e3\u00e9\5<\36\2\u00e4\u00e9\5>\37\2\u00e5\u00e9\5@ \2\u00e6"+
		"\u00e9\5B!\2\u00e7\u00e9\5D\"\2\u00e8\u00e1\3\2\2\2\u00e8\u00e2\3\2\2"+
		"\2\u00e8\u00e3\3\2\2\2\u00e8\u00e4\3\2\2\2\u00e8\u00e5\3\2\2\2\u00e8\u00e6"+
		"\3\2\2\2\u00e8\u00e7\3\2\2\2\u00e9\65\3\2\2\2\u00ea\u00eb\5F#\2\u00eb"+
		"\67\3\2\2\2\u00ec\u00ee\5^/\2\u00ed\u00ec\3\2\2\2\u00ee\u00ef\3\2\2\2"+
		"\u00ef\u00ed\3\2\2\2\u00ef\u00f0\3\2\2\2\u00f0\u00f1\3\2\2\2\u00f1\u00f2"+
		"\7[\2\2\u00f29\3\2\2\2\u00f3\u00f5\5^/\2\u00f4\u00f3\3\2\2\2\u00f5\u00f6"+
		"\3\2\2\2\u00f6\u00f4\3\2\2\2\u00f6\u00f7\3\2\2\2\u00f7\u00f8\3\2\2\2\u00f8"+
		"\u00f9\7O\2\2\u00f9;\3\2\2\2\u00fa\u00fc\5^/\2\u00fb\u00fa\3\2\2\2\u00fc"+
		"\u00fd\3\2\2\2\u00fd\u00fb\3\2\2\2\u00fd\u00fe\3\2\2\2\u00fe\u00ff\3\2"+
		"\2\2\u00ff\u0100\7F\2\2\u0100=\3\2\2\2\u0101\u0103\5^/\2\u0102\u0101\3"+
		"\2\2\2\u0103\u0104\3\2\2\2\u0104\u0102\3\2\2\2\u0104\u0105\3\2\2\2\u0105"+
		"\u0106\3\2\2\2\u0106\u0107\7J\2\2\u0107?\3\2\2\2\u0108\u010a\5^/\2\u0109"+
		"\u0108\3\2\2\2\u010a\u010b\3\2\2\2\u010b\u0109\3\2\2\2\u010b\u010c\3\2"+
		"\2\2\u010c\u010d\3\2\2\2\u010d\u010e\7o\2\2\u010eA\3\2\2\2\u010f\u0111"+
		"\5^/\2\u0110\u010f\3\2\2\2\u0111\u0112\3\2\2\2\u0112\u0110\3\2\2\2\u0112"+
		"\u0113\3\2\2\2\u0113\u0114\3\2\2\2\u0114\u0115\7U\2\2\u0115C\3\2\2\2\u0116"+
		"\u0118\5^/\2\u0117\u0116\3\2\2\2\u0118\u0119\3\2\2\2\u0119\u0117\3\2\2"+
		"\2\u0119\u011a\3\2\2\2\u011a\u011b\3\2\2\2\u011b\u011c\7u\2\2\u011cE\3"+
		"\2\2\2\u011d\u011f\5d\62\2\u011e\u011d\3\2\2\2\u011e\u011f\3\2\2\2\u011f"+
		"\u0120\3\2\2\2\u0120\u0121\5^/\2\u0121\u0122\5^/\2\u0122\u0123\5^/\2\u0123"+
		"\u0124\5^/\2\u0124\u0125\7\\\2\2\u0125G\3\2\2\2\u0126\u0129\5P(\2\u0127"+
		"\u0129\5X,\2\u0128\u0126\3\2\2\2\u0128\u0127\3\2\2\2\u0129\u012a\3\2\2"+
		"\2\u012a\u012b\5l\66\2\u012bI\3\2\2\2\u012c\u0130\5P(\2\u012d\u0130\5"+
		"X,\2\u012e\u0130\5N\'\2\u012f\u012c\3\2\2\2\u012f\u012d\3\2\2\2\u012f"+
		"\u012e\3\2\2\2\u0130\u0131\3\2\2\2\u0131\u0132\5h\64\2\u0132K\3\2\2\2"+
		"\u0133\u0135\5P(\2\u0134\u0136\5j\65\2\u0135\u0134\3\2\2\2\u0135\u0136"+
		"\3\2\2\2\u0136\u013e\3\2\2\2\u0137\u0138\5X,\2\u0138\u0139\5j\65\2\u0139"+
		"\u013e\3\2\2\2\u013a\u013b\5N\'\2\u013b\u013c\5j\65\2\u013c\u013e\3\2"+
		"\2\2\u013d\u0133\3\2\2\2\u013d\u0137\3\2\2\2\u013d\u013a\3\2\2\2\u013e"+
		"M\3\2\2\2\u013f\u0140\7-\2\2\u0140\u0141\7P\2\2\u0141\u0142\7c\2\2\u0142"+
		"\u014e\7P\2\2\u0143\u0144\5d\62\2\u0144\u0145\7K\2\2\u0145\u0146\7p\2"+
		"\2\u0146\u0147\7h\2\2\u0147\u0148\7k\2\2\u0148\u0149\7p\2\2\u0149\u014a"+
		"\7k\2\2\u014a\u014b\7v\2\2\u014b\u014c\7{\2\2\u014c\u014e\3\2\2\2\u014d"+
		"\u013f\3\2\2\2\u014d\u0143\3\2\2\2\u014eO\3\2\2\2\u014f\u0151\5d\62\2"+
		"\u0150\u014f\3\2\2\2\u0150\u0151\3\2\2\2\u0151\u0171\3\2\2\2\u0152\u0154"+
		"\5^/\2\u0153\u0152\3\2\2\2\u0154\u0155\3\2\2\2\u0155\u0153\3\2\2\2\u0155"+
		"\u0156\3\2\2\2\u0156\u0157\3\2\2\2\u0157\u015b\5\"\21\2\u0158\u015a\5"+
		"^/\2\u0159\u0158\3\2\2\2\u015a\u015d\3\2\2\2\u015b\u0159\3\2\2\2\u015b"+
		"\u015c\3\2\2\2\u015c\u015f\3\2\2\2\u015d\u015b\3\2\2\2\u015e\u0160\5R"+
		")\2\u015f\u015e\3\2\2\2\u015f\u0160\3\2\2\2\u0160\u0172\3\2\2\2\u0161"+
		"\u0163\5\"\21\2\u0162\u0164\5^/\2\u0163\u0162\3\2\2\2\u0164\u0165\3\2"+
		"\2\2\u0165\u0163\3\2\2\2\u0165\u0166\3\2\2\2\u0166\u0168\3\2\2\2\u0167"+
		"\u0169\5R)\2\u0168\u0167\3\2\2\2\u0168\u0169\3\2\2\2\u0169\u0172\3\2\2"+
		"\2\u016a\u016c\5^/\2\u016b\u016a\3\2\2\2\u016c\u016d\3\2\2\2\u016d\u016b"+
		"\3\2\2\2\u016d\u016e\3\2\2\2\u016e\u016f\3\2\2\2\u016f\u0170\5R)\2\u0170"+
		"\u0172\3\2\2\2\u0171\u0153\3\2\2\2\u0171\u0161\3\2\2\2\u0171\u016b\3\2"+
		"\2\2\u0172Q\3\2\2\2\u0173\u0175\5n\67\2\u0174\u0176\5d\62\2\u0175\u0174"+
		"\3\2\2\2\u0175\u0176\3\2\2\2\u0176\u0178\3\2\2\2\u0177\u0179\5^/\2\u0178"+
		"\u0177\3\2\2\2\u0179\u017a\3\2\2\2\u017a\u0178\3\2\2\2\u017a\u017b\3\2"+
		"\2\2\u017bS\3\2\2\2\u017c\u017d\5X,\2\u017d\u017e\5f\63\2\u017eU\3\2\2"+
		"\2\u017f\u0180\5Z-\2\u0180\u0181\5f\63\2\u0181W\3\2\2\2\u0182\u0184\5"+
		"d\62\2\u0183\u0182\3\2\2\2\u0183\u0184\3\2\2\2\u0184\u0186\3\2\2\2\u0185"+
		"\u0187\5^/\2\u0186\u0185\3\2\2\2\u0187\u0188\3\2\2\2\u0188\u0186\3\2\2"+
		"\2\u0188\u0189\3\2\2\2\u0189Y\3\2\2\2\u018a\u018c\5d\62\2\u018b\u018a"+
		"\3\2\2\2\u018b\u018c\3\2\2\2\u018c\u018d\3\2\2\2\u018d\u018e\5`\60\2\u018e"+
		"\u0190\5p8\2\u018f\u0191\5\\.\2\u0190\u018f\3\2\2\2\u0191\u0192\3\2\2"+
		"\2\u0192\u0190\3\2\2\2\u0192\u0193\3\2\2\2\u0193[\3\2\2\2\u0194\u0197"+
		"\5^/\2\u0195\u0197\t\3\2\2\u0196\u0194\3\2\2\2\u0196\u0195\3\2\2\2\u0197"+
		"]\3\2\2\2\u0198\u019b\5`\60\2\u0199\u019b\5b\61\2\u019a\u0198\3\2\2\2"+
		"\u019a\u0199\3\2\2\2\u019b_\3\2\2\2\u019c\u019d\7\62\2\2\u019da\3\2\2"+
		"\2\u019e\u019f\4\63;\2\u019fc\3\2\2\2\u01a0\u01a1\t\4\2\2\u01a1e\3\2\2"+
		"\2\u01a2\u01a3\t\5\2\2\u01a3g\3\2\2\2\u01a4\u01a5\t\6\2\2\u01a5i\3\2\2"+
		"\2\u01a6\u01a7\t\7\2\2\u01a7k\3\2\2\2\u01a8\u01a9\t\b\2\2\u01a9m\3\2\2"+
		"\2\u01aa\u01ab\t\t\2\2\u01abo\3\2\2\2\u01ac\u01ad\t\n\2\2\u01adq\3\2\2"+
		"\2\u01ae\u01af\5t:\2\u01af\u01b0\3\2\2\2\u01b0\u01b1\b9\2\2\u01b1s\3\2"+
		"\2\2\u01b2\u01b3\7)\2\2\u01b3u\3\2\2\2\u01b4\u01b6\t\13\2\2\u01b5\u01b4"+
		"\3\2\2\2\u01b6\u01b7\3\2\2\2\u01b7\u01b5\3\2\2\2\u01b7\u01b8\3\2\2\2\u01b8"+
		"\u01b9\3\2\2\2\u01b9\u01ba\b;\3\2\u01baw\3\2\2\2\u01bb\u01bc\7\61\2\2"+
		"\u01bc\u01bd\7,\2\2\u01bd\u01c1\3\2\2\2\u01be\u01c0\13\2\2\2\u01bf\u01be"+
		"\3\2\2\2\u01c0\u01c3\3\2\2\2\u01c1\u01c2\3\2\2\2\u01c1\u01bf\3\2\2\2\u01c2"+
		"\u01c4\3\2\2\2\u01c3\u01c1\3\2\2\2\u01c4\u01c5\7,\2\2\u01c5\u01c6\7\61"+
		"\2\2\u01c6\u01c7\3\2\2\2\u01c7\u01c8\b<\3\2\u01c8y\3\2\2\2\u01c9\u01ca"+
		"\7\61\2\2\u01ca\u01cb\7\61\2\2\u01cb\u01cf\3\2\2\2\u01cc\u01ce\n\f\2\2"+
		"\u01cd\u01cc\3\2\2\2\u01ce\u01d1\3\2\2\2\u01cf\u01cd\3\2\2\2\u01cf\u01d0"+
		"\3\2\2\2\u01d0\u01d2\3\2\2\2\u01d1\u01cf\3\2\2\2\u01d2\u01d3\b=\3\2\u01d3"+
		"{\3\2\2\2\u01d4\u01d5\13\2\2\2\u01d5}\3\2\2\2\u01d6\u01d7\7^\2\2\u01d7"+
		"\177\3\2\2\2\u01d8\u01d9\5~?\2\u01d9\u01da\7w\2\2\u01da\u01db\5\\.\2\u01db"+
		"\u01dc\5\\.\2\u01dc\u01dd\5\\.\2\u01dd\u01de\5\\.\2\u01de\u0081\3\2\2"+
		"\2\u01df\u01e0\5~?\2\u01e0\u01e1\7d\2\2\u01e1\u0083\3\2\2\2\u01e2\u01e3"+
		"\5~?\2\u01e3\u01e4\7v\2\2\u01e4\u0085\3\2\2\2\u01e5\u01e6\5~?\2\u01e6"+
		"\u01e7\7p\2\2\u01e7\u0087\3\2\2\2\u01e8\u01e9\5~?\2\u01e9\u01ea\7h\2\2"+
		"\u01ea\u0089\3\2\2\2\u01eb\u01ec\5~?\2\u01ec\u01ed\7t\2\2\u01ed\u008b"+
		"\3\2\2\2\u01ee\u01ef\5~?\2\u01ef\u01f0\7)\2\2\u01f0\u008d\3\2\2\2\u01f1"+
		"\u01f2\5~?\2\u01f2\u01f3\5~?\2\u01f3\u008f\3\2\2\2\u01f4\u01f6\n\r\2\2"+
		"\u01f5\u01f4\3\2\2\2\u01f6\u01f7\3\2\2\2\u01f7\u01f5\3\2\2\2\u01f7\u01f8"+
		"\3\2\2\2\u01f8\u0091\3\2\2\2\u01f9\u01fa\5t:\2\u01fa\u01fb\3\2\2\2\u01fb"+
		"\u01fc\bI\4\2\u01fc\u0093\3\2\2\2*\2\3\u009d\u00c8\u00ce\u00d2\u00e8\u00ef"+
		"\u00f6\u00fd\u0104\u010b\u0112\u0119\u011e\u0128\u012f\u0135\u013d\u014d"+
		"\u0150\u0155\u015b\u015f\u0165\u0168\u016d\u0171\u0175\u017a\u0183\u0188"+
		"\u018b\u0192\u0196\u019a\u01b7\u01c1\u01cf\u01f7\5\7\3\2\b\2\2\6\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}