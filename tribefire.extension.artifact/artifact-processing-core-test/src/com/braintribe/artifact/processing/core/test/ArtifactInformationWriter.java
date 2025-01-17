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
package com.braintribe.artifact.processing.core.test;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

import com.braintribe.model.artifact.info.ArtifactInformation;
import com.braintribe.model.artifact.info.LocalRepositoryInformation;
import com.braintribe.model.artifact.info.PartInformation;
import com.braintribe.model.artifact.info.RemoteRepositoryInformation;

/**
 * simple dumper 
 * 
 * @author pit
 *
 */
public class ArtifactInformationWriter {

	private Writer writer;
	
	public ArtifactInformationWriter( Writer writer) {
		this.writer = writer; 		
	}
	

	/**
	 * d
	 * @param information
	 */
	public void dump(ArtifactInformation information) throws IOException{
		if (information == null) {			
			return;
		}
		
		writer.write( "retrieved information about : " + information.getGroupId() + ":" + information.getArtifactId() + "#" + information.getVersion() + "\n");
		LocalRepositoryInformation localInformation = information.getLocalInformation();
		if (localInformation != null) {
			writer.write( "local information:");
			writer.write( "\t@ " + localInformation.getUrl() + "\n");			
			for (PartInformation partInformation : localInformation.getPartInformation()) {
				String classifier = partInformation.getClassifier();
				if (classifier != null)
					writer.write( "\t\t" + classifier + ":" + partInformation.getType() + "\t" + partInformation.getUrl() + "\n");
				else
					writer.write( "\t\t" + partInformation.getType() + "\t" + partInformation.getUrl() + "\n");
			}
		}
		List<RemoteRepositoryInformation> remoteInformation = information.getRemoteInformation();
		writer.write( "remote information:");
		for (RemoteRepositoryInformation remoteArtifactInformation : remoteInformation) {
			writer.write(  "\t" + remoteArtifactInformation.getName() + " @ " + remoteArtifactInformation.getUrl() + "\n");
			for (PartInformation partInformation : remoteArtifactInformation.getPartInformation()) {
				String classifier = partInformation.getClassifier();
				if (classifier != null)
					writer.write(  "\t\t" + classifier + ":" + partInformation.getType() + "\t" + partInformation.getUrl() + "\n");
				else
					writer.write(  "\t\t" + partInformation.getType() + "\t" + partInformation.getUrl() + "\n");
			}
		}
	}
	
}
