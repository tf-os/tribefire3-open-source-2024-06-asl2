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
package com.braintribe.artifacts.test.maven.pom.marshall;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.braintribe.artifacts.test.maven.pom.marshall.validator.BasicValidatorContext;
import com.braintribe.model.artifact.Dependency;
import com.braintribe.model.artifact.Solution;

/**
 * test dependencies 
 * 
 * @author pit
 *
 */
public class RedirectionDependenciesTest extends AbstractPomMarshallerTest {
	
	
	@Override
	public boolean validate(Solution solution) {
		String groupId = "com.braintribe.test";
		if (!validateHeader(solution, new BasicValidatorContext(groupId, "A", "1.0"))) {
			Assert.fail( "header not as expected");
			return false;
		}
		
		List<Dependency> dependencies = solution.getDependencies();
		
		// 
		Dependency dep1 = retrieveDependency(dependencies, groupId, "Dep1", "1.0");
		if (dep1 == null) {
			Assert.fail("dependency com.braintribe.test:Dep1#1.0 not found");
			return false;
		}
		BasicValidatorContext context = new BasicValidatorContext( dep1, groupId, "Dep1", "1.0");
		Map<String,String> redirects = new HashMap<>();
		redirects.put( "a", "com.braintribe.redirect:A-Dep1#1.0");
		redirects.put( "b", "com.braintribe.redirect:B-Dep1#1.0");
		context.setRedirects(redirects);
		validateDependency( context);
		
		
		/*
		Dependency dep2 = retrieveDependency(dependencies, groupId, "Dep2", "1.0");
		if (dep2 == null) {
			Assert.fail("dependency com.braintribe.test:Dep2#1.0 not found");
			return false;
		}
		BasicValidatorContext context_d2 = new BasicValidatorContext(dep2, groupId, "Dep2", "1.0");
		context_d2.setScope("provided");
		validateDependency(context_d2);
		
		Dependency dep3 = retrieveDependency(dependencies, groupId, "Dep3", "1.0");
		if (dep3 == null) {
			Assert.fail("dependency com.braintribe.test:Dep3#1.0 not found");
			return false;
		}
		BasicValidatorContext context_d3 = new BasicValidatorContext(dep3, groupId, "Dep3", "1.0");
		context_d3.setType("jar");
		validateDependency(context_d3);
				
		Dependency dep4 = retrieveDependency(dependencies, groupId, "Dep4", "1.0");
		if (dep4 == null) {
			Assert.fail("dependency com.braintribe.test:Dep4#1.0 not found");
			return false;
		}
		BasicValidatorContext context_d4 = new BasicValidatorContext(dep4, groupId, "Dep4", "1.0");
		context_d4.setOptional(true);
		validateDependency(context_d4);
		*/
			
		return true;
	}

	@Test
	public void dependenciesTest() {
		read( "redirects.xml");
	}

}
