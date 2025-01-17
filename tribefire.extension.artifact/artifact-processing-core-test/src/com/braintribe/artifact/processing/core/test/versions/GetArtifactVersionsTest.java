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
package com.braintribe.artifact.processing.core.test.versions;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.braintribe.artifact.processing.ArtifactProcessingCoreExpert;
import com.braintribe.artifact.processing.core.test.AbstractArtifactProcessingLab;
import com.braintribe.artifact.processing.core.test.Commons;
import com.braintribe.build.artifact.test.repolet.LauncherShell.RepoType;
import com.braintribe.model.artifact.processing.ArtifactIdentification;
import com.braintribe.model.artifact.processing.HasArtifactIdentification;
import com.braintribe.model.artifact.processing.cfg.repository.RepositoryConfiguration;

/**
 * TestCase for the GetArtifactVersion test 
 * 
 * 
 * @author pit
 *
 */

public class GetArtifactVersionsTest extends AbstractArtifactProcessingLab{
	
	private static final String grpId = "com.braintribe.devrock.test.ranges";
	private static final String artId = "RangeTestTerminal";
	private static final String version = "1.0.1";
	private Map<String, RepoType> map;
	private File configuration = new File( testSetup, "settings.xml");
	
	{
		map = new HashMap<>();		
		map.put( "archive," + new File( testSetup, "archive.zip").getAbsolutePath(), RepoType.singleZip);
	}
	
	@Before
	public void before() {
		runBefore(map);		
	}
	
	@After
	public void after() {
		runAfter();
	}
	/**
	 * tests with the {@link ArtifactIdentification} fully qualified per properties, with revisions
	 */
	@Test
	public void testFullyQualified() {
		HasArtifactIdentification hasArtifactIdentification = Commons.generateDenotation(grpId, artId, version);		
		RepositoryConfiguration scopeConfiguration = generateResourceScopeConfiguration( configuration, overridesMap);
		List<String> artifactVersions = ArtifactProcessingCoreExpert.getArtifactVersions( repo, hasArtifactIdentification, scopeConfiguration, false);
		
		Assert.assertTrue( "result is unexpectedly empty", artifactVersions.size() > 0);
		String [] expectedVersions = new String[] {"1.0.1", "1.0.2", "1.0.3", "1.0.4"};
		validate(Arrays.asList(expectedVersions), artifactVersions);
		
	}
	/**
	 * tests with the {@link ArtifactIdentification} fully qualified per properties, with revisions
	 */
	@Test
	public void testFullyQualifiedWithoutRevisions() {
		HasArtifactIdentification hasArtifactIdentification = Commons.generateDenotation(grpId, artId, version);		
		RepositoryConfiguration scopeConfiguration = generateResourceScopeConfiguration( configuration, overridesMap);
		List<String> artifactVersions = ArtifactProcessingCoreExpert.getArtifactVersions( repo, hasArtifactIdentification, scopeConfiguration, true);
		
		Assert.assertTrue( "result is unexpectedly empty", artifactVersions.size() > 0);
		String [] expectedVersions = new String[] {"1.0"};
		validate(Arrays.asList(expectedVersions), artifactVersions);
		
	}
	
	/**
	 * tests with the {@link ArtifactIdentification} partially qualified (no version range) by properties. with revisions
	 */
	@Test
	public void testPartiallyQualified() {
		HasArtifactIdentification hasArtifactIdentification = Commons.generateDenotation(grpId, artId, null);
		RepositoryConfiguration scopeConfiguration = generateResourceScopeConfiguration( configuration, overridesMap);
		List<String> artifactVersions = ArtifactProcessingCoreExpert.getArtifactVersions( repo, hasArtifactIdentification, scopeConfiguration, false);
		
		Assert.assertTrue( "result is unexpectedly empty", artifactVersions.size() > 0);
		String [] expectedVersions = new String[] {"1.0.1", "1.0.2", "1.0.3", "1.0.4","1.1.1","1.1.2", "1.2.1-pc"};
		validate(Arrays.asList(expectedVersions), artifactVersions);
	}
	
	@Test
	public void testPartiallyQualifiedWithoutRevision() {
		HasArtifactIdentification hasArtifactIdentification = Commons.generateDenotation(grpId, artId, null);
		RepositoryConfiguration scopeConfiguration = generateResourceScopeConfiguration( configuration, overridesMap);
		List<String> artifactVersions = ArtifactProcessingCoreExpert.getArtifactVersions( repo, hasArtifactIdentification, scopeConfiguration, true);
		
		Assert.assertTrue( "result is unexpectedly empty", artifactVersions.size() > 0);
		String [] expectedVersions = new String[] {"1.0","1.1", "1.2"};
		validate(Arrays.asList(expectedVersions), artifactVersions);
	}
	
	private void validate(List<String> expectedVersions, List<String> foundVersions) {
		List<String> unexpected = new ArrayList<>();
		List<String> matched = new ArrayList<>();
		for (String version : foundVersions) {
			if (!expectedVersions.contains( version)) {
				unexpected.add(version);
			}
			else {
				matched.add( version);
			}
		}
		List<String> missing = new ArrayList<>( expectedVersions);
		missing.removeAll(matched);
		
		if (unexpected.size() != 0) {
			String msg = "Unexpected :" + unexpected.stream().collect( Collectors.joining( ","));
			Assert.fail( msg);
		}
		
		if (missing.size() != 0) {
			String msg = "Missing :" + missing.stream().collect( Collectors.joining( ","));
			Assert.fail( msg);
		}
	}

}
