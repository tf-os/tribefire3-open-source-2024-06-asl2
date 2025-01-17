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
package com.braintribe.wire.impl.scope.caller;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.braintribe.wire.api.scope.InstanceHolder;
import com.braintribe.wire.api.scope.InstanceHolderSupplier;
import com.braintribe.wire.api.scope.InstanceParameterization;
import com.braintribe.wire.api.scope.WireScope;
import com.braintribe.wire.api.space.WireSpace;
import com.braintribe.wire.impl.scope.AbstractWireScope;
import com.braintribe.wire.impl.scope.singleton.SingletonScope;

public class CallerScope extends AbstractWireScope {
	private Map<SupplierKey, InstanceHolderSupplier> holderSuppliers = new ConcurrentHashMap<>();
	
	@Override
	public InstanceHolderSupplier createHolderSupplier(WireSpace managedSpace, String name, InstanceParameterization parameterization) {
		
		return new CallerScopeInstanceHolderSupplier(managedSpace, name, parameterization);
	}
	
	private class CallerScopeInstanceHolderSupplier implements InstanceHolderSupplier {
		private WireSpace managedSpace;
		private String name;
		private InstanceParameterization parameterization;

		public CallerScopeInstanceHolderSupplier(WireSpace managedSpace, String name, InstanceParameterization parameterization) {
			this.managedSpace = managedSpace;
			this.name = name;
			this.parameterization = parameterization;
		}

		@Override
		public InstanceHolder getHolder(Object instanceContext) {
			InstanceHolder lastHolder = context.currentInstancePath().lastElement();
			
			WireScope effectiveScope = lastHolder != null? lastHolder.scope(): context.getScope(SingletonScope.class);
			
			SupplierKey key = new SupplierKey(effectiveScope, this);
			InstanceHolderSupplier effectiveSupplier = holderSuppliers.computeIfAbsent(key, k -> effectiveScope.createHolderSupplier(managedSpace, name, parameterization));
			return effectiveSupplier.getHolder(instanceContext);
		}
	}

	@Override
	public void close() throws Exception {
		// noop as this is only a delegator
	}
	
	public static class SupplierKey {
		WireScope scope;
		InstanceHolderSupplier holderSupplier;
		
		public SupplierKey(WireScope scope, InstanceHolderSupplier holderSupplier) {
			super();
			this.scope = scope;
			this.holderSupplier = holderSupplier;
		}
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((holderSupplier == null) ? 0 : holderSupplier.hashCode());
			result = prime * result + ((scope == null) ? 0 : scope.hashCode());
			return result;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			SupplierKey other = (SupplierKey) obj;
			if (holderSupplier == null) {
				if (other.holderSupplier != null)
					return false;
			} else if (holderSupplier != other.holderSupplier)
				return false;
			if (scope == null) {
				if (other.scope != null)
					return false;
			} else if (scope != other.scope)
				return false;
			return true;
		}
		
	}

}
