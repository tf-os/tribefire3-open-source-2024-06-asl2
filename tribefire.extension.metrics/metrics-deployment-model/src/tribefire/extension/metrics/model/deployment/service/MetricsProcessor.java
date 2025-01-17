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
package tribefire.extension.metrics.model.deployment.service;

import java.util.Set;

import com.braintribe.model.extensiondeployment.ServiceProcessor;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

import tribefire.extension.metrics.model.deployment.service.aspect.MetricsAspect;

/**
 * Metrics Processor
 * 
 *
 */
// TODO: maybe add description for meters?
public interface MetricsProcessor extends ServiceProcessor {

	final EntityType<MetricsProcessor> T = EntityTypes.T(MetricsProcessor.class);

	String metricsBinderConfig = "metricsBinderConfig";
	String metricsAspects = "metricsAspects";

	MetricsBinderConfig getMetricsBinderConfig();
	void setMetricsBinderConfig(MetricsBinderConfig metricsBinderConfig);

	Set<MetricsAspect> getMetricsAspects();
	void setMetricsAspects(Set<MetricsAspect> metricsAspects);

}
