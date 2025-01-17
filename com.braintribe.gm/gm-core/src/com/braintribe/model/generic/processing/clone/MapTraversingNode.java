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
package com.braintribe.model.generic.processing.clone;

import java.util.Map;

import com.braintribe.model.generic.pr.criteria.CriterionType;
import com.braintribe.model.generic.reflection.CollectionType;

public class MapTraversingNode extends TraversingNode {
	private Map<?, ?> value;
	private CollectionType type;
	
	public MapTraversingNode(Map<?, ?> value, CollectionType type) {
		super();
		this.value = value;
		this.type = type;
	}

	@Override
	public CollectionType getType() {
		return type;
	}
	
	@Override
	public Map<?, ?> getValue() {
		return value;
	}
	
	@Override
	public CriterionType getCriterionType() {
		return CriterionType.MAP;
	}
}
