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
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import com.braintribe.artifacts.test.maven.pom.marshall.validator.BasicValidatorContext;
import com.braintribe.model.artifact.Dependency;
import com.braintribe.model.artifact.Distribution;
import com.braintribe.model.artifact.License;
import com.braintribe.model.artifact.Property;
import com.braintribe.model.artifact.Solution;

/**
 * test exclusions
 * 
 * @author pit
 *
 */
public class GarbageInLab extends AbstractPomMarshallerTest {
	
	
	private static final String LICENSE_NAME = "Eclipse Public License 1.0";
	private static final Distribution LICENSE_DISTRIBUTION_REPO = Distribution.repo;
	private static final String LICENSE_URL = "http://www.eclipse.org/legal/epl-v10.html";



	@Override
	public boolean validate(Solution solution) {
		String groupId = "junit";
		if (!validateHeader(solution, new BasicValidatorContext(groupId, "junit", "4.12"))) {
			Assert.fail( "header not as expected");
			return false;
		}

		List<Dependency> dependencies = solution.getDependencies();
		if (dependencies.size() != 1) {
			Assert.fail("expected [1] dependency, but found [" + dependencies.size() + "]");
			return false;
		}

		// 
		Dependency dep1 = retrieveDependency(dependencies, "org.hamcrest", "hamcrest-core", "1.3");
		if (dep1 == null) {
			Assert.fail("dependency com.braintribe.test:Dep1#1.0 not found");
			return false;
		}		
		BasicValidatorContext c1 = new BasicValidatorContext(dep1, "org.hamcrest", "hamcrest-core", "1.3");
		validateDependency(c1);
		
		// license
		Set<License> licenses = solution.getLicenses();
		if (licenses.size() != 1) {
			Assert.fail("expected number of licenses to be [1], but found [" + licenses.size() + "]");
		}
		boolean found = false;
		for (License license : licenses) {
			if (LICENSE_NAME.equalsIgnoreCase( license.getName())) {
				found = true;
				if (!LICENSE_URL.equalsIgnoreCase( license.getUrl())) {
					Assert.fail("expected URL to be [" + LICENSE_URL + " but found [" + license.getUrl() + "]");
					return false;
				}
				if (LICENSE_DISTRIBUTION_REPO != license.getDistribution()) {
					Assert.fail("expected distribution to be [" + LICENSE_DISTRIBUTION_REPO + " but found [" + license.getDistribution() + "]");
					return false;
				}
				break;
			}
		}
		if (found == false) {
			Assert.fail("expected license [" + LICENSE_NAME + "] not found");
			return false;
		}
		
		// properties
		Set<Property> properties = solution.getProperties();
		if (properties.size() != 4) {
			Assert.fail("expected number of properties to be [4], but found [" + properties.size() + "]");
			return false;
		}
		Map<String, String> expectedProperties = new HashMap<>();
		expectedProperties.put("jdkVersion", "1.5");
		expectedProperties.put( "project.build.sourceEncoding", "ISO-8859-1");
		expectedProperties.put("arguments", "");
		expectedProperties.put( "gpg.keyname", "67893CC4");
		
		validateProperties(solution, expectedProperties);
		return true;

	
	}
	
	

	@Test
	public void junit412() {
		read( "junit-4.12.pom");
	}
	

}
