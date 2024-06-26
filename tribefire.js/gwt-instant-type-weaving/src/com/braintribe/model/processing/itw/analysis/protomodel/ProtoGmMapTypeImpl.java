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
package com.braintribe.model.processing.itw.analysis.protomodel;

import com.braintribe.model.generic.pseudo.GenericEntity_pseudo;
import com.braintribe.model.meta.GmMapType;
import com.braintribe.model.meta.GmTypeKind;
import com.braintribe.model.weaving.ProtoGmMapType;
import com.braintribe.model.weaving.ProtoGmType;

/**
 * Pseudo-implementation of {@link GmMapType}
 * 
 * @see GenericEntity_pseudo
 * 
 * @author peter.gazdik
 */
public class ProtoGmMapTypeImpl extends ProtoGmTypeImpl implements ProtoGmMapType {

	private ProtoGmType keyType;
	private ProtoGmType valueType;

	@Override
	public ProtoGmType getKeyType() {
		return keyType;
	}

	public void setKeyType(ProtoGmType keyType) {
		this.keyType = keyType;
	}

	@Override
	public ProtoGmType getValueType() {
		return valueType;
	}

	public void setValueType(ProtoGmType valueType) {
		this.valueType = valueType;
	}

	@Override
	public GmTypeKind typeKind() {
		return GmTypeKind.MAP;
	}

}