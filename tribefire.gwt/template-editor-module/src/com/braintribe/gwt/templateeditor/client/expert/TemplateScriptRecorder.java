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
package com.braintribe.gwt.templateeditor.client.expert;

import java.util.ArrayList;
import java.util.List;

import com.braintribe.gwt.templateeditor.client.TemplateEditorManager;
import com.braintribe.model.generic.GMF;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.manipulation.AddManipulation;
import com.braintribe.model.generic.manipulation.AtomicManipulation;
import com.braintribe.model.generic.manipulation.ChangeValueManipulation;
import com.braintribe.model.generic.manipulation.CompoundManipulation;
import com.braintribe.model.generic.manipulation.LocalEntityProperty;
import com.braintribe.model.generic.manipulation.Manipulation;
import com.braintribe.model.generic.manipulation.PropertyManipulation;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.reflection.StandardCloningContext;
import com.braintribe.model.generic.tracking.ManipulationListener;
import com.braintribe.model.processing.am.AssemblyMonitoring;
import com.braintribe.model.processing.session.api.persistence.PersistenceGmSession;
import com.braintribe.model.template.Template;

public class TemplateScriptRecorder implements ManipulationListener{
	
	private boolean isRecording = false;
	private boolean toReplace = true;
	private PersistenceGmSession session;
	private Template template;
	private final TemplateEditorManager templateEditorManager;
	private AssemblyMonitoring assemblyMonitoring;
	private final List<Manipulation> recordedManipulations = new ArrayList<>();
	private final List<Manipulation> postProcessedManipulations = new ArrayList<>();
	
	public TemplateScriptRecorder() {
		templateEditorManager = new TemplateEditorManager();
	}
	
	public void setTemplate(Template template) {
		this.template = template;
	}
	
	public void setSession(PersistenceGmSession session) {
		this.session = session;
	}
	
	public boolean isRecording() {
		return isRecording;
	}
	
	public void startRecording() {
		startRecording(true);
	}
	
	public void startRecording(boolean toReplace) {
		session.suspendHistory();
		isRecording = true;
		this.toReplace = toReplace;
		templateEditorManager.analyzePrototype(template.getPrototype());
		assemblyMonitoring = AssemblyMonitoring.newInstance().build(session, template);
		assemblyMonitoring.addManpiulationListener(this);
	}
	
	public void stopRecording(){
		isRecording = false;
		assemblyMonitoring.removeManpiulationListener(this);
		session.resumeHistory();
		processRecordedManipulations();
		apply();
	}
	
	@Override
	public void noticeManipulation(Manipulation manipulation) {
		if (manipulation instanceof AtomicManipulation) {
			if (isRecording && isManipulationRelevant(manipulation))
				recordedManipulations.add(manipulation);
		} else
			((CompoundManipulation)manipulation).getCompoundManipulationList().forEach(m -> noticeManipulation(m));
	}
	
	private boolean isManipulationRelevant(Manipulation manipulation) {
		if (manipulation instanceof PropertyManipulation) {
			LocalEntityProperty owner = (LocalEntityProperty) ((PropertyManipulation) manipulation).getOwner();
			if (manipulation instanceof ChangeValueManipulation || manipulation instanceof AddManipulation)
				return assemblyMonitoring.getEntities().contains(owner.getEntity());
		}
		
		return false;
	}
	
	
	private void processRecordedManipulations(){
		if (recordedManipulations != null && !recordedManipulations.isEmpty())
			postProcessedManipulations.addAll(templateEditorManager.evoluteManipulations(templateEditorManager.filterManipulations(recordedManipulations)));
	}
	
	public void apply() {
		StandardCloningContext cloningContext = new StandardCloningContext() {
			@SuppressWarnings("unusable-by-js")
			@Override
			public GenericEntity supplyRawClone(EntityType<? extends GenericEntity> entityType,	GenericEntity instanceToBeCloned) {
				Object idValue = instanceToBeCloned.getId();
				return idValue == null ? session.create(entityType) : instanceToBeCloned;
			}
		};
		
		if (postProcessedManipulations != null && !postProcessedManipulations.isEmpty()) {
			GenericModelType type = GMF.getTypeReflection().getType(postProcessedManipulations);
			if (template.getScript() == null) {
				CompoundManipulation shapeScript = session.create(CompoundManipulation.T);
				List<Manipulation> postProcessedManipulationsClone = (List<Manipulation>) type.clone(cloningContext, postProcessedManipulations, null);		
				shapeScript.setCompoundManipulationList(postProcessedManipulationsClone);
				template.setScript(shapeScript);
			} else {
				CompoundManipulation shapeScript = (CompoundManipulation) template.getScript();
				if (toReplace)
					shapeScript.setCompoundManipulationList((List<Manipulation>) type.clone(cloningContext, postProcessedManipulations, null));
				else
					shapeScript.getCompoundManipulationList().addAll((List<Manipulation>) type.clone(cloningContext, postProcessedManipulations, null));				
			}
			
			postProcessedManipulations.clear();
		}
		
		recordedManipulations.clear();
	}

}
