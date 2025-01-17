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

import static com.braintribe.model.processing.bootstrapping.TribefireRuntime.ENVIRONMENT_MESSAGING_QUEUE_DBL_REMOTE;
import static com.braintribe.model.processing.bootstrapping.TribefireRuntime.ENVIRONMENT_MESSAGING_QUEUE_TRUSTED_REQUEST;
import static com.braintribe.model.processing.bootstrapping.TribefireRuntime.ENVIRONMENT_MESSAGING_TOPIC_DBL_BROADCAST;
import static com.braintribe.model.processing.bootstrapping.TribefireRuntime.ENVIRONMENT_MESSAGING_TOPIC_DBL_REMOTE;
import static com.braintribe.model.processing.bootstrapping.TribefireRuntime.ENVIRONMENT_MESSAGING_TOPIC_HEARTBEAT;
import static com.braintribe.model.processing.bootstrapping.TribefireRuntime.ENVIRONMENT_MESSAGING_TOPIC_MULTICAST_REQUEST;
import static com.braintribe.model.processing.bootstrapping.TribefireRuntime.ENVIRONMENT_MESSAGING_TOPIC_MULTICAST_RESPONSE;
import static com.braintribe.model.processing.bootstrapping.TribefireRuntime.ENVIRONMENT_MESSAGING_TOPIC_TRUSTED_RESPONSE;
import static com.braintribe.model.processing.bootstrapping.TribefireRuntime.ENVIRONMENT_MESSAGING_TOPIC_UNLOCK;
import static com.braintribe.model.processing.bootstrapping.TribefireRuntime.getProperty;

import com.braintribe.logging.Logger;
import com.braintribe.model.processing.bootstrapping.TribefireRuntime;
import com.braintribe.utils.FileTools;
import com.braintribe.utils.StringTools;
import com.braintribe.wire.api.annotation.Managed;

import tribefire.module.wire.contract.MessagingDestinationsContract;

@Managed
public class MessagingDestinationsSpace implements MessagingDestinationsContract {

	private static Logger logger = Logger.getLogger(MessagingDestinationsSpace.class);

	private static final String DEFAULT_MESSAGING_TOPIC_MULTICAST_REQUEST = "tf.topic.multicastRequest";
	private static final String DEFAULT_MESSAGING_TOPIC_MULTICAST_RESPONSE = "tf.topic.multicastResponse";
	private static final String DEFAULT_MESSAGING_QUEUE_TRUSTED_REQUEST = "tf.queue.trustedRequest";
	private static final String DEFAULT_MESSAGING_TOPIC_TRUSTED_RESPONSE = "tf.topic.trustedResponse";
	private static final String DEFAULT_MESSAGING_TOPIC_HEARTBEAT = "tf.topic.heartbeat";
	private static final String DEFAULT_MESSAGING_TOPIC_UNLOCK = "tf.topic.unlock";
	private static final String DEFAULT_MESSAGING_TOPIC_DBL_BROADCAST = "tf.topic.dblBroadcast";
	private static final String DEFAULT_MESSAGING_TOPIC_DBL_REMOTE = "tf.topic.remoteToDbl";
	private static final String DEFAULT_MESSAGING_QUEUE_DBL_REMOTE = "tf.queue.remoteToDbl";

	@Override
	@Managed
	public String multicastRequestTopicName() {
		return prefixName(getProperty(ENVIRONMENT_MESSAGING_TOPIC_MULTICAST_REQUEST, DEFAULT_MESSAGING_TOPIC_MULTICAST_REQUEST));
	}

	@Override
	@Managed
	public String multicastResponseTopicName() {
		return prefixName(getProperty(ENVIRONMENT_MESSAGING_TOPIC_MULTICAST_RESPONSE, DEFAULT_MESSAGING_TOPIC_MULTICAST_RESPONSE));
	}

	@Override
	@Managed
	public String trustedRequestQueueName() {
		return prefixName(getProperty(ENVIRONMENT_MESSAGING_QUEUE_TRUSTED_REQUEST, DEFAULT_MESSAGING_QUEUE_TRUSTED_REQUEST));
	}

	@Override
	@Managed
	public String trustedResponseTopicName() {
		return prefixName(getProperty(ENVIRONMENT_MESSAGING_TOPIC_TRUSTED_RESPONSE, DEFAULT_MESSAGING_TOPIC_TRUSTED_RESPONSE));
	}

	@Override
	@Managed
	public String heartbeatTopicName() {
		return prefixName(getProperty(ENVIRONMENT_MESSAGING_TOPIC_HEARTBEAT, DEFAULT_MESSAGING_TOPIC_HEARTBEAT));
	}

	@Override
	@Managed
	public String unlockTopicName() {
		return prefixName(getProperty(ENVIRONMENT_MESSAGING_TOPIC_UNLOCK, DEFAULT_MESSAGING_TOPIC_UNLOCK));
	}

	@Override
	@Managed
	public String dblBroadcastTopicName() {
		return prefixName(getProperty(ENVIRONMENT_MESSAGING_TOPIC_DBL_BROADCAST, DEFAULT_MESSAGING_TOPIC_DBL_BROADCAST));
	}

	@Override
	@Managed
	public String remoteToDblTopicName() {
		return prefixName(getProperty(ENVIRONMENT_MESSAGING_TOPIC_DBL_REMOTE, DEFAULT_MESSAGING_TOPIC_DBL_REMOTE));
	}

	@Override
	@Managed
	public String remoteToDblQueueName() {
		return prefixName(getProperty(ENVIRONMENT_MESSAGING_QUEUE_DBL_REMOTE, DEFAULT_MESSAGING_QUEUE_DBL_REMOTE));
	}

	private String tenantId() {
		String tenantId = getProperty(TribefireRuntime.ENVIRONMENT_TENANT_ID);
		if (!StringTools.isBlank(tenantId)) {
			logger.debug(() -> "Tenant Id: "+tenantId);
			return tenantId;
		} else {
			logger.debug(() -> "No Tenant Id registered.");
			return null;
		}
	}

	private String normalizedTenantId() {
		String tenantId = tenantId();
		if (tenantId == null) {
			return null;
		} else {
			//Consciously using the same normalization as for filenames (as we do not know the restrictions of the underlying messaging system)
			String normalizedTenantId = FileTools.normalizeFilename(tenantId, '_');
			logger.debug(() -> "Changed \""+tenantId+"\" to \""+normalizedTenantId+"\" for use as destination prefix.");
			return normalizedTenantId;
		}
	}
	
	private String getPrefix() {
		String prefix = getProperty(TribefireRuntime.ENVIRONMENT_MESSAGING_DESTINATION_PREFIX);
		if (StringTools.isBlank(prefix)) {
			return normalizedTenantId();
		}
		if (prefix.equalsIgnoreCase("none")) {
			return null;
		}
		return prefix;
	}

	@Override
	public String prefixName(String name) {
		String prefix = getPrefix();
		logger.trace(() -> "Prefix for name "+name+": "+prefix);
		if (StringTools.isBlank(prefix)) {
			return name;
		} else {
			return prefix+"."+name;
		}
	}

}
