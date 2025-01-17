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
package tribefire.platform.impl.deployment;

import java.util.Arrays;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

import com.braintribe.logging.Logger;
import com.braintribe.model.deployment.Deployable;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.processing.deployment.api.ComponentBinder;
import com.braintribe.model.processing.deployment.api.ConfigurableComponentInterfaceBindings;
import com.braintribe.model.processing.deployment.api.DeploymentException;

/**
 * @author peter.gazdik
 */
public class ComponentInterfaceBindingsRegistry implements ConfigurableComponentInterfaceBindings {

	private static final Logger log = Logger.getLogger(ComponentInterfaceBindingsRegistry.class);

	private final Map<EntityType<? extends Deployable>, Class<?>[]> componentInterfaceBindings = new IdentityHashMap<>();

	@Override
	public Class<?>[] findComponentInterfaces(EntityType<? extends Deployable> componentType) {
		return retrieveComponentInterfaces(componentType, false);
	}

	@Override
	public Class<?>[] getComponentInterfaces(EntityType<? extends Deployable> componentType) throws DeploymentException {
		return retrieveComponentInterfaces(componentType, true);
	}

	protected Class<?>[] retrieveComponentInterfaces(EntityType<? extends Deployable> componentType, boolean required) {
		Objects.requireNonNull(componentType, "componentType must not be null");

		Class<?>[] componentInterfaces = componentInterfaceBindings.get(componentType);

		if (componentInterfaces == null && required)
			throw new DeploymentException("No component interface is registered for the component type [" + componentType.getTypeSignature() + "]");

		return componentInterfaces;
	}

	@Override
	public void registerComponentInterfaces(ComponentBinder<?, ?> binder) {
		checkBinderOk(binder);

		EntityType<? extends Deployable> componentType = binder.componentType();
		Class<?>[] newInterfaces = binder.componentInterfaces();

		Class<?>[] oldInterfaces = componentInterfaceBindings.put(componentType, newInterfaces);

		logComponentIfaceRegistration(componentType, newInterfaces, oldInterfaces);
	}
	
	public void registerComponentInterfaces(EntityType<? extends Deployable> componentType, Class<?>... interfaces) {
		Class<?>[] newInterfaces = interfaces;
		Class<?>[] oldInterfaces = componentInterfaceBindings.put(componentType, newInterfaces);
		logComponentIfaceRegistration(componentType, newInterfaces, oldInterfaces);
	}

	private void checkBinderOk(ComponentBinder<?, ?> binder) {
		Objects.requireNonNull(binder, "binder must not be null");

		EntityType<?> ct = binder.componentType();
		Class<?>[] cis = binder.componentInterfaces();

		verifyBinderState(binder, ct == null, "componentType() returned null");
		verifyBinderState(binder, cis == null, "componentInterfaces() returned null");
		verifyBinderState(binder, cis.length == 0, "componentInterfaces() returned an empty array");

		for (Class<?> ci : cis) {
			verifyBinderState(binder, ci == null, "componentInterfaces() returned an array with null entries");
			verifyBinderState(binder, !ci.isInterface(), () -> "componentInterfaces() contains a class which is not an interface: " + ci.getName());
		}
	}

	private static void verifyBinderState(ComponentBinder<?, ?> binder, boolean illegalState, String message) {
		if (illegalState)
			throw new IllegalStateException(binder.getClass().getSimpleName() + " is invalid. " + message);
	}

	private static void verifyBinderState(ComponentBinder<?, ?> binder, boolean illegalState, Supplier<String> messageSupplier) {
		if (illegalState)
			throw new IllegalStateException(binder.getClass().getSimpleName() + " is invalid. " + messageSupplier.get());
	}

	private void logComponentIfaceRegistration(EntityType<? extends Deployable> componentType, Class<?>[] newIfaces, Class<?>[] oldIfaces) {
		if (oldIfaces == null)
			log.debug(() -> "Registered " + componentType.getTypeSignature() + " component type interfaces: " + Arrays.toString(newIfaces));

		else if (!Arrays.equals(newIfaces, oldIfaces))
			log.warn("Registered different " + componentType.getTypeSignature() + " component type interfaces: "
					+ Arrays.toString(newIfaces) + ". Previous interfaces: " + Arrays.toString(oldIfaces));
	}

}
