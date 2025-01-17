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
package com.braintribe.devrock.mc.core.wired.resolving.transitive.buildrange;

import java.io.File;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;

import com.braintribe.common.lcd.Pair;
import com.braintribe.devrock.mc.api.classpath.ClasspathResolutionContext;
import com.braintribe.devrock.mc.api.transitive.TransitiveDependencyResolver;
import com.braintribe.devrock.mc.api.transitive.TransitiveResolutionContext;
import com.braintribe.devrock.mc.core.commons.test.HasCommonFilesystemNode;
import com.braintribe.devrock.mc.core.commons.utils.TestUtils;
import com.braintribe.devrock.mc.core.wired.resolving.Validator;
import com.braintribe.devrock.mc.core.wirings.maven.configuration.MavenConfigurationWireModule;
import com.braintribe.devrock.mc.core.wirings.transitive.TransitiveResolverWireModule;
import com.braintribe.devrock.mc.core.wirings.transitive.contract.TransitiveResolverContract;
import com.braintribe.devrock.mc.core.wirings.venv.contract.VirtualEnvironmentContract;
import com.braintribe.devrock.model.repolet.content.RepoletContent;
import com.braintribe.devrock.repolet.generator.RepositoryGenerations;
import com.braintribe.devrock.repolet.launcher.Launcher;
import com.braintribe.exception.Exceptions;
import com.braintribe.model.artifact.analysis.AnalysisArtifactResolution;
import com.braintribe.model.artifact.compiled.CompiledDependencyIdentification;
import com.braintribe.model.artifact.compiled.CompiledTerminal;
import com.braintribe.ve.impl.OverridingEnvironment;
import com.braintribe.ve.impl.StandardEnvironment;
import com.braintribe.wire.api.Wire;
import com.braintribe.wire.api.context.WireContext;

/**
 * abstract base class for all filter tests
 * @author pit
 *
 */
public abstract class AbstractBuildRangeResolvingContextTest implements HasCommonFilesystemNode {


	protected File repo;
	protected File input;
	protected File output;
	
	{	
		Pair<File,File> pair = filesystemRoots("wired/transitive/buildrange");
		input = pair.first;
		output = pair.second;
		repo = new File( output, "repo");			
	}
	protected File settings = new File(input, "settings.xml");
	protected File initial = new File( input, "local-repo");
	
	private Launcher launcher; 
	{
		launcher = Launcher.build()
				.repolet()
				.name("archive")
					.descriptiveContent()
						.descriptiveContent(archiveInput())
					.close()
				.close()
			.done();
	}
	
	protected void additionalTasks() {}
	
	@Before
	public void runBefore() {
		
		TestUtils.ensure(repo); 			
		launcher.launch();		
		additionalTasks();
	}
	
	@After
	public void runAfter() {
		launcher.shutdown();
	}
	
	protected abstract RepoletContent archiveInput();		
	
	protected RepoletContent archiveInput(String definition) {
		File file = new File( input, definition);
		try {
			return RepositoryGenerations.unmarshallConfigurationFile(file);
		} catch (Exception e) {
			throw Exceptions.unchecked(e, "cannot load parser file [" + file.getAbsolutePath() + "]" , IllegalStateException::new);
		} 
	}
	
	protected OverridingEnvironment buildVirtualEnvironement(Map<String,String> overrides) {
		OverridingEnvironment ove = new OverridingEnvironment(StandardEnvironment.INSTANCE);
		if (overrides != null && !overrides.isEmpty()) {
			ove.setEnvs(overrides);						
		}
		ove.setEnv("repo", repo.getAbsolutePath());
		ove.setEnv("ARTIFACT_REPOSITORIES_EXCLUSIVE_SETTINGS", settings.getAbsolutePath());
		ove.setEnv( "port", Integer.toString( launcher.getAssignedPort()));
				
		return ove;		
	}

	
	/**
	 * run a standard transitive resolving 
	 * @param terminal - the String of the terminal
	 * @param resolutionContext - the {@link ClasspathResolutionContext}
	 * @return - the resulting {@link AnalysisArtifactResolution}
	 */
	protected AnalysisArtifactResolution run(String terminal, TransitiveResolutionContext resolutionContext) throws Exception {
		try (				
				WireContext<TransitiveResolverContract> resolverContext = Wire.contextBuilder( TransitiveResolverWireModule.INSTANCE, MavenConfigurationWireModule.INSTANCE)
					.bindContract(VirtualEnvironmentContract.class, () -> buildVirtualEnvironement(null))				
					.build();
			) {
			
			TransitiveDependencyResolver transitiveResolver = resolverContext.contract().transitiveDependencyResolver();
			
			CompiledTerminal cdi = CompiledTerminal.from ( CompiledDependencyIdentification.parse( terminal));
			AnalysisArtifactResolution artifactResolution = transitiveResolver.resolve( resolutionContext, cdi);
			return artifactResolution;													
		}		
	}
	
	protected AnalysisArtifactResolution runAndShow( String terminal, TransitiveResolutionContext trc){
		AnalysisArtifactResolution resolution;
		try {
			resolution = run( terminal, trc);
		} catch (Exception e) {
			throw Exceptions.unchecked(e, "Error on [" + terminal + "]", IllegalStateException::new);
		}
		
		if (!resolution.getSolutions().isEmpty()) {
			resolution.getSolutions().stream().forEach( s -> System.out.println( s.asString() + ","));
		}
		else {
			System.out.println("no results");
		}
		
		return resolution;
	}
	
	/**
	 * @param trc - the {@link TransitiveResolutionContext}
	 * @param validationFile - the name of the validation file
	 * @return - the {@link AnalysisArtifactResolution}
	 */
	protected AnalysisArtifactResolution runAndValidate( String terminal, TransitiveResolutionContext trc, String validationFile) {
		return runAndValidate(terminal, trc, validationFile, false);
	}
	
	/**
	 * runs and validates a run on the TDR
	 * @param trc - the {@link TransitiveResolutionContext}
	 * @param validationFile - the name of the validation file 
	 * @param validateParts - true if parts should be validated
	 */
	protected AnalysisArtifactResolution runAndValidate( String terminal, TransitiveResolutionContext trc, String validationFile, boolean validateParts) {
		try {
			AnalysisArtifactResolution resolution = run( terminal, trc);
			
			if (!resolution.getSolutions().isEmpty()) {
				resolution.getSolutions().stream().forEach( s -> System.out.println( s.asString() + ","));
			}
			else {
				System.out.println("no results");
			}
			
			Validator validator = new Validator();			
			validator.validateYaml( new File( input, validationFile), resolution, validateParts, false);			
			validator.assertResults();
			return resolution;
			
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("exception thrown " + e.getLocalizedMessage());
		}			
		return null;
	}
		
	
}
