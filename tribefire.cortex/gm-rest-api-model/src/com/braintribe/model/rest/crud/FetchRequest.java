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
package com.braintribe.model.rest.crud;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;


import com.braintribe.model.rest.HasCodec;
import com.braintribe.model.rest.HasDynamicParameters;
import com.braintribe.model.rest.HasEntityType;
import com.braintribe.model.rest.HasProjection;
import com.braintribe.model.rest.RestRequest;


public interface FetchRequest extends RestRequest, HasCodec, HasProjection, HasEntityType, HasDynamicParameters{

	EntityType<FetchRequest> T = EntityTypes.T(FetchRequest.class);
	
	void setOrderBy(String orderBy);
	String getOrderBy();
	
	void setOrderDir(OrderingDirection orderingDirection);
	OrderingDirection getOrderDir();
	
	void setPageStart(Integer pageStart);
	Integer getPageStart();
	
	void setPageSize(Integer pageSize);
	Integer getPageSize();
	
	void setFulltext(String fulltext);
	String getFulltext();
}
