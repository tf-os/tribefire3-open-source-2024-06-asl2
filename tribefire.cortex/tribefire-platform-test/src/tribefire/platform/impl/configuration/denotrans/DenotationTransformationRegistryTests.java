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
package tribefire.platform.impl.configuration.denotrans;

import static com.braintribe.testing.junit.assertions.gm.assertj.core.api.GmAssertions.assertThat;
import static com.braintribe.utils.lcd.CollectionTools2.last;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.Test;

import com.braintribe.gm.model.reason.Maybe;
import com.braintribe.gm.model.reason.Reason;
import com.braintribe.gm.model.reason.essential.InvalidArgument;
import com.braintribe.gm.model.reason.essential.NotFound;
import com.braintribe.model.generic.manipulation.AddManipulation;
import com.braintribe.model.generic.manipulation.InstantiationManipulation;
import com.braintribe.model.generic.manipulation.Manipulation;
import com.braintribe.model.generic.manipulation.ManipulationComment;
import com.braintribe.model.generic.manipulation.RemoveManipulation;
import com.braintribe.model.generic.pr.AbsenceInformation;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.value.EntityReference;
import com.braintribe.model.generic.value.EnumReference;
import com.braintribe.model.generic.value.PersistentEntityReference;
import com.braintribe.model.meta.GmEntityType;
import com.braintribe.model.meta.GmEnumConstant;
import com.braintribe.model.meta.GmEnumType;
import com.braintribe.model.query.EntityQuery;
import com.braintribe.model.query.PropertyQuery;
import com.braintribe.model.query.SelectQuery;

/**
 * Tests for {@link DenotationTransformerRegistryImpl#resolveTransformationPipeline}
 * 
 * @author peter.gazdik
 */
public class DenotationTransformationRegistryTests extends AbstractDenotransTest {

	// ############################################
	// ## . . . . . . . . Works . . . . . . . . .##
	// ############################################

	@Test
	public void noTransformers_NoneNeeded() {
		assertPathOk(ManipulationComment.T);
	}

	@Test
	public void simplePath() {
		registerDummy(ManipulationComment.T, GmEntityType.T);

		assertPathOk(ManipulationComment.T, GmEntityType.T);
	}

	@Test
	public void multiStepPath() {
		registerDummy(ManipulationComment.T, GmEntityType.T);
		registerDummy(GmEntityType.T, GmEnumType.T);
		registerDummy(GmEnumType.T, PersistentEntityReference.T);

		assertPathOk(ManipulationComment.T, GmEntityType.T, GmEnumType.T, PersistentEntityReference.T);
	}

	@Test
	public void multiStepPath_InLabyrinth() {
		registerDummy(ManipulationComment.T, InstantiationManipulation.T);
		registerDummy(ManipulationComment.T, AddManipulation.T);
		registerDummy(AddManipulation.T, RemoveManipulation.T);
		registerDummy(ManipulationComment.T, GmEntityType.T);

		registerDummy(GmEntityType.T, GmEnumType.T);
		registerDummy(GmEntityType.T, PropertyQuery.T);
		registerDummy(GmEntityType.T, SelectQuery.T);
		registerDummy(GmEntityType.T, EntityQuery.T);

		registerDummy(GmEnumType.T, GmEnumConstant.T);
		registerDummy(GmEnumType.T, PersistentEntityReference.T);

		assertPathOk(ManipulationComment.T, GmEntityType.T, GmEnumType.T, PersistentEntityReference.T);
	}

	@Test
	public void disambiguatesPathByMoreSpecificType() {
		registerDummy(AbsenceInformation.T, AddManipulation.T);

		registerDummy(AddManipulation.T, GmEnumType.T);
		registerDummy(Manipulation.T, GmEntityType.T);

		registerDummy(GmEntityType.T, PersistentEntityReference.T);
		registerDummy(GmEnumType.T, PersistentEntityReference.T);

		assertPathOk(AbsenceInformation.T, AddManipulation.T, GmEnumType.T, PersistentEntityReference.T);
	}

	private void assertPathOk(EntityType<?> source, EntityType<?>... otherTypes) {
		EntityType<?> target = otherTypes.length == 0 ? source : last(otherTypes);

		Maybe<List<DtStep>> maybePath = transformerRegistry.resolveTransformationPipeline(source, target);
		assertThat(maybePath).isNotNull();
		assertThat(maybePath.isSatisfied()).as(() -> maybePath.whyUnsatisfied().stringify()).isTrue();

		List<DtStep> path = maybePath.get();
		assertThat(path).hasSize(otherTypes.length);
	}

	// ############################################
	// ## . . . . . . . . Errors . . . . . . . . ##
	// ############################################

	@Test
	public void noPath_NoConfiguration() throws Exception {
		// no morphers

		assertPathError(NotFound.T, ManipulationComment.T, EntityReference.T);
	}

	@Test
	public void noPath_DifferentConfiguration() throws Exception {
		registerDummy(ManipulationComment.T, GmEntityType.T);
		registerDummy(GmEntityType.T, EntityReference.T);

		assertPathError(NotFound.T, ManipulationComment.T, EnumReference.T);
	}

	@Test
	public void ambiguousPath() throws Exception {
		registerDummy(ManipulationComment.T, GmEntityType.T);
		registerDummy(GmEntityType.T, EntityReference.T);

		registerDummy(ManipulationComment.T, AbsenceInformation.T);
		registerDummy(AbsenceInformation.T, EntityReference.T);

		assertPathError(InvalidArgument.T, ManipulationComment.T, EntityReference.T);
	}

	private void assertPathError(EntityType<? extends Reason> reasonType, EntityType<?> source, EntityType<?> target) {
		Maybe<List<DtStep>> maybePath = transformerRegistry.resolveTransformationPipeline(source, target);
		assertThat(maybePath).isNotNull();
		assertThat(maybePath.isUnsatisfied()).isTrue();
		assertThat(maybePath.<Reason> whyUnsatisfied()).isInstanceOf(reasonType);
	}

}
