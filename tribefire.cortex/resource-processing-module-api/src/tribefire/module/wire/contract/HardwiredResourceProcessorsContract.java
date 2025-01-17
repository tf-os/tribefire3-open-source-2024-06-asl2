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
package tribefire.module.wire.contract;

import java.util.Set;
import java.util.function.Supplier;

import com.braintribe.model.extensiondeployment.BinaryPersistence;
import com.braintribe.model.extensiondeployment.BinaryRetrieval;
import com.braintribe.model.extensiondeployment.HardwiredBinaryPersistence;
import com.braintribe.model.extensiondeployment.HardwiredBinaryProcessor;
import com.braintribe.model.extensiondeployment.HardwiredBinaryRetrieval;
import com.braintribe.model.extensiondeployment.HardwiredResourceEnricher;
import com.braintribe.model.generic.reflection.Model;
import com.braintribe.model.processing.resource.enrichment.ResourceEnricher;
import com.braintribe.model.processing.service.api.ServiceProcessor;
import com.braintribe.model.resource.Resource;
import com.braintribe.model.resource.specification.ResourceSpecification;
import com.braintribe.model.resourceapi.enrichment.EnrichResource;
import com.braintribe.model.resourceapi.enrichment.EnrichResourceResponse;
import com.braintribe.model.resourceapi.persistence.BinaryPersistenceRequest;
import com.braintribe.model.resourceapi.persistence.BinaryPersistenceResponse;
import com.braintribe.model.resourceapi.stream.BinaryRetrievalRequest;
import com.braintribe.model.resourceapi.stream.BinaryRetrievalResponse;

/**
 * Offers methods for binding various {@link Resource} (i.e. binary data) processing components.
 * 
 * @see HardwiredDeployablesContract
 */
public interface HardwiredResourceProcessorsContract extends HardwiredDeployablesContract {

	public static final String defaultResourceEnrichersConfigModel = "tribefire.cortex:default-resource-enrichers-configuration-model";

	/** Eager instantiation version of {@link #bindBinaryServiceProcessor(String, String, Supplier, Supplier)} */
	default HardwiredBinaryProcessor bindBinaryServiceProcessor( //
			String externalId, String name, //
			ServiceProcessor<? super BinaryPersistenceRequest, ? super BinaryPersistenceResponse> binaryPersistence,
			ServiceProcessor<? super BinaryRetrievalRequest, ? super BinaryRetrievalResponse> binaryRetrieval) {

		return bindBinaryServiceProcessor(externalId, name, () -> binaryPersistence, () -> binaryRetrieval);
	}

	/** Binds {@link BinaryPersistence} and {@link BinaryRetrieval} suppliers as two components of the same Deployable. */
	HardwiredBinaryProcessor bindBinaryServiceProcessor( //
			String externalId, String name, //
			Supplier<ServiceProcessor<? super BinaryPersistenceRequest, ? super BinaryPersistenceResponse>> binaryPersistenceSupplier,
			Supplier<ServiceProcessor<? super BinaryRetrievalRequest, ? super BinaryRetrievalResponse>> binaryRetrievalSupplier);

	/** Eager instantiation version of {@link #bindBinaryPersistenceProcessor(String, String, Supplier)} */
	default HardwiredBinaryPersistence bindBinaryPersistenceProcessor( //
			String externalId, String name, //
			ServiceProcessor<? super BinaryPersistenceRequest, ? super BinaryPersistenceResponse> binaryPersistence) {

		return bindBinaryPersistenceProcessor(externalId, name, () -> binaryPersistence);
	}

	HardwiredBinaryPersistence bindBinaryPersistenceProcessor(String externalId, String name,
			Supplier<ServiceProcessor<? super BinaryPersistenceRequest, ? super BinaryPersistenceResponse>> binaryPersistenceSupplier);

	/** Eager instantiation version of {@link #bindBinaryRetrievalProcessor(String, String, Supplier)} */
	default HardwiredBinaryRetrieval bindBinaryRetrievalProcessor( //
			String externalId, String name, ServiceProcessor<? super BinaryRetrievalRequest, ? super BinaryRetrievalResponse> binaryRetrieval) {

		return bindBinaryRetrievalProcessor(externalId, name, () -> binaryRetrieval);
	}

	HardwiredBinaryRetrieval bindBinaryRetrievalProcessor(String externalId, String name,
			Supplier<ServiceProcessor<? super BinaryRetrievalRequest, ? super BinaryRetrievalResponse>> binaryRetrievalSupplier);

	HardwiredResourceEnricher bindResourceEnricherProcessor(String externalId, String name,
			Supplier<ServiceProcessor<? super EnrichResource, ? super EnrichResourceResponse>> resourceEnricherSupplier);

	/**
	 * Registers a hardwired {@link ResourceEnricher} for given mime-types. Optionally, one can also provide {@link Model}s containing custom
	 * entities, if such are attached to the Resource by the enricher, e.g. custom {@link ResourceSpecification}s.
	 * 
	 * <h3>How to configure an access to use these enrichers</h3>
	 * 
	 * There is a special model generated in cortex - {@link #defaultResourceEnrichersConfigModel} which you need to add as a dependency to the data
	 * model of your access.
	 * <p>
	 * This model contains two enrichers - one which detects the mime-type and another one which (based on specified/already detected mime type)
	 * delegates to enrichers registered via this method. This model also depends on all the models specified with all the calls of this method.
	 * <p>
	 * This means that by adding the model as a dependency you get both the meta-data specifying the enrichers to use and types these enrichers use.
	 */
	HardwiredResourceEnricher bindResourceEnricherForMimeTypes( //
			String externalId, String name, Set<String> mimeTypes, Set<Model> models,
			Supplier<ServiceProcessor<? super EnrichResource, ? super EnrichResourceResponse>> resourceEnricherSupplier);

}
