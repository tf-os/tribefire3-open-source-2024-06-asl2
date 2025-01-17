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
package tribefire.extension.metrics.service.aspect;

import java.io.ByteArrayOutputStream;

import com.braintribe.cfg.Configurable;
import com.braintribe.cfg.Required;
import com.braintribe.codec.marshaller.api.Marshaller;
import com.braintribe.logging.Logger;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.pr.criteria.EntityCriterion;
import com.braintribe.model.generic.reflection.BaseType;
import com.braintribe.model.generic.reflection.EntityVisitor;
import com.braintribe.model.generic.reflection.TraversingContext;
import com.braintribe.model.processing.service.api.ProceedContext;
import com.braintribe.model.processing.service.api.ServiceRequestContext;
import com.braintribe.model.resource.Resource;
import com.braintribe.model.service.api.ServiceRequest;
import com.braintribe.utils.lcd.CommonTools;

import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.DistributionSummary.Builder;
import tribefire.extension.metrics.connector.api.MetricsConnector;
import tribefire.extension.metrics.model.deployment.service.aspect.SummaryStatistics;

public class MetricsSummaryAspect extends MetricsAspect {

	private final static Logger logger = Logger.getLogger(MetricsSummaryAspect.class);

	private tribefire.extension.metrics.model.deployment.service.aspect.MetricsSummaryAspect deployable;
	private Marshaller marshaller;

	@Override
	public Object process(ServiceRequestContext requestContext, ServiceRequest request, ProceedContext proceedContext) {
		boolean metricsActive = true;

		if (metricsActive) {
			logger.trace(() -> "Metrics enabled for request: '" + request + "'");

			Object result = null;
			try {

				result = proceedContext.proceed(request);

				metricsConnectors.forEach(metricsConnector -> {

					DistributionSummary summary = fetchSummary(metricsConnector, request, tagsSuccess);

					record(summary, request);
				});

				return result;
			} catch (Throwable t) {
				metricsConnectors.forEach(metricsConnector -> {
					DistributionSummary summary = fetchSummary(metricsConnector, request, tagsError);

					record(summary, request);
				});
				throw t;
			}
		} else {
			logger.trace(() -> "Metrics disabled for request: '" + request + "'");
			Object result = proceedContext.proceed(request);
			return result;
		}
	}

	// -----------------------------------------------------------------------
	// HELPERS
	// -----------------------------------------------------------------------

	private DistributionSummary fetchSummary(MetricsConnector metricsConnector, ServiceRequest request, String[] tags) {
		//@formatter:off
		Builder builder = DistributionSummary
				  .builder(name)
				  .tags(enrichTags(request, tags));
		//@formatter:on

		if (!CommonTools.isEmpty(description)) {
			builder.description(description);
		}
		String baseUnit = deployable.getBaseUnit();
		if (!CommonTools.isEmpty(baseUnit)) {
			builder.baseUnit(baseUnit);
		}
		Double scale = deployable.getScale();
		if (scale != null) {
			builder.scale(scale);
		}

		// TODO: many other functionalities like percentiles, sla,...

		DistributionSummary summary = builder.register(metricsConnector.registry());

		return summary;
	}

	private void record(DistributionSummary summary, ServiceRequest request) {

		SummaryStatistics summaryStatistics = deployable.getSummaryStatistics();

		DoubleHolder value = new DoubleHolder();
		switch (summaryStatistics) {
			case REQUEST_SIZE:
				value.assign(calcRequestSize(request));
				break;
			case RESOURCE_SIZE:
				calcResourceSize(request, value);
				break;
			case REQUEST_RESOURCE_SIZE:
				value.assign(calcRequestSize(request));
				calcResourceSize(request, value);
				break;

			default:
				throw new IllegalArgumentException("'" + SummaryStatistics.class.getName() + "': '" + summaryStatistics + "' not supported");
		}

		summary.record(value.get());
	}

	private void calcResourceSize(ServiceRequest request, DoubleHolder value) {
		BaseType.INSTANCE.traverse(request, null, new EntityVisitor() {
			@Override
			protected void visitEntity(GenericEntity entity, EntityCriterion criterion, TraversingContext traversingContext) {

				if (entity instanceof Resource) {
					Resource resource = (Resource) entity;
					value.increment(resource.getFileSize());
				}
			}
		});
	}

	private double calcRequestSize(ServiceRequest request) {
		double value;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		marshaller.marshall(baos, request);
		value = baos.size();
		return value;
	}

	// -----------------------------------------------------------------------
	// GETTER & SETTER
	// -----------------------------------------------------------------------

	@Configurable
	@Required
	public void setDeployable(tribefire.extension.metrics.model.deployment.service.aspect.MetricsSummaryAspect deployable) {
		this.deployable = deployable;
	}

	@Configurable
	@Required
	public void setMarshaller(Marshaller marshaller) {
		this.marshaller = marshaller;
	}

}
