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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import com.braintribe.model.management.MetaModelValidationViolation;
import com.braintribe.model.management.MetaModelValidationViolationType;
import com.braintribe.model.meta.GmEntityType;
import com.braintribe.model.meta.GmMetaModel;

public class TypesLoopsInHierarchyCheck implements ValidatorCheck {
	
	@Override
	public boolean check(GmMetaModel metaModel, List<MetaModelValidationViolation> validationErrors) {	
		Set<List<String>> loops = new HashSet<List<String>>();
//		if (metaModel.getEntityTypes() != null) {
//			for (GmEntityType t : metaModel.getEntityTypes()) {
//				Stack<GmEntityType> stack = new Stack<GmEntityType>();
//				
//				if (t != null) {
//					stack.push(t);
//					checkLoops(stack, loops);
//					stack.pop();
//				}
//			}
//		}
		
		for (List<String> loop : loops) {
			ViolationBuilder.to(validationErrors).withEntityTypeList(loop)
				.add(MetaModelValidationViolationType.TYPE_HIERARCHY_LOOP, describeLoop(loop));
		}
		
		return true;
	}

	private String describeLoop(List<String> loop) {
		StringBuilder sb = new StringBuilder();
		if (loop != null) {
			for (int i = 0; i <= loop.size(); i++) { //size + 1 to show that it loops 
				sb.append(loop.get(i % loop.size()));
				if (i != loop.size()) {
					sb.append("->");
				}
			}
		}
		return null;
	}

	private void checkLoops(Stack<GmEntityType> stack, Set<List<String>> loops) {
		int loopIndex = -1;
		GmEntityType entityType = stack.peek();
		if (stack.size() > 1) {
			for (int i = stack.size() - 2; i >= 0; i--) {
				if ((stack.get(i) != null && entityType != null) && 
						stack.get(i).getTypeSignature().equals(entityType.getTypeSignature())) {
					loopIndex = i;
					break;
				}
			}
		}
		
		if (loopIndex > -1) {
			loops.add(normalizeLoop(stack, loopIndex));
		} else {
			if ((entityType != null) && (entityType.getSuperTypes() != null)) {
				for (GmEntityType superType : entityType.getSuperTypes()) {

					if (superType != null) {
						stack.push(superType);
						checkLoops(stack, loops);
						stack.pop();
					}
				}
			}
		}
	}

	private List<String> normalizeLoop(Stack<GmEntityType> stack, int loopIndex) {
		List<String> loop = new ArrayList<String>(); 
		for (int i = loopIndex; i < stack.size() - 1; i++) {
			loop.add(stack.get(i) == null ? null : stack.get(i).getTypeSignature());
		}
		
		int indexOfMin = loop.indexOf(Collections.min(loop));
		
		List<String> res = new ArrayList<String>();
		for (int i = indexOfMin; i < indexOfMin + loop.size(); i++) {
			res.add(loop.get(i % loop.size()));
		}
		return res;
	}

}
