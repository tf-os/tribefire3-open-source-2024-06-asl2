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
package com.braintribe.model.generic.manipulation;

import java.util.List;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

/**
 * This class serves almost purely as a marker for a compound manipulation which has already been normalized, adding
 * only a single convenience method for retrieving the underlying {@link AtomicManipulation}s. Other than that, it
 * should be 100% compatible with all the existing general {@link Manipulation}-handling code, but makes it possible for
 * the "manipulation normalizer" to recognize that given manipulation was already normalized, thus avoiding unnecessary
 * normalization process.
 */

public interface NormalizedCompoundManipulation extends CompoundManipulation {

	EntityType<NormalizedCompoundManipulation> T = EntityTypes.T(NormalizedCompoundManipulation.class);

	/**
	 * Convenience method that returns the underlying compoundManipulationList, but casted to a list of
	 * {@link AtomicManipulation} (which is the normal form for compound manipulation).
	 * 
	 * @deprecated use {@link #inline()}
	 */
	@Deprecated
	default List<AtomicManipulation> atomicManipulations() {
		return inline();
	}

	@Override
	default List<AtomicManipulation> inline() {
		return (List<AtomicManipulation>) (List<?>) getCompoundManipulationList();
	}

}
