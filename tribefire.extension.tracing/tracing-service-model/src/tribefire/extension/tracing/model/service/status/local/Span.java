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
package tribefire.extension.tracing.model.service.status.local;

import java.util.List;
import java.util.Map;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

public interface Span extends GenericEntity {

	EntityType<Span> T = EntityTypes.T(Span.class);

	String duration = "duration";
	String logs = "logs";
	String operationName = "operationName";
	String serviceName = "serviceName";
	String start = "start";
	String attributes = "attributes";

	long getDuration();
	void setDuration(long duration);

	List<LogData> getLogs();
	void setLogs(List<LogData> logs);

	String getOperationName();
	void setOperationName(String operationName);

	String getServiceName();
	void setServiceName(String serviceName);

	long getStart();
	void setStart(long start);

	Map<String, Object> getAttributes();
	void setAttributes(Map<String, Object> attributes);

}