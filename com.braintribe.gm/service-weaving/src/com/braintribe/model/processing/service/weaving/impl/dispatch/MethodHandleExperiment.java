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
package com.braintribe.model.processing.service.weaving.impl.dispatch;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;

public class MethodHandleExperiment {

	public static void main(String[] args) {
		try {
			Method method = MethodHandleExperiment.class.getDeclaredMethod("foo", String.class);
			MethodHandle handle = MethodHandles.lookup().unreflect(method);
			
			Object object = "Hallo Welt!";
			
			MethodHandleExperiment instance = new MethodHandleExperiment();
			
			handle.invoke(instance, object);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	public void foo(String s) {
		System.out.println(s);
	}
}
