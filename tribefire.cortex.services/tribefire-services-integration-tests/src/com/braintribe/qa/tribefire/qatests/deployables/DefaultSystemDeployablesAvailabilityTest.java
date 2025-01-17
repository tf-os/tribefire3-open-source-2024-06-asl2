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

import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;

import com.braintribe.model.deployment.Deployable;
import com.braintribe.model.deployment.DeploymentStatus;
import com.braintribe.product.rat.imp.ImpApi;
import com.braintribe.testing.internal.tribefire.tests.AbstractTribefireQaTest;
import com.braintribe.utils.CollectionTools;

public class DefaultSystemDeployablesAvailabilityTest extends AbstractTribefireQaTest {

	@Test
	public void test() {

		ImpApi imp = apiFactory().build();

		logger.info("Checking if expected System deployables were found...");

		// @formatter:off
		String[] expectedDeployableIds = new String[] {
				"auth",
				"auth.wb",
				"cortex",
				"cortex.wb",
				"setup.wb",
				"setup",
				"user-sessions",
				"user-statistics",
				"workbench",
				"aspect.fulltext.default",
				"aspect.idGenerator.default"   ,
				"aspect.stateProcessing.default",
				"aspect.security.default",
				"processorRule.bidiProperty.default",
				"processorRule.metaData.default",
				"binaryPersistence.default",
				"binaryProcessor.fileSystem",
				"binaryRetrieval.template",
				"checkProcessor.hardwired.DatabaseConnectionsCheck",
				"checkProcessor.hardwired.MemoryCheckProcessor",
				"checkProcessor.hardwired.SelectedDatabaseConnectionsCheck",
				"checkProcessor.hardwired.BaseFunctionalityCheckProcessor",
				"checkProcessor.hardwired.BaseConnectivityCheckProcessor",
				"checkProcessor.hardwired.BaseVitalityCheckProcessor",
				"resourceEnricher.postPersistence.default",
				"resourceEnricher.prePersistence.default",
		};
		// @formatter:on

		List<Deployable> foundDeployables = imp.deployable().with(expectedDeployableIds).get();
		List<Deployable> foundTotalDeployables = imp.deployable().findAll(Deployable.T, "*");

		CollectionTools.getMissingElements(foundDeployables, foundTotalDeployables)
				.forEach(d -> logger.warn("Found unexpected deployable with external id: " + d.getExternalId()));

		assertThat(foundDeployables) //
				.extracting("globalId").as("System deployable has unexpected globalId") //
				.allMatch(globalId -> globalId.toString().startsWith("hardwired:") || globalId.toString().startsWith("default:"));

		assertThat(foundDeployables) //
				.extracting("externalId").as("System deployable not found by externalId") //
				.contains((Object[]) expectedDeployableIds);

		List<Deployable> undeployedDeployables = foundDeployables.stream() //
				.filter(d -> d.getDeploymentStatus() != DeploymentStatus.deployed) //
				.collect(Collectors.toList());

		assertThat(undeployedDeployables) //
				.as(() -> {
					return "These deployables are not deployed: " + undeployedDeployables.stream() //
							.map(d -> d.getDeploymentStatus().toString()) //
							.collect(Collectors.joining(", ")); //
				}) //
				.isEmpty();

		logger.info("Test successful.");
	}
}
