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
package com.braintribe.common.lcd;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Provides empty objects, e.g. an empty list or array. Unless otherwise specified, the empty objects are immutable.
 *
 * @author michael.lafite
 */
public final class Empty {

	private static final List<?> LIST = Collections.EMPTY_LIST;
	private static final Set<?> SET = Collections.EMPTY_SET;
	private static final Map<?, ?> MAP = Collections.EMPTY_MAP;

	private static final String STRING = "";

	private static final boolean[] BOOLEAN_ARRAY = new boolean[0];
	private static final byte[] BYTE_ARRAY = new byte[0];
	private static final char[] CHAR_ARRAY = new char[0];
	private static final double[] DOUBLE_ARRAY = new double[0];
	private static final float[] FLOAT_ARRAY = new float[0];
	private static final int[] INT_ARRAY = new int[0];
	private static final long[] LONG_ARRAY = new long[0];
	private static final short[] SHORT_ARRAY = new short[0];
	private static final Object[] OBJECT_ARRAY = new Object[0];
	private static final String[] STRING_ARRAY = new String[0];

	private Empty() {
		// no instantiation required
	}

	public static <E> List<E> list() {
		return (List<E>) LIST;
	}

	public static <E> Set<E> set() {
		return (Set<E>) SET;
	}

	public static <K, V> Map<K, V> map() {
		return (Map<K, V>) MAP;
	}

	public static <E> Collection<E> collection() {
		return list();
	}

	public static String string() {
		return STRING;
	}

	public static boolean[] booleanArray() {
		return BOOLEAN_ARRAY;
	}

	public static byte[] byteArray() {
		return BYTE_ARRAY;
	}

	public static char[] charArray() {
		return CHAR_ARRAY;
	}

	public static double[] doubleArray() {
		return DOUBLE_ARRAY;
	}

	public static float[] floatArray() {
		return FLOAT_ARRAY;
	}

	public static int[] intArray() {
		return INT_ARRAY;
	}

	public static long[] longArray() {
		return LONG_ARRAY;
	}

	public static short[] shortArray() {
		return SHORT_ARRAY;
	}

	public static Object[] objectArray() {
		return OBJECT_ARRAY;
	}

	public static String[] stringArray() {
		return STRING_ARRAY;
	}

}
