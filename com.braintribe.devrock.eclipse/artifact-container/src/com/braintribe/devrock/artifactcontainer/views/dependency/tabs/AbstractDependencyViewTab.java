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
package com.braintribe.devrock.artifactcontainer.views.dependency.tabs;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Layout;

import com.braintribe.devrock.artifactcontainer.ArtifactContainerPlugin;
import com.braintribe.devrock.artifactcontainer.control.container.ArtifactContainerRegistry;
import com.braintribe.devrock.artifactcontainer.views.dependency.ContainerMode;
import com.braintribe.devrock.artifactcontainer.views.dependency.listener.DependencyViewNotificationListener;
import com.braintribe.model.artifact.Solution;
import com.braintribe.model.malaclypse.WalkMonitoringResult;
import com.braintribe.plugin.commons.views.tabbed.tabs.AbstractTreeViewTab;

public abstract class AbstractDependencyViewTab extends AbstractTreeViewTab implements DependencyViewNotificationListener {
	protected ContainerMode currentContainerMode = ContainerMode.compile;
	protected Solution terminalSolution;
	protected WalkMonitoringResult monitoringResult;
	protected IProject currentProject;
	protected boolean overridenMonitoringResult;
	protected boolean deferUpdate = false;
	protected boolean lockCurrent = false;
	protected Image validRelevancyImage;
	protected Image warningRelevancyImage;
	protected Image errorRelevancyImage;
	private DependencyViewTabState currentTabState = DependencyViewTabState.noState;
	private ArtifactContainerRegistry artifactContainerRegistry = ArtifactContainerPlugin.getArtifactContainerRegistry();

	public AbstractDependencyViewTab(Display display) {
		super(display);
		setColumnNames( new String [] { "Artifact"});
		setColumnWeights( new int [] { 150});
				
		ImageDescriptor imageDescriptor = ImageDescriptor.createFromFile( AbstractDependencyViewTab.class, "accept.png");
		validRelevancyImage = imageDescriptor.createImage();
		
		imageDescriptor = ImageDescriptor.createFromFile( AbstractDependencyViewTab.class, "warning.png");
		warningRelevancyImage = imageDescriptor.createImage();
		
		imageDescriptor = ImageDescriptor.createFromFile( AbstractDependencyViewTab.class, "error.gif");
		errorRelevancyImage = imageDescriptor.createImage();		
	}
	
	
	protected void setTabState( DependencyViewTabState state) {
		switch (state) {
			case errorState:
				currentTabState = state;
				tabImageListener.setItemImage( instance, errorRelevancyImage);
				break;
			case warningState:
				if (currentTabState != DependencyViewTabState.errorState) {
					currentTabState = state;
					tabImageListener.setItemImage( instance, warningRelevancyImage);
				}
				break;
			case validState:
				if (
						currentTabState != DependencyViewTabState.errorState &&
						currentTabState != DependencyViewTabState.warningState
					) {
					currentTabState = state;
					tabImageListener.setItemImage( instance, validRelevancyImage);
				}
				break;
			case noState:		
				currentTabState = state;
				tabImageListener.setItemImage( instance, null);
				break;
			default:
				break;			
		}
	}
	
	

	@Override
	protected void handleTreeEvent(Event event) {	
	}



	@Override
	public void dispose() {
		validRelevancyImage.dispose();
		warningRelevancyImage.dispose();
		errorRelevancyImage.dispose();
		super.dispose();
	}

	


	@Override
	protected void addAdditionalButtons(Composite composite, Layout layout) {
	}

	@Override
	protected void initializeTree() {		
	}

	@Override
	public void acknowledgeProjectChanged(IProject project) {
		if (lockCurrent)
			return;
		
		currentProject = project;
		if (active == false || visible == false) {
			deferUpdate = true;
		}
		else {
			deferUpdate = false;
		}		
		if (!deferUpdate) {
			populateView(true);
		}
		broadcastTabState();
	}

	

	@Override
	public void acknowledgeLockTerminal(boolean lock) {
		lockCurrent = lock;
		if (!lock)
			overridenMonitoringResult = false;
	}
	@Override
	public void acknowledgeExternalMonitorResult( WalkMonitoringResult walkResult) {		
		monitoringResult = walkResult;				
		terminalSolution = walkResult.getTerminal();
		overridenMonitoringResult = true;
		
		currentProject = null;
		if (active == false || visible == false) {
			deferUpdate = true;
		}
		else {
			deferUpdate = false;
		}		
		if (!deferUpdate) {
			populateView(true);
		}
		broadcastTabState();		//
	}

	@Override
	public void acknowledgeContainerModeChanged(ContainerMode mode) {
		currentContainerMode = mode;
		if (active == false || visible == false) {
			deferUpdate = true;
		}
		else {
			deferUpdate = false;
		}
		if (!deferUpdate) {
			populateView(true);
		}
		
	}
	
	@Override
	public void acknowledgeVisibility(String key) {	
		super.acknowledgeVisibility(key);
		if (deferUpdate && visible && active) {
			populateView(true);
			deferUpdate = false;
		}
	}

	@Override
	public void acknowledgeActivation() {
		super.acknowledgeActivation();
		if (deferUpdate && visible && active) {
			populateView(true);
			deferUpdate = false;
		}
		
	}	
	
	protected boolean ensureMonitorData() {
		if (overridenMonitoringResult) {
			if (monitoringResult == null) {
				return false;
			} else {
				return true;
			}
		}
		
		WalkMonitoringResult walkResult = null;
		switch (currentContainerMode) {
			case compile:
				walkResult = artifactContainerRegistry.getCompileWalkResult(currentProject);						
				break;
			case runtime:
				walkResult = artifactContainerRegistry.getRuntimeWalkResult(currentProject);
				break;
			}
		if (walkResult == null)
			return false;
		monitoringResult = walkResult;				
		terminalSolution = walkResult.getTerminal();
		return true;
		
	}
	
	protected void populateView( final boolean interactive){
		display.asyncExec( new Runnable() {
			@Override
			public void run() {
				tree.removeAll();				
				setTabState( DependencyViewTabState.noState);
				if (ensureMonitorData()) { // only rebuild contents if anything's there.
					buildContents( interactive);
				}
			}
		});		
	}
	
		
	protected void broadcastTabState() {
		setTabState( DependencyViewTabState.noState);
	}
	
	protected abstract void buildContents( boolean interactive);
}
