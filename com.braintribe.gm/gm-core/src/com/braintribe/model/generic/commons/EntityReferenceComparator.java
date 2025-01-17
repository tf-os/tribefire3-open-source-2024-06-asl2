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
package com.braintribe.model.generic.commons;

import java.util.Comparator;

import com.braintribe.model.generic.value.EntityReference;

/**
 * This is useful for keeping a stable order when processing entity references. 
 */
public class EntityReferenceComparator implements Comparator<EntityReference> {
	public final static EntityReferenceComparator INSTANCE = new EntityReferenceComparator();

	public static boolean isSameReferenceKind(EntityReference e1, EntityReference e2) {
		return e1.referenceType() == e2.referenceType();
	}

	@Override
	public int compare(EntityReference o1, EntityReference o2) {
		if (o1 == o2)
			return 0;

		if (!isSameReferenceKind(o1, o2)) {
			return o1.referenceType().compareTo(o2.referenceType());
		}

		String t1 = o1.getTypeSignature();
		String t2 = o2.getTypeSignature();

		if (t1 != t2) {
			if (t1 == null)
				return -1;

			if (t2 == null)
				return 1;

			int res = t1.compareTo(t2);
			if (res != 0)
				return res;
		}

		Comparable<Object> id1 = (Comparable<Object>) o1.getRefId();
		Comparable<Object> id2 = (Comparable<Object>) o2.getRefId();

		if (id1 != id2) {
			if (id1 == null)
				return -1;
			if (id2 == null)
				return 1;

			int res = id1.compareTo(id2);
			if (res != 0)
				return res;
		}

		String p1 = o1.getRefPartition();
		String p2 = o1.getRefPartition();

		if (p1 != p2) {
			if (p1 == null)
				return -1;
			if (p2 == null)
				return 1;

			return p1.compareTo(p2);
		}

		return 0;
	}

}
