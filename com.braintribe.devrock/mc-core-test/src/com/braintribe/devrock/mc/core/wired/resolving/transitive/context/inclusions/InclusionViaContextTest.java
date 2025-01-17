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
package com.braintribe.devrock.mc.core.wired.resolving.transitive.context.inclusions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.braintribe.devrock.mc.api.transitive.TransitiveResolutionContext;
import com.braintribe.devrock.mc.core.wired.resolving.Validator;
import com.braintribe.model.artifact.analysis.AnalysisArtifact;
import com.braintribe.model.artifact.analysis.AnalysisArtifactResolution;

/**
 * tests inclusions of standard dependnecies, parents and import.. see separate test for relocations
 * @author pit
 *
 */
public class InclusionViaContextTest extends AbstractResolvingContextTest {
	
	private List<String> expectedTerminalSolution;
	{
		expectedTerminalSolution = Collections.singletonList( "com.braintribe.devrock.test:t#1.0.1");
	}
	
	private List<String> expectedStandardDependencyContents;
	{
		expectedStandardDependencyContents = new ArrayList<>();
			
		expectedStandardDependencyContents.add( "com.braintribe.devrock.test:a#1.0.1");
		expectedStandardDependencyContents.add( "com.braintribe.devrock.test:b#1.0.1");
		expectedStandardDependencyContents.add( "com.braintribe.devrock.test:a-1#1.0.1");
		expectedStandardDependencyContents.add( "com.braintribe.devrock.test:b-1#1.0.1");
	}
	
	private List<String> expectedParents;
	{
		expectedParents = new ArrayList<>();
		
		expectedParents.add( "com.braintribe.devrock.test:parent#1.0.1");
		expectedParents.add( "com.braintribe.devrock.test:parent-1#1.0.1");
	}

	private List<String> expectedImports;
	{
		expectedImports = new ArrayList<>();
		
		expectedImports.add( "com.braintribe.devrock.test:import#1.0.1");
		expectedImports.add( "com.braintribe.devrock.test:import-1#1.0.1");
	}
	
	/**
	 * tests all switched off -> only terminal dependency is included... should it?
	 */
	@Test
	public void emptyTest() {
		TransitiveResolutionContext trc = TransitiveResolutionContext.build().includeStandardDependencies(false).done();
		
		try {
			AnalysisArtifactResolution resolution = run(terminal, trc);
			List<AnalysisArtifact> solutions = resolution.getSolutions();

			Validator validator = new Validator();			
			// only contains standard			
			validator.validate(solutions, expectedTerminalSolution);			
			
			validator.assertResults();

			
		} catch (Exception e) {
			Assert.fail("unexpected exception [" + e.getMessage() + "] thrown");
		}
	}

	

	/**
	 * tests default : standard dependencies and terminal dependency 
	 */
	@Test
	public void defaultTest() {
		TransitiveResolutionContext trc = TransitiveResolutionContext.build().done();
		
		try {
			AnalysisArtifactResolution resolution = run(terminal, trc);
			List<AnalysisArtifact> solutions = resolution.getSolutions();

			List<String> expectations = new ArrayList<>( expectedStandardDependencyContents.size() + expectedParents.size());
			expectations.addAll( expectedTerminalSolution);
			expectations.addAll(expectedStandardDependencyContents);

			
			Validator validator = new Validator();			
			// only contains standard			
			validator.validate(solutions, expectations);			
			
			validator.assertResults();

		} catch (Exception e) {
			Assert.fail("unexpected exception [" + e.getMessage() + "] thrown");
		}
	}

	
	
	/**
	 * tests parents : standard dependencies, terminal dependency, parents
	 */
	@Test
	public void parentInclusionTest() {
		TransitiveResolutionContext trc = TransitiveResolutionContext.build().includeParentDependencies(true).done();
		
		try {
			AnalysisArtifactResolution resolution = run(terminal, trc);
			List<AnalysisArtifact> solutions = resolution.getSolutions();
			
			List<String> expectations = new ArrayList<>( expectedStandardDependencyContents.size() + expectedParents.size() + expectedTerminalSolution.size());
			expectations.addAll(expectedStandardDependencyContents);
			expectations.addAll( expectedParents);
			expectations.addAll(expectedTerminalSolution);

			Validator validator = new Validator();			
			// only contains standard			
			validator.validate(solutions, expectations);			
			
			validator.assertResults();
			
		} catch (Exception e) {
			Assert.fail("unexpected exception [" + e.getMessage() + "] thrown");
		}
	}


	/**
	 * tests imports : standard dependencies, terminal dependency, import dependencies
	 */
	@Test
	public void importInclusionTest() {
		TransitiveResolutionContext trc = TransitiveResolutionContext.build().includeImportDependencies(true).done();
		
		try {
			AnalysisArtifactResolution resolution = run(terminal, trc);
			List<AnalysisArtifact> solutions = resolution.getSolutions();
			
			List<String> expectations = new ArrayList<>( expectedStandardDependencyContents.size() + expectedTerminalSolution.size());
			expectations.addAll(expectedStandardDependencyContents);
			expectations.addAll(expectedTerminalSolution);

			Validator validator = new Validator();			
			// only contains standard			
			validator.validate(solutions, expectations);			
			
			validator.assertResults();
			
		} catch (Exception e) {
			Assert.fail("unexpected exception [" + e.getMessage() + "] thrown");
		}
	}

		
}
