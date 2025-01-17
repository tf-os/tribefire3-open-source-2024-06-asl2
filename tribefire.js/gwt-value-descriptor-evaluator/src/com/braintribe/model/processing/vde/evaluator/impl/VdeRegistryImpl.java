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
package com.braintribe.model.processing.vde.evaluator.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.braintribe.common.lcd.Pair;
import com.braintribe.model.generic.value.ValueDescriptor;
import com.braintribe.model.processing.vde.evaluator.api.ValueDescriptorEvaluator;
import com.braintribe.model.processing.vde.evaluator.api.VdeRegistry;

public class VdeRegistryImpl implements VdeRegistry {

	private Map<Class<? extends ValueDescriptor>, ValueDescriptorEvaluator<?>> concreteExperts = new HashMap<Class<? extends ValueDescriptor>, ValueDescriptorEvaluator<?>>();
	private List<Pair<Class<? extends ValueDescriptor>, ValueDescriptorEvaluator<?>>> abstractExperts = new ArrayList<Pair<Class<? extends ValueDescriptor>, ValueDescriptorEvaluator<?>>>();

	@Override
	public Map<Class<? extends ValueDescriptor>, ValueDescriptorEvaluator<?>> getConcreteExperts() {
		return this.concreteExperts;
	}

	@Override
	public void setConcreteExperts(Map<Class<? extends ValueDescriptor>, ValueDescriptorEvaluator<?>> concreteExperts) {
		this.concreteExperts.putAll(concreteExperts);
	}

	@Override
	public List<Pair<Class<? extends ValueDescriptor>, ValueDescriptorEvaluator<?>>> getAbstractExperts() {
		return this.abstractExperts;
	}

	@Override
	public void setAbstractExperts(List<Pair<Class<? extends ValueDescriptor>, ValueDescriptorEvaluator<?>>> abstractExperts) {
		this.abstractExperts.addAll(abstractExperts);
	}


	@Override
	public <D extends ValueDescriptor> void putConcreteExpert(Class<D> vdType, ValueDescriptorEvaluator<? super D> vdEvaluator) {
		this.concreteExperts.put(vdType, vdEvaluator);
	}

	@Override
	public <D extends ValueDescriptor> void putAbstractExpert(Class<D> vdType, ValueDescriptorEvaluator<? super D> vdEvaluator) {
		this.abstractExperts.add(new Pair<Class<? extends ValueDescriptor>, ValueDescriptorEvaluator<?>>(vdType, vdEvaluator));
	}

	@Override
	public void resetRegistry() {

		this.concreteExperts = new HashMap<Class<? extends ValueDescriptor>, ValueDescriptorEvaluator<?>>();
		this.abstractExperts = new ArrayList<Pair<Class<? extends ValueDescriptor>, ValueDescriptorEvaluator<?>>>();

	}

	@Override
	public void loadOtherRegistry(VdeRegistry otherRegistry) {
		this.concreteExperts.putAll(otherRegistry.getConcreteExperts());
		this.abstractExperts.addAll(otherRegistry.getAbstractExperts());
	}


	@Override
	public void removeConcreteExpert(Class<? extends ValueDescriptor> valueDescriptorClass) {
		this.concreteExperts.remove(valueDescriptorClass);
	}

	@Override
	public void removeAbstractExpert(Class<? extends ValueDescriptor> valueDescriptorClass) {
		int counter = -1;
		boolean found = false;
		for (Pair<Class<? extends ValueDescriptor>, ValueDescriptorEvaluator<?>> abstractExpertPair : this.abstractExperts) {
			counter++;
			Class<? extends ValueDescriptor> abstractExpertJavaClass = abstractExpertPair.getFirst();
			if (abstractExpertJavaClass.equals(valueDescriptorClass)) {
				found = true;
				break;
			}
		}
		if (found)
			this.abstractExperts.remove(counter);
	}

}
