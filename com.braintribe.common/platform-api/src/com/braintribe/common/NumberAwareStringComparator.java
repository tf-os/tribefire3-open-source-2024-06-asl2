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
package com.braintribe.common;

import java.math.BigInteger;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Compares two strings alphabetically with the twist that numbers within those strings are being compared as numbers, not as strings. Any number is
 * considered "smaller" than any other character. Signs and commas are considered characters.
 * <p>
 * For example <code>"somestring 000000989"</code> is equal to <code>"somestring 989"</code> or <code>"somestring 10"</code> is bigger than
 * <code>"somestring 2"</code>
 * <p>
 * This works of course as well with multiple groups of numbers in a string
 */
public class NumberAwareStringComparator implements Comparator<CharSequence> {
	public static final NumberAwareStringComparator INSTANCE = new NumberAwareStringComparator();

	private static final Pattern PATTERN = Pattern.compile("(\\D*)(\\d*)");

	/**
	 * @deprecated There is no need to create an instance of this class. Just use {@link #INSTANCE} instead.
	 */
	@Deprecated
	public NumberAwareStringComparator() {
		// nothing to do
	}

	@Override
	public int compare(CharSequence s1, CharSequence s2) {
		Matcher m1 = PATTERN.matcher(s1);
		Matcher m2 = PATTERN.matcher(s2);

		// The only way find() could fail is at the end of a string
		while (m1.find() && m2.find()) {
			// matcher.group(1) fetches any non-digits captured by the
			// first parentheses in PATTERN.
			int nonDigitCompare = m1.group(1).compareTo(m2.group(1));
			if (0 != nonDigitCompare) {
				return nonDigitCompare;
			}

			// matcher.group(2) fetches any digits captured by the
			// second parentheses in PATTERN.
			if (m1.group(2).isEmpty()) {
				return m2.group(2).isEmpty() ? 0 : -1;
			} else if (m2.group(2).isEmpty()) {
				return +1;
			}

			BigInteger n1 = new BigInteger(m1.group(2));
			BigInteger n2 = new BigInteger(m2.group(2));
			int numberCompare = n1.compareTo(n2);
			if (0 != numberCompare) {
				return numberCompare;
			}
		}

		// Handle if one string is a prefix of the other.
		// Nothing comes before something.
		return m1.hitEnd() && m2.hitEnd() ? 0 : m1.hitEnd() ? -1 : +1;
	}
}
