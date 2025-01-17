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
package com.braintribe.model.activemqdeployment;

import java.util.List;

import com.braintribe.model.extensiondeployment.Worker;
import com.braintribe.model.generic.annotation.Initializer;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

public interface ActiveMqWorker extends Worker {

	final EntityType<ActiveMqWorker> T = EntityTypes.T(ActiveMqWorker.class);
	
	void setBindAddress(String bindAddress);
	@Initializer("'0.0.0.0'")
	String getBindAddress();
	
	void setPort(Integer port);
	@Initializer("61616")
	Integer getPort();
	
	void setDataDirectory(String dataDirectory);
	@Initializer("'WEB-INF/activemq-data'")
	String getDataDirectory();
	
	void setBrokerName(String brokerName);
	String getBrokerName();
	
	void setUseJmx(Boolean useJmx);
	@Initializer("false")
	Boolean getUseJmx();
	
	void setPersistenceDbDir(String persistenceDbDir);
	@Initializer("'WEB-INF/activemq-db'")
	String getPersistenceDbDir();
	
	void setClusterNodes(List<NetworkConnector> clusterNodes);
	List<NetworkConnector> getClusterNodes();
	
	void setHeapUsageInPercent(Integer heapUsageInPercent);
	Integer getHeapUsageInPercent();
	
	void setDiskUsageLimit(Long diskUsageLimit);
	Long getDiskUsageLimit();
	
	void setTempUsageLimit(Long tempUsageLimit);
	Long getTempUsageLimit();
	
	void setCreateVmConnector(Boolean createVmConnector);
	Boolean getCreateVmConnector();
	
	void setPersistent(Boolean persistent);
	@Initializer("false")
	Boolean getPersistent();

	void setDiscoveryMulticastUri(String discoveryMulticastUri);
	String getDiscoveryMulticastUri();

	void setDiscoveryMulticastGroup(String discoveryMulticastGroup);
	String getDiscoveryMulticastGroup();

	void setDiscoveryMulticastNetworkInterface(String discoveryMulticastNetworkInterface);
	String getDiscoveryMulticastNetworkInterface();

	void setDiscoveryMulticastAddress(String discoveryMulticastAddress);
	String getDiscoveryMulticastAddress();

	void setDiscoveryMulticastInterface(String discoveryMulticastInterface);
	String getDiscoveryMulticastInterface();

}
