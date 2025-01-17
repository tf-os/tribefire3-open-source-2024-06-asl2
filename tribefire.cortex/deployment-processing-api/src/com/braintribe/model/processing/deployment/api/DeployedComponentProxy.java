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
package com.braintribe.model.processing.deployment.api;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import com.braintribe.model.deployment.Deployable;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.utils.ReflectionTools;

/**
 * This proxy class allows to loosely bind to a deployed component of some deployable. If the deployable is currently not deployed an explicit or auto default delegate will be used
 * @author dirk.scheffler
 *
 */
public class DeployedComponentProxy implements InvocationHandler {

	private DeployRegistry deployRegistry;
	private Deployable deployable;
	private Object defaultDelegate;
	private EntityType<? extends Deployable> componentType;

	public static <I> I create(Class<I> componentInterface, DeployRegistry deployRegistry, Deployable deployable, EntityType<? extends Deployable> componentType) {
		return create(componentInterface, deployRegistry, deployable, componentType, null);
	}

	public static <I> I create(Class<I> componentInterface, DeployRegistry deployRegistry, Deployable deployable, EntityType<? extends Deployable> componentType, Object defaultDelegate) {
		DeployedComponentProxy deployedComponentProxy = new DeployedComponentProxy(deployRegistry, deployable, defaultDelegate, componentType);
		@SuppressWarnings("unchecked")
		I implementation = (I) Proxy.newProxyInstance(componentInterface.getClassLoader(), new Class<?>[] { componentInterface }, deployedComponentProxy);
		return implementation;
	}

	private DeployedComponentProxy(DeployRegistry deployRegistry, Deployable deployable, Object defaultDelegate, EntityType<? extends Deployable> componentType) {
		super();
		this.deployRegistry = deployRegistry;
		this.deployable = deployable;
		this.defaultDelegate = defaultDelegate;
		this.componentType = componentType;
	}

	private Object getDelegate() {
		DeployedUnit deployedUnit = deployRegistry.resolve(deployable);

		if (deployedUnit != null) {
			Object delegate = deployedUnit.getComponent(componentType);
			return delegate;
		}

		return defaultDelegate;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		if (method.getDeclaringClass() == Object.class)
			return method.invoke(this, args);
		else {
			Object delegate = getDelegate();

			if (delegate != null)
				try {
					return method.invoke(delegate, args);
				} catch (InvocationTargetException e) {
					throw e.getCause() != null ? e.getCause() : e;
				}
			else {
				return ReflectionTools.getDefaultValue(method.getReturnType());
			}
		}
	}

}
