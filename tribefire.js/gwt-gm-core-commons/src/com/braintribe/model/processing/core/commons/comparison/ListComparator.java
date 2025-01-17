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

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.processing.traversing.api.path.TraversingListItemModelPathElement;

public class ListComparator implements Comparator<Object> {
	private Comparator<Object> elementComparator;
	private AssemblyComparison assemblyComparison;
	private GenericModelType elementType;
	
	public ListComparator(AssemblyComparison assemblyComparison, GenericModelType elementType, Comparator<Object> elementComparator) {
		super();
		this.assemblyComparison = assemblyComparison;
		this.elementType = elementType;
		this.elementComparator = elementComparator;
	}

	@Override
	public int compare(Object o1, Object o2) {
		if (o1 == o2)
			return 0;
		
		if (o1 == null)
			return -1;
		
		if (o2 == null)
			return 1;
		
		List<Object> l1 = (List<Object>)o1;
		List<Object> l2 = (List<Object>)o2;
		
		int res = l1.size() - l2.size();

		if (res != 0) {
			assemblyComparison.setMismatchDescription("lists differ in size: " + l1.size() + " vs. " + l2.size());
			return res; 
		}
		
		Iterator<Object> it1 = l1.iterator();
		Iterator<Object> it2 = l2.iterator();
		
		int index = 0;
		while (it1.hasNext()) {
			Object e1 = it1.next();
			Object e2 = it2.next();
			
			int currentIndex = index;
			
			assemblyComparison.pushElement(p -> new TraversingListItemModelPathElement(p, e1, elementType.getActualType(e1), currentIndex));

			res = elementComparator.compare(e1, e2);
			
			if (res != 0)
				return res;
			else
				assemblyComparison.popElement();
			
			index++;
		}
		
		return 0;
	}
}
