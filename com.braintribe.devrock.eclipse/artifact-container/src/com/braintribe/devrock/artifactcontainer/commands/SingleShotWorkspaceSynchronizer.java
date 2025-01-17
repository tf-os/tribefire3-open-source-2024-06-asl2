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
package com.braintribe.devrock.artifactcontainer.commands;



import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;

import com.braintribe.devrock.artifactcontainer.ArtifactContainerPlugin;
import com.braintribe.devrock.artifactcontainer.ArtifactContainerStatus;
import com.braintribe.devrock.artifactcontainer.container.ArtifactContainer;
import com.braintribe.devrock.artifactcontainer.control.walk.ArtifactContainerUpdateRequestType;
import com.braintribe.devrock.artifactcontainer.control.walk.wired.WiredArtifactContainerWalkController;
import com.braintribe.logging.Logger;
import com.braintribe.model.artifact.Artifact;


/**
 * a standalone command for workspace sync - only used to be able to wire a short-cut key to it.
 * @author pit
 *
 */
public class SingleShotWorkspaceSynchronizer extends AbstractHandler {
	private static Logger log = Logger.getLogger(SingleShotWorkspaceSynchronizer.class);

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
	
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();			
		IProject [] rProjects = root.getProjects();
		executeMultipleCommand(rProjects);				
		return null;
	}
		
	protected void executeMultipleCommand(IProject... projects) {
		// update registry
		ArtifactContainerPlugin.getWorkspaceProjectRegistry().update();
		
		Set<ArtifactContainer> containers = new HashSet<ArtifactContainer>();
		if (projects != null) {
			for (IProject project : projects) {
				Artifact artifact = ArtifactContainerPlugin.getWorkspaceProjectRegistry().getArtifactForProject(project);
				if (artifact == null)
					continue;
				ArtifactContainer container = ArtifactContainerPlugin.getArtifactContainerRegistry().getContainerOfProject( project);
				if (container == null) {
					ArtifactContainerStatus status = new ArtifactContainerStatus( "Project [" + project.getName() + "] has no associated ArtifactContainer", IStatus.WARNING);
					ArtifactContainerPlugin.getInstance().log(status);	
					continue;
				}
				// if we do sync, we remove the stored data and let the processor refill it
				container.clear();		
				containers.add(container);
				
				// calculate dependers
				if (ArtifactContainerPlugin.getInstance().getArtifactContainerPreferences(false).getDynamicContainerPreferences().getChainArtifactSync()) {
					List<ArtifactContainer> dependerContainers = ArtifactContainerPlugin.getWorkspaceProjectRegistry().getDependerContainers(container);
					if (dependerContainers.size() > 0) {
						containers.addAll( dependerContainers);
						String synchedProjects = dependerContainers.stream().map( c -> {
							return c.getProject().getProject().getName();
						}).collect( Collectors.joining( ","));
						String msg = "adding [" + synchedProjects + "] (" + dependerContainers.size() + ") containers as they are dependers of [" + project.getName() + "] which was the sync target ";
						log.debug(msg);
						ArtifactContainerPlugin.log(msg);
							
					}
				}								
			}
		}
		// shouldn't be required... but *maybe* there a several containers for the same project?
		Set<String> projectNames = new HashSet<>();
		Iterator<ArtifactContainer> iterator = containers.iterator();
		while (iterator.hasNext()) {
			ArtifactContainer container = iterator.next();
			String name = container.getProject().getProject().getName();
			if (!projectNames.add( name)) {
				String msg = "duplicate container [" + name + "] detected, skipping";
				log.debug(msg);
				ArtifactContainerPlugin.log(msg);
				iterator.remove();
			}			
		}

		// protocol 
		if (ArtifactContainerPlugin.getInstance().getArtifactContainerPreferences(false).getDynamicContainerPreferences().getChainArtifactSync()) {
			String initiations = Arrays.asList( projects).stream().map( c -> {
				return c.getProject().getProject().getName();
			}).collect( Collectors.joining( ","));		
			
			String requests = projectNames.stream().collect(Collectors.joining(","));
			String msg = "auto-chain : sync initiation on [" + initiations + "] lead to (" + projectNames.size() + ") resync requests for [" + requests + "]";
			log.debug(msg);
			ArtifactContainerPlugin.log(msg);
			
			ArtifactContainerStatus status = new ArtifactContainerStatus( msg, IStatus.INFO);
			ArtifactContainerPlugin.getInstance().log(status);
		}
						
		WiredArtifactContainerWalkController.getInstance().updateContainers( new ArrayList<>(containers), ArtifactContainerUpdateRequestType.combined);
	}

	
}
