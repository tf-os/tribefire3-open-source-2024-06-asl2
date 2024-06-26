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
package tribefire.extension.metrics.templates.api.connector;

/**
 *
 */
public class MetricsTemplateNewRelicConnectorContextImpl extends MetricsTemplateConnectorContextImpl
		implements MetricsTemplateNewRelicConnectorContext, MetricsTemplateNewRelicConnectorContextBuilder {

	@Override
	public MetricsTemplateNewRelicConnectorContext build() {
		return this;
	}

	@Override
	public String toString() {
		return "MetricsTemplateNewRelicConnectorContextImpl []";
	}

}