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
package tribefire.cortex.manipulation.conversion.beans;

import static com.braintribe.utils.lcd.CollectionTools2.newLinkedSet;

import java.util.Set;

/**
 * Root beans are {@link NewBean}s which have dependencies on all the {@link NewBean}s in out pool. Only these need to be stated in the "initialize"
 * method.
 */
public class BeansFinder_Root {

	public static Set<NewBean> findRootBeans(BeanRegistry beanRegistry) {
		return new BeansFinder_Root(beanRegistry).findEm();
	}

	private final BeanRegistry beanRegistry;

	private final Set<NewBean> roots = newLinkedSet();
	private final Set<EntityBean<?>> handled = newLinkedSet();

	private BeansFinder_Root(BeanRegistry beanRegistry) {
		this.beanRegistry = beanRegistry;
	}

	private Set<NewBean> findEm() {
		for (NewBean newBean : beanRegistry.newBeans) {
			if (!handled.add(newBean))
				continue;

			removeAllDepsOf(newBean);
			roots.add(newBean);
		}

		return roots;
	}

	private void removeAllDepsOf(EntityBean<?> bean) {
		for (EntityBean<?> dep : bean.deps) {
			roots.remove(dep);

			if (handled.add(dep))
				removeAllDepsOf(dep);
		}
	}
}
