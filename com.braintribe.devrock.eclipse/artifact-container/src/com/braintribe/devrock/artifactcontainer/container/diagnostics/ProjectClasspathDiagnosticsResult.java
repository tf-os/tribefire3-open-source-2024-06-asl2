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
package com.braintribe.devrock.artifactcontainer.container.diagnostics;

import java.util.Map;

import org.eclipse.core.resources.IProject;

import com.braintribe.devrock.artifactcontainer.control.walk.ArtifactContainerUpdateRequestType;

public class ProjectClasspathDiagnosticsResult {
	
	private IProject project;
	private Map<ArtifactContainerUpdateRequestType, ContainerClasspathDiagnosticsResult> results;
	
	public IProject getProject() {
		return project;
	}
	public void setProject(IProject project) {
		this.project = project;
	}
	public Map<ArtifactContainerUpdateRequestType, ContainerClasspathDiagnosticsResult> getResults() {
		return results;
	}
	public void setResults(Map<ArtifactContainerUpdateRequestType, ContainerClasspathDiagnosticsResult> results) {
		this.results = results;
	}
	
	
}
