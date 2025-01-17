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
package tribefire.extension.metrics.templates.wire.space;

import com.braintribe.logging.Logger;
import com.braintribe.model.extensiondeployment.meta.AroundProcessWith;
import com.braintribe.model.extensiondeployment.meta.ProcessWith;
import com.braintribe.model.meta.GmMetaModel;
import com.braintribe.model.processing.meta.editor.BasicModelMetaDataEditor;
import com.braintribe.wire.api.annotation.Import;
import com.braintribe.wire.api.annotation.Managed;
import com.braintribe.wire.api.scope.InstanceConfiguration;
import com.braintribe.wire.api.space.WireSpace;

import tribefire.extension.metrics.MetricsConstants;
import tribefire.extension.metrics.model.deployment.service.aspect.MetricsCounterAspect;
import tribefire.extension.metrics.model.deployment.service.aspect.MetricsInProgressAspect;
import tribefire.extension.metrics.model.deployment.service.aspect.MetricsSummaryAspect;
import tribefire.extension.metrics.model.deployment.service.aspect.MetricsTimerAspect;
import tribefire.extension.metrics.model.service.MetricsRequest;
import tribefire.extension.metrics.model.service.test.MetricsDemoService;
import tribefire.extension.metrics.templates.api.MetricsTemplateContext;
import tribefire.extension.metrics.templates.util.MetricsTemplateUtil;
import tribefire.extension.metrics.templates.wire.contract.MetricsMetaDataContract;
import tribefire.extension.metrics.templates.wire.contract.MetricsTemplatesContract;

@Managed
public class MetricsMetaDataSpace implements WireSpace, MetricsMetaDataContract {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(MetricsMetaDataSpace.class);

	@Import
	private MetricsTemplatesContract metricsTemplates;

	@Override
	@Managed
	public GmMetaModel serviceModel(MetricsTemplateContext context) {
		GmMetaModel model = context.create(GmMetaModel.T, InstanceConfiguration.currentInstance());
		GmMetaModel rawServiceModel = (GmMetaModel) context.lookup("model:" + MetricsConstants.SERVICE_MODEL_QUALIFIEDNAME);
		setModelDetails(model, MetricsTemplateUtil.resolveServiceModelName(context), rawServiceModel);
		return model;
	}

	@Override
	public void metaData(MetricsTemplateContext context) {
		// -----------------------------------------------------------------------
		// DATA MODEL
		// -----------------------------------------------------------------------

		// nothing

		// -----------------------------------------------------------------------
		// SERVICE MODEL
		// -----------------------------------------------------------------------

		GmMetaModel serviceModel = serviceModel(context);
		BasicModelMetaDataEditor serviceModelEditor = new BasicModelMetaDataEditor(serviceModel);

		serviceModelEditor.onEntityType(MetricsRequest.T).addMetaData(processWithMetricsRequest(context));

		if (context.getAddDemo()) {
			serviceModelEditor.onEntityType(MetricsDemoService.T).addMetaData(processWithMetricsDemoService(context));
			serviceModelEditor.onEntityType(MetricsDemoService.T).addMetaData(aroundProcessWithMetricsDemoServiceMetricsCounterAspect(context));
			serviceModelEditor.onEntityType(MetricsDemoService.T).addMetaData(aroundProcessWithMetricsDemoServiceMetricsTimerAspect(context));
			serviceModelEditor.onEntityType(MetricsDemoService.T).addMetaData(aroundProcessWithMetricsDemoServiceMetricsSummaryAspect(context));
			serviceModelEditor.onEntityType(MetricsDemoService.T).addMetaData(aroundProcessWithMetricsDemoServiceMetricsInProgressAspect(context));
		}

	}

	// --------------------------–

	// -----------------------------------------------------------------------
	// META DATA - PROCESS WITH
	// -----------------------------------------------------------------------

	@Managed
	public ProcessWith processWithMetricsRequest(MetricsTemplateContext context) {
		ProcessWith bean = context.create(ProcessWith.T, InstanceConfiguration.currentInstance());
		bean.setProcessor(metricsTemplates.metricsServiceProcessor(context));

		return bean;
	}

	@Managed
	public ProcessWith processWithMetricsDemoService(MetricsTemplateContext context) {
		ProcessWith bean = context.create(ProcessWith.T, InstanceConfiguration.currentInstance());
		bean.setProcessor(metricsTemplates.metricsDemoProcessor(context));

		return bean;
	}

	// -----------------------------------------------------------------------
	// META DATA - AROUND PROCESS WITH
	// -----------------------------------------------------------------------

	@Managed
	public AroundProcessWith aroundProcessWithMetricsDemoServiceMetricsCounterAspect(MetricsTemplateContext context) {
		AroundProcessWith bean = context.create(AroundProcessWith.T, InstanceConfiguration.currentInstance());

		MetricsCounterAspect metricsCounterAspect = metricsTemplates.metricsCounterAspect(context);
		bean.setProcessor(metricsCounterAspect);

		return bean;
	}
	@Managed
	public AroundProcessWith aroundProcessWithMetricsDemoServiceMetricsTimerAspect(MetricsTemplateContext context) {
		AroundProcessWith bean = context.create(AroundProcessWith.T, InstanceConfiguration.currentInstance());

		MetricsTimerAspect metricsCounterAspect = metricsTemplates.metricsTimerAspect(context);
		bean.setProcessor(metricsCounterAspect);

		return bean;
	}
	@Managed
	public AroundProcessWith aroundProcessWithMetricsDemoServiceMetricsSummaryAspect(MetricsTemplateContext context) {
		AroundProcessWith bean = context.create(AroundProcessWith.T, InstanceConfiguration.currentInstance());

		MetricsSummaryAspect metricsCounterAspect = metricsTemplates.metricsSummaryAspect(context);
		bean.setProcessor(metricsCounterAspect);

		return bean;
	}
	@Managed
	public AroundProcessWith aroundProcessWithMetricsDemoServiceMetricsInProgressAspect(MetricsTemplateContext context) {
		AroundProcessWith bean = context.create(AroundProcessWith.T, InstanceConfiguration.currentInstance());

		MetricsInProgressAspect metricsInProgressAspect = metricsTemplates.metricsInProgressAspect(context);
		bean.setProcessor(metricsInProgressAspect);

		return bean;
	}

	// -----------------------------------------------------------------------
	// HELPER METHODS
	// -----------------------------------------------------------------------

	private static void setModelDetails(GmMetaModel targetModel, String modelName, GmMetaModel... dependencies) {
		targetModel.setName(modelName);
		targetModel.setVersion(MetricsConstants.MAJOR_VERSION + ".0");
		if (dependencies != null) {
			for (GmMetaModel dependency : dependencies) {
				if (dependency != null) {
					targetModel.getDependencies().add(dependency);
				}
			}
		}
	}
}
