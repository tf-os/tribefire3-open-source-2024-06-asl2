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
package tribefire.platform.wire.space.common;

import java.io.File;

import com.braintribe.cartridge.common.processing.streaming.StandardResourceEnrichingStreamer;
import com.braintribe.cartridge.common.processing.streaming.StandardResourceEnrichingStreamer2;
import com.braintribe.mimetype.MimeTypeDetector;
import com.braintribe.mimetype.PlatformMimeTypeDetector;
import com.braintribe.model.cortex.deployment.CortexConfiguration;
import com.braintribe.model.processing.deployment.api.SchrodingerBean;
import com.braintribe.model.processing.resource.enrichment.ResourceEnrichingStreamer;
import com.braintribe.model.resource.api.MimeTypeRegistry;
import com.braintribe.model.resource.api.ResourceBuilder;
import com.braintribe.model.resource.utils.MimeTypeRegistryImpl;
import com.braintribe.model.resource.utils.StreamPipeTransientResourceBuilder;
import com.braintribe.utils.FileTools;
import com.braintribe.utils.stream.pools.CompoundBlockPool;
import com.braintribe.utils.stream.pools.SmartBlockPoolFactory;
import com.braintribe.wire.api.annotation.Import;
import com.braintribe.wire.api.annotation.Managed;

import tribefire.module.wire.contract.ResourceProcessingContract;
import tribefire.platform.wire.space.SchrodingerBeansSpace;

@Managed
public class ResourceProcessingSpace implements ResourceProcessingContract {

	public static final String DEFAULT_MIME_TYPE_DETECTOR_EXTERNAL_ID = "default.MimeTypeDetector";

	@Import
	private BindersSpace binders;

	@Import
	private SchrodingerBeansSpace schrodingerBeans;

	@Import
	private SpecificationsSpace specifications;

	@Override
	@Managed
	public MimeTypeDetector mimeTypeDetector() {
		return mimeTypeDetectorSchrodingerBean().proxy();
	}

	public PlatformMimeTypeDetector defaultMimeTypeDetector() {
		return PlatformMimeTypeDetector.instance;
	}

	@Managed
	public SchrodingerBean<MimeTypeDetector> mimeTypeDetectorSchrodingerBean() {
		return schrodingerBeans.newBean("MimeTypeDetector", CortexConfiguration::getMimeTypeDetector, binders.mimeTypeDetector());
	}

	@Override
	@Managed
	public ResourceEnrichingStreamer enrichingStreamer() {
		StandardResourceEnrichingStreamer bean = new StandardResourceEnrichingStreamer();
		bean.setMimeTypeDetector(mimeTypeDetector());
		bean.setSpecificationDetector(specifications.standardSpecificationDetector());
		bean.setStreamPipeFactory(streamPipeFactory());
		return bean;
	}

	@Managed
	public ResourceEnrichingStreamer enrichingStreamer2() {
		StandardResourceEnrichingStreamer2 bean = new StandardResourceEnrichingStreamer2();
		bean.setMimeTypeDetector(mimeTypeDetector());
		bean.setSpecificationDetector(specifications.standardSpecificationDetector());
		bean.setStreamPipeFactory(streamPipeFactory());
		return bean;
	}

	/** {@inheritDoc} */
	@Override
	@Managed
	public ResourceBuilder transientResourceBuilder() {
		return new StreamPipeTransientResourceBuilder(streamPipeFactory());
	}

	/** {@inheritDoc} */
	@Override
	@Managed
	public CompoundBlockPool streamPipeFactory() {
		SmartBlockPoolFactory poolFactory = SmartBlockPoolFactory.usingAvailableMemory(0.1);
		poolFactory.setStreamPipeFolder(streamPipeFolder());

		return poolFactory.create();
	}

	@Override
	@Managed
	public File streamPipeFolder() {
		File tempDir = FileTools.getTempDir();
		File bean = new File(tempDir, "platform/basicStreamPipe");

		return bean;
	}

	@Override
	@Managed
	public MimeTypeRegistry mimeTypeRegistry() {
		return new MimeTypeRegistryImpl();
	}

}
