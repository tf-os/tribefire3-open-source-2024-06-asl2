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
package com.braintribe.model.processing.access.service.impl.standard;

import static com.braintribe.model.generic.typecondition.TypeConditions.hasCollectionElement;
import static com.braintribe.model.generic.typecondition.TypeConditions.isAssignableTo;
import static com.braintribe.utils.lcd.CollectionTools2.newList;
import static com.braintribe.utils.lcd.CollectionTools2.newSet;

import java.util.List;
import java.util.Set;

import com.braintribe.model.access.AccessServiceException;
import com.braintribe.model.meta.GmMetaModel;
import com.braintribe.model.meta.GmType;
import com.braintribe.model.meta.data.MetaData;
import com.braintribe.model.processing.query.fluent.SelectQueryBuilder;
import com.braintribe.model.processing.session.api.persistence.PersistenceGmSession;
import com.braintribe.model.query.SelectQuery;
import com.braintribe.model.record.ListRecord;
import com.braintribe.utils.lcd.SetTools;

/**
 * @author peter.gazdik
 */
public class ModelForTypesFinder {

	public static List<GmMetaModel> find(PersistenceGmSession session, Set<String> typeSignatures) throws AccessServiceException {
		SelectQuery selectQuery = buildQueryForSignatureAndDeclaringModel(typeSignatures);
		List<ListRecord> rows = runQuery(session, typeSignatures, selectQuery);
		return extractDeclaringModels(typeSignatures, rows);
	}

	private static SelectQuery buildQueryForSignatureAndDeclaringModel(Set<String> typeSignatures) {
		// @formatter:off
		return new SelectQueryBuilder()
					.select("t", "typeSignature")
					.select("t", "declaringModel")
					.from(GmType.T, "t")
					.where()
						.property("t", "typeSignature").in(typeSignatures)
					.tc()
						.typeCondition(hasCollectionElement(isAssignableTo(MetaData.T)))
					.done();
		// @formatter:on
	}

	private static List<ListRecord> runQuery(PersistenceGmSession session, Set<String> typeSignatures, SelectQuery query) {
		try {
			return session.queryDetached().select(query).list();

		} catch (Exception e) {
			throw new AccessServiceException("Error while querying declaringModel for types:" + typeSignatures, e);
		}
	}

	private static List<GmMetaModel> extractDeclaringModels(Set<String> typeSignatures, List<ListRecord> rows) {
		Set<String> foundTypes = newSet();
		List<GmMetaModel> result = newList();

		for (ListRecord row : rows) {
			List<Object> values = row.getValues();

			foundTypes.add((String) values.get(0));

			GmMetaModel declaringModel = (GmMetaModel) values.get(1);
			if (!result.contains(declaringModel))
				result.add(declaringModel);
		}

		if (foundTypes.size() < typeSignatures.size()) {
			Set<String> notFoundTypes = SetTools.subtract(typeSignatures, foundTypes);
			throw new AccessServiceException("Following types were not found via query: " + notFoundTypes
					+ ". If you expect them in your cortex, check that the right versions of the corresponding models are on the classpath.");
		}

		return result;
	}

}
