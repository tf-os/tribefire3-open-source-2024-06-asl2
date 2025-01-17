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
package com.braintribe.plugin.commons.views;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;

import com.braintribe.model.malaclypse.WalkMonitoringResult;
import com.braintribe.plugin.commons.selection.SelectionExtractor;
import com.braintribe.plugin.commons.views.listener.ViewNotificationBroadcaster;
import com.braintribe.plugin.commons.views.listener.ViewNotificationListener;

/**
 * an abstract view for eclipse 
 * @author pit
 *
 */
public abstract class AbstractView extends ViewPart implements ViewNotificationBroadcaster, ViewNotificationListener,
															   ISelectionListener, 
															   IPartListener2 {
	protected Set<ViewNotificationListener> listeners = new HashSet<ViewNotificationListener>();
	protected IWorkbenchWindow workbenchWindow;
	protected IViewSite site;	
	protected Display display;
	protected String viewKey ="view title";

	protected IWorkingSet currentWorkingSet;
	protected IProject currentProject;
	protected ISelection currentISelection;
	
	protected IActionBars actionBars;
	protected IMenuManager menuManager;
	protected IToolBarManager toolbarManager;
	protected boolean visible;
	
	@Override
	public void addListener(ViewNotificationListener listener) {	
		listeners.add(listener);
	}
	@Override
	public void removeListener(ViewNotificationListener listener) {
		listeners.remove(listener);		
	}

	@Override
	public void init(IViewSite site) throws PartInitException {		
		super.init(site);
		this.site = site;
		workbenchWindow = site.getWorkbenchWindow();
		display = workbenchWindow.getShell().getDisplay();
		
		workbenchWindow.getSelectionService().addPostSelectionListener( this);		
		workbenchWindow.getActivePage().addPartListener( this);
		
		actionBars = site.getActionBars();
		menuManager = actionBars.getMenuManager();
		toolbarManager = actionBars.getToolBarManager();
	}
	
	@Override
	public void dispose() {
		workbenchWindow.getSelectionService().removePostSelectionListener( this);
		workbenchWindow.getActivePage().removePartListener( this);		
		super.dispose();
	}
	
	@Override
	public void partActivated(IWorkbenchPartReference arg0) {			
	}
	@Override
	public void partBroughtToTop(IWorkbenchPartReference arg0) {	
	}
	@Override
	public void partClosed(IWorkbenchPartReference arg0) {				
	}
	@Override
	public void partDeactivated(IWorkbenchPartReference arg0) {		
	}
	@Override
	public void partInputChanged(IWorkbenchPartReference arg0) {		
	}
	@Override
	public void partOpened(IWorkbenchPartReference arg0) {		
	}
	
	@Override
	public void partVisible(IWorkbenchPartReference partReference) {
		String partName = partReference.getPartName();
		if (partName.startsWith(viewKey) == false)
			return;
		visible = true;
		acknowledgeVisibility( partName);
		
	}
	@Override
	public void partHidden(IWorkbenchPartReference partReference) {
		String partName = partReference.getPartName();
		if (partName.startsWith(viewKey) == false)
			return;
		visible = false;
		acknowledgeInvisibility( partName);
	}
	
	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		currentISelection = selection;
		currentWorkingSet = SelectionExtractor.extractSelectedWorkingSet(selection);
		IProject project = SelectionExtractor.extractSelectedProject(selection);
		if (
				project != null &&
				project != currentProject
			) {
			currentProject = project;
			acknowledgeProjectChanged( project);
		}
	}
	@Override
	public void acknowledgeVisibility(String key) {
		for (ViewNotificationListener listener : listeners) {
			listener.acknowledgeVisibility(key);
		}		
	}
	@Override
	public void acknowledgeInvisibility(String key) {
		for (ViewNotificationListener listener : listeners) {
			listener.acknowledgeInvisibility(key);
		}		
		
	}
	@Override
	public void acknowledgeProjectChanged(IProject project) {
		for (ViewNotificationListener listener : listeners) {
			listener.acknowledgeProjectChanged( project);
		}				
	}
	@Override
	public void acknowledgeLockTerminal(boolean lock) {
		for (ViewNotificationListener listener : listeners) {
			listener.acknowledgeLockTerminal(lock);
		}						
	}
	@Override
	public void acknowledgeExternalMonitorResult(WalkMonitoringResult result) {
		for (ViewNotificationListener listener : listeners) {
			listener.acknowledgeExternalMonitorResult( result);
		}						
	}
	
	
	
	
	
	
	
	
		
}
