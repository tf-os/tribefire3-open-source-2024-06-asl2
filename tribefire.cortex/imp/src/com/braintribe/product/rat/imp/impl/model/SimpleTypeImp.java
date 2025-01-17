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
package com.braintribe.product.rat.imp.impl.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.braintribe.model.meta.GmCollectionType;
import com.braintribe.model.meta.GmEntityType;
import com.braintribe.model.meta.GmEnumType;
import com.braintribe.model.meta.GmLinearCollectionType;
import com.braintribe.model.meta.GmMapType;
import com.braintribe.model.meta.GmModelElement;
import com.braintribe.model.meta.GmType;
import com.braintribe.model.processing.query.fluent.SelectQueryBuilder;
import com.braintribe.model.processing.session.api.persistence.PersistenceGmSession;
import com.braintribe.model.query.SelectQuery;
import com.braintribe.product.rat.imp.AbstractImp;
import com.braintribe.product.rat.imp.ImpException;

/**
 * A {@link AbstractImp} specialized in {@link GmType}
 */
public class SimpleTypeImp<T extends GmType> extends AbstractImp<T> {
	protected final String typeSignature;

	public SimpleTypeImp(PersistenceGmSession session, T gmType) {
		super(session, gmType);

		this.typeSignature = gmType.getTypeSignature();
	}

	public String typeNameWithoutGroupId() {
		int lastDotIndex = typeSignature.lastIndexOf('.');
		return typeSignature.substring(lastDotIndex + 1);
	}

	public String groupId() {
		int lastDotIndex = typeSignature.lastIndexOf('.');

		return lastDotIndex > -1 ? typeSignature.substring(0, lastDotIndex) : "";
	}

	/**
	 * deletes the entity of the GmType managed by this imp as well as eventual properties and constants
	 */
	public void deleteRecursively() {
		List<GmModelElement> entitiesToDelete = new ArrayList<>();

		logger.trace("Prepare to delete all CollectionType(s) that refer to " + instance.getTypeSignature());
		entitiesToDelete.addAll(getReferringCollectionTypes());

		if (instance.isGmEntity()) {
			logger.trace("Prepare to delete all properties of entity " + instance.getTypeSignature());
			entitiesToDelete.addAll(((GmEntityType) instance).getProperties());
		} else if (instance.isGmEnum()) {
			logger.trace("Prepare to delete all constants of enum " + instance.getTypeSignature());
			entitiesToDelete.addAll(((GmEnumType) instance).getConstants());
		}

		logger.trace("Prepare to delete entity type " + instance.getTypeSignature());
		entitiesToDelete.add(instance);

		for (GmModelElement element : entitiesToDelete) {
			session().deleteEntity(element);
		}

		logger.trace("Finished erasing entity type " + instance.getTypeSignature());
	}

	private Set<GmCollectionType> getReferringCollectionTypes() {
		Set<GmCollectionType> typesToDelete = new HashSet<>();

		//@formatter:off
		SelectQuery collectionQuery = new SelectQueryBuilder()
				.from(GmLinearCollectionType.T, "p")
					.where()
						.disjunction()
						.property("elementType").eq(instance)
						.property("typeSignature").like("*<" + typeSignature + ">")
						.close()
				.done();

		List<GmLinearCollectionType> foundCollectionEntities = session().query().select(collectionQuery).list();
		typesToDelete.addAll(foundCollectionEntities);

		SelectQuery mapQuery = new SelectQueryBuilder()
				.from(GmMapType.T, "p")
					.where()
						.disjunction()
						.property("keyType").eq(instance)
						.property("valueType").eq(instance)
						.close()
					.done();
		//@formatter:on

		List<GmMapType> foundMapEntities = session().query().select(mapQuery).list();
		typesToDelete.addAll(foundMapEntities);

		return typesToDelete;
	}

	/**
	 * @return an imp managing the model that declares this type
	 * @throws ImpException
	 *             if this imp's type has no declared model set
	 */
	public ModelImp model() {
		if (instance.getDeclaringModel() == null) {
			throw new ImpException("Could not create model imp - this type has no declaring model set");
		}

		return new ModelImp(session(), instance.getDeclaringModel());
	}

}
