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
package tribefire.platform.wire.space.cortex.accesses;

import static java.util.Collections.singletonList;

import java.util.List;

import com.braintribe.model.access.collaboration.CollaborativeSmoodAccess;
import com.braintribe.model.access.impl.aop.AopAccess;
import com.braintribe.model.accessdeployment.HardwiredAccess;
import com.braintribe.model.accessdeployment.HardwiredCollaborativeAccess;
import com.braintribe.model.generic.reflection.Model;
import com.braintribe.model.meta.GmMetaModel;
import com.braintribe.model.processing.aop.api.aspect.AccessAspect;
import com.braintribe.model.processing.aop.api.service.AopIncrementalAccess;
import com.braintribe.model.processing.bootstrapping.TribefireRuntime;
import com.braintribe.model.processing.sp.api.StateChangeProcessorRule;
import com.braintribe.model.processing.sp.aspect.StateProcessingAspect;
import com.braintribe.model.processing.sp.commons.ConfigurableStateChangeProcessorRuleSet;
import com.braintribe.model.processing.sp.invocation.multithreaded.MultiThreadedSpInvocation;
import com.braintribe.wire.api.annotation.Import;
import com.braintribe.wire.api.annotation.Managed;

import tribefire.platform.wire.space.common.EnvironmentSpace;
import tribefire.platform.wire.space.cortex.AccessAspectsSpace;
import tribefire.platform.wire.space.cortex.TraversingCriteriaSpace;
import tribefire.platform.wire.space.cortex.deployment.DeploymentSpace;
import tribefire.platform.wire.space.cortex.deployment.StateChangeProcessorsSpace;
import tribefire.platform.wire.space.security.AuthContextSpace;
import tribefire.platform.wire.space.streaming.ResourceAccessSpace;

@Managed
public abstract class HardwiredAccessSpaceBase extends SystemAccessSpaceBase {

	// @formatter:off
	@Import protected AccessAspectsSpace aspects;
	@Import protected AuthContextSpace authContext;
	@Import protected DeploymentSpace deployment;
	@Import protected EnvironmentSpace environment;
	@Import protected ResourceAccessSpace resourceAccess;
	@Import private StateChangeProcessorsSpace stateChangeProcessors;
	@Import protected SystemAccessCommonsSpace systemAccessCommons;
	@Import protected SystemAccessesSpace systemAccesses;
	@Import protected TraversingCriteriaSpace traversingCriteria;
	// @formatter:on

	@Managed
	public AopIncrementalAccess aopAccess() {
		AopAccess bean = new AopAccess();
		bean.setAccessId(id());
		bean.setDelegate(access());
		bean.setSystemSessionFactory(gmSessions.systemSessionFactory());
		bean.setUserSessionFactory(gmSessions.sessionFactory());
		bean.setAspects(aopAspects());

		return bean;
	}

	protected abstract List<AccessAspect> aopAspects();

	public CollaborativeSmoodAccess collaborativeAccess() {
		if (!isCollaborativeAccess())
			throw new IllegalStateException("Access '" + id() + "' is not a collaboratie access!");
		return (CollaborativeSmoodAccess) access();
	}

	public abstract boolean isCollaborativeAccess();

	public abstract HardwiredAccessSpaceBase workbenchAccessSpace();

	@Managed
	protected StateProcessingAspect stateProcessingAspect() {
		StateProcessingAspect bean = new StateProcessingAspect();
		bean.setProcessorRuleSet(stateChangeRuleSet());
		bean.setAsyncInvocationQueue(asyncInvocationQueue());
		return bean;
	}

	@Managed
	private MultiThreadedSpInvocation asyncInvocationQueue() {
		MultiThreadedSpInvocation bean = new MultiThreadedSpInvocation();
		bean.setThreadCount(environment.property(TribefireRuntime.ENVIRONMENT_STATEPROCESSING_THREADS, Integer.class, 20));
		bean.setProcessorRuleSet(stateChangeRuleSet());
		bean.setUserSessionScoping(authContext.masterUser().userSessionScoping());
		bean.setSessionFactory(gmSessions.sessionFactory());
		bean.setSystemSessionFactory(gmSessions.systemSessionFactory());
		bean.setName(name());
		return bean;
	}

	@Managed
	private ConfigurableStateChangeProcessorRuleSet stateChangeRuleSet() {
		ConfigurableStateChangeProcessorRuleSet bean = new ConfigurableStateChangeProcessorRuleSet();
		bean.setProcessorRules(stateChangeProcessorRules());
		return bean;
	}

	protected List<StateChangeProcessorRule> stateChangeProcessorRules() {
		return singletonList(stateChangeProcessors.bidiProperty());
	}

	@Managed
	public HardwiredAccess hardwiredDeployable() {
		HardwiredAccess bean = isCollaborativeAccess() ? HardwiredCollaborativeAccess.T.create() : HardwiredAccess.T.create();

		bean.setExternalId(id());
		bean.setName(name());
		bean.setGlobalId(globalId());

		String modelName = modelName();
		GmMetaModel metaModel = GmMetaModel.T.create(Model.modelGlobalId(modelName));
		metaModel.setName(modelName);

		bean.setMetaModel(metaModel);

		if (workbenchAccessSpace() != null) {
			bean.setWorkbenchAccess(workbenchAccessSpace().hardwiredDeployable());
		}

		String serviceModelName = serviceModelName();
		if (serviceModelName != null) {
			GmMetaModel serviceModel = GmMetaModel.T.create(Model.modelGlobalId(serviceModelName));
			serviceModel.setName(serviceModelName);
			bean.setServiceModel(serviceModel);
		}

		return bean;
	}

}
