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
package com.braintribe.model.processing.exchange.service;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

import com.braintribe.model.exchangeapi.ExportDescriptor;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.pr.AbsenceInformation;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.generic.reflection.StandardCloningContext;

public class DetachingAndShallowfyingCloningContext extends StandardCloningContext {
	
	private static final Property globalIdProperty = GenericEntity.T.getProperty(GenericEntity.globalId);
	private static final Property partitionProperty = GenericEntity.T.getProperty(GenericEntity.partition);
	
	private Predicate<GenericEntity> matcher;
	private ExportDescriptor descriptor;
	private Set<GenericEntity> externalReferences = new HashSet<>();
	private Set<GenericEntity> followedReferences = new HashSet<>();

	public DetachingAndShallowfyingCloningContext(Predicate<GenericEntity> matcher, ExportDescriptor descriptor) {
		this.matcher = matcher;
		this.descriptor = descriptor;
	}
	
	public Set<GenericEntity> getExternalReferences() {
		return externalReferences;
	}
	
	public Set<GenericEntity> getFollowedReferences() {
		return followedReferences;
	}
	
	@Override
	public boolean isAbsenceResolvable(Property property, GenericEntity entity, AbsenceInformation absenceInformation) {
		return true;
	}
	@Override
	public <T> T getAssociated(GenericEntity entity) {
		T associated = super.getAssociated(entity);
		if (associated != null)
			return associated;
		
		if(this.matcher.test(entity)) {
			return createShallowAssociatedCopy(entity);
		}
		
		followedReferences.add(entity);
		return null;
	}
	private <T extends GenericEntity> T createShallowAssociatedCopy(GenericEntity entity) {
		T shallowEntity = entity.<T> entityType().createRaw();
		shallowEntity.setGlobalId(entity.getGlobalId());
		registerAsVisited(entity, shallowEntity); // i.e. associate
		externalReferences.add(shallowEntity);
		return shallowEntity;
	}
	@Override
	public boolean canTransferPropertyValue(EntityType<? extends GenericEntity> entityType, Property property,
			GenericEntity instanceToBeCloned, GenericEntity clonedInstance, AbsenceInformation sourceAbsenceInformation) {
		
		if (property.isIdentifier()) {
			return !descriptor.getSkipId();
		}
		if (globalIdProperty == property) {
			return !descriptor.getSkipGlobalId();
		}
		if (partitionProperty == property) {
			return !descriptor.getSkipPartition();
		}
		return true;

	}
}
