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
package com.braintribe.devrock.ant.test.dependencies;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;

import com.braintribe.devrock.ant.test.TaskRunner;
import com.braintribe.devrock.ant.test.Validator;
import com.braintribe.devrock.ant.test.common.TestUtils;
import com.braintribe.devrock.model.repolet.content.RepoletContent;
import com.braintribe.wire.api.util.Lists;

/**
 * tests the RepositoryExtract task
 *
 * NOTE : this test is made for bt-ant-tasks-ng while it was built using bt-ant-tasks. Both ant-plugins can run the test, but 
 * the results are differing.
 * - legacy : no files are pulled from the repository (other than the pom)
 * - ng : all files (expressable as parts of course) are pulled - the :data is in the example.  
 *   
 * @author pit
 *
 */
public class RepositoryExtractTest extends TaskRunner {

	private List<File> extraction;
	
	@Override
	protected String filesystemRoot() {
		return "repository.extract";
	}

	@Override
	protected RepoletContent archiveContent() {

		return archiveInput( "simplest.tree.definition.yaml");
	}

	@Override
	protected void preProcess() {		 
		// copy build file 
		TestUtils.copy( new File(input, "build.xml"), new File(output, "build.xml"));
	
		TestUtils.copy( new File(input, "pom.xml"), new File(output, "pom.xml"));
		
		TestUtils.copy( new File(input, "exclusions.txt"), new File(output, "exclusions.txt"));
		
		// copy initial files
		TestUtils.copy( new File( input, "initial"), repo);
	}

	@Override
	protected void postProcess() {
		extraction = loadFilesFromFilesetDump( new File(output, "extracted.txt"));
	}

	@Test
	public void runUnfilteredExtractTask() {
		process( new File( output, "build.xml"), "extract");
				
		// assert
		Validator validator = new Validator();
		 
		List<String> found = extraction.stream().map( f -> f.getName()).filter( n -> !(n.startsWith("maven-metadata") || n.equals(".updateinfo.main") ||  n.endsWith( ".solution"))).collect( Collectors.toList());
		
		List<String> expected = Lists.list(
												"a-1.0.1.jar","a-1.0.1.pom",
												"b-1.0.1.jar","b-1.0.1.pom",
												"t-1.0.1.jar","t-1.0.1.pom",
												"x-1.0.1.jar","x-1.0.1.pom",
												"y-1.0.1.jar","y-1.0.1.pom",
												"z-1.0.1.jar","z-1.0.1.pom",
												
												// for bt-ant-tasks-ng
												"a-1.0.1.data","b-1.0.1.data"
											  ); 

		validator.validate("extract", found, expected);
		validator.assertResults();		
	}
	
	@Test
	public void runFilteredExtractTask() {
		process( new File( output, "build.xml"), "extractWithExclusions");
				
		// assert
		Validator validator = new Validator();
		 
		List<String> found = extraction.stream().map( f -> f.getName()).filter( n -> !(n.startsWith("maven-metadata") || n.equals(".updateinfo.main") ||  n.endsWith( ".solution"))).collect( Collectors.toList());
		
		List<String> expected = Lists.list(
												"a-1.0.1.jar","a-1.0.1.pom",
												"b-1.0.1.jar","b-1.0.1.pom",
												"t-1.0.1.jar","t-1.0.1.pom",
												//"x-1.0.1.jar","x-1.0.1.pom",  excluded per filter 
												//"y-1.0.1.jar","y-1.0.1.pom",  excluded as child of x
												//"z-1.0.1.jar","z-1.0.1.pom",  excluded as child of y
												
												// for bt-ant-tasks-ng
												"a-1.0.1.data","b-1.0.1.data"
											  ); 

		validator.validate("extract", found, expected);
		validator.assertResults();		
	}
}
