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

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.braintribe.model.artifact.processing.ArtifactIdentification;
import com.braintribe.model.artifact.processing.HasArtifactIdentification;
import com.braintribe.model.artifact.processing.ResolvedArtifact;
import com.braintribe.model.artifact.processing.ResolvedArtifactPart;
import com.braintribe.model.artifact.processing.cfg.env.OverridingEnvironmentVariable;
import com.braintribe.model.artifact.processing.cfg.repository.RepositoryConfiguration;
import com.braintribe.model.generic.reflection.EntityType;

public class Commons {

	
	public static HasArtifactIdentification generateDenotation( String grp, String art, String vrs) {
		HasArtifactIdentification hasArtifactIdentification = HasArtifactIdentification.T.create();
		ArtifactIdentification artifactIdentification = ArtifactIdentification.T.create();		
		hasArtifactIdentification.setArtifact( parameterizeDenotation(artifactIdentification, grp, art, vrs));
		return hasArtifactIdentification;
	}
	
	public static ArtifactIdentification generate( String grp, String art, String vrs) {
		ArtifactIdentification artifactIdentification = ArtifactIdentification.T.create();
		artifactIdentification.setGroupId(grp);
		artifactIdentification.setArtifactId(art);
		artifactIdentification.setVersion(vrs);
		return artifactIdentification;		
	}

	
	public static ArtifactIdentification parameterizeDenotation( ArtifactIdentification artifactIdentification, String grp, String art, String vrs) {			
		artifactIdentification.setGroupId(grp);
		artifactIdentification.setArtifactId(art);
		artifactIdentification.setVersion(vrs);
		return artifactIdentification;		
	}

	
	public static void attachVirtualEnvironment( RepositoryConfiguration scopeConfiguration, Map<String,String> properties) {
		
		for (Entry<String,String> entry : properties.entrySet()) {
			OverridingEnvironmentVariable oevPort = OverridingEnvironmentVariable.T.create();
			oevPort.setName( entry.getKey());
			oevPort.setValue( entry.getValue());		
			scopeConfiguration.getEnvironmentOverrides().add( oevPort);		
		}					
	}
	
	public static ResolvedArtifact createResolvedArtifact( String condensed) {
		String [] values = condensed.split( "[:#]");
		ResolvedArtifact resolvedArtifact = ResolvedArtifact.T.create();
		resolvedArtifact.setGroupId( values[0]);
		resolvedArtifact.setArtifactId( values[1]);
		resolvedArtifact.setVersion( values[2]);
		return resolvedArtifact;		
	}
	public static <T extends ArtifactIdentification> T  parameterize( EntityType<T> type, String condensed) {
		String [] values = condensed.split( "[:#]");
		T resolvedArtifact = type.create();	
		resolvedArtifact.setGroupId( values[0]);
		resolvedArtifact.setArtifactId( values[1]);
		resolvedArtifact.setVersion( values[2]);	
		return resolvedArtifact;
	}
	
	
		
	public static ResolvedArtifactPart createResolvedArtifactPart(ResolvedArtifact owner, String classifier, String type, String url) {		
		ResolvedArtifactPart resolvedArtifactPart = ResolvedArtifactPart.T.create();
		resolvedArtifactPart.setClassifier(classifier);
		resolvedArtifactPart.setType(type);
		resolvedArtifactPart.setUrl( url);				
		return resolvedArtifactPart;		
	}
	
	
	
	public static boolean compare( ResolvedArtifact found, ResolvedArtifact expected) {
		if (found == null && expected != null) {
			return false;
		}
		if (found != null && expected == null) {
			return false;
		}
		if (!found.getGroupId().equalsIgnoreCase( expected.getGroupId()))
			return false;
		if (!found.getArtifactId().equalsIgnoreCase( expected.getArtifactId()))
			return false;
		if (!found.getVersion().equalsIgnoreCase( expected.getVersion()))
			return false;
		
		return true;
	}
	
	public static String toString( ResolvedArtifact ra) {
		return ra.getGroupId() + ":" + ra.getArtifactId() + "#" + ra.getVersion();
	}
	
	public static String toString( ResolvedArtifact ra, ResolvedArtifactPart pa) {
		return ra.getGroupId() + ":" + ra.getArtifactId() + "#" + ra.getVersion() + "@" + pa.getUrl() + " <" + pa.getClassifier() + ":" + pa.getType() + ">";
	}
	
	public static String psToString( List<ResolvedArtifactPart> ras) {
		StringBuilder builder = new StringBuilder();
		ras.stream().forEach( ra -> {
			if (builder.length() > 0)
				builder.append(",");
			builder.append( toString( ra));
		});
		return builder.toString();
	}
	
	public static String toString( ResolvedArtifactPart pa) {
		return pa.getUrl() + " <" + pa.getClassifier() + ":" + pa.getType() + ">";
	}
		
	public static String rsToString( List<ResolvedArtifact> ras) {
		StringBuilder builder = new StringBuilder();
		ras.stream().forEach( ra -> {
			if (builder.length() > 0)
				builder.append(",");
			builder.append( toString( ra));
		});
		return builder.toString();
	}



	/**
	 * matches part URLs.. currently, rp has a full qualified URL, p only has the releveant file name 
	 * @param rp - the {@link ResolvedArtifactPart} fully qualified part
	 * @param p - the {@link ResolvedArtifactPart} to match (partially qualified only)
	 * @return - true if the URL are considered to be equivalent
	 */
	public static boolean matchPartUrl(ResolvedArtifactPart rp, ResolvedArtifactPart p) {
		return rp.getUrl().endsWith( p.getUrl());		
	}
	

	/**
	 * @param resolvedArtifact
	 * @return
	 */
	public static Collection<ResolvedArtifact> extractTransitiveDependencies(ResolvedArtifact resolvedArtifact) {
		Set<ResolvedArtifact> ras = new HashSet<>();
		resolvedArtifact.getDependencies().stream().forEach( ra -> {
			ras.add(ra);
			ras.addAll( extractTransitiveDependencies(ra));
		});
		return ras;
	}
	
	
}
