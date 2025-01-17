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
package com.braintribe.wire.impl.scope.dynamic;

import java.util.Stack;

import com.braintribe.cfg.ScopeContext;
import com.braintribe.wire.api.context.InternalWireContext;
import com.braintribe.wire.api.context.ScopeContextHolders;
import com.braintribe.wire.api.scope.InstanceHolder;
import com.braintribe.wire.api.scope.InstanceHolderSupplier;
import com.braintribe.wire.api.scope.InstanceParameterization;
import com.braintribe.wire.api.scope.InstanceQualification;
import com.braintribe.wire.api.scope.WireScope;
import com.braintribe.wire.api.space.WireSpace;
import com.braintribe.wire.impl.scope.AbstractWireScope;

public class DynamicScope<C extends ScopeContext> extends AbstractWireScope {
	
	private final ThreadLocal<Stack<ScopeEntry<C>>> scopeLocal = new ThreadLocal<Stack<ScopeEntry<C>>>() {
		@Override
		protected Stack<ScopeEntry<C>> initialValue() {
			return new Stack<>();
		}
	};
	
	@Override
	public void attachContext(InternalWireContext context) {
		super.attachContext(context);
	}
	
	@Override
	public InstanceHolderSupplier createHolderSupplier(WireSpace managedSpace, String name,
			InstanceParameterization parameterization) {
		return new DynamicBeanHolderSupplier(managedSpace, this, name);
	}
	
	public void push(C context) {
		
		Stack<ScopeEntry<C>> stack = scopeLocal.get();
		
		ScopeContextHolders scopeContextHolders = getContext().getScopeForContext(context); 
		ScopeEntry<C> entry = new ScopeEntry<>(scopeContextHolders, context);
		
		stack.push(entry);

	}

	public C pop() {
		Stack<ScopeEntry<C>> stack = scopeLocal.get();
		
		if (stack.isEmpty())
			throw new RuntimeException("corrupted stack exception. Check if pushing scopes corresponds with popping scopes");
		
		ScopeEntry<C> entry = stack.pop();
		
		if (stack.isEmpty()) {
			scopeLocal.remove();
		}
		
		return entry.context;
	}
	
	public void end(C context) {
		getContext().close(context);
	}
	
	public C context() {
		return peekScopeEntry().context;
	}
	
	private ScopeEntry<C> peekScopeEntry() {
		return scopeLocal.get().peek();
	}
	
	private static class ScopeEntry<C> {
		final ScopeContextHolders scopeContextHolders;
		final C context;

		public ScopeEntry(ScopeContextHolders scopeContextHolders, C context) {
			this.scopeContextHolders = scopeContextHolders;
			this.context = context;
		}
		
		public ScopeContextHolders getScopeContextHolders() {
			return scopeContextHolders;
		}
	}
	
	@Override
	public void close() throws Exception {
		// noop
	}
	
	private class DynamicBeanHolderSupplier implements InstanceHolderSupplier, InstanceQualification {
		private final DynamicScope<?> scope;
		private final String name;
		private final WireSpace space;
		
		public DynamicBeanHolderSupplier(WireSpace space, DynamicScope<?> scope, String name) {
			this.space = space;
			this.scope = scope;
			this.name = name;
		}
		
		@Override
		public WireSpace space() {
			return space;
		}
		
		@Override
		public String name() {
			return name; 
		}

		@Override
		public InstanceHolder getHolder(Object context) {
			if (context == null)
				return peekScopeEntry().getScopeContextHolders().acquireHolder(this);
			else
				return getContext().getScopeForContext((ScopeContext)context).acquireHolder(this);
		}

		@Override
		public WireScope scope() {
			return scope;
		}


	}
}
