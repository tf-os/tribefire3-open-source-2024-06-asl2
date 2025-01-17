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
package com.braintribe.model.processing.oracle;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;

import com.braintribe.model.generic.reflection.GenericModelTypeReflection;
import com.braintribe.model.meta.GmMetaModel;
import com.braintribe.model.processing.meta.oracle.BasicModelDependencies;
import com.braintribe.model.processing.meta.oracle.ModelDependencies;
import com.braintribe.testing.junit.assertions.assertj.core.api.Assertions;

/**
 * @see ModelDependencies
 * @see BasicModelDependencies
 * 
 * @author peter.gazdik
 */
public class BasicModelDependenciesTest extends AbstractOracleTest {

	private final ModelDependencies types = oracle.getDependencies();
	private List<String> names;

	@Test
	public void getDirectDependencies() throws Exception {
		collectNames(types.asGmMetaModels());

		assertNames(ANIMAL_MODEL, MAMMAL_MODEL, FISH_MODEL);
	}

	@Test
	public void getTransitiveDependencies() throws Exception {
		collectNames(types.transitive().asGmMetaModels());

		assertNames(MAMMAL_MODEL, FISH_MODEL, ANIMAL_MODEL, GenericModelTypeReflection.rootModelName);
	}

	@Test
	public void getTransitiveDependenciesAndSelf() throws Exception {
		collectNames(types.transitive().includeSelf().asGmMetaModels());

		assertNames(FARM_MODEL, MAMMAL_MODEL, FISH_MODEL, ANIMAL_MODEL, GenericModelTypeReflection.rootModelName);
	}

	private void collectNames(Stream<GmMetaModel> models) {
		names = models.map(GmMetaModel::getName).collect(Collectors.toList());
	}

	private void assertNames(String... expected) {
		Assertions.assertThat(names).containsOnly(expected);
	}

}
