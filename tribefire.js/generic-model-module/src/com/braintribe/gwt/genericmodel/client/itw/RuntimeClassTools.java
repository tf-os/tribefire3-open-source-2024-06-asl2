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
package com.braintribe.gwt.genericmodel.client.itw;

import com.braintribe.common.attribute.AttributeAccessor;
import com.braintribe.common.attribute.TypeSafeAttribute;
import com.braintribe.model.generic.GmCoreApiInteropNamespaces;
import com.google.gwt.core.client.JavaScriptObject;

import jsinterop.annotations.JsMethod;

/**
 * @author peter.gazdik
 */
public class RuntimeClassTools {

	@JsMethod(namespace = GmCoreApiInteropNamespaces.attr)
	public static <T> AttributeAccessor<T> createAttribute(String name) {
		Class<TypeSafeAttribute<T>> key = (Class<TypeSafeAttribute<T>>) createForInterface(name);
		return AttributeAccessor.create(key);
	}

	public static <T> Class<T> createForClass(String typeSignature, Class<?> superClass) {
		ClassNameDesc cnd = new ClassNameDesc(typeSignature);
		return ScriptOnlyItwTools.createForClass(cnd.packageName, cnd.className, null, superClass);
	}

	public final static Class<?> createForInterface(String typeSignature) {
		ClassNameDesc cnd = new ClassNameDesc(typeSignature);

		return ScriptOnlyItwTools.createForInterface(cnd.packageName, cnd.className);
	}

	public static <T extends Enum<T>> Class<T> newEnumClass(String typeSignature, String[] values) {
		ClassNameDesc cnd = new ClassNameDesc(typeSignature);

		Enum<?> enumConstants[] = new Enum<?>[values.length];
		GenericJavaScriptObject enumConstantMap = JavaScriptObject.createObject().cast();

		JavaScriptObject enumConstantsFunc = createEnumConstantsFunc(enumConstants);
		JavaScriptObject enumValueOfFunc = createEnumValueOfFunc(enumConstantMap);

		Class<T> newClassLiteral = ScriptOnlyItwTools.createForEnum(cnd.packageName, cnd.className, null, Enum.class, enumConstantsFunc,
				enumValueOfFunc);

		for (int ordinal = 0; ordinal < values.length; ordinal++) {
			String name = values[ordinal];
			Enum<?> enumConstant = createEnumInstance(name, ordinal, newClassLiteral);
			enumConstants[ordinal] = enumConstant;
			enumConstantMap.setProperty(name, enumConstant);
		}

		return newClassLiteral;
	}

	private static Enum<?> createEnumInstance(String name, int ordinal, Class<? extends Enum<?>> enumClass) {
		return ScriptOnlyItwTools.createEnumInstance(name, ordinal, RuntimeMethodNames.instance.objectGetClass(),
				RuntimeMethodNames.instance.enumGetDeclaringClass(), enumClass);
	}

	// @formatter:off
	private static native JavaScriptObject createEnumConstantsFunc(Enum<?>[] constants) /*-{
		return function() {
			return constants;
		};
	}-*/;

	private static native JavaScriptObject createEnumValueOfFunc(JavaScriptObject constants) /*-{
		return function(name) {
			return constants[name];
		};
	}-*/;
	// @formatter:on

	public static class ClassNameDesc {
		public String packageName;
		public String className;

		public ClassNameDesc(String qualifiedClassName) {
			int index = qualifiedClassName.lastIndexOf('.');

			packageName = qualifiedClassName.substring(0, index);
			className = qualifiedClassName.substring(index + 1);
		}
	}

}
