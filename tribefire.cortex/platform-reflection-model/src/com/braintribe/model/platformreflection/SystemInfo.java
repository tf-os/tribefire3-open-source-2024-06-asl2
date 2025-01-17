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
package com.braintribe.model.platformreflection;

import java.util.List;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.platformreflection.check.cpu.Cpu;
import com.braintribe.model.platformreflection.check.io.IoMeasurements;
import com.braintribe.model.platformreflection.check.java.JavaEnvironment;
import com.braintribe.model.platformreflection.check.power.PowerSource;
import com.braintribe.model.platformreflection.db.DatabaseInformation;
import com.braintribe.model.platformreflection.disk.DiskInfo;
import com.braintribe.model.platformreflection.disk.FileSystemDetailInfo;
import com.braintribe.model.platformreflection.disk.FileSystemInfo;
import com.braintribe.model.platformreflection.hardware.ComputerSystem;
import com.braintribe.model.platformreflection.memory.Memory;
import com.braintribe.model.platformreflection.network.NetworkInterface;
import com.braintribe.model.platformreflection.network.NetworkParams;
import com.braintribe.model.platformreflection.os.OperatingSystem;
import com.braintribe.model.platformreflection.request.PlatformReflectionResponse;
import com.braintribe.model.platformreflection.tf.Concurrency;
import com.braintribe.model.platformreflection.tf.Messaging;
import com.braintribe.model.platformreflection.threadpools.ThreadPools;

public interface SystemInfo extends PlatformReflectionResponse {

	EntityType<SystemInfo> T = EntityTypes.T(SystemInfo.class);

	List<DiskInfo> getDisks();
	void setDisks(List<DiskInfo> disks);

	ComputerSystem getComputerSystem();
	void setComputerSystem(ComputerSystem computerSystem);

	List<FileSystemInfo> getFileSystems();
	void setFileSystems(List<FileSystemInfo> fileSystems);

	FileSystemDetailInfo getFileSystemDetailInfo();
	void setFileSystemDetailInfo(FileSystemDetailInfo fileSystemDetailInfo);

	OperatingSystem getOperatingSystem();
	void setOperatingSystem(OperatingSystem operatingSystem);

	Memory getMemory();
	void setMemory(Memory memory);

	Cpu getCpu();
	void setCpu(Cpu cpu);

	List<NetworkInterface> getNetworkInterfaces();
	void setNetworkInterfaces(List<NetworkInterface> networkInterfaces);

	NetworkParams getNetworkParams();
	void setNetworkParams(NetworkParams networkPaar);

	List<PowerSource> getPowerSources();
	void setPowerSources(List<PowerSource> powerSources);

	List<com.braintribe.model.platformreflection.os.Process> getJavaProcesses();
	void setJavaProcesses(List<com.braintribe.model.platformreflection.os.Process> javaProcesses);

	JavaEnvironment getJavaEnvironment();
	void setJavaEnvironment(JavaEnvironment javaEnvironment);

	IoMeasurements getIoMeasurements();
	void setIoMeasurements(IoMeasurements ioMeasurements);

	DatabaseInformation getDatabaseInformation();
	void setDatabaseInformation(DatabaseInformation databaseInformation);

	ThreadPools getThreadPools();
	void setThreadPools(ThreadPools threadPools);

	List<String> getFontFamilies();
	void setFontFamilies(List<String> fontFamilies);

	Messaging getMessaging();
	void setMessaging(Messaging messaging);

	Concurrency getConcurrency();
	void setConcurrency(Concurrency concurrency);

}
