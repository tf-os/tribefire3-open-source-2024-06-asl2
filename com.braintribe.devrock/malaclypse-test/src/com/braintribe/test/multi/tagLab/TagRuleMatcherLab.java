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
package com.braintribe.test.multi.tagLab;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.braintribe.artifacts.test.maven.pom.AbstractPomReaderLab;
import com.braintribe.build.artifact.name.NameParser;
import com.braintribe.build.artifact.walk.multi.filters.TagRuleFilter;
import com.braintribe.model.artifact.Dependency;
import com.braintribe.model.artifact.processing.version.VersionRangeProcessor;
import com.braintribe.model.artifact.version.VersionRange;


public class TagRuleMatcherLab extends AbstractPomReaderLab {
	
	private static List<Dependency> dependencies;
	private static final String groupId = "com.braintribe.devrock.test.tags";
	private static final VersionRange versionRange = VersionRangeProcessor.createFromString( "1.0");
	
	@BeforeClass	
	public static void runbefore() {
		dependencies = new ArrayList<>();
		
		Dependency none = Dependency.T.create();
		none.setGroupId( groupId);
		none.setVersionRange(versionRange);
		none.setArtifactId("none");
		
		dependencies.add(none);
		
		Dependency standard = Dependency.T.create();
		standard.setGroupId( groupId);
		standard.setVersionRange(versionRange);
		standard.setArtifactId("standard");
		standard.getTags().add( "standard");

		dependencies.add(standard);
		
		Dependency one = Dependency.T.create();
		one.setGroupId( groupId);
		one.setVersionRange(versionRange);
		one.setArtifactId("one");
		one.getTags().add( "one");
		
		dependencies.add(one);

		Dependency oneAndTwo = Dependency.T.create();
		oneAndTwo.setGroupId( groupId);
		oneAndTwo.setVersionRange(versionRange);
		oneAndTwo.setArtifactId("one-and-two");
		oneAndTwo.getTags().add( "one");
		oneAndTwo.getTags().add( "two");
		
		Dependency classpath = Dependency.T.create();
		classpath.setGroupId( groupId);
		classpath.setVersionRange(versionRange);
		classpath.setArtifactId("classpath");
		classpath.getTags().add( "classpath");
		classpath.getTags().add( "asset");
		
		dependencies.add(classpath);		
	}
	
	public void test( String rule, List<String> expectedNames) {
		TagRuleFilter matcher = new TagRuleFilter();
		matcher.setRule(rule);
		
		List<String> unexpectedNames = new ArrayList<>();
		List<String> expectedNames2 = new ArrayList<>( expectedNames);
		List<Dependency> filtered = matcher.filterDependencies(dependencies);
		List<Dependency> processed = new ArrayList<>( filtered);
		Iterator<Dependency> iterator = processed.iterator();
		
		while (iterator.hasNext()) {
			Dependency dependency = iterator.next();
			String name = NameParser.buildName(dependency);			
			if (expectedNames.contains( name)) {
				iterator.remove();
				expectedNames2.remove( name);
			}
			else {
				unexpectedNames.add( name);
			}
		}
		if (unexpectedNames.size() == 0 && processed.size() == 0) {			
			return;
		}
		
		Assert.assertTrue( "unexpected [" + nameListToString(unexpectedNames) + "]", unexpectedNames.size() == 0);
		Assert.assertTrue( "undelivered [" + nameListToString( expectedNames2) + "]", expectedNames2.size() == 0);
	
		
	}
	
	private String nameListToString( List<String> names) {
		StringBuffer buffer = new StringBuffer();
		for (String name : names) {
			if (buffer.length() > 0)
				buffer.append( ",");
			buffer.append( name);
		}
		return buffer.toString();
	}
	/*
	private String dependencyListToString( List<Dependency> names) {
		StringBuffer buffer = new StringBuffer();
		for (Dependency name : names) {			
			if (buffer.length() > 0)
				buffer.append( ",");
			buffer.append( NameParser.buildName( name));
		}
		return buffer.toString();
	}
*/
	@Test
	public void noRuleTest() {
		test( null, Arrays.asList( 
				"com.braintribe.devrock.test.tags:none#1.0",
				"com.braintribe.devrock.test.tags:standard#1.0",
				"com.braintribe.devrock.test.tags:one#1.0",
				"com.braintribe.devrock.test.tags:one-and-two#1.0",
				"com.braintribe.devrock.test.tags:classpath#1.0"
		));
	}
	
	@Test
	public void allInRuleTest() {
		test( "*", Arrays.asList( 		
				"com.braintribe.devrock.test.tags:standard#1.0",
				"com.braintribe.devrock.test.tags:one#1.0",
				"com.braintribe.devrock.test.tags:one-and-two#1.0",
				"com.braintribe.devrock.test.tags:classpath#1.0"
		));
	}
	
	@Test
	public void noneInRuleTest() {
		test( "!*", Arrays.asList( 		
				"com.braintribe.devrock.test.tags:none#1.0",		
				"com.braintribe.devrock.test.tags:classpath#1.0"
		));
	}
	
	@Test
	public void namedExclusionRuleTest() {
		test( "!one", Arrays.asList( 		
				"com.braintribe.devrock.test.tags:none#1.0",
				"com.braintribe.devrock.test.tags:standard#1.0",
				"com.braintribe.devrock.test.tags:classpath#1.0"
		));
	}
	
	@Test
	public void namedInclusionRuleTest() {
		test( "one", Arrays.asList( 		
				"com.braintribe.devrock.test.tags:one#1.0",
				"com.braintribe.devrock.test.tags:one-and-two#1.0"
		));
	}
	@Test
	public void namedCombinedRuleTest() {
		test( "one,!two", Arrays.asList( 		
				"com.braintribe.devrock.test.tags:one#1.0"				
		));
	}

}
