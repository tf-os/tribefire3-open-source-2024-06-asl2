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

import java.util.List;

import com.braintribe.model.management.MetaModelValidationViolation;
import com.braintribe.model.meta.GmMetaModel;

public class TypesIsPlainConsistencyCheck implements ValidatorCheck {
	
	@SuppressWarnings("unused")
	@Override
	public boolean check(GmMetaModel metaModel, List<MetaModelValidationViolation> validationErrors) {
		
//		if (metaModel.getEntityTypes() != null) {
//			for (GmEntityType t : metaModel.getEntityTypes()) {
//				if (t != null) {
//					
//					//only true means plain, null or false means not plain
//					if (/*isPlain(t)*/false) {
//						//check if max one isPlain supertype
//						List<String> plainSupertypes = new ArrayList<String>();
//						if (t.getSuperTypes() != null) {
//							for (GmEntityType supertype : t.getSuperTypes()) {
//								if (/*supertype != null && isPlain(supertype)*/ false) {
//									plainSupertypes.add(supertype.getTypeSignature());
//								}
//							}
//						}
//						
//						if (plainSupertypes.size() > 1) {
//							ViolationBuilder.to(validationErrors).withEntityType(t)
//								.add(MetaModelValidationViolationType.TYPE_PLAIN_TYPE_WITH_MORE_THAN_ONE_PLAIN_SUPERTYPE, 
//									"entity type '" + t.getTypeSignature() + "' is plain, but has more than one plain supertype " + 
//									Arrays.toString(plainSupertypes.toArray()));
//						}
//					} else {
//						//check all supertypes are not isPlain
//						if (t.getSuperTypes() != null) {
//							for (GmEntityType supertype : t.getSuperTypes()) {
//								if (/*supertype != null && isPlain(supertype)*/ false) {
//									ViolationBuilder.to(validationErrors).withEntityType(t)
//										.add(MetaModelValidationViolationType.TYPE_NOT_PLAIN_TYPE_WITH_PLAIN_SUPERTYPE, 
//											"entity type '" + t.getTypeSignature() + "' is not plain, but has plain supertype '" + 
//											supertype.getTypeSignature() + "'");
//								}
//							}
//						}
//					}
//				}
//			}
//		}
		
		return true;
	}

}
