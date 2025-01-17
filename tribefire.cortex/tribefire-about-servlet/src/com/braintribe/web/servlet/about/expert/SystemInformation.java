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
package com.braintribe.web.servlet.about.expert;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

import com.braintribe.common.lcd.Numbers;
import com.braintribe.logging.Logger;
import com.braintribe.model.generic.eval.EvalContext;
import com.braintribe.model.generic.eval.Evaluator;
import com.braintribe.model.platformreflection.PlatformReflection;
import com.braintribe.model.platformreflection.SystemInfo;
import com.braintribe.model.platformreflection.TribefireInfo;
import com.braintribe.model.platformreflection.check.java.JavaEnvironment;
import com.braintribe.model.platformreflection.request.ReflectPlatform;
import com.braintribe.model.processing.service.common.FailureCodec;
import com.braintribe.model.service.api.InstanceId;
import com.braintribe.model.service.api.MulticastRequest;
import com.braintribe.model.service.api.ServiceRequest;
import com.braintribe.model.service.api.result.Failure;
import com.braintribe.model.service.api.result.MulticastResponse;
import com.braintribe.model.service.api.result.ResponseEnvelope;
import com.braintribe.model.service.api.result.ServiceResult;
import com.braintribe.web.servlet.TypedVelocityContext;

public class SystemInformation {

	private static Logger logger = Logger.getLogger(SystemInformation.class);

	public void processSysInfoRequest(Evaluator<ServiceRequest> requestEvaluator, Collection<InstanceId> selectedServiceInstances,
			TypedVelocityContext context, String userSessionId, ExecutorService executor) {

		logger.debug(() -> "Sending a request to return system information to " + selectedServiceInstances + " with session " + userSessionId);

		Map<String, PlatformReflection> sysInfoMap = Collections.synchronizedMap(new TreeMap<>());

		AbstractMulticastingExpert.execute(selectedServiceInstances, executor, "SystemInformation", i -> {

			ReflectPlatform reflectPlatform = ReflectPlatform.T.create();

			MulticastRequest mcR = MulticastRequest.T.create();
			mcR.setAsynchronous(false);
			mcR.setServiceRequest(reflectPlatform);
			mcR.setAddressee(i);
			mcR.setTimeout((long) Numbers.MILLISECONDS_PER_MINUTE);
			mcR.setSessionId(userSessionId);
			EvalContext<? extends MulticastResponse> eval = mcR.eval(requestEvaluator);
			MulticastResponse multicastResponse = eval.get();

			for (Map.Entry<InstanceId, ServiceResult> entry : multicastResponse.getResponses().entrySet()) {

				InstanceId instanceId = entry.getKey();

				logger.debug(() -> "Received a response from instance: " + instanceId);

				String nodeId = instanceId.getNodeId();

				ServiceResult result = entry.getValue();
				if (result instanceof Failure) {
					Throwable throwable = FailureCodec.INSTANCE.decode(result.asFailure());
					logger.error("Received failure from " + instanceId, throwable);
				} else if (result instanceof ResponseEnvelope) {

					ResponseEnvelope envelope = (ResponseEnvelope) result;
					PlatformReflection platformReflection = (PlatformReflection) envelope.getResult();

					sortCollections(platformReflection);

					sysInfoMap.put(nodeId, platformReflection);

				} else {
					logger.error("Unsupported response type: " + result);
				}

			}

		});

		context.put("sysInfoMap", sysInfoMap);

		logger.debug(() -> "Done with processing a request to return system information.");

	}

	private static void sortCollections(PlatformReflection platformReflection) {
		if (platformReflection == null) {
			return;
		}
		TribefireInfo tribefireInfo = platformReflection.getTribefire();
		if (tribefireInfo != null) {
			sortMap(tribefireInfo.getTribefireRuntimeProperties(), tribefireInfo::setTribefireRuntimeProperties);
		}
		SystemInfo system = platformReflection.getSystem();
		if (system != null) {
			JavaEnvironment environment = system.getJavaEnvironment();
			if (environment != null) {
				sortMap(environment.getSystemProperties(), environment::setSystemProperties);
				sortMap(environment.getEnvironmentVariables(), environment::setEnvironmentVariables);
			}
		}
	}

	private static void sortMap(Map<String, String> source, Consumer<Map<String, String>> consumer) {
		if (source == null || source.isEmpty()) {
			return;
		}
		TreeMap<String, String> sortedProperties = new TreeMap<>();
		sortedProperties.putAll(source);
		consumer.accept(sortedProperties);
	}
}
