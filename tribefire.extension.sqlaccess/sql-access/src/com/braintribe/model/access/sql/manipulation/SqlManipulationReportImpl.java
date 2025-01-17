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
package com.braintribe.model.access.sql.manipulation;

import static com.braintribe.utils.lcd.CollectionTools2.newList;
import static com.braintribe.utils.lcd.CollectionTools2.newMap;

import java.util.List;
import java.util.Map;

import com.braintribe.model.access.sql.SqlManipulationReport;
import com.braintribe.model.generic.GenericEntity;

/**
 * @author peter.gazdik
 */
public class SqlManipulationReportImpl implements SqlManipulationReport {

	public final List<GenericEntity> newEntities = newList();
	public final List<GenericEntity> existingEntities = newList();

	public final Map<GenericEntity, String> assignedIds = newMap();
	public final Map<GenericEntity, String> assignedGlobalIds = newMap();
	public final Map<GenericEntity, String> assignedPartitions = newMap();

	@Override
	public List<GenericEntity> getNewEntities() {
		return newEntities;
	}

	@Override
	public List<GenericEntity> getExistingEntities() {
		return existingEntities;
	}

	@Override
	public Map<GenericEntity, String> getAssignedIds() {
		return assignedIds;
	}

	@Override
	public Map<GenericEntity, String> getAssignedGlobalIds() {
		return assignedGlobalIds;
	}

	@Override
	public Map<GenericEntity, String> getAssignedPartitions() {
		return assignedPartitions;
	}

}
