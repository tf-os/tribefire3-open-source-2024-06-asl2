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
package com.braintribe.devrock.mc.core.wired.resolving.transitive.classpath;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;

import com.braintribe.codec.marshaller.stax.StaxMarshaller;
import com.braintribe.codec.marshaller.yaml.YamlMarshaller;
import com.braintribe.common.lcd.Pair;
import com.braintribe.devrock.mc.api.classpath.ClasspathDependencyResolver;
import com.braintribe.devrock.mc.api.classpath.ClasspathResolutionContext;
import com.braintribe.devrock.mc.api.transitive.TransitiveResolutionContext;
import com.braintribe.devrock.mc.core.commons.test.HasCommonFilesystemNode;
import com.braintribe.devrock.mc.core.commons.utils.TestUtils;
import com.braintribe.devrock.mc.core.wirings.classpath.ClasspathResolverWireModule;
import com.braintribe.devrock.mc.core.wirings.classpath.contract.ClasspathResolverContract;
import com.braintribe.devrock.mc.core.wirings.maven.configuration.MavenConfigurationWireModule;
import com.braintribe.devrock.mc.core.wirings.venv.contract.VirtualEnvironmentContract;
import com.braintribe.devrock.model.repolet.content.RepoletContent;
import com.braintribe.devrock.repolet.generator.RepositoryGenerations;
import com.braintribe.devrock.repolet.launcher.Launcher;
import com.braintribe.exception.Exceptions;
import com.braintribe.gm.model.reason.Maybe;
import com.braintribe.model.artifact.analysis.AnalysisArtifact;
import com.braintribe.model.artifact.analysis.AnalysisArtifactResolution;
import com.braintribe.model.artifact.compiled.CompiledArtifact;
import com.braintribe.model.artifact.compiled.CompiledArtifactIdentification;
import com.braintribe.model.artifact.compiled.CompiledDependencyIdentification;
import com.braintribe.model.artifact.compiled.CompiledTerminal;
import com.braintribe.model.artifact.consumable.Part;
import com.braintribe.ve.impl.OverridingEnvironment;
import com.braintribe.ve.impl.StandardEnvironment;
import com.braintribe.wire.api.Wire;
import com.braintribe.wire.api.context.WireContext;

/**
 * @author pit
 *
 */
public abstract class AbstractClasspathResolvingTest implements HasCommonFilesystemNode {

	protected File repo;
	protected File input;
	protected File output;
	
	{	
		Pair<File,File> pair = filesystemRoots("wired/transitive/classpath");
		input = pair.first;
		output = pair.second;
		repo = new File( output, "repo");			
	}
	
	private File settings = new File( input, "settings.xml");
	
	protected TransitiveResolutionContext standardResolutionContext = TransitiveResolutionContext.build().done();
			
	protected abstract RepoletContent archiveInput();	
	
	private boolean dumpResolutions = true;
	private boolean useYamlMarshaller = true;
	
	private YamlMarshaller marshaller;
	{
		marshaller = new YamlMarshaller();
		marshaller.setWritePooled(true);
	}
	
	private StaxMarshaller staxMarshaller = StaxMarshaller.defaultInstance;
	
	
	
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

	@Before
	public void runBefore() {
		TestUtils.ensure(repo); 			
		launcher.launch();
	}
	
	@After
	public void runAfter() {
		launcher.shutdown();
	}
	
	
	protected OverridingEnvironment buildVirtualEnvironement(Map<String,String> overrides) {
		OverridingEnvironment ove = new OverridingEnvironment(StandardEnvironment.INSTANCE);
		if (overrides != null && !overrides.isEmpty()) {
			ove.setEnvs(overrides);						
		}
		ove.setEnv("M2_REPO", repo.getAbsolutePath());
		ove.setEnv("ARTIFACT_REPOSITORIES_EXCLUSIVE_SETTINGS", settings.getAbsolutePath());
		ove.setEnv( "port", Integer.toString( launcher.getAssignedPort()));
				
		return ove;		
	}
	protected AnalysisArtifactResolution run(String terminal, ClasspathResolutionContext resolutionContext, String suffix) {
		return run(terminal, resolutionContext, false, suffix);
	}
	
	protected AnalysisArtifactResolution run(String terminal, ClasspathResolutionContext resolutionContext, boolean expectException, String suffix) {
		boolean thrown = false;
		try (				
				WireContext<ClasspathResolverContract> resolverContext = Wire.contextBuilder( ClasspathResolverWireModule.INSTANCE, MavenConfigurationWireModule.INSTANCE)
					.bindContract(VirtualEnvironmentContract.class, () -> buildVirtualEnvironement(null))				
					.build();
			) {
			
			ClasspathDependencyResolver classpathResolver = resolverContext.contract().classpathResolver();
			
			CompiledTerminal cdi = CompiledTerminal.from ( CompiledDependencyIdentification.parse( terminal));			
			AnalysisArtifactResolution artifactResolution = classpathResolver.resolve( resolutionContext, cdi);
			
			String name = terminal.replace( ":", ".") + "-" + suffix;		
			if (useYamlMarshaller) {
				name += ".yaml";
			}
			else {
				name += ".xml";
			}
			File out = new File( output, name);
			dump( out, artifactResolution);
			return artifactResolution;					
								
		}
		
		catch( Exception e) {
			e.printStackTrace();
			thrown = true;
			if (!expectException) {
				Assert.fail("exception thrown [" + e.getLocalizedMessage() + "]");
			}
		}
		if (expectException && !thrown) {
			Assert.fail("expected exception not thrown ");
		}
		return null;
	}
	protected AnalysisArtifactResolution runAsArtifact(String terminal, ClasspathResolutionContext resolutionContext, String suffix) {
		return runAsArtifact(terminal, resolutionContext, false, suffix);
	}
	protected AnalysisArtifactResolution runAsArtifact(String terminal, ClasspathResolutionContext resolutionContext, boolean expectException, String suffix) {
		boolean thrown = false;
		try (				
				WireContext<ClasspathResolverContract> resolverContext = Wire.contextBuilder( ClasspathResolverWireModule.INSTANCE, MavenConfigurationWireModule.INSTANCE)
					.bindContract(VirtualEnvironmentContract.class, () -> buildVirtualEnvironement(null))				
					.build();
			) {
			
			ClasspathDependencyResolver classpathResolver = resolverContext.contract().classpathResolver();
			CompiledArtifactIdentification cai = CompiledArtifactIdentification.parse(terminal);
			Maybe<CompiledArtifact> compiledArtifactOptional = resolverContext.contract().transitiveResolverContract().dataResolverContract().directCompiledArtifactResolver().resolve( cai);
						
			CompiledTerminal cdi = compiledArtifactOptional.get();			
			AnalysisArtifactResolution artifactResolution = classpathResolver.resolve( resolutionContext, cdi);
			String name = terminal.replace( ":", ".") + "-" + suffix;		
			if (useYamlMarshaller) {
				name += ".yaml";
			}
			else {
				name += ".xml";
			}
			File out = new File( output, name);
			dump( out, artifactResolution);
			return artifactResolution;					
								
		}
		catch( Exception e) {
			e.printStackTrace();
			thrown = true;
			if (!expectException) {
				Assert.fail("exception thrown [" + e.getLocalizedMessage() + "]");
			}
		}
		if (expectException && !thrown) {
			Assert.fail("expected exception not thrown ");
		}
		return null;
	}
	
	
	protected Stream<Part> getCpJarParts(AnalysisArtifact artifact) {
		return artifact.getParts().entrySet().stream().filter(e -> e.getKey().endsWith(":jar")).map(Map.Entry::getValue);
	}

	protected RepoletContent loadInput(File file) {		
		try {
			if (file.getName().endsWith(".yaml")) {
				return RepositoryGenerations.unmarshallConfigurationFile(file);
			}
			else {
				return RepositoryGenerations.parseConfigurationFile( file);
			}
		} catch (Exception e) {
			throw Exceptions.unchecked(e, "cannot parse file [" + file.getAbsolutePath() + "]", IllegalStateException::new);
		} 
	}	
	
	protected void dump(File file, AnalysisArtifactResolution resolution) {
		if (!dumpResolutions)
			return;
		try (OutputStream out = new FileOutputStream(file)) {
			if (useYamlMarshaller) {
				marshaller.marshall(out, resolution);
			}
			else {
				staxMarshaller.marshall(out, resolution);
			}			
		}
		catch (Exception e) {
			throw Exceptions.unchecked(e, "can't dump resolution to [" + file.getAbsolutePath() + "]", IllegalStateException::new);
		}
		
	}
	
}
