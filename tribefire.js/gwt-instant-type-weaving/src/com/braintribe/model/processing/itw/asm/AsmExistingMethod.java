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
package com.braintribe.model.processing.itw.asm;

import java.lang.reflect.Method;

import com.braintribe.asm.Type;

/**
 * 
 */
public class AsmExistingMethod extends AsmMethod {

	private final Method method;

	public AsmExistingMethod(AsmClassPool classPool, Method method) {
		super(classPool);
		this.method = method;
	}

	public Method getExistingMethod() {
		return method;
	}

	@Override
	public String getName() {
		return method.getName();
	}

	@Override
	public String getReturnTypeName() {
		return Type.getDescriptor(method.getReturnType());
	}

	@Override
	public int getModifiers() {
		return method.getModifiers();
	}

	private AsmClass[] params;
	private AsmClass declaringClass;
	private AsmClass returnType;

	@Override
	public AsmClass[] getParams() {
		if (params == null) {
			params = AsmUtils.toAsmClasses(classPool, method.getParameterTypes());
		}
		return params;
	}

	@Override
	public AsmClass getDeclaringClass() {
		if (declaringClass == null) {
			declaringClass = classPool.get(method.getDeclaringClass());
		}

		return declaringClass;
	}

	@Override
	public AsmClass getReturnType() {
		if (returnType == null) {
			returnType = classPool.get(method.getReturnType());
		}

		return returnType;
	}

	public boolean isDefault() {
		return method.isDefault();
	}

	@Override
	public String toString() {
		return method.toString();
	}
}
