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
package com.braintribe.model.processing.manipulation.marshaller;

import static com.braintribe.utils.lcd.CollectionTools2.newMap;

import java.io.IOException;
import java.util.Map;

import com.braintribe.model.generic.GMF;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.manipulation.AcquireManipulation;
import com.braintribe.model.generic.manipulation.DeleteManipulation;
import com.braintribe.model.generic.manipulation.InstantiationManipulation;
import com.braintribe.model.generic.manipulation.LocalEntityProperty;
import com.braintribe.model.generic.manipulation.PropertyManipulation;
import com.braintribe.model.generic.reflection.BaseType;
import com.braintribe.model.generic.reflection.CustomType;
import com.braintribe.model.generic.reflection.EnumType;
import com.braintribe.model.generic.reflection.GenericModelType;

public class LocalManipulationStringifier extends ManipulationStringifier {
	
	private final Map<Object, String> entityOrTypeToVar;

	public LocalManipulationStringifier() {
		this(newMap());
	}
	
	public LocalManipulationStringifier(Map<Object, String> entityOrTypeToVar) {
		this.entityOrTypeToVar = entityOrTypeToVar;
		recognizeVarNames(entityOrTypeToVar.values());
	}

	@Override
	protected void writeInstantiationManipulation(Appendable writer, InstantiationManipulation manipulation) throws IOException {
		GenericEntity entity = manipulation.getEntity();
		lastEntity = entity;
		writeReferenceExpression(writer, entity, ReferenceMode.instantiation);
	}
	
	@Override
	protected void writeAcquireManipulation(Appendable writer, AcquireManipulation manipulation) throws IOException {
		GenericEntity entity = manipulation.getEntity();
		lastEntity = entity;
		writeReferenceExpression(writer, entity, ReferenceMode.acquire);
	}

	@Override
	protected void writeDeleteManipulation(Appendable writer, DeleteManipulation manipulation) throws IOException {
		writer.append("-");
		writeReferenceExpression(writer, manipulation.getEntity(), ReferenceMode.value);
	}
	
	private enum ReferenceMode {owner, value, instantiation, acquire}
	
	private void writeReferenceExpression(Appendable writer, GenericEntity entity, ReferenceMode referenceMode) throws IOException {
		String varName = entityOrTypeToVar.get(entity);
		
		if (varName == null) {
			varName = newReferenceVarName();
			entityOrTypeToVar.put(entity, varName);
			
			switch (referenceMode) {
			case instantiation:
				writer.append(varName);
				writer.append('=');
				writeTypeReference(writer, entity.entityType());
				writer.append("()");
				break;
			case acquire:
				writer.append(varName);
				writer.append('=');
				writeTypeReference(writer, entity.entityType());
				writer.append("['");
				writer.append(entity.getGlobalId());
				writer.append("']");
				break;
			case owner:
				writeReferenceAsAssignmentExpression(writer, entity, varName);
				writer.append('\n');
				break;
			case value:
				writer.append('(');
				writeReferenceAsAssignmentExpression(writer, entity, varName);
				writer.append(')');
				break;
			}
		}
		else
			writer.append(varName);
	}

	private void writeReferenceAsAssignmentExpression(Appendable writer, GenericEntity entity, String varName) throws IOException {
		writer.append(varName);
		writer.append('=');
		writeTypeReference(writer, entity.entityType());
		writer.append("('");
		writer.append(entity.getGlobalId());
		writer.append("')");
	}

	private void writeTypeReference(Appendable writer, CustomType type) throws IOException {
		String varName = entityOrTypeToVar.get(type);
		
		if (varName == null) {
			varName = newTypeVarName(type.getShortName());
			entityOrTypeToVar.put(type, varName);
		
			writer.append('(');
			writer.append(varName);
			writer.append('=');
			writer.append(type.getTypeSignature());
			writer.append(')');
		}
		else {
			writer.append(varName);
		}
	}

	@Override
	protected void writeEntity(Appendable writer, GenericEntity entity) throws IOException {
		writeReferenceExpression(writer, entity, ReferenceMode.value);
	}

	@Override
	protected void writeEnum(Appendable writer, Object enumValue) throws IOException {
		Enum<?> enumConstant = (Enum<?>)enumValue;
		EnumType enumType = GMF.getTypeReflection().getType(enumConstant);
		writeTypeReference(writer, enumType);
		writer.append("::");
		writer.append(enumConstant.name());
	}

	@Override
	protected void writePropertyManipulationStart(Appendable writer, PropertyManipulation manipulation) throws IOException {
		writePropertyManipulationStart(writer, manipulation, false);
	}

	@Override
	protected GenericModelType writePropertyManipulationStartAndReturnPropertyType(Appendable writer, PropertyManipulation manipulation) throws IOException {
		return writePropertyManipulationStart(writer, manipulation, true);
	}

	private GenericModelType writePropertyManipulationStart(Appendable writer, PropertyManipulation manipulation, boolean needsResult) throws IOException {
		LocalEntityProperty owner = (LocalEntityProperty) manipulation.getOwner();
		GenericEntity entity = owner.getEntity();
		
		if (isLastReference(entity)) {
			writeReferenceExpression(writer, entity, ReferenceMode.owner);
			lastEntity = entity;
		}
		
		writer.append('.');
		writer.append(owner.getPropertyName());
		
		if (!needsResult)
			return null;
		
		return owner.property().getType();
	}

	@Override
	protected GenericModelType resolveActualType(Object value) {
		return BaseType.INSTANCE.getActualType(value);
	}
}
