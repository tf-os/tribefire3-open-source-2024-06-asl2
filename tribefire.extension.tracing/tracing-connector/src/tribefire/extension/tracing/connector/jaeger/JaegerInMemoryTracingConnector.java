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
package tribefire.extension.tracing.connector.jaeger;

import java.time.Duration;

import com.braintribe.cfg.Configurable;
import com.braintribe.cfg.Required;
import com.braintribe.logging.Logger;
import com.braintribe.model.check.service.CheckResultEntry;
import com.braintribe.model.check.service.CheckStatus;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.testing.exporter.InMemorySpanExporter;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.SpanProcessor;
import io.opentelemetry.sdk.trace.export.BatchSpanProcessor;
import io.opentelemetry.semconv.resource.attributes.ResourceAttributes;
import tribefire.extension.tracing.connector.api.AbstractTracingConnector;

/**
 *
 */
public class JaegerInMemoryTracingConnector extends AbstractTracingConnector {

	private final static Logger logger = Logger.getLogger(JaegerInMemoryTracingConnector.class);

	private tribefire.extension.tracing.model.deployment.connector.JaegerInMemoryTracingConnector deployable;

	private Tracer tracer;
	
	private SdkTracerProvider tracerProvider;

	private OpenTelemetry openTelemetry;

	private InMemorySpanExporter spanExporter;

	// -----------------------------------------------------------------------
	// LifecycleAware
	// -----------------------------------------------------------------------

	// -----------------------------------------------------------------------
	// METHODS
	// -----------------------------------------------------------------------

	@Override
	public InMemorySpanExporter spanExporter() {
		return spanExporter;
	}

	@Override
	public OpenTelemetry openTelemetry() {
		return openTelemetry;
	}

	@Override
	public Tracer tracer() {
		return tracer;
	}

	@Override
	public SdkTracerProvider tracerProvider() {
		return tracerProvider;
	}

	@Override
	public void initialize() {
		closeTracer();

		spanExporter = InMemorySpanExporter.create();
		
		SpanProcessor spanProcessor = 
			BatchSpanProcessor.builder(spanExporter)
			.setMaxExportBatchSize(1)
			.setMaxQueueSize(1)
			.setScheduleDelay(Duration.ofNanos(1))
			.build();
		
		Resource resource = Resource.getDefault()
				.merge(Resource.create(Attributes.of(ResourceAttributes.SERVICE_NAME, serviceName)));
		
		tracerProvider = SdkTracerProvider
				.builder()
				.setResource(resource)
				.addSpanProcessor(spanProcessor)
				.build();

		openTelemetry = OpenTelemetrySdk.builder()
				.setTracerProvider(tracerProvider)
				.build();
	    
	    tracer = openTelemetry
	    		.getTracer(serviceName);
	    
		logger.info(() -> "Setup inmemory jaeger tracer");
	}

	@Override
	public CheckResultEntry actualHealth() {
		CheckResultEntry entry = CheckResultEntry.T.create();

		entry.setCheckStatus(CheckStatus.ok);
		entry.setName(deployable.getName() + " (TYPE: '" + deployable.entityType().getShortName() + "')");
		entry.setMessage("(" + deployable.getGlobalId() + ")");

		if (tracer() == null) {
			entry.setCheckStatus(CheckStatus.fail);
			entry.setName("Tracer is not set");
		}

		return entry;
	}

	// -----------------------------------------------------------------------
	// GETTER & SETTER
	// -----------------------------------------------------------------------

	@Required
	@Configurable
	public void setDeployable(tribefire.extension.tracing.model.deployment.connector.JaegerInMemoryTracingConnector deployable) {
		this.deployable = deployable;
	}

}
