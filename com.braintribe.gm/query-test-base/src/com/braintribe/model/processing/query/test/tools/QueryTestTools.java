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
package com.braintribe.model.processing.query.test.tools;

import static com.braintribe.utils.lcd.CollectionTools2.newList;
import static com.braintribe.utils.lcd.CollectionTools2.newMap;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.braintribe.model.record.ListRecord;

public class QueryTestTools {

	public static Map<Object, Integer> processResult(List<?> results) {
		if (results.isEmpty())
			return Collections.emptyMap();

		boolean areRecords = results.get(0) instanceof ListRecord;

		if (areRecords)
			return fill(newMap(), copyListRecords(results));
		else
			return fill(newMap(), results);
	}

	private static List<?> copyListRecords(List<?> records) {
		List<List<?>> result = newList();

		for (Object o : records) {
			List<Object> values = ((ListRecord) o).getValues();
			result.add(values);
		}

		return result;
	}

	public static List<?> asList(Object o, Object[] os) {
		List<Object> values = newList();
		values.add(o);
		values.addAll(Arrays.asList(os));

		return values;
	}

	private static Map<Object, Integer> fill(Map<?, Integer> _map, List<?> results) {
		Map<Object, Integer> map = (Map<Object, Integer>) _map;

		for (Object o : results) {
			int val = 1;
			if (map.containsKey(o))
				val = map.get(o) + 1;

			map.put(o, val);
		}

		return map;
	}

}
