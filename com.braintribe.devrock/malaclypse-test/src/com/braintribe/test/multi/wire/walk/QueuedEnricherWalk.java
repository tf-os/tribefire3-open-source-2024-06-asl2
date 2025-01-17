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
package com.braintribe.test.multi.wire.walk;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.braintribe.build.artifact.name.NameParser;
import com.braintribe.build.artifact.walk.multi.Walker;
import com.braintribe.build.artifacts.mc.wire.classwalk.context.WalkerContext;
import com.braintribe.build.artifacts.mc.wire.classwalk.contract.ClasspathResolverContract;
import com.braintribe.model.artifact.PartTuple;
import com.braintribe.model.artifact.Solution;
import com.braintribe.model.artifact.processing.part.PartTupleProcessor;
import com.braintribe.model.artifact.processing.version.VersionProcessor;
import com.braintribe.model.malaclypse.cfg.denotations.clash.ResolvingInstant;
import com.braintribe.test.framework.TestUtil;
import com.braintribe.test.multi.wire.AbstractWalkerWireTest;
import com.braintribe.testing.category.KnownIssue;
import com.braintribe.wire.api.context.WireContext;
@Category(KnownIssue.class)
public class QueuedEnricherWalk extends AbstractWalkerWireTest {
	private File repo = new File( "res/wire/issues/enricher");
	private String [] tuples = new String[] {"jar", "sources:jar", "javadoc.jar", "asset:man"}; 

	
	@Before 
	public void before() {
		TestUtil.ensure( repo);		
	}
	/**
	 * 
	 */
	@Test
	public void testStandard() {
	
		try {
			WireContext<ClasspathResolverContract> classpathWalkContext = getClasspathWalkContext( null, repo, ResolvingInstant.adhoc);
			
			WalkerContext walkerContext = new WalkerContext();
			
			List<PartTuple> partTuples = new ArrayList<>();
			for (String t : tuples) {
				PartTuple tuple = PartTupleProcessor.fromString(t);
				partTuples.add(tuple);
			}
			
			walkerContext.setRelevantPartTuples( partTuples);
			
			Walker walker = classpathWalkContext.contract().enrichingWalker(walkerContext);
			
			String walkScopeId = UUID.randomUUID().toString();
			
			Solution terminal = Solution.T.create();
			terminal.setGroupId( "tribefire.cortex");
			terminal.setArtifactId( "platform-asset-resolving");
			terminal.setVersion( VersionProcessor.createFromString("2.0.13"));
			
			long before = System.nanoTime();
			Collection<Solution> collection = walker.walk( walkScopeId, terminal);
			long after = System.nanoTime();
			
			System.out.println("found [" + collection.size() + "] dependencies");
			collection.stream().forEach( s -> System.out.println( NameParser.buildName(s)));
			double dif = (after - before) / 1E6;
			
			System.out.println("elapsed time: [" + dif + "]");
			
		} catch (Exception e) {

			e.printStackTrace();
		} 
	}
}
