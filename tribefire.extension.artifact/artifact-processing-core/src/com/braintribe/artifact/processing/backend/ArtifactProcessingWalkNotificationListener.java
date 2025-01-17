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
package com.braintribe.artifact.processing.backend;

import java.util.List;

import com.braintribe.build.artifact.representations.artifact.pom.listener.PomReaderNotificationListener;
import com.braintribe.build.artifact.walk.multi.clash.listener.ClashResolverNotificationListener;
import com.braintribe.build.artifact.walk.multi.listener.WalkNotificationListener;
import com.braintribe.model.artifact.Artifact;
import com.braintribe.model.artifact.Dependency;
import com.braintribe.model.artifact.Solution;
import com.braintribe.model.malaclypse.cfg.denotations.WalkDenotationType;

/**
 * a {@link WalkNotificationListener} for the extension
 *   
 * @author pit
 *
 */
public class ArtifactProcessingWalkNotificationListener implements WalkNotificationListener, ClashResolverNotificationListener, PomReaderNotificationListener {

	//
	// start / stop
	//
	@Override
	public void acknowledgeStartOn(String walkScopeId, Solution solution, WalkDenotationType denotationType) {}

	@Override
	public void acknowledgeEndOn(String walkScopeId, Solution solution) {}

	//
	// phases
	//
	@Override
	public void acknowledgeTraversingPhase(String walkScopeId) {}

	@Override
	public void acknowledgeDeterminationPhase(String walkScopeId, int numUndetermined) {}

	@Override
	public void acknowledgeDependencyClashResolvingPhase(String walkScopeId, int nunDependencies) {}

	@Override
	public void acknowledgeSolutionClashResolvingPhase(String walkScopeId, int numSolutions) {}

	@Override
	public void acknowledgeEnrichingPhase(String walkScopeId, int numSolutions) {}
	
	
	
	//
	// clash resolving
	//
	
	// dependency
	@Override
	public void acknowledgeDependencyClashResolving(String walkScopeId, Dependency dependency) {}
	
	@Override
	public void acknowledgeDependencyClashes(String walkScopeId, Dependency dependency, List<Dependency> clashes) {}

	// solution (obsolete, but still functional)
	@Override
	public void acknowledgeSolutionClashResolving(String walkScopeId, Solution solution) {}
	
	@Override
	public void acknowledgeSolutionClashes(String walkScopeId, Solution solution, List<Solution> clashes) {}

	// classifier clash
	@Override
	public void acknowledgeClashOnDependencyClassifier(String walkScopeId, Dependency dependency, String current, String requested) {}
	
	
	//
	// unresolved, reassigned, undetermined (obsolete, but still functional)
	// 
	@Override
	public void acknowledgeUnresolvedDependency(String walkScopeId, Dependency dependency) {}

	@Override
	public void acknowledgeReassignedDependency(String walkScopeId, Dependency undetermined, Dependency replacement) {}

	@Override
	public void acknowledgeUndeterminedDependency(String walkScopeId, Dependency undetermined) {}

	//
	// traversing
	//
	@Override
	public void acknowledgeTraversing(String walkScopeId, Solution artifact, Dependency parent, int level, boolean valid) {}

	@Override
	public void acknowledgeTraversing(String walkScopeId, Dependency dependency, Solution parent, int level) {}

	@Override
	public void acknowledgeTraversingEndpoint(String walkScopeId, Dependency dependency, Solution parent, int level) {}

	@Override
	public void acknowledgeTraversingEndpoint(String walkScopeId, Solution solution, Dependency parent, int level) {}
	
	

	//
	// POM
	//
	
	// reading
	@Override
	public void acknowledgeReadErrorOnFile(String walkScopeId, String location, String reason) {		
	}

	@Override
	public void acknowledgeReadErrorOnArtifact(String walkScopeId, Artifact artifact, String reason) {		
	}

	@Override
	public void acknowledgeReadErrorOnString(String walkScopeId, String contents, String reason) {		
	}

	// parsing
	@Override
	public void acknowledgeVariableResolvingError(String walkScopeId, Artifact artifact, String expression) {				
	}
	
	// associating
	@Override
	public void acknowledgeSolutionAssociation(String walkScopeId, String location, Artifact artifact) {		
	}

	@Override
	public void acknowledgeParentAssociation(String walkScopeId, Artifact child, Solution parent) {
	}

	@Override
	public void acknowledgeParentAssociationError(String walkScopeId, Artifact child, String groupId, String artifactId, String version) {
	}

	@Override
	public void acknowledgeImportAssociation(String walkScopeId, Artifact requestingSolution, Solution requestedSolution) {
	}

	@Override
	public void acknowledgeImportAssociationError(String walkScopeId, Artifact requestingSolution, String groupId, String artifactId, String version) {		
	}

	//
	// results
	//
	@Override
	public void acknowledgeWalkResult(String walkScopeId, List<Solution> solutions) {}

	@Override
	public void acknowledgeCollectedDependencies(String walkScopeId, List<Dependency> collectedDependencies) {}

}
