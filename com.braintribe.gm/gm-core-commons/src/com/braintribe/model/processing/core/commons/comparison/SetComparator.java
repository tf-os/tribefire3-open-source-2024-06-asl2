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
package com.braintribe.model.processing.core.commons.comparison;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.processing.traversing.api.path.TraversingSetItemModelPathElement;

public class SetComparator implements Comparator<Object> {
	private Comparator<Object> elementComparator;
	private Comparator<Object> internalElementComparator;
	private AssemblyComparison assemblyComparison;
	private GenericModelType elementType;
	
	public SetComparator(AssemblyComparison assemblyComparison, GenericModelType elementType, Comparator<Object> elementComparator, Comparator<Object> internalElementComparator) {
		super();
		this.assemblyComparison = assemblyComparison;
		this.elementType = elementType;
		this.elementComparator = elementComparator;
		this.internalElementComparator = internalElementComparator;
	}
	
	@Override
	public int compare(Object o1, Object o2) {
		if (o1 == o2)
			return 0;
		
		if (o1 == null)
			return -1;
		
		if (o2 == null)
			return 1;
		
		@SuppressWarnings("unchecked")
		Set<Object> s1 = (Set<Object>)o1;
		@SuppressWarnings("unchecked")
		Set<Object> s2 = (Set<Object>)o2;
		
		int res = s1.size() - s2.size();
		
		if (res != 0) {
			assemblyComparison.setMismatchDescription("sets differ in size: " + s1.size() + " vs. " + s2.size());
			return res;
		}
		
		List<Object> sortedSet1 = new ArrayList<>(s1);
		List<Object> sortedSet2 = new ArrayList<>(s2);
		sortedSet1.sort(internalElementComparator);
		sortedSet2.sort(internalElementComparator);
		
		Iterator<Object> it1 = sortedSet1.iterator();
		Iterator<Object> it2 = sortedSet2.iterator();
		
		while (it1.hasNext()) {
			Object e1 = it1.next();
			Object e2 = it2.next();
			
			assemblyComparison.pushElement(p -> new TraversingSetItemModelPathElement(p, e1, elementType.getActualType(e1)));

			res = elementComparator.compare(e1, e2);

			if (res != 0)
				return res;
			else
				assemblyComparison.popElement();
		}
		
		return 0;
	}
	
}
