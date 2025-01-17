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
package com.braintribe.model.processing.itw.synthesis.java.asm;

import com.braintribe.asm.MethodVisitor;
import com.braintribe.asm.Opcodes;
import com.braintribe.model.processing.itw.asm.AsmClass;
import com.braintribe.model.processing.itw.asm.AsmClassPool;
import com.braintribe.model.processing.itw.asm.AsmField;
import com.braintribe.model.processing.itw.asm.AsmMethod;
import com.braintribe.model.processing.itw.asm.AsmNewClass;
import com.braintribe.model.processing.itw.asm.AsmType;
import com.braintribe.model.processing.itw.asm.AsmUtils;
import com.braintribe.model.processing.itw.asm.TypeBuilder;
import com.braintribe.model.processing.itw.asm.TypeBuilderUtils;

/**
 * Base class for {@link TypeBuilder} wrappers which provide GM-specific extension to this class.
 */
public abstract class AbstractTypeBuilder<T extends TypeBuilder> implements Opcodes {

	protected T b;
	protected AsmClassPool asmClassPool;
	protected MethodVisitor mv;
	protected AsmNewClass asmClass;

	public AbstractTypeBuilder(T b, AsmClassPool asmClassPool) {
		this.b = b;
		this.asmClassPool = asmClassPool;
		this.asmClass = b.getPreliminaryClass();
	}

	public final T getDelegate() {
		return b;
	}

	public final AsmClass build() {
		return b.build();
	}

	public final AsmNewClass getPreliminaryClass() {
		return asmClass;
	}

	protected final void newInstance_DefaultConstructor(AsmClass type) {
		TypeBuilderUtils.newInstance_DefaultConstructor(mv, type);
	}

	protected final void addConversionFromPrimitiveIfNecessary(AsmType primitive) {
		TypeBuilderUtils.addConversionFromPrimitiveIfNecessary(mv, primitive);
	}

	protected final void addConversionToPrimitiveIfEligible(AsmType primitive) {
		TypeBuilderUtils.addConversionToPrimitiveIfEligible(mv, primitive);
	}

	protected final void checkCast(AsmType asmType) {
		TypeBuilderUtils.checkCast(mv, asmType.getRawType());
	}

	protected final void checkCast(AsmClass asmClass) {
		TypeBuilderUtils.checkCast(mv, asmClass);
	}
	
	protected final void getInstanceField(AsmField field) {
		TypeBuilderUtils.getInstanceField(mv, field);
	}

	protected final void getStaticField(AsmField field) {
		TypeBuilderUtils.getStaticField(mv, field);
	}

	protected final void getStaticField(String declaringClass, String fieldName, String fieldType) {
		TypeBuilderUtils.getStaticField(mv, declaringClass, fieldName, fieldType);
	}

	protected final void putInstanceField(AsmField field) {
		TypeBuilderUtils.putInstanceField(mv, field);
	}

	protected final void putStaticField(AsmField field) {
		TypeBuilderUtils.putStaticField(mv, field);
	}

	protected final void invokeMethod(AsmMethod asmMethod) {
		TypeBuilderUtils.invokeMethod(mv, asmMethod);
	}

	protected final void pushDefault(AsmType primitiveType) {
		TypeBuilderUtils.pushDefault(mv, primitiveType.getRawType());
	}
	
	protected final void pushDefault(AsmClass primitiveClass) {
		TypeBuilderUtils.pushDefault(mv, primitiveClass);
	}
	
	protected final void pushConstant(int value) {
		TypeBuilderUtils.pushConstant(mv, value);
	}

	protected final void pushConstant(boolean value) {
		TypeBuilderUtils.pushConstant(mv, value);
	}

	protected final void addReturn(AsmType asmType) {
		addReturn(asmType.getRawType());
	}

	protected final void addReturn(AsmClass asmClass) {
		mv.visitInsn(AsmUtils.getReturnOpCode(asmClass));
	}

	/**
	 * @return internalName of class that is being build by this builder
	 */
	protected final String internalName() {
		return asmClass.getInternalName();
	}

}
