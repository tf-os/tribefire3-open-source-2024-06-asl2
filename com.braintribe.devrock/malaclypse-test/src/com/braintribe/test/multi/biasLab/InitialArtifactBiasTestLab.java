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
package com.braintribe.test.multi.biasLab;

import java.io.File;
import java.util.Collection;
import java.util.List;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.braintribe.build.artifact.retrieval.multi.repository.reflection.impl.bias.ArtifactBias;
import com.braintribe.model.artifact.Identification;
import com.braintribe.model.artifact.Solution;
import com.braintribe.test.framework.TestUtil;

public class InitialArtifactBiasTestLab extends AbstractBiasLab {


	protected static File settings = new File( "res/biasLab/contents/settings.xml");
	protected static File bias = new File( "res/biasLab/contents/base_bias.txt");
	
	@BeforeClass
	public static void before() {
		before( settings);
		TestUtil.copy(bias, localRepository, ".pc_bias");
	}


	@Override
	protected void testPresence(Collection<Solution> solutions, File repository) {
		super.testPresence(solutions, repository);
	}	
	
	@Test
	public void testActiveBias() {
		List<ArtifactBias> biasInformation = repositoryRegistry.getBiasInformation();
	}
	
	@Test
	public void testArtifactBias() {
		String line = "com.braintribe.gm";
		ArtifactBias bias = new ArtifactBias(line);
		
		line = "com.braintribe.devrock:malaclypse";
		bias = new ArtifactBias(line);
	
		Identification identification = bias.getIdentification();
		Assert.assertTrue("group id doesn't match", identification.getGroupId().equalsIgnoreCase("com.braintribe.devrock"));
		Assert.assertTrue("artifact id doesn't match", identification.getArtifactId().equalsIgnoreCase("malaclypse"));
		
		String stringified = bias.toString();
		Assert.assertTrue("expected [" + line + "], retrieved [" + stringified + "]", line.equalsIgnoreCase( stringified));
		
		Assert.assertTrue("bias isn't local", bias.hasLocalBias());
		Assert.assertTrue("bias is positive", !bias.hasPositiveBias());
		Assert.assertTrue("bias is negative", !bias.hasNegativeBias());
		
		line = "com.braintribe.gm.devrock:malaclypse;!third-party,!devrock";
		bias = new ArtifactBias(line);
		identification = bias.getIdentification();
		Assert.assertTrue("bias is local", !bias.hasLocalBias());
		Assert.assertTrue("bias is positive", !bias.hasPositiveBias());
		Assert.assertTrue("bias isn't negative", bias.hasNegativeBias());
		stringified = bias.toString();
		Assert.assertTrue("expected [" + line + "], retrieved [" + stringified + "]", line.equalsIgnoreCase( stringified));
		
		Assert.assertTrue( "third-party isn't blocked", bias.hasNegativeBias("third-party"));
		Assert.assertTrue( "devrock isn't blocked", bias.hasNegativeBias("devrock"));
		Assert.assertTrue( "core-dev is blocked", !bias.hasNegativeBias("core-dev"));
		
		line = "com.braintribe.gm.devrock:malaclypse;core-dev";
		bias = new ArtifactBias(line);
		identification = bias.getIdentification();
		Assert.assertTrue("bias is local", !bias.hasLocalBias());
		Assert.assertTrue("bias isn't positive", bias.hasPositiveBias());
		Assert.assertTrue("bias is negative", !bias.hasNegativeBias());
		
		Assert.assertTrue( "third-party isn't blocked", !bias.hasPositiveBias("third-party"));
		Assert.assertTrue( "devrock isn't blocked", !bias.hasPositiveBias("devrock"));
		Assert.assertTrue( "core-dev is blocked", bias.hasPositiveBias("core-dev"));
		stringified = bias.toString();
		Assert.assertTrue("expected [" + line + "], retrieved [" + stringified + "]", line.equalsIgnoreCase( stringified));
		
		line = "com.braintribe.gm.devrock:malaclypse;core-dev,!third-party,!devrock";
		bias = new ArtifactBias(line);
		identification = bias.getIdentification();
		Assert.assertTrue("bias is local", !bias.hasLocalBias());
		Assert.assertTrue("bias is not positive", bias.hasPositiveBias());
		Assert.assertTrue("bias is not negative", bias.hasNegativeBias());
		
		Assert.assertTrue( "third-party isn't blocked", !bias.hasPositiveBias("third-party"));
		Assert.assertTrue( "devrock isn't blocked", !bias.hasPositiveBias("devrock"));
		Assert.assertTrue( "core-dev is blocked", bias.hasPositiveBias("core-dev"));
		stringified = bias.toString();
		Assert.assertTrue("expected [" + line + "], retrieved [" + stringified + "]", line.equalsIgnoreCase( stringified));
		
		
	}

}
