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
package com.braintribe.model.processing.resource.filesystem.wire.contract;

import java.nio.file.Path;
import java.util.Map;

import com.braintribe.model.access.IncrementalAccess;
import com.braintribe.model.generic.eval.Evaluator;
import com.braintribe.model.processing.resource.filesystem.FileSystemBinaryProcessor;
import com.braintribe.model.processing.resource.filesystem.common.ProcessorConfig;
import com.braintribe.model.processing.resource.filesystem.common.TestFile;
import com.braintribe.model.service.api.ServiceRequest;
import com.braintribe.provider.Hub;
import com.braintribe.wire.api.space.WireSpace;

public interface MainContract extends WireSpace {

	Hub<Path> tempPathHolder();

	IncrementalAccess access1();

	IncrementalAccess access2();

	Path access1Path();

	Path access2Path();

	ProcessorConfig simpleFileSystemBinaryProcessorConfig();

	FileSystemBinaryProcessor simpleFileSystemBinaryProcessor();

	ProcessorConfig enrichingFileSystemBinaryProcessorConfig();

	FileSystemBinaryProcessor enrichingFileSystemBinaryProcessor();

	Map<String, TestFile> testFiles();
	
	Evaluator<ServiceRequest> evaluator();

}
