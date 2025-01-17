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
package tribefire.extension.messaging.model.conditions;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.annotation.Abstract;
import com.braintribe.model.generic.annotation.meta.Name;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.meta.selector.Operator;

import java.util.Collection;
import java.util.Map;
import java.util.regex.Pattern;

@Abstract
@Name("Compare")
public interface Comparison extends GenericEntity {

	EntityType<Comparison> T = EntityTypes.T(Comparison.class);

	static int compare(Object left, Object right) {
		if (left == null) {
			return right == null ? 0 : -1;
		}

		if (right == null) {
			return 1;
		}

		try {
			return ((Comparable<Object>) left).compareTo(right);
		} catch (ClassCastException e) {
			throw new IllegalArgumentException("Unsupported left comparison operand: " + left + ". Right operand: " + right);
		}
	}

	static boolean compareEquality(Object a, Object b) {
		if (a == b) {
			return true;
		}
		if (a == null || b == null) {
			return false;
		}
		return a.equals(b);
	}

	static boolean compareContains(Object collection, Object element, Operator operator) {
		if (collection == null) {
			return false;
		}

		if (collection instanceof Collection<?>) {
			return ((Collection<?>) collection).contains(element);

		} else if (collection instanceof Map<?, ?>) {
			return ((Map<?, ?>) collection).containsValue(element);

		} else {
			throw new IllegalArgumentException("Cannot evaluate " + operator + " operator. Operand is not a collection: " + collection + "["
					+ collection.getClass().getName() + "]");
		}
	}


	static boolean compareLike(Object left, Object right, boolean caseInsensitvie) {
		if (left instanceof String && right instanceof String)
			return caseInsensitvie ? ilike((String) left, (String) right) : like((String) left, (String) right);
		else
			return false;
	}

	static boolean like(String left, String right) {
		return left != null && likeHelper(left, right);
	}

	static boolean ilike(String left, String right) {
		return left != null && likeHelper(left.toLowerCase(), right.toLowerCase());
	}

	static boolean likeHelper(String left, String right) {
		return left.matches(convertToRegexPattern(right));
	}

	static String convertToRegexPattern(String pattern) {
		StringBuilder builder = new StringBuilder();
		StringBuilder tokenBuilder = new StringBuilder();

		int escapeLock = -1;
		for (int i = 0; i < pattern.length(); i++) {
			char c = pattern.charAt(i);
			switch (c) {
				case '*'->{
					if (escapeLock == i) {
						tokenBuilder.append(c);
					} else {
						appendToken(builder, tokenBuilder);
						builder.append(".*");
					}
				}
				case '?'-> {
					if (escapeLock == i) {
						tokenBuilder.append(c);
					} else {
						appendToken(builder, tokenBuilder);
						builder.append(".");
					}
				}
				case '\\'-> {
					if (escapeLock == i) {
						tokenBuilder.append(c);
					} else {
						escapeLock = i + 1;
					}
				}
				default->
					tokenBuilder.append(c);
			}
		}
		appendToken(builder, tokenBuilder);

		return builder.toString();
	}

	static void appendToken(StringBuilder builder, StringBuilder tokenBuilder) {
		String token = tokenBuilder.toString();
		builder.append(Pattern.quote(token));
		tokenBuilder.setLength(0);
	}
}

