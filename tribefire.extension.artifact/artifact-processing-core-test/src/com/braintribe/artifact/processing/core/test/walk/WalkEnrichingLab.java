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
package com.braintribe.artifact.processing.core.test.walk;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.braintribe.artifact.processing.core.test.Commons;
import com.braintribe.build.artifact.test.repolet.LauncherShell.RepoType;
import com.braintribe.model.artifact.processing.ArtifactResolution;
import com.braintribe.model.artifact.processing.HasArtifactIdentification;
import com.braintribe.model.artifact.processing.ResolvedArtifact;
import com.braintribe.model.artifact.processing.ResolvedArtifactPart;
import com.braintribe.model.artifact.processing.cfg.repository.RepositoryConfiguration;
import com.braintribe.model.artifact.processing.cfg.resolution.ResolutionConfiguration;

public class WalkEnrichingLab extends AbstractDependenciesLab {
	private static final String grpId = "com.braintribe.devrock.test.ape";
	private static final String artId = "ape-terminal";
	private static final String version = "1.0.1-pc";	
	
	private Map<String, RepoType> launcherMap;
	
	{
		launcherMap = new HashMap<>();		
		launcherMap.put( "archive," + new File( testSetup, "archive.zip").getAbsolutePath(), RepoType.singleZip);
		
	}
	
	private ResolvedArtifact expectedTerminal;
	private ResolvedArtifact expectedA;	
	private ResolvedArtifact expectedC;
	private ResolvedArtifact expectedCommons;
	{	
		expectedTerminal = Commons.createResolvedArtifact( grpId + ":" + artId + "#" + version);				
		expectedA = Commons.createResolvedArtifact( grpId + ":" + "a" + "#" + version);		
		expectedC = Commons.createResolvedArtifact( grpId + ":" + "c" + "#" + version);
		expectedCommons = Commons.createResolvedArtifact( grpId + ":" + "ape-commons" + "#" + version);
	}
	
	private ResolvedArtifactPart ape_term_javadoc_part;
	private ResolvedArtifactPart ape_term_cmdzip_part;
	private ResolvedArtifactPart ape_a_javadoc_part;
	private ResolvedArtifactPart ape_commons_javadoc_part;
	private ResolvedArtifactPart ape_c_assetman_part;
	
	private List<ResolvedArtifactPart> expectedParts = new ArrayList<>();
	{
		ape_term_javadoc_part = Commons.createResolvedArtifactPart(expectedTerminal, "javadoc", "jar", "ape-terminal-1.0.1-pc-javadoc.jar");		
		expectedParts.add(ape_term_javadoc_part);
		
		ape_term_cmdzip_part = Commons.createResolvedArtifactPart(expectedTerminal, "cmd", "zip", "ape-terminal-1.0.1-pc-cmd.zip");
		expectedParts.add(ape_term_cmdzip_part);
		
		ape_a_javadoc_part = Commons.createResolvedArtifactPart(expectedA, "javadoc", "jar", "a-1.0.1-pc-javadoc.jar");		
		expectedParts.add(ape_a_javadoc_part);
		
		ape_commons_javadoc_part = Commons.createResolvedArtifactPart(expectedCommons, "javadoc", "jdar", "ape-commons-1.0.1-pc-javadoc.jdar");		
		expectedParts.add(ape_commons_javadoc_part);
		
		ape_c_assetman_part = Commons.createResolvedArtifactPart(expectedC, "asset", "man", "c-1.0.1-pc-asset.man");		
		expectedParts.add(ape_c_assetman_part);
				
	}
	
	private int port;
		
	@Before
	public void before() {
		port = runBefore(launcherMap);
	}
	
	@After
	public void after() {
		runAfter();
	}
	
	/**
	 * run a test from the standard parent while *adding* the passed parts' tuples to the relevant tuple list. Validates result automatically
	 * @param expectedParts - the {@link ResolvedArtifactPart} to add their tuples 
	 */
	private void runAdditionalEnrichingTest(ResolvedArtifactPart ...expectedParts) {
		RepositoryConfiguration scopeConfiguration = generateResourceScopeConfiguration( new File( testSetup, "settings.xml"), overridesMap);
		
		HasArtifactIdentification hasArtifactIdentification = Commons.generateDenotation(grpId, artId, version);
				
		ResolutionConfiguration walkConfiguration = createWalkConfiguration( expectedParts);						
		
		ArtifactResolution resolved = resolvedDependencies( repo, hasArtifactIdentification, scopeConfiguration, walkConfiguration);
		
		validateParts( resolved.getResolvedArtifact(), Arrays.asList(expectedParts));
	}
	
	private void runSimplifiedAdditionalEnrichingTest(ResolvedArtifactPart ...expectedParts) {
		RepositoryConfiguration scopeConfiguration = generateModelledScopeConfiguration("braintribe.Base", "http://localhost:"+ port + "/archive/", "builder", "operating2005", null, overridesMap);
		
		HasArtifactIdentification hasArtifactIdentification = Commons.generateDenotation(grpId, artId, version);
				
		ResolutionConfiguration walkConfiguration = createWalkConfiguration( expectedParts);						
		
		ArtifactResolution resolved = resolvedDependencies( repo, hasArtifactIdentification, scopeConfiguration, walkConfiguration);
		
		validateParts( resolved.getResolvedArtifact(), Arrays.asList(expectedParts));
	}
	
		
	/**
	 * validate whether the parts passed are actually found somewhere amongst the artifact
	 * @param resolvedArtifacts - {@link List} containing the {@link ResolvedArtifactPart} to look at
	 * @param expected - a {@link List} of {@link ResolvedArtifactPart} to search for
	 */
	@SuppressWarnings("unused")
	private void validateParts(List<ResolvedArtifact> resolvedArtifacts, List<ResolvedArtifactPart> expected) {
		List<ResolvedArtifactPart> matched = new ArrayList<>();
		resolvedArtifacts.stream().forEach( ra -> {
			ra.getParts().stream().forEach( rp -> {
				for (ResolvedArtifactPart p : expected) {				
					if (Commons.matchPartUrl( rp, p)) {
						matched.add( p);
						break;
					}
				}								
			});
		});
		List<ResolvedArtifactPart> temp = new ArrayList<>( expected);
		temp.removeAll(matched);
		if (temp.size() > 0) {
			Assert.fail( "not all expected parts were retrieved : [" + Commons.psToString( temp) + "]");
		}		
	}
	
	
	/**
	 * @param resolvedArtifact
	 * @param expected
	 */
	private void validateParts(ResolvedArtifact resolvedArtifact, List<ResolvedArtifactPart> expected) {
		List<ResolvedArtifactPart> matched = new ArrayList<>();
		Collection<ResolvedArtifact> resolvedArtifacts = Commons.extractTransitiveDependencies( resolvedArtifact);
		resolvedArtifacts.add(resolvedArtifact);
		resolvedArtifacts.stream().forEach( ra -> {
			ra.getParts().stream().forEach( rp -> {
				for (ResolvedArtifactPart p : expected) {				
					if (Commons.matchPartUrl( rp, p)) {
						matched.add( p);
						break;
					}
				}								
			});
		});
		List<ResolvedArtifactPart> temp = new ArrayList<>( expected);
		temp.removeAll(matched);
		if (temp.size() > 0) {
			Assert.fail( "not all expected parts were retrieved : [" + Commons.psToString( temp) + "]");
		}		
	}
	
	

	/**
	 * deduces the part tuple from the expected parts, and parameterizes a configuration by adding the tuple
	 * @param artifactParts - the expected {@link ResolvedArtifactPart} to extract the tuples from 
	 * @return - the {@link ResolutionConfiguration} where the tuples were added to the standard
	 */
	private ResolutionConfiguration createWalkConfiguration( ResolvedArtifactPart ...artifactParts) {
		ResolutionConfiguration wc = ResolutionConfiguration.T.create();
		for (ResolvedArtifactPart part : artifactParts) {
			String tuple;
			if (part.getClassifier() != null) {
				tuple = part.getClassifier() + ":" + part.getType();
			}
			else {
				tuple = part.getType();
			}
			wc.getParts().add(tuple);
		}
		return wc;
	}
	
	
	
	
	/**
	 * just adds the tuple for asset:man files and makes sure it's retrieved
	 */
	@Test
	public void runAssetTest() {
		runAdditionalEnrichingTest( ape_c_assetman_part);
	}

	@Test
	public void runCmdZipTerminalTest() {
		runAdditionalEnrichingTest( ape_term_cmdzip_part);
	}
	
	@Test
	public void runJavadocTerminalTest() {
		runAdditionalEnrichingTest( ape_term_javadoc_part, ape_a_javadoc_part);
	}

	
	@Test
	public void runJavadocJdarTest() {
		runAdditionalEnrichingTest( ape_commons_javadoc_part);
	}
	
	/**
	 * just adds the tuple for asset:man files and makes sure it's retrieved
	 */
	@Test
	public void runSimplyfiedAssetTest() {
		runSimplifiedAdditionalEnrichingTest( ape_c_assetman_part);
	}

	@Test
	public void runSimplyfiedCmdZipTerminalTest() {
		runSimplifiedAdditionalEnrichingTest( ape_term_cmdzip_part);
	}
	
	@Test
	public void runSimplyfiedJavadocTerminalTest() {
		runSimplifiedAdditionalEnrichingTest( ape_term_javadoc_part, ape_a_javadoc_part);
	}

	
	@Test
	public void runSimplyfiedJavadocJdarTest() {
		runSimplifiedAdditionalEnrichingTest( ape_commons_javadoc_part);
	}


}
