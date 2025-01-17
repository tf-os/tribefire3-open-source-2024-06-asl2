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
package com.braintribe.qa.tribefire.qatests.deployables;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.braintribe.model.accessdeployment.smood.CollaborativeSmoodAccess;
import com.braintribe.model.extensiondeployment.AccessAspect;
import com.braintribe.product.rat.imp.ImpApi;
import com.braintribe.qa.tribefire.qatests.deployables.access.AbstractPersistenceTest;
import com.braintribe.utils.CommonTools;

/**
 * tests the SetupAspects service on a temporary SMOOD access
 */
public class SetupAspectsTest extends AbstractPersistenceTest {

	// @formatter:off
	private static List<String> defaultAspectsExternalIds = CommonTools.getList(
			"aspect.fulltext.default",
			"aspect.idGenerator.default",
			"aspect.security.default",
			"aspect.stateProcessing.default");

	private static List<String> addedAspectMessages = defaultAspectsExternalIds.stream()
			.map(aspect -> "Added missing default aspect: " + aspect)
			.collect(Collectors.toList());
	// @formatter:on

	@Before
	@After
	public void tearDown() {
		eraseTestEntities();
	}

	@Test
	public void test() {
		logger.info("Starting DevQA-test: Testing SetupAspects service...");

		ImpApi imp = apiFactory().build();

		CollaborativeSmoodAccess testAccess = createAndDeployFamilyAccess(imp);

		if (testAccess.getAspectConfiguration() != null) {
			List<AccessAspect> aspects = testAccess.getAspectConfiguration().getAspects();
			assertThat(aspects).as("Unexpected aspects already defined in freshly created access").isEmpty();
		}

		String ensuredDefaultMessage = "Ensured default aspects for access: " + testAccess.getExternalId();
		String ensureAspectsMessage = "Ensure aspects on existing AspectConfiguration of access: " + testAccess.getExternalId();
		List<String> expectedMessages1 = new ArrayList<String>();
		expectedMessages1.add(ensuredDefaultMessage);
		expectedMessages1.addAll(addedAspectMessages);
		expectedMessages1.add("Create new AspectConfiguration for access: " + testAccess.getExternalId());

		logger.info("Executing service request with 'resetToDefault'=false...");
		List<String> response = imp.service().setupAspectsRequest(testAccess, false).callAndGetMessages();

		assertExpectedResponseAndResult(response, testAccess, expectedMessages1);

		logger.info("Executing same service request again...");
		response = imp.service().setupAspectsRequest(testAccess, false).callAndGetMessages();

		// @formatter:off
		assertExpectedResponseAndResult(response, testAccess, CommonTools.toList(
				ensuredDefaultMessage,
				"All default aspects were found on AspectConfiguration of access: " + testAccess.getExternalId() + ". Nothing added.",
				ensureAspectsMessage));
		// @formatter:on

		logger.info("Executing service request with 'resetToDefault'=true...");

		response = imp.service().setupAspectsRequest(testAccess, true).callAndGetMessages();

		List<String> expectedMessages2 = new ArrayList<String>();
		expectedMessages2.add(ensuredDefaultMessage);
		expectedMessages2.addAll(addedAspectMessages);
		expectedMessages2.add(ensureAspectsMessage);
		expectedMessages2.add("Cleaning aspects on existing AspectConfiguration of access: " + testAccess.getExternalId() + " (reset=true)");

		assertExpectedResponseAndResult(response, testAccess, expectedMessages2);
		logger.info("Test succeeded.");
	}

	/**
	 * + asserts that the response carries exactly the given expectedMessages in its notifications <br>
	 * + asserts that that the expected aspects are in the access's aspect configuration
	 */
	private void assertExpectedResponseAndResult(List<String> messages, CollaborativeSmoodAccess access, List<String> expectedMessages) {
		assertThat(messages).containsAll(expectedMessages).containsOnlyElementsOf(expectedMessages);

		assertThat(access.getAspectConfiguration()).as("No aspect configuration found for access " + access.getExternalId()).isNotNull();

		List<AccessAspect> aspects = access.getAspectConfiguration().getAspects();

		assertThat(aspects).extracting("externalId").containsExactlyInAnyOrder(defaultAspectsExternalIds.toArray());
	}

}
