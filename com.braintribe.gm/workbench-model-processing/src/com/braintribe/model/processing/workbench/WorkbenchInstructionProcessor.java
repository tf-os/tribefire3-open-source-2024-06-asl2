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
package com.braintribe.model.processing.workbench;

import java.util.ArrayList;
import java.util.List;

import com.braintribe.cfg.Configurable;
import com.braintribe.cfg.Required;
import com.braintribe.model.generic.session.exception.GmSessionException;
import com.braintribe.model.processing.core.expert.api.GmExpertDefinition;
import com.braintribe.model.processing.core.expert.api.GmExpertRegistry;
import com.braintribe.model.processing.core.expert.impl.ConfigurableGmExpertDefinition;
import com.braintribe.model.processing.core.expert.impl.ConfigurableGmExpertRegistry;
import com.braintribe.model.processing.session.api.persistence.PersistenceGmSession;
import com.braintribe.model.processing.workbench.experts.AddFolderExpert;
import com.braintribe.model.processing.workbench.experts.AddFolderToPerspectiveExpert;
import com.braintribe.model.processing.workbench.experts.CompoundInstructionExpert;
import com.braintribe.model.processing.workbench.experts.CreateTemplateBasedActionExpert;
import com.braintribe.model.processing.workbench.experts.DeleteFolderExpert;
import com.braintribe.model.processing.workbench.experts.EnsureFoldersExpert;
import com.braintribe.model.processing.workbench.experts.EnsurePerspectivesExpert;
import com.braintribe.model.processing.workbench.experts.RemoveFolderExpert;
import com.braintribe.model.processing.workbench.experts.UpdateFolderExpert;
import com.braintribe.model.processing.workbench.experts.UpdateUiStyleExpert;
import com.braintribe.model.workbench.instruction.AddFolder;
import com.braintribe.model.workbench.instruction.AddFolderToPerspective;
import com.braintribe.model.workbench.instruction.CompoundInstruction;
import com.braintribe.model.workbench.instruction.CreateTemplateBasedAction;
import com.braintribe.model.workbench.instruction.DeleteFolder;
import com.braintribe.model.workbench.instruction.EnsureFolders;
import com.braintribe.model.workbench.instruction.EnsurePerspectives;
import com.braintribe.model.workbench.instruction.RemoveFolder;
import com.braintribe.model.workbench.instruction.UpdateFolder;
import com.braintribe.model.workbench.instruction.UpdateUiStyle;
import com.braintribe.model.workbench.instruction.WorkbenchInstruction;

public class WorkbenchInstructionProcessor {

	private GmExpertRegistry expertRegistry = buildDefaultExpertRegistry();
	private PersistenceGmSession session;
	private boolean commitAfterProcessing;
	
	@Required
	@Configurable
	public void setSession(PersistenceGmSession session) {
		this.session = session;
	}

	@Configurable
	public void setCommitAfterProcessing(boolean commitAfterProcessing) {
		this.commitAfterProcessing = commitAfterProcessing;
	}
	
	@Configurable
	public void setExpertRegistry(GmExpertRegistry expertRegistry) {
		this.expertRegistry = expertRegistry;
	}

	public void processInstructions(List<WorkbenchInstruction> instructions) throws WorkbenchInstructionProcessorException {

		WorkbenchInstructionContext context = new BasicWorkbenchInstructionContext(session, expertRegistry);

		for (WorkbenchInstruction instruction : instructions) {
			// Get the expert for current instruction
			WorkbenchInstructionExpert<WorkbenchInstruction> expert = context.getExpertForInstruction(instruction); 
			// Process instruction.
			expert.process(instruction, context);
		}

		commitIfNecessary();
	}

	private void commitIfNecessary() throws WorkbenchInstructionProcessorException {
		if (commitAfterProcessing && session.getTransaction().hasManipulations()) {
			try {
				session.commit();
			} catch (GmSessionException e) {
				throw new WorkbenchInstructionProcessorException("Error while committing folder instructions.",e);
			}
		}
	}
	
	/**
	 * Creates an expertRegistry with default experts.
	 */
	protected ConfigurableGmExpertRegistry buildDefaultExpertRegistry() {
		ConfigurableGmExpertRegistry expertRegistry = new ConfigurableGmExpertRegistry();
		
		List<GmExpertDefinition> expertDefinitions = new ArrayList<GmExpertDefinition>();
		addExpertDefinition(expertDefinitions, AddFolder.class, new AddFolderExpert());
		addExpertDefinition(expertDefinitions, DeleteFolder.class, new DeleteFolderExpert());
		addExpertDefinition(expertDefinitions, EnsureFolders.class, new EnsureFoldersExpert());
		addExpertDefinition(expertDefinitions, RemoveFolder.class, new RemoveFolderExpert());
		addExpertDefinition(expertDefinitions, UpdateFolder.class, new UpdateFolderExpert());
		addExpertDefinition(expertDefinitions, CompoundInstruction.class, new CompoundInstructionExpert());
		addExpertDefinition(expertDefinitions, UpdateUiStyle.class, new UpdateUiStyleExpert());
		addExpertDefinition(expertDefinitions, EnsurePerspectives.class, new EnsurePerspectivesExpert());
		addExpertDefinition(expertDefinitions, AddFolderToPerspective.class, new AddFolderToPerspectiveExpert());
		addExpertDefinition(expertDefinitions, CreateTemplateBasedAction.class, new CreateTemplateBasedActionExpert());
		
		expertRegistry.setExpertDefinitions(expertDefinitions);
		return expertRegistry;
	}
	
	
	protected void addExpertDefinition(List<GmExpertDefinition> expertDefinitions, Class<?> denotationType, WorkbenchInstructionExpert<?> expert) {
		ConfigurableGmExpertDefinition expertDefinition = new ConfigurableGmExpertDefinition();
		expertDefinition.setDenotationType(denotationType);
		expertDefinition.setExpertType(WorkbenchInstructionExpert.class);
		expertDefinition.setExpert(expert);
		expertDefinitions.add(expertDefinition);
	}


}
