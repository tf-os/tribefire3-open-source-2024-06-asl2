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
package com.braintribe.devrock.artifactcontainer.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com.braintribe.devrock.artifactcontainer.ArtifactContainerPlugin;

public class ValidateSettingsCommand extends AbstractHandler  {

	public ValidateSettingsCommand() {	
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		Job validationJob = new Job( "Internal validation") {				
			@Override
			protected IStatus run(IProgressMonitor progressMonitor) {
				ArtifactContainerPlugin.getInstance().getValidator().validate( progressMonitor);
				return Status.OK_STATUS;
			}
		};
					
		validationJob.schedule();
		return null;
	}
	

}
