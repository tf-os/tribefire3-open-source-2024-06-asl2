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
package com.braintribe.model.processing.smood;

import java.util.Map;
import java.util.Set;

import com.braintribe.model.access.ModelAccessException;
import com.braintribe.model.accessapi.ManipulationRequest;
import com.braintribe.model.accessapi.ManipulationResponse;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.value.PreliminaryEntityReference;
import com.braintribe.model.processing.session.api.managed.ManipulationReport;

/**
 * 
 */
@SuppressWarnings("unusable-by-js")
public class ContextBuilder implements ManipulationApplicationBuilder {

	private final Smood smood;

	private boolean generateId = true;
	private boolean ignoreManipulationsReferingToUnknownEntities = false;
	private boolean manifestUnknownEntities;
	private boolean ignoreAbsentCollectionManipulations;
	private boolean isLocalRequest = false;
	private boolean checkRefereesOnDelete = false;
	private ManipulationApplicationListener listener;
	private Map<PreliminaryEntityReference, GenericEntity> instantiations;
	private Map<EntityType<?>, Set<GenericEntity>> lenientManifestations;
	private ManipulationResponse response;

	public ContextBuilder(Smood smood) {
		this.smood = smood;
	}

	@Override
	public Map<PreliminaryEntityReference, GenericEntity> getInstantiations() {
		return instantiations;
	}

	public void setInstantiations(Map<PreliminaryEntityReference, GenericEntity> instantiations) {
		this.instantiations = instantiations;
	}

	@Override
	public Map<EntityType<?>, Set<GenericEntity>> getLenientManifestations() {
		return lenientManifestations;
	}

	public void setLenientManifestations(Map<EntityType<?>, Set<GenericEntity>> lenientManifestations) {
		this.lenientManifestations = lenientManifestations;
	}

	@Override
	public ManipulationResponse getManipulationResponse() {
		return response;
	}

	public void setManipulationResponse(ManipulationResponse response) {
		this.response = response;
	}

	@Override
	public ManipulationReport request(ManipulationRequest request) throws ModelAccessException {
		smood.applyManipulation(request, this);
		return this;
	}

	@Override
	public ManipulationApplicationBuilder generateId(boolean _generateId) {
		this.generateId = _generateId;
		return this;
	}

	@Override
	public boolean generateId() {
		return generateId;
	}

	@Override
	public ManipulationApplicationBuilder ignoreUnknownEntitiesManipulations(boolean ignoreUnknownEntitiesManipulations) {
		this.ignoreManipulationsReferingToUnknownEntities = ignoreUnknownEntitiesManipulations;
		return this;
	}

	@Override
	public boolean ignoreManipulationsReferingToUnknownEntities() {
		return ignoreManipulationsReferingToUnknownEntities;
	}

	@Override
	public ManipulationApplicationBuilder manifestUnkownEntities(boolean _manifestUnknownEntities) {
		this.manifestUnknownEntities = _manifestUnknownEntities;
		return this;
	}

	@Override
	public boolean manifestUnknownEntities() {
		return manifestUnknownEntities;
	}

	@Override
	public ManipulationApplicationBuilder ignoreAbsentCollectionManipulations(boolean _ignoreAbsentCollectionManipulations) {
		this.ignoreAbsentCollectionManipulations = _ignoreAbsentCollectionManipulations;
		return this;
	}

	@Override
	public boolean ignoreAbsentCollectionManipulations() {
		return ignoreAbsentCollectionManipulations;
	}

	@Override
	public ManipulationApplicationBuilder localRequest(boolean _isLocalRequest) {
		this.isLocalRequest = _isLocalRequest;
		return this;
	}

	@Override
	public ManipulationApplicationBuilder checkRefereesOnDelete(boolean _checkRefereesOnDelete) {
		this.checkRefereesOnDelete = _checkRefereesOnDelete;
		return this;
	}

	public boolean isLocalRequest() {
		return isLocalRequest;
	}

	/** @see ManipulationApplicationBuilder#checkRefereesOnDelete(boolean) */
	public boolean checkRefereesOnDelete() {
		return checkRefereesOnDelete;
	}

	@Override
	public ManipulationApplicationBuilder manipulationApplicationListener(ManipulationApplicationListener listener) {
		this.listener = listener;
		return this;
	}

	@Override
	public ManipulationApplicationListener getManipulationApplicationListener() {
		return listener;
	}
	
	@Override
	public ManipulationApplicationBuilder instantiations(Map<PreliminaryEntityReference, GenericEntity> instantiations) {
		this.instantiations = instantiations;
		return this;
	}

}
