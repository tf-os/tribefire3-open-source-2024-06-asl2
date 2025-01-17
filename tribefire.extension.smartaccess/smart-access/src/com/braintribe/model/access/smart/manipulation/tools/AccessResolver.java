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
package com.braintribe.model.access.smart.manipulation.tools;

import static com.braintribe.model.access.smart.manipulation.tools.SmartManipulationTools.newRefMap;

import java.util.Map;

import com.braintribe.model.accessdeployment.IncrementalAccess;
import com.braintribe.model.accessdeployment.smart.meta.DefaultDelegate;
import com.braintribe.model.generic.manipulation.Manipulation;
import com.braintribe.model.generic.value.EntityReference;
import com.braintribe.model.processing.smart.SmartAccessException;
import com.braintribe.model.processing.smart.query.planner.structure.ModelExpert;
import com.braintribe.utils.lcd.CollectionTools2;

/**
 * Resolves delegate access for given smart {@link EntityReference}. The access is resolved, using the following strategy:
 * <ol>
 * <li>if {@link EntityReference#getPartition() partition} is set, the corresponding access is used.</li>
 * <li>if entity is only mapped to one access, that one is used.</li>
 * <li>if {@link AccessInferer} was able to infer an access, it is used.</li>
 * <li>Use default access as configured per {@link DefaultDelegate} meta-data.</li>
 * </ol>
 * Note that {@link AccessInferer} is also considered in the first two cases, to make sure there is no conflict (Though there should be no
 * conflict if the entity is only mapped to one delegate).
 * 
 * Imagine situation I have instances e1 and e2. They reference each other and none of them has partition assigned explicitly. How to
 * determine the access, if they both have different default partitions (different accesses). For now, we throw an exception in this case,
 * stating that we were not able to set a reasonable default.
 * 
 * @author peter.gazdik
 */
public class AccessResolver {

	private final ModelExpert modelExpert;
	private final Map<EntityReference, IncrementalAccess> accessCache = newRefMap();

	private AccessInferer accessInferer;

	public AccessResolver(ModelExpert modelExpert) {
		this.modelExpert = modelExpert;
	}

	// ####################################
	// ## . . . . Initialization . . . . ##
	// ####################################

	public void initialize(Manipulation smartManipulation) {
		this.accessInferer = new AccessInferer(smartManipulation, modelExpert);
	}

	// ####################################
	// ## . . . . . Resolution . . . . . ##
	// ####################################

	public IncrementalAccess resolveAccess(EntityReference smartReference) {
		// try to get the result from the cache
		IncrementalAccess result = accessCache.get(smartReference);
		if (result != null)
			return result;

		// try checking if there is partition explicitly stated
		String partition = smartReference.getRefPartition();
		if (partition != null && !partition.equals(EntityReference.ANY_PARTITION)) {
			result = getAccess(partition);
			accessCache.put(smartReference, result);

			return result;
		}

		result = resolveAccessHelper(smartReference);
		if (result != null) {
			accessCache.put(smartReference, result);

			return result;
		}

		throw new SmartAccessException("Unable to resolve access for reference: " + smartReference +
				". This reference does not contain the partition information, it does not seem to be assigned to/from anything" +
				" which would indicate the correct delegate access, and there is no default partition configured.");
	}

	private IncrementalAccess resolveAccessHelper(EntityReference smartReference) {
		IncrementalAccess access = accessInferer.resolveAccess(smartReference);
		if (access != null)
			return access;

		access = modelExpert.resolveDefaultDelegate(smartReference.getTypeSignature());
		if (access != null)
			return access;

		return accessInferer.resolveAccessIfMappedToSingleDelegate(smartReference);
	}

	private IncrementalAccess getAccess(String partition) {
		IncrementalAccess result = modelExpert.getAccess(partition);
		if (result == null)
			throw new SmartAccessException("No access found for partition:" + partition);

		return result;
	}

	public void onReferenceUpdate(EntityReference smartReference, EntityReference newSmartRef) {
		CollectionTools2.updateMapKey(accessCache, smartReference, newSmartRef);
	}
}
