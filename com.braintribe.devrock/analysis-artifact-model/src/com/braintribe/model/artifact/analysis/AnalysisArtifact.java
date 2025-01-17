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
package com.braintribe.model.artifact.analysis;

import java.util.List;
import java.util.Set;

import com.braintribe.model.artifact.compiled.CompiledArtifact;
import com.braintribe.model.artifact.consumable.Artifact;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.version.Version;

/**
 * 
 * represents an artifact with information used for analysis 
 * @author pit
 *
 */
public interface AnalysisArtifact extends Artifact, AnalysisTerminal {
	
	EntityType<AnalysisArtifact> T = EntityTypes.T(AnalysisArtifact.class);

	// TODO: add missing string literals
	
	/**
	 * @return - the {@link AbstractCompiledArtifact} that was the origin of this {@link AnalysisArtifact}
	 */
	CompiledArtifact getOrigin();
	void setOrigin( CompiledArtifact origin);
	
	/**
	 * @return - a {@link List} of {@link AnalysisDependency}
	 */
	List<AnalysisDependency> getDependencies();
	void setDependencies( List<AnalysisDependency> dependencies);
	
	AnalysisDependency getParent();
	void setParent(AnalysisDependency parent);
	
	Set<AnalysisDependency> getParentDependers();
	void setParentDependers(Set<AnalysisDependency> parentDependers);
	
	List<AnalysisDependency> getImports();
	void setImports(List<AnalysisDependency> imports);
	
	Set<AnalysisDependency> getImporters();
	void setImporters(Set<AnalysisDependency> importers);
	
	/**
	 * @return - a {@link Set} of the all the {@link AnalysisDependency} that reference this {@link AnalysisArtifact}
	 */
	Set<AnalysisDependency> getDependers();
	void setDependers( Set<AnalysisDependency> dependencies);
	
	/**
	 * @return - the order in which the dependency stands in the dependency tree 
	 */
	int getDependencyOrder();
	void setDependencyOrder(int dependencyOrder);
	 
	/**
	 * @return - the order in which the dependency was visited 
	 */
	int getVisitOrder();
	void setVisitOrder(int visitOrder);

	/**
	 * @param origin - the {@link CompiledArtifact}
	 * @return - a transposed {@link AnalysisArtifact}
	 */
	static AnalysisArtifact of(CompiledArtifact origin) {
		AnalysisArtifact artifact = AnalysisArtifact.T.create();
		artifact.setOrigin(origin);
		artifact.setGroupId(origin.getGroupId());
		artifact.setArtifactId(origin.getArtifactId());
		
		Version version = origin.getVersion();
		if (version != null) {
			artifact.setVersion(version.asString());
		}		
		artifact.setPackaging(origin.getPackaging());
		artifact.setArchetype(origin.getArchetype());
		return artifact;
	}
		
}
