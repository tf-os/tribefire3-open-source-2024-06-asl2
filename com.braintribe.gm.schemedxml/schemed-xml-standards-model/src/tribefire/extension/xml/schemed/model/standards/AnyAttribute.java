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
package tribefire.extension.xml.schemed.model.standards;

import com.braintribe.model.generic.annotation.GlobalId;

@GlobalId("type:com.braintribe.gm.schemedxml.standards.AnyAttribute")  
public interface AnyAttribute extends com.braintribe.model.generic.GenericEntity {

	com.braintribe.model.generic.reflection.EntityType<AnyAttribute> T = com.braintribe.model.generic.reflection.EntityTypes.T(AnyAttribute.class);
	
	String name = "name";
	String value = "value";

	@GlobalId("property:com.braintribe.gm.schemedxml.standards.AnyAttribute/name")  
	java.lang.String getName();
	void setName(java.lang.String value);

	@GlobalId("property:com.braintribe.gm.schemedxml.standards.AnyAttribute/value")  
	java.lang.String getValue();
	void setValue(java.lang.String value);

}