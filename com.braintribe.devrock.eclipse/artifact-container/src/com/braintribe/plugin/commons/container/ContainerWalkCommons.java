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
package com.braintribe.plugin.commons.container;

import com.braintribe.devrock.artifactcontainer.container.ArtifactContainer;
import com.braintribe.model.malaclypse.cfg.container.ArtifactContainerConfiguration;
import com.braintribe.model.malaclypse.cfg.denotations.ExclusionControlDenotationType;
import com.braintribe.model.malaclypse.cfg.denotations.ScopeControlDenotationType;
import com.braintribe.model.malaclypse.cfg.denotations.WalkDenotationType;
import com.braintribe.model.malaclypse.cfg.denotations.WalkDomain;
import com.braintribe.model.malaclypse.cfg.denotations.WalkKind;
import com.braintribe.model.malaclypse.cfg.denotations.WalkScope;
import com.braintribe.model.malaclypse.cfg.denotations.clash.ClashResolverByDepthDenotationType;
import com.braintribe.model.malaclypse.cfg.denotations.clash.ClashResolverByIndexDenotationType;
import com.braintribe.model.malaclypse.cfg.denotations.clash.ClashResolverDenotationType;
import com.braintribe.model.malaclypse.cfg.denotations.clash.OptimisticClashResolverDenotationType;
import com.braintribe.model.malaclypse.cfg.denotations.scopes.DependencyScope;
import com.braintribe.model.malaclypse.cfg.denotations.scopes.MagicScope;
import com.braintribe.model.malaclypse.cfg.denotations.scopes.ScopeTreatement;

public class ContainerWalkCommons {
	
	public static ClashResolverDenotationType buildClashResolverDenotationType( ArtifactContainer container) {
		ArtifactContainerConfiguration configuration = container.getConfiguration();
		if (configuration == null) {
			return OptimisticClashResolverDenotationType.T.create();
		}
		switch (configuration.getResolverKind()) {
		case hierarchy:
			return ClashResolverByDepthDenotationType.T.create();			
		case index:
			return ClashResolverByIndexDenotationType.T.create();
		case optimistic:		
		default:
			return OptimisticClashResolverDenotationType.T.create();					
		}
	}
	
	
	/**
	 * creates a magic scope for compile 
	 */
	public static MagicScope createClasspathMagicScope() {
		MagicScope classpathScope = MagicScope.T.create();
		classpathScope.setName("classpathScope");
		
		DependencyScope compileScope = DependencyScope.T.create();
		compileScope.setName("compile");
		compileScope.setScopeTreatement( ScopeTreatement.INCLUDE);
		classpathScope.getScopes().add( compileScope);
		
		DependencyScope providedScope = DependencyScope.T.create();
		providedScope.setName("provided");
		providedScope.setScopeTreatement( ScopeTreatement.INCLUDE);		
		classpathScope.getScopes().add( providedScope);
		
		return classpathScope;
	}
	
	public static MagicScope createRuntimeMagicScope() {
		MagicScope runtimeScope = MagicScope.T.create();
		runtimeScope.setName("runtimeScope");
		
		DependencyScope compileScope = DependencyScope.T.create();
		compileScope.setName("compile");
		compileScope.setScopeTreatement( ScopeTreatement.INCLUDE);
		runtimeScope.getScopes().add( compileScope);
		
		DependencyScope providedScope = DependencyScope.T.create();
		providedScope.setName("runtime");
		providedScope.setScopeTreatement( ScopeTreatement.INCLUDE);		
		runtimeScope.getScopes().add( providedScope);
		
		return runtimeScope;
	}
	
	public static WalkDenotationType buildCompileWalkDenotationType(WalkScope scopeLevel, WalkDomain walkDomain, WalkKind walkKind, boolean skipOptional) {
		WalkDenotationType walkType = WalkDenotationType.T.create();
		
		ClashResolverDenotationType clashResolverType = OptimisticClashResolverDenotationType.T.create();
		walkType.setClashResolverDenotationType( clashResolverType);
		
		ExclusionControlDenotationType exclusionControlType = ExclusionControlDenotationType.T.create();		
		walkType.setExclusionControlDenotationType( exclusionControlType);
		
		ScopeControlDenotationType scopeControlType = ScopeControlDenotationType.T.create();
		scopeControlType.setSkipOptional( skipOptional);		
		
		MagicScope scope;
		switch (scopeLevel) {
			case compile:
			default:
				scope = createClasspathMagicScope();
			break;
			case launch:
				scope = createRuntimeMagicScope();
			break;
		}
		scopeControlType.getScopes().add( scope);

		walkType.setScopeControlDenotationType( scopeControlType);
		walkType.setWalkDomain(walkDomain);
		walkType.setWalkKind(walkKind);
		
		walkType.setTypeFilter( "jar");
		
		return walkType;
	}
	
}
