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

import java.util.List;

import com.braintribe.model.generic.pseudo.GenericEntity_pseudo;
import com.braintribe.model.meta.GmProperty;
import com.braintribe.model.weaving.ProtoGmType;
import com.braintribe.model.weaving.restriction.ProtoGmTypeRestriction;

/**
 * Pseudo-implementation of {@link GmProperty}
 * 
 * @see GenericEntity_pseudo
 * 
 * @author peter.gazdik
 */
public class ProtoGmTypeRestrictionImpl extends GenericEntity_pseudo implements ProtoGmTypeRestriction {

	private List<ProtoGmType> types;
	private List<ProtoGmType> keyTypes;
	private boolean allowVd;
	private boolean allowKeyVd;

	@Override
	public List<ProtoGmType> getTypes() {
		return types;
	}

	public void setTypes(List<ProtoGmType> types) {
		this.types = types;
	}

	@Override
	public List<ProtoGmType> getKeyTypes() {
		return keyTypes;
	}

	public void setKeyTypes(List<ProtoGmType> keyTypes) {
		this.keyTypes = keyTypes;
	}

	@Override
	public boolean getAllowVd() {
		return allowVd;
	}

	@Override
	public void setAllowVd(boolean allowVd) {
		this.allowVd = allowVd;
	}

	@Override
	public boolean getAllowKeyVd() {
		return allowKeyVd;
	}

	@Override
	public void setAllowKeyVd(boolean allowKeyVd) {
		this.allowKeyVd = allowKeyVd;
	}

}
