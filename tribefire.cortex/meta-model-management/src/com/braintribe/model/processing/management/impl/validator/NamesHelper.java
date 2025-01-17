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
package com.braintribe.model.processing.management.impl.validator;

import static com.braintribe.utils.lcd.CollectionTools2.asSet;

import java.util.Set;
import java.util.regex.Pattern;

import com.braintribe.model.meta.GmEntityType;
import com.braintribe.model.meta.GmEnumType;
import com.braintribe.model.meta.GmType;

public class NamesHelper {
	private static Set<String> JAVA_RESERVED_WORDS = asSet( //
			"abstract", "assert", "boolean", "break", "case", "catch", "class", "const", "continue", "default", "do", "double", "else", "extends",
			"final", "finally", "float", "for", "goto", "if", "implements", "import", "instanceof", "int", "interface", "long", "native", "new",
			"package", "private", "protected", "public", "return", "short", "static", "strictfp", "super", "switch", "synchronized", "this", "throw",
			"throws", "transient", "try", "void", "volatile", "while", "false", "null", "true" //
	);

	public static boolean validMetaModelName(String name) {
		if (name == null || name.trim().isEmpty())
			return false;

		String groupAndId = name;
		int colonIndex = groupAndId.indexOf(':');
		// at least one char before ':', one after ':' and only one ':' separator
		if (colonIndex <= 0 || colonIndex == groupAndId.length() - 1 || groupAndId.lastIndexOf(':') != colonIndex)
			return false;

		String groupPart = groupAndId.substring(0, colonIndex);
		if (!validDotSeparatedIdentifierSequence(groupPart))
			return false;

		String idPart = groupAndId.substring(colonIndex + 1);
		// Separate validation method for model names following maven artifact naming conventions.
		return validArtifactName(idPart);
	}
	
	public static boolean validVersion(String version) {
		if (version == null || version.isEmpty()) {
			return false;
		}

		return version.matches("[0-9\\.]+");
	}

	public static boolean validPropertyOrConstantName(String name, GmType containingType) {
		boolean valid = validAsciiJavaIdentifier(name);
		if (valid) {
			char firstChar = name.charAt(0);
			valid &= Character.isLetter(firstChar) || '$' == firstChar || '_' == firstChar;
			if (containingType instanceof GmEntityType) {
				valid &= !Character.isUpperCase(firstChar);
				if (name.length() > 1) {
					valid &= !Character.isUpperCase(name.charAt(1));
				}
			}
		}
		return valid;
	}

	public static boolean validTypeSignature(String typeSignature, GmType type) {
		boolean valid = validDotSeparatedIdentifierSequence(typeSignature);
		
		if (valid && ((type instanceof GmEntityType) || (type instanceof GmEnumType))) {
			int lastDot = typeSignature.lastIndexOf('.');
			String simpleClassName = typeSignature;
			if (lastDot > 0) {
				simpleClassName = typeSignature.substring(lastDot + 1);
			}
			valid &= validSimpleClassName(simpleClassName);
		}
		return valid;
	}

	private static boolean validArtifactName(String identifier) {
		if (identifier == null)
			return false;

		if (Character.isDigit(identifier.charAt(0)))
			return false;

		if (!Pattern.matches("[a-zA-Z0-9_\\$-]+", identifier))
			return false;

		return true;
	}
	
	private static boolean validSimpleClassName(String simpleClassName) {
		return validAsciiJavaIdentifier(simpleClassName) && Character.isUpperCase(simpleClassName.charAt(0));
	}

	private static boolean validDotSeparatedIdentifierSequence(String identifier) {
		boolean valid = (identifier != null) && (identifier.trim().length() > 0);
		if (valid) {
			String[] parts = identifier.split("\\.");
			for (String part : parts) {
				valid &= NamesHelper.validAsciiJavaIdentifier(part);
			}
		}
		return valid;
	}

	private static boolean validAsciiJavaIdentifier(String identifier) {
		if (identifier == null)
			return false;

		if (Character.isDigit(identifier.charAt(0)))
			return false;

		if (!Pattern.matches("[a-zA-Z0-9_\\$]+", identifier))
			return false;

		if (JAVA_RESERVED_WORDS.contains(identifier))
			return false;

		return true;
	}

}
