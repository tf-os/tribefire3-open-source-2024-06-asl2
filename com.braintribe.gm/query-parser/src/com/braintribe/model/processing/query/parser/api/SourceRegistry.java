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
package com.braintribe.model.processing.query.parser.api;

import java.util.Map;

import com.braintribe.model.query.EntityQuery;
import com.braintribe.model.query.Join;
import com.braintribe.model.query.PropertyQuery;
import com.braintribe.model.query.Source;

/**
 * An interface that represents all the needed operation for linking aliases and
 * sources ({@link Source})
 */
public interface SourceRegistry {

	/**
	 * Registers an alias and a source together. If a temporary source
	 * (SourceLink) already exists, it is adjusted to reflect the actual source.
	 * 
	 * Validates that a source can only be registered once with the same alias.
	 * 
	 * @param alias
	 *            Alias for source
	 * @param source
	 *            {@link Source}
	 * 
	 */
	void registerSource(String alias, Source source);

	/**
	 * If a {@link Source} has been registered for the alias, it is returned.
	 * Otherwise a temporary {@link Source} (SourceLink) is returned as a place
	 * holder.
	 * 
	 * @param alias
	 *            Alias for Source
	 * @return {@link Source} that is represented by the alias.
	 */
	Source acquireSource(String alias);

	/**
	 * If a {@link Join} has been registered for the alias, it is returned.
	 * Otherwise a temporary {@link Source} (SourceLink) is returned as a place
	 * holder.
	 * 
	 * @param alias
	 *            Alias for Join
	 * @return {@link Join} that is represented by the alias.
	 */
	Join acquireJoin(String alias);

	/**
	 * Checks if a source has been registered with a certain alias.
	 * 
	 * @param alias
	 *            Alias for the source
	 * @return true if source is registered, false otherwise
	 */
	boolean validateIfSourceExists(String alias);

	/**
	 * Validates that all {@link Source} have been provided and that there is no
	 * alias without a proper {@link Source}.
	 * 
	 * Validates null sources for {@link EntityQuery} and {@link PropertyQuery}
	 * as well.
	 * 
	 * In case of strictValidation is true, the validation will throw an
	 * exception if any source has not been defined properly. Otherwise, in case
	 * of strictValidation being false, the offending source will be deleted
	 * from the registry instead.
	 * 
	 * @param strictValidation
	 *            A flag that indicates if an exception will be thrown in case
	 *            of malformed sources, or if the source will be simply deleted
	 *            from the registry
	 */
	void validateRegistry(boolean strictValidation);

	/**
	 * @return a {@link Map} where the keys are aliases and the values are the
	 *         corresponding {@link Source}
	 */
	Map<String, Source> getSourcesRegistry();

}
