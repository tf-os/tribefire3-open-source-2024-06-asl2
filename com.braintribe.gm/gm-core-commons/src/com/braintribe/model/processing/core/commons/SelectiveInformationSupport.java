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
package com.braintribe.model.processing.core.commons;

import static com.braintribe.utils.lcd.CollectionTools2.newList;

import java.util.List;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.pr.criteria.TraversingCriterion;
import com.braintribe.model.generic.processing.pr.fluent.JunctionBuilder;
import com.braintribe.model.generic.processing.pr.fluent.PatternBuilder;
import com.braintribe.model.generic.processing.pr.fluent.TC;
import com.braintribe.model.generic.typecondition.TypeConditions;
import com.braintribe.model.generic.typecondition.basic.TypeKind;

/**
 * @see #extractPropertyChains(String)
 * @see #buildTcFor(List)
 * 
 * @author peter.gazdik
 */
public class SelectiveInformationSupport {

	public final static String SI_TYPE_ALT = "_type";
	public final static String SI_TYPE = "#type";
	public final static String SI_TYPE_SHORT = "#type_short";
	public final static String SI_ID = "#id";
	public final static String SI_RUNTIME_ID = "#runtimeId";

	/**
	 * From given selectiveInformation template it extracts the property chains, i.e. each expression like
	 * ${property1.property2} is converted into the corresponding String array (here it would be ["property1",
	 * "property2"]).
	 */
	public static List<String[]> extractPropertyChains(String selectiveInformation) {
		String s = removeEscapedExpressions(selectiveInformation);
		return extractPropertyExpressionsHelper(s);
	}

	// protected to be testable
	protected static String removeEscapedExpressions(String s) {
		boolean firstPass = true;
		StringBuilder sb = null;

		while (true) {
			int start = s.indexOf("$${");
			if (start < 0) {
				if (firstPass)
					return s;

				sb.append(s);
				return sb.toString();
			}

			if (firstPass) {
				sb = new StringBuilder();
				firstPass = false;
			}

			sb.append(s.substring(0, start));

			s = s.substring(start + 3);
		}
	}

	/**
	 * This method assumes every single ${ is a beginning of a resolvable expression, i.e. no escaped ones are present
	 * in given String.
	 */
	private static List<String[]> extractPropertyExpressionsHelper(String s) {
		List<String[]> result = newList();

		while (true) {
			int start = s.indexOf("${");
			if (start < 0)
				return result;

			s = s.substring(start + 2);
			int end = s.indexOf('}');

			if (end < 0)
				return result;

			String resolvableExpression = s.substring(0, end);
			if (isPropertyExpression(resolvableExpression))
				result.add(resolvableExpression.split("\\."));

			s = s.substring(end + 1);
		}
	}

	/**
	 * @return <tt>true</tt> iff given expression represents a property, and not one of the special values like type
	 *         signature.
	 */
	public static boolean isPropertyExpression(String resolvableExpression) {
		char firstChar = resolvableExpression.charAt(0);
		return firstChar != '#' && firstChar != '_';
	}

	/**
	 * Builds {@link TraversingCriterion} which ensure property chains given as params are present in the query result
	 * for some entity.
	 */
	public static TraversingCriterion buildTcFor(List<String[]> propertyChains) {
		// Please do not delete the "close" invocations, they make the last line easier to understand.

		// @formatter:off
		JunctionBuilder<PatternBuilder<JunctionBuilder<TC>>> tcBuilder = TC.create()
					.negation()
						.disjunction()
							.property(GenericEntity.id)
							.typeCondition(TypeConditions.isKind(TypeKind.simpleType))
							.pattern()
								  .entity()
								  .disjunction();
								  // property chains...
			//					  .close()
			//				 .close()
			//			.close()
			//	.done();
		// @formatter:on

		for (String[] propertyChain : propertyChains)
			tcBuilder = propertyChain(tcBuilder, propertyChain);

		return tcBuilder.close().close().close().done();
	}

	/**
	 * For a property chain like 'entity.property1.property2' we need to ad two patterns:
	 * <ol>
	 * <li>entity.property1</li>
	 * <li>entity.property1.entity.property2</li>
	 * </ol>
	 * 
	 * That is why we do the thing with {@link #propertyChainPrefix(JunctionBuilder, String[], int)}
	 */
	private static <T> JunctionBuilder<T> propertyChain(JunctionBuilder<T> tcBuilder, String[] propertyChain) {
		tcBuilder = tcBuilder.property(propertyChain[0]);

		for (int i = 2; i <= propertyChain.length; i++)
			tcBuilder = propertyChainPrefix(tcBuilder, propertyChain, i);

		return tcBuilder;
	}

	private static <T> JunctionBuilder<T> propertyChainPrefix(JunctionBuilder<T> tcBuilder, String[] propertyChain, int steps) {
		PatternBuilder<JunctionBuilder<T>> patternBuilder = tcBuilder.pattern();
		for (int i = 0; i < steps; i++) {
			if (i > 0)
				patternBuilder = patternBuilder.entity();

			String property = propertyChain[i];
			patternBuilder = patternBuilder.property(property);
		}

		return patternBuilder.close();
	}

}
