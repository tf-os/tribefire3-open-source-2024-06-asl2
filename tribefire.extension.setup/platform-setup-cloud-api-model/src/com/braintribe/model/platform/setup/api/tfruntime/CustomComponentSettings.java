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
package com.braintribe.model.platform.setup.api.tfruntime;

import java.util.List;
import java.util.Map;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

public interface CustomComponentSettings extends GenericEntity {
	EntityType<CustomComponentSettings> T = EntityTypes.T(CustomComponentSettings.class);

	String getName();
	void setName(String name);

	String getNameRegex();
	void setNameRegex(String nameRegex);

	LogLevel getLogLevel();
	void setLogLevel(LogLevel logLevel);

	Integer getReplicas();
	void setReplicas(Integer replicas);

	Boolean getEnableJpda();
	void setEnableJpda(Boolean enableJpda);

	Map<String, String> getEnv();
	void setEnv(Map<String, String> env);

	Resources getResources();
	void setResources(Resources resources);

	List<PersistentVolume> getPersistentVolumes();
	void setPersistentVolumes(List<PersistentVolume> persistentVolumes);

	String getCustomPath();
	void setCustomPath(String customPath);

	String getCustomHealthCheckPath();
	void setCustomHealthCheckPath(String customHealthCheckPath);
}