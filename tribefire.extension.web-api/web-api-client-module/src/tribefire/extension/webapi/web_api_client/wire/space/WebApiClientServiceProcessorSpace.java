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
package tribefire.extension.webapi.web_api_client.wire.space;

import java.util.Set;

import com.braintribe.model.deployment.http.processor.ConfigurableHttpServiceProcessor;
import com.braintribe.model.deployment.http.processor.DynamicHttpServiceProcessor;
import com.braintribe.model.meta.GmMetaModel;
import com.braintribe.model.processing.deployment.api.ExpertContext;
import com.braintribe.model.processing.http.resolver.AbstractContextResolver;
import com.braintribe.model.processing.http.resolver.DynamicContextResolver;
import com.braintribe.model.processing.http.resolver.StaticContextResolver;
import com.braintribe.model.processing.service.api.ServiceProcessor;
import com.braintribe.model.processing.session.api.managed.ModelAccessory;
import com.braintribe.model.service.api.ServiceRequest;
import com.braintribe.model.service.domain.ServiceDomain;
import com.braintribe.processing.http.client.HttpClient;
import com.braintribe.wire.api.annotation.Import;
import com.braintribe.wire.api.annotation.Managed;
import com.braintribe.wire.api.space.WireSpace;

import tribefire.module.wire.contract.TribefireWebPlatformContract;

@Managed
public class WebApiClientServiceProcessorSpace implements WireSpace {

	@Import
	// TODO: relate to TribefirePlatformContract after it got the web unrelated methods
	private TribefireWebPlatformContract tfPlatform;
	
	@Managed
	public ServiceProcessor<ServiceRequest, Object> configurableServiceProcessor(ExpertContext<ConfigurableHttpServiceProcessor> context) {
		com.braintribe.model.processing.http.WebApiClientServiceProcessor bean = new com.braintribe.model.processing.http.WebApiClientServiceProcessor();
		bean.setHttpContextResolver(staticContextResolver(context));
		return bean;
	}

	@Managed
	public ServiceProcessor<ServiceRequest, Object> metaDataMappedServiceProcessor(ExpertContext<DynamicHttpServiceProcessor> context) {
		com.braintribe.model.processing.http.WebApiClientServiceProcessor bean = new com.braintribe.model.processing.http.WebApiClientServiceProcessor();
		bean.setHttpContextResolver(dynamicContextResolver(context));
		return bean;
	}

	private DynamicContextResolver dynamicContextResolver(ExpertContext<DynamicHttpServiceProcessor> context) {
		DynamicHttpServiceProcessor deployable = context.getDeployable();
		DynamicContextResolver bean = new DynamicContextResolver();
		bean.setClientResolver(c -> context.resolve(c, com.braintribe.model.deployment.http.client.HttpClient.T));
		bean.setModelAccessoryFactory(tfPlatform.requestUserRelated().modelAccessoryFactory());
		Set<String> resolverUseCases = deployable.getResolverUseCases();
		if (!resolverUseCases.isEmpty()) {
			bean.setResolverUseCases(resolverUseCases);
		}
		return bean;
	}

	private AbstractContextResolver staticContextResolver(ExpertContext<ConfigurableHttpServiceProcessor> context) {
		ConfigurableHttpServiceProcessor deployable = context.getDeployable();
		com.braintribe.model.deployment.http.client.HttpClient clientDeployable = deployable.getClient();
		HttpClient httpClient = context.resolve(clientDeployable, com.braintribe.model.deployment.http.client.HttpClient.T);

		ServiceDomain domain = deployable.getServiceDomain();
		GmMetaModel serviceModel = domain.getServiceModel();
		ModelAccessory modelAccessory = tfPlatform.systemUserRelated().modelAccessoryFactory().getForModel(serviceModel.getName());

		StaticContextResolver bean = new StaticContextResolver();
		bean.setHttpClient(httpClient);
		bean.setModelAccessory(modelAccessory);
		Set<String> resolverUseCases = deployable.getResolverUseCases();
		if (!resolverUseCases.isEmpty())
			bean.setResolverUseCases(resolverUseCases);

		return bean;
	}
}
