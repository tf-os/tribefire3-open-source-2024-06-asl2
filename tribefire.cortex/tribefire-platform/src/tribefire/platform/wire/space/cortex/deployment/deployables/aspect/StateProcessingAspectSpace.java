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
package tribefire.platform.wire.space.cortex.deployment.deployables.aspect;

import java.util.List;
import java.util.concurrent.ThreadFactory;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.braintribe.execution.virtual.CountingVirtualThreadFactory;
import com.braintribe.model.processing.bootstrapping.TribefireRuntime;
import com.braintribe.model.processing.deployment.api.ExpertContext;
import com.braintribe.model.processing.sp.api.StateChangeProcessorRule;
import com.braintribe.model.processing.sp.aspect.StateProcessingAspect;
import com.braintribe.model.processing.sp.commons.ConfigurableStateChangeProcessorRuleSet;
import com.braintribe.model.processing.sp.invocation.multithreaded.MultiThreadedSpInvocation;
import com.braintribe.model.spapi.StateChangeProcessorInvocationPacket;
import com.braintribe.wire.api.annotation.Managed;

import tribefire.platform.impl.deployment.NoopStateChangeProcessorRule;
import tribefire.platform.wire.space.cortex.deployment.deployables.DeployableBaseSpace;

@Managed
public class StateProcessingAspectSpace extends DeployableBaseSpace {

	@Managed
	public StateProcessingAspect stateProcessingAspect(ExpertContext<com.braintribe.model.cortex.aspect.StateProcessingAspect> context) {
		StateProcessingAspect bean = new StateProcessingAspect();
		bean.setProcessorRuleSet(ruleSet(context));
		bean.setAsyncInvocationQueue(asyncInvocationQueue(context));
		return bean;
	}

	@Managed
	public ConfigurableStateChangeProcessorRuleSet ruleSet(ExpertContext<com.braintribe.model.cortex.aspect.StateProcessingAspect> context) {

		com.braintribe.model.cortex.aspect.StateProcessingAspect deployable = context.getDeployable();

		ConfigurableStateChangeProcessorRuleSet bean = new ConfigurableStateChangeProcessorRuleSet();

		List<StateChangeProcessorRule> rules = deployable.getProcessors().stream()
				.map(d -> context.resolve(d, com.braintribe.model.extensiondeployment.StateChangeProcessorRule.T, StateChangeProcessorRule.class,
						NoopStateChangeProcessorRule.instance))
				.collect(Collectors.toList());

		bean.setProcessorRules(rules);
		return bean;
	}

	@Managed
	public Consumer<StateChangeProcessorInvocationPacket> asyncInvocationQueue(
			ExpertContext<com.braintribe.model.cortex.aspect.StateProcessingAspect> context) {
		MultiThreadedSpInvocation bean = new MultiThreadedSpInvocation();
		bean.setThreadCount(environment.property(TribefireRuntime.ENVIRONMENT_STATEPROCESSING_THREADS, Integer.class, 20));
		bean.setProcessorRuleSet(ruleSet(context));
		bean.setUserSessionScoping(authContext.masterUser().userSessionScoping());
		bean.setSessionFactory(gmSessions.sessionFactory());
		bean.setSystemSessionFactory(gmSessions.systemSessionFactory());
		bean.setName("Aspect");
		return bean;
	}

	@Managed
	public ThreadFactory threadFactory(ExpertContext<com.braintribe.model.cortex.aspect.StateProcessingAspect> context) {
		return new CountingVirtualThreadFactory(executorId(context) + "-");
	}

	@Managed
	public String executorId(ExpertContext<com.braintribe.model.cortex.aspect.StateProcessingAspect> context) {
		return "tribefire.stateprocessing.aspect.executor[" + context.getDeployable().getExternalId() + "]";
	}

}
