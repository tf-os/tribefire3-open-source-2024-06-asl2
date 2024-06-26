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
package tribefire.extension.xml.schemed.model.xsd;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

import tribefire.extension.xml.schemed.model.xsd.archetypes.HasAll;
import tribefire.extension.xml.schemed.model.xsd.archetypes.HasAttributeGroups;
import tribefire.extension.xml.schemed.model.xsd.archetypes.HasAttributes;
import tribefire.extension.xml.schemed.model.xsd.archetypes.HasBase;
import tribefire.extension.xml.schemed.model.xsd.archetypes.HasChoice;
import tribefire.extension.xml.schemed.model.xsd.archetypes.HasGroup;
import tribefire.extension.xml.schemed.model.xsd.archetypes.HasSequence;

public interface Extension extends SchemaEntity, Annoted, SequenceAware, HasAttributes, HasAttributeGroups, HasGroup, HasChoice, HasAll, HasSequence, HasBase {
	
	final EntityType<Extension> T = EntityTypes.T(Extension.class);
			
}