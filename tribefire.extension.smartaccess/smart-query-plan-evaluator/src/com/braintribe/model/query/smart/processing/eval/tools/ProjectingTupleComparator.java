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
package com.braintribe.model.query.smart.processing.eval.tools;

import java.util.Collection;

import com.braintribe.cc.lcd.HashingComparator;
import com.braintribe.model.processing.query.eval.api.Tuple;

/**
 * A comparator that compares a projection of a tuple (by only considering a sub-set of the tuple components, given as a
 * {@code Collection<Integer>} via constructor).
 */
public class ProjectingTupleComparator implements HashingComparator<Tuple> {

	private final Collection<Integer> positions;

	public ProjectingTupleComparator(Collection<Integer> positions) {
		this.positions = positions;
	}

	@Override
	public boolean compare(Tuple t1, Tuple t2) {
		for (Integer position: positions) {
			Object o1 = t1.getValue(position);
			Object o2 = t2.getValue(position);

			if (!compareValues(o1, o2)) {
				return false;
			}

		}
		return true;
	}

	@Override
	public int computeHash(Tuple t) {
		int result = 0;

		// equal objects must have equal hash codes
		//
		for (Integer position: positions) {
			Object o1 = t.getValue(position);
			result = 31 * result + hashValue(o1);
		}

		return result;
	}

	private boolean compareValues(Object o1, Object o2) {
		if (o1 == o2) {
			return true;
		}

		return o1 == null ? false : o1.equals(o2);
	}

	private int hashValue(Object o1) {
		return o1 == null ? 1 : o1.hashCode();
	}

}
