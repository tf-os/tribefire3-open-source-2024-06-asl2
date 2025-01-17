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
package com.braintribe.model.processing.itw.synthesis.gm.template;

import static com.braintribe.model.processing.itw.asm.AsmClassPool.objectType;
import static com.braintribe.model.processing.itw.asm.AsmUtils.toInternalName;
import static com.braintribe.model.processing.itw.asm.AsmUtils.toInternalNameLonger;

import java.util.List;

import com.braintribe.asm.Label;
import com.braintribe.asm.MethodVisitor;
import com.braintribe.asm.Opcodes;
import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.processing.core.commons.SelectiveInformationSupport;
import com.braintribe.model.processing.itw.asm.AsmClass;
import com.braintribe.model.processing.itw.asm.AsmClassPool;
import com.braintribe.model.processing.itw.asm.AsmUtils;
import com.braintribe.model.processing.itw.asm.TypeBuilderUtils;
import com.braintribe.model.processing.itw.synthesis.gm.GmtsHelper;
import com.braintribe.model.processing.itw.synthesis.gm.PreliminaryEntityType;
import com.braintribe.model.processing.itw.synthesis.gm.asm.GmClassPool;
import com.braintribe.model.processing.itw.synthesis.gm.experts.PropertyPathResolver;
import com.braintribe.model.weaving.ProtoGmProperty;

/**
 * 
 */
public class VariableResolver implements Opcodes {

	private final PreliminaryEntityType pet;
	private final GmClassPool gcp;
	private final AsmClassPool asmClassPool;

	public VariableResolver(PreliminaryEntityType pet, GmClassPool gmClassPool) {
		this.pet = pet;
		this.gcp = gmClassPool;
		this.asmClassPool = gmClassPool.getAsmClassPool();
	}

	/**
	 * Produces such code whose result is, that the value of this variable will be the only object that is added to the top of the stack.
	 */
	@SuppressWarnings("unused")
	public void mergeVariable(String variable, List<ProtoGmProperty> nonOverlayProperties, MethodVisitor mv) {
		switch (variable) {
			case SelectiveInformationSupport.SI_TYPE:
			case SelectiveInformationSupport.SI_TYPE_ALT:
				StaticText.mergeText(mv, pet.entityTypeName);
				return;

			case SelectiveInformationSupport.SI_TYPE_SHORT:
				StaticText.mergeText(mv, pet.shortName);
				return;

			case SelectiveInformationSupport.SI_ID:
				mv.visitVarInsn(ALOAD, 1);
				TypeBuilderUtils.checkCast(mv, gcp.gmtsEntityStubType);
				mv.visitMethodInsn(INVOKEVIRTUAL, "com/braintribe/model/generic/reflection/GmtsEntityStub", "idOrRid", "()Ljava/lang/Object;", false);
				mv.visitMethodInsn(INVOKEVIRTUAL, "com/braintribe/model/processing/itw/tools/ItwStringBuilder", "append", "(Ljava/lang/Object;)Lcom/braintribe/model/processing/itw/tools/ItwStringBuilder;", false);
				return;

			case SelectiveInformationSupport.SI_RUNTIME_ID:
				mv.visitVarInsn(ALOAD, 1);
				mv.visitMethodInsn(INVOKEINTERFACE, "com/braintribe/model/generic/GenericEntity", "runtimeId", "()J", true);
				mv.visitMethodInsn(INVOKEVIRTUAL, "com/braintribe/model/processing/itw/tools/ItwStringBuilder", "append", "(J)Lcom/braintribe/model/processing/itw/tools/ItwStringBuilder;", false);
				return;

			default: {
				AsmClass propertyClass = asmClassPool.get(Property.class);
				AsmClass propertyArrayClass = asmClassPool.acquireArrayClass(propertyClass);
				AsmClass propertyPathResolverClass = asmClassPool.get(PropertyPathResolver.class);
				String getPropertySignature = AsmUtils.createMethodSignature(propertyClass);

				String propertyNameChain[] = variable.split("\\.");

				mv.visitLabel(new Label());
				mv.visitVarInsn(ALOAD, 1); // 1, because our method is public String toXy(GenericEntity entity) {...}
				TypeBuilderUtils.pushConstant(mv, propertyNameChain.length);
				mv.visitTypeInsn(ANEWARRAY, "com/braintribe/model/generic/reflection/Property");

				int arrayIndex = 0;

				for (ProtoGmProperty nonOverlayProperty : nonOverlayProperties) {
					String pcn = GmtsHelper.getPropertyClassName(nonOverlayProperty.getDeclaringType(), nonOverlayProperty.getName());

					mv.visitInsn(DUP);
					TypeBuilderUtils.pushConstant(mv, arrayIndex++);
					mv.visitFieldInsn(GETSTATIC, toInternalName(pcn), "INSTANCE", toInternalNameLonger(pcn));
					mv.visitInsn(AASTORE);
				}

				mv.visitMethodInsn(INVOKESTATIC, propertyPathResolverClass.getInternalName(), "resolvePropertyPath",
						AsmUtils.createMethodSignature(objectType, gcp.genericEntityType, propertyArrayClass), false);
				mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/braintribe/model/processing/itw/tools/ItwStringBuilder", "append", "(Ljava/lang/Object;)Lcom/braintribe/model/processing/itw/tools/ItwStringBuilder;",
						false);
			}
		}

	}

}
