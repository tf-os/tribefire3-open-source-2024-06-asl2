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
package com.braintribe.model.processing.smood.api;

import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Set;

import com.braintribe.model.access.ModelAccessException;
import com.braintribe.model.accessapi.ReferencesCandidate;
import com.braintribe.model.accessapi.ReferencesCandidates;
import com.braintribe.model.accessapi.ReferencesRequest;
import com.braintribe.model.accessapi.ReferencesResponse;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.manipulation.DeleteManipulation;
import com.braintribe.model.generic.manipulation.DeleteMode;
import com.braintribe.model.generic.manipulation.ManifestationManipulation;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.tracking.ManipulationListener;
import com.braintribe.model.generic.value.EntityReference;

/**
 * 
 */
public interface SmoodInterface {

	/**
	 * Traverses given entity and registers all generic entities reached. Note that this method can be invoked at any
	 * time, so it does not really initializes anything.
	 */
	void initialize(Object genericModelValue);

	/**
	 * Iterates through given entities and registers all. Note that this method can be invoked at any time, so it does
	 * not really initializes anything.
	 * 
	 * ensureIds is set to true, the implementation guarantees that an Id is generated and assigned before registration
	 */
	void initializePopulation(Iterable<GenericEntity> entities, boolean ensureIds);

	/**
	 * Ensures each entity in the Smood has a globalId and a persistence id (if id property exists). This is used as an
	 * alternative to {@link #initializePopulation(Iterable, boolean)} when we initialize our population using the
	 * Smood's session, but still want the ids to be generated automatically.
	 */
	void ensureIds();

	void registerEntity(GenericEntity entity, boolean autoGenerateId);

	/**
	 * Removes all the references on given entity within the smood (i.e. within all entities that are part of the smood
	 * population) and then unregisters this entity (see {@link #unregisterEntity(GenericEntity)}). The session is
	 * notified with a {@link DeleteManipulation}, where the inverse of this manipulation consists of
	 * {@link ManifestationManipulation} and manipulations that would restore all the deleted references. This means
	 * this delete is exactly the same as a delete triggered by applying a {@linkplain DeleteManipulation}.
	 */
	void deleteEntity(GenericEntity entity);

	/** Removes given entity using chosen {@link DeleteMode}. */
	void deleteEntity(GenericEntity entity, DeleteMode deleteMode);

	/** Basically removes given entity from the smood, i.e. removes it from the population and all indices. */
	void unregisterEntity(GenericEntity entity);

	/**
	 * Returns an unmodifiable {@link Set} or all entities registered in the smood. This method returns a live view on
	 * the data in the smood, which means it does not have almost any memory overhead, but on the other hand it's the
	 * caller who is responsible for synchronization. If some other thread would modify the smood while we are iterating
	 * over this collection, we would get a {@link ConcurrentModificationException}.
	 */
	Collection<GenericEntity> getAllEntities();

	/** Return all instances of given type.. */
	<T extends GenericEntity> Set<T> getEntitiesPerType(EntityType<T> entityType);

	/** Returns entity by it's <tt>type</tt> and <tt>id</tt>. If no entity is found, exception is thrown. */
	<T extends GenericEntity> T getEntity(EntityType<T> entityType, Object id);

	/** Returns entity by corresponding <tt>reference</tt>. If no entity is found, exception is thrown. */
	<T extends GenericEntity> T getEntity(EntityReference entityReference);

	/** Returns entity by it's <tt>type</tt> and <tt>id</tt>. If no entity is found, <tt>null</tt> is returned. */
	<T extends GenericEntity> T findEntity(EntityType<T> entityType, Object id);

	/** Returns entity by corresponding <tt>reference</tt>. If no entity is found, <tt>null</tt> is returned. */
	<T extends GenericEntity> T findEntity(EntityReference reference);

	/** Returns entity by corresponding <tt>globalId</tt>. If no entity is found, <tt>null</tt> is returned. */
	<T extends GenericEntity> T findEntityByGlobalId(String globalId);

	/**
	 * returns types in the Smood. Note that there might be no instances of given type inside the DB, but for every
	 * single entity inside, it's corresponding {@link EntityType} (and all super-types) will be part of the result.
	 */
	Set<EntityType<?>> getUsedTypes();

	/**
	 * Not sure what this is supposed to do in general, but the current implementation looks for references of given
	 * entity (given by {@link EntityReference} which has nothing to do with references we are talking about here) and
	 * returns an instance of {@link ReferencesCandidates}, so basically a set of pairs (typeSignature, propertyName).
	 * For every existing reference (i.e. there is another entity that references given one with some of it's
	 * properties), we would add such {@link ReferencesCandidate} in the result, if it is not there already. So
	 * basically you can find out, that there is an instance of entity <tt>X</tt> which references given entity with
	 * it's property <tt>propFoo</tt>.
	 */
	ReferencesResponse getReferences(ReferencesRequest referencesRequest) throws ModelAccessException;

	/** Closes the smood. Currently this means it removes whatever {@link ManipulationListener}s it has registered. */
	void close();

}
