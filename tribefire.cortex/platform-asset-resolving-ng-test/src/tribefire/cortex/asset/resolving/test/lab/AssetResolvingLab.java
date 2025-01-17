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
package tribefire.cortex.asset.resolving.test.lab;

import java.io.File;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.braintribe.common.lcd.Pair;
import com.braintribe.devrock.mc.core.wirings.maven.configuration.MavenConfigurationWireModule;
import com.braintribe.devrock.mc.core.wirings.venv.contract.VirtualEnvironmentContract;
import com.braintribe.model.artifact.compiled.CompiledDependencyIdentification;
import com.braintribe.testing.category.KnownIssue;
import com.braintribe.ve.impl.OverridingEnvironment;
import com.braintribe.ve.impl.StandardEnvironment;
import com.braintribe.wire.api.Wire;
import com.braintribe.wire.api.context.WireContext;

import tribefire.cortex.asset.resolving.ng.api.AssetDependencyResolver;
import tribefire.cortex.asset.resolving.ng.api.AssetResolutionContext;
import tribefire.cortex.asset.resolving.ng.api.PlatformAssetResolution;
import tribefire.cortex.asset.resolving.ng.impl.PlatformAssetSolution;
import tribefire.cortex.asset.resolving.ng.wire.AssetResolverWireModule;
import tribefire.cortex.asset.resolving.ng.wire.contract.AssetResolverContract;

/**
 * simple little framework for platform-asset-resolutions with overloaded maven setup.
 * @author pit
 *
 */
@Category(KnownIssue.class)
public class AssetResolvingLab implements HasCommonFilesystemNode {
	private WireContext<AssetResolverContract> context;
	
	protected File repo;
	protected File input;
	protected File output;	
	{	
		Pair<File,File> pair = filesystemRoots("par-test");
		input = pair.first;
		output = pair.second;
		repo = new File( output, "repo");			
	}
	
	private File settings = new File( input, "settings.xml");
	
	
	protected OverridingEnvironment buildVirtualEnvironement(Map<String,String> overrides) {
		OverridingEnvironment ove = new OverridingEnvironment(StandardEnvironment.INSTANCE);
		if (overrides != null && !overrides.isEmpty()) {
			ove.setEnvs(overrides);						
		}
		ove.setEnv("M2_REPO", repo.getAbsolutePath());
		// ove.setEnv("ARTIFACT_REPOSITORIES_EXCLUSIVE_SETTINGS", settings.getAbsolutePath());
		// ove.setEnv( "port", Integer.toString( launcher.getAssignedPort()));
				
		return ove;		
	}


	@Before
	public void initialize() {
		// make sure output exists and is cleared before run - unless you want the test to reuse the local repo in output/repo
		TestUtils.ensure( output);
		
		context = Wire.contextBuilder(AssetResolverWireModule.INSTANCE, MavenConfigurationWireModule.INSTANCE)
				 	.bindContract(VirtualEnvironmentContract.class, () -> buildVirtualEnvironement(null))               
	                .build();
	}
	
	@After
	public void dispose() {
		if (context != null)
			context.shutdown();
	}
	
	@Test
	public void labTest() throws Exception {
		AssetDependencyResolver assetResolver = context.contract().assetDependencyResolver();
		
		//String name = "tribefire.extension.demo:demo-setup#2.0";
		String name = "tribefire.extension.test:asset-test-model#1.0.1";
		CompiledDependencyIdentification setupDependency = CompiledDependencyIdentification.parseAndRangify(name, true);

		AssetResolutionContext resolutionContext = AssetResolutionContext.build().selectorFiltering(true).done();
		
		PlatformAssetResolution assetResolution = assetResolver.resolve(resolutionContext, setupDependency);
		
		Assert.assertTrue( "expected resolution not to fail, but it did", !assetResolution.hasFailed());
		
		if (assetResolution.hasFailed()) {
			System.out.println( assetResolution.getFailure().stringify());
		}

		for (PlatformAssetSolution solution: assetResolution.getSolutions()) {
			System.out.println(solution.solution.asString() + " -> " + solution.nature.entityType());
		}
		
		
		//Validator.validate(yaml, assetResolution.getSolutions());
	}
	
}
