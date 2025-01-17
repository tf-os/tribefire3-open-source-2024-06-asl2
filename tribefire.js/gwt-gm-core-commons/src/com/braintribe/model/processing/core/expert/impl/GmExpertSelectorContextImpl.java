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
package com.braintribe.model.processing.core.expert.impl;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.processing.core.expert.api.GmExpertSelectorContext;

class GmExpertSelectorContextImpl implements GmExpertSelectorContext {
	
	private final Class<?> expertType;
	private final GenericModelType denotationType;
	private final GenericEntity denotationInstance;
	
	/**
	 * Creates a new <code>ExpertKey</code> instance.
	 * 
	 * @param expertClass
	 *            the expert (super) type. If there e.g. exists an interface <code>PersonExpert</code> and a concrete
	 *            implementation <code>EmployeeExpert</code> one would pass the <code>PersonExpert</code> class.
	 */
	public GmExpertSelectorContextImpl(Class<?> expertClass, GenericModelType denotationType, GenericEntity denotionationInstance) {
		this.expertType = expertClass;
		this.denotationType = denotationType;
		this.denotationInstance = denotionationInstance;
	}
	
	public Class<?> getExpertType() {
		return expertType;
	}
	
	@Override
	public GenericEntity getDenotationInstance() {
		return denotationInstance;
	}
	
	@Override
	public GenericModelType getDenotationType() {
		return denotationType;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((expertType == null) ? 0 : expertType.hashCode());
		result = prime * result
				+ ((denotationType == null) ? 0 : denotationType.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;

		if (obj == null || getClass() != obj.getClass())
			return false;

		GmExpertSelectorContextImpl other = (GmExpertSelectorContextImpl) obj;
		return expertType == other.expertType && denotationType == other.denotationType;
	}

}
