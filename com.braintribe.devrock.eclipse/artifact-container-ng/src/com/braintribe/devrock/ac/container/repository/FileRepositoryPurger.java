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
package com.braintribe.devrock.ac.container.repository;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.IStatus;

import com.braintribe.common.lcd.Pair;
import com.braintribe.devrock.api.storagelocker.StorageLocker;
import com.braintribe.devrock.api.storagelocker.StorageLockerSlots;
import com.braintribe.devrock.eclipse.model.resolution.nodes.AnalysisNode;
import com.braintribe.devrock.mc.api.commons.PartIdentifications;
import com.braintribe.devrock.mc.api.repository.configuration.RepositoryReflection;
import com.braintribe.devrock.mc.core.commons.ArtifactRemover;
import com.braintribe.devrock.model.repository.MavenFileSystemRepository;
import com.braintribe.devrock.model.repository.Repository;
import com.braintribe.devrock.plugin.DevrockPlugin;
import com.braintribe.devrock.plugin.DevrockPluginStatus;
import com.braintribe.gm.model.reason.Reason;
import com.braintribe.model.artifact.analysis.AnalysisArtifact;
import com.braintribe.model.artifact.consumable.Part;
import com.braintribe.model.artifact.essential.VersionedArtifactIdentification;
import com.braintribe.model.version.Version;

public class FileRepositoryPurger {
	
	public static List<VersionedArtifactIdentification> purge(List<AnalysisNode> nodes, FileRepositoryPurgeListener listener) {
		RepositoryReflection repositoryReflection = DevrockPlugin.mcBridge().reflectRepositoryConfiguration().get();
		
		//System.out.println("Removing "  + nodes.stream().map( n -> n.getBackingSolution().asString()).collect( Collectors.joining(",")));
		
		DevrockPlugin devrockPlugin = DevrockPlugin.instance();

		List<VersionedArtifactIdentification> purgedArtifacts = new ArrayList<>( nodes.size());
		
		List<File> nonDeletable = new ArrayList<>( nodes.size());
		int i = 0;
		for (AnalysisNode node : nodes) {
			i++;
			// start purge
			if (listener != null) {
				listener.acknowledgePurge(node, i);
			}
			
			// determine location 
			AnalysisArtifact artifact = node.getBackingSolution();
			String artifactName = artifact.asString();
			
			// check whether that's something to delete  
			Version version = Version.parse( artifact.getVersion());
			if (!version.isPreliminary()) {			
				continue;
			}

			// deduce from pom 
			Part pomPart = artifact.getParts().get(PartIdentifications.pom.asString());
			if (pomPart == null) {
				DevrockPluginStatus status = new DevrockPluginStatus("cannot remove pc-artifact has no pom attached: " + artifactName, IStatus.ERROR);
				devrockPlugin.log(status);
				continue;
			}
			String repositoryOrigin = pomPart.getRepositoryOrigin();
			Repository repository = repositoryReflection.getRepository( repositoryOrigin);
			if (repository == null) {
				// no repository found 
				DevrockPluginStatus status = new DevrockPluginStatus("cannot remove pc-artifact [" + artifactName + "] as no repository found: " + repositoryOrigin, IStatus.ERROR);
				devrockPlugin.log(status);
				continue;
			}
			if (repository instanceof MavenFileSystemRepository == false) {
				// no file system repo
				DevrockPluginStatus status = new DevrockPluginStatus("cannot remove pc-artifact [" + artifactName + "] as repository is no filesystem-repo: " + repositoryOrigin, IStatus.ERROR);
				devrockPlugin.log(status);
				continue;
			}
			
			
			MavenFileSystemRepository fileRepo = (MavenFileSystemRepository) repository;
			File repoRoot = new File( fileRepo.getRootPath());
			
			Pair<Reason,File> pair = ArtifactRemover.removeArtifactFromFilesystemRepo(node.getSolutionIdentification(), repoRoot);
			if (pair.first != null) {
				DevrockPluginStatus status = new DevrockPluginStatus("cannot remove pc-artifact [" + artifactName + "] : " + pair.first.stringify(), IStatus.ERROR);
				devrockPlugin.log(status);
			}
			if (pair.second != null) {
				nonDeletable.add( pair.second);
			}
			// add to controller's list of purged artifacts
			purgedArtifacts.add(artifact);
			node.setIsPurged(true);
			// done purge
			if (listener != null) {
				listener.acknowledgePurged(node, i);
			}
		}				
		// write to locker
		storeNonDeleteables(nonDeletable);
		return purgedArtifacts;
	}
	
	public static void storeNonDeleteables(List<File> nonDeletableFiles) {
		StorageLocker storageLocker = DevrockPlugin.instance().storageLocker();
		// retrieve already stored ones
		List<String> value = storageLocker.getValue(StorageLockerSlots.SLOT_ARTIFACT_VIEWER_PC_PURGE_NONDELETES, new ArrayList<>());	
		// determine 
		List<String> filesNamesForDeferredDelete = nonDeletableFiles.stream().map( f -> f.getAbsolutePath()).collect( Collectors.toList());
		// combine
		Set<String> uniques = new HashSet<>( value.size() + filesNamesForDeferredDelete.size());
		
		uniques.addAll( filesNamesForDeferredDelete);
		uniques.addAll(value);
		
		storageLocker.setValue( StorageLockerSlots.SLOT_ARTIFACT_VIEWER_PC_PURGE_NONDELETES, new ArrayList<String>( uniques));
	}
	
}
