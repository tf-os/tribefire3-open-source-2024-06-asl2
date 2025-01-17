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
package com.braintribe.artifact.processing.core.test.writer;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.braintribe.logging.Logger;
import com.braintribe.model.artifact.processing.ResolvedArtifact;

public class ResolvedArtifactWriter extends WriterCommons{
	private static Logger log = Logger.getLogger(ResolvedArtifactWriter.class);
	
	private Writer writer;
	private Set<ResolvedArtifact> dumped = new HashSet<>();

	public ResolvedArtifactWriter(Writer writer) {
		this.writer = writer;					
	}

	public void dump( Collection<ResolvedArtifact> artifacts){
		artifacts.stream().forEach( ra -> {
			try {
				dump( ra);
			} catch (IOException e) {
				log.error( "cannot dump [" + getKey(ra) + "]");
			}
		});
	}
	
	public void dump( ResolvedArtifact artifact) throws IOException{
		dump( artifact, 0, true);
	}
	
	private void dump( ResolvedArtifact artifact, int index, boolean full) throws IOException {
		if (full) {
			writer.write( tab( index) + getKey( artifact) + "\t<-" + dump( artifact.getRepositoryOrigins()) + "\n");			
			artifact.getDependencies().stream()			
				.forEach( d -> {
					boolean explicit = false;
					if (!dumped.contains( d)) {
						dumped.add(d);
						explicit = true;
					}
					try {
						dump( d, index+1, explicit);
					} catch (IOException e) {
						log.error( "cannot dump [" + getKey(d) + "]");					
					}							
				});
		}
		else {
			writer.write( tab( index) + "[" + getKey( artifact) + "]\n");
		}
		
	}
	
	private String getKey( ResolvedArtifact artifact) {
		return artifact.getGroupId() + ":" + artifact.getArtifactId() + "#" + artifact.getVersion();		
	}
	
	
}
