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
package com.braintribe.devrock.artifactcontainer.control.project;

import org.eclipse.core.resources.IProject;

import com.braintribe.model.artifact.Artifact;

/**
 * @author pit
 *
 */
public class ProjectImporterTuple {

	private String projectFile;
	private Artifact artifact;
	private IProject project;

	public ProjectImporterTuple(String projectFile, Artifact artifact) {
		this.projectFile = projectFile;
		this.artifact = artifact;
	}
	
	public ProjectImporterTuple( IProject project, Artifact artifact) {
		this.project = project;
		this.artifact = artifact;	
	}

	public String getProjectFile() {
		return projectFile;
	}

	public Artifact getArtifact() {
		return artifact;
	}

	public IProject getProject() {
		return project;
	}

	public void setProject(IProject project) {
		this.project = project;
	}
	
	
	

}