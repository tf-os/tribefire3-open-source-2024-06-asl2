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
package tribefire.extension.messaging.model.conditions.properties;

import com.braintribe.model.generic.annotation.Abstract;
import com.braintribe.model.generic.annotation.meta.Name;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import tribefire.extension.messaging.model.conditions.Condition;
import tribefire.extension.messaging.model.conditions.Junction;

import java.util.List;

@Abstract
public interface PropertyJunction extends PropertyCondition, Junction {

	EntityType<PropertyJunction> T = EntityTypes.T(PropertyJunction.class);

	String operands = "operands";

	@Name("Operands")
	List<PropertyCondition> getOperands();
	void setOperands(List<PropertyCondition> operands);

	@Override
	default <C extends Condition> List<C> operands() {
		return (List<C>) getOperands();
	}
}