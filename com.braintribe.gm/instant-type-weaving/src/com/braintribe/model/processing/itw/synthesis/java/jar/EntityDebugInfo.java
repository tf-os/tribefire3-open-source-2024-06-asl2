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
package com.braintribe.model.processing.itw.synthesis.java.jar;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import com.braintribe.model.processing.itw.asm.DebugInfoProvider;

/**
 * Provides debug information for entity source code given as a {@link EntityDebugInfo#EntityDebugInfo(String)
 * constructor} argument.
 * 
 * @see DebugInfoProvider
 */
class EntityDebugInfo {

	private final Map<String, Integer> methodLines = new HashMap<String, Integer>();
	private final Map<String, String> setterParameters = new HashMap<String, String>();

	private static final String VAR = "[^\\s(){};]+";
	private static final String S = "\\s*";
	private static final String Sp = "\\s+";
	private static final String LINE_WITH_GETTER = "public" + Sp + VAR + Sp + "get" + VAR + S + "\\(" + S + "\\).*";
	private static final String LINE_WITH_SETTER = "public\\s+void\\s+set" + VAR + S + "+\\(" + S + VAR + Sp + VAR + S + "\\).*";

	public EntityDebugInfo(String source) {
		int counter = 0;

		Scanner scanner = new Scanner(source);
		while (scanner.hasNextLine()) {
			process(scanner.nextLine(), ++counter);
		}

		scanner.close();
	}

	private void process(String line, int num) {
		line = line.trim();
		if (line.matches(LINE_WITH_GETTER)) {
			processGetter(line, num);

		} else if (line.matches(LINE_WITH_SETTER)) {
			processSetter(line, num);
		}

	}

	private void processGetter(String line, int num) {
		registerMethod(line, num);
	}

	private void processSetter(String line, int num) {
		String setterName = registerMethod(line, num);
		registerSetterParameter(setterName, line);
	}

	private String registerMethod(String line, int num) {
		int pos = line.indexOf("(");
		line = line.substring(0, pos);

		pos = line.lastIndexOf(" ");
		line = line.substring(pos + 1);

		methodLines.put(line, num + 1); // we want the next line

		return line;
	}

	private void registerSetterParameter(String setterName, String line) {
		int pos = line.indexOf(")");
		line = line.substring(0, pos).trim();

		pos = line.lastIndexOf(" ");
		line = line.substring(pos + 1);

		setterParameters.put(setterName, line);
	}

	public Integer getMethodLine(String methodName) {
		return methodLines.get(methodName);
	}

	public String getSetterParameterName(String setterName) {
		return setterParameters.get(setterName);
	}

}
