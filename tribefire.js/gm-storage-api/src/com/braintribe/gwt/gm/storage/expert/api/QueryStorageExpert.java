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
package com.braintribe.gwt.gm.storage.expert.api;

import com.braintribe.gwt.gm.storage.api.ColumnData;
import com.braintribe.gwt.gm.storage.api.StorageHandle;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.query.Query;

public interface QueryStorageExpert {

	public QueryStorageInput prepareStorageInput(Query query, String queryString, ColumnData columnData);

	public StorageHandle prepareStorageHandle(GenericEntity entity);

	public String getQueryString(StorageHandle handle);
}
