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
package com.braintribe.devrock.preferences.pages.cfg;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.braintribe.devrock.api.ui.editors.BooleanEditor;
import com.braintribe.devrock.api.ui.fonts.FontHandler;
import com.braintribe.devrock.api.ui.viewers.reason.transpose.TransposedReasonViewer;
import com.braintribe.devrock.api.ui.viewers.repository.RepositoryViewer;
import com.braintribe.devrock.mc.api.repository.configuration.RepositoryReflection;
import com.braintribe.devrock.model.repository.RepositoryConfiguration;
import com.braintribe.devrock.plugin.DevrockPlugin;
import com.braintribe.gm.model.reason.Maybe;
import com.braintribe.gm.model.reason.Reason;

public class ConfigurationPage extends PreferencePage implements IWorkbenchPreferencePage {
	private static final String PREFERENCES_CFG = "Devrock's repository configuration";
	
	private Font bigFont;	

	private RepositoryViewer repositoryViewer;

	private TransposedReasonViewer reasonViewer;
	
	public ConfigurationPage() {
		setDescription(PREFERENCES_CFG);						
	}

	@Override
	public void init(IWorkbench arg0) {		
	}

	@Override
	protected Control createContents(Composite parent) {
		
		bigFont = FontHandler.buildBoldFont(parent.getDisplay(), parent.getFont());
					
		Composite composite = new Composite(parent, SWT.NONE);
		
		GridLayout layout = new GridLayout();
		layout.numColumns = 4;
		composite.setLayout( layout);
		
		//
		Label imageLabel = new Label(composite, SWT.NONE);
		imageLabel.setLayoutData(new GridData( SWT.CENTER, SWT.TOP, false, false, 4, 1));
		imageLabel.setText("Currently active repository configuration");
		imageLabel.setFont(bigFont);

		
		Maybe<RepositoryReflection> potReflectRepositoryConfiguration = null;
		
		
		potReflectRepositoryConfiguration = DevrockPlugin.mcBridge().reflectRepositoryConfiguration();
		
		 	
		if (!potReflectRepositoryConfiguration.isSatisfied()) {		 
			reasonViewer = new TransposedReasonViewer( potReflectRepositoryConfiguration.whyUnsatisfied());
			reasonViewer.setUiSupport( DevrockPlugin.instance().uiSupport());
    		Composite reasonViewerComposite = reasonViewer.createControl(composite, "reasons for failure");
    		reasonViewerComposite.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true, 4, 6));    					
		}
		else {
			
			Composite dataComposite = new Composite(composite, SWT.NONE);
			dataComposite.setLayout(layout);
			dataComposite.setLayoutData(  new GridData( SWT.FILL, SWT.FILL, true, true, 4, 10));
			
			RepositoryReflection repositoryReflection = potReflectRepositoryConfiguration.get();
			RepositoryConfiguration repositoryConfiguration = repositoryReflection.getRepositoryConfiguration();
						

			// offline global
			BooleanEditor globallyOffline = new BooleanEditor();
			globallyOffline.setLabelToolTip("Whether the configuration is globally set to be offline");
			globallyOffline.setCheckToolTip("If checked, the configuration is offline. Otherwise, the state of the repositories is dynamically checked");
			Composite offlineComposite = globallyOffline.createControl( dataComposite, "globally offline");
			globallyOffline.setEnabled(false);
			globallyOffline.setSelection( repositoryConfiguration.getOffline());
			offlineComposite.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 4, 1));
			
			// origination (use text to show reason)
			Reason origination = repositoryConfiguration.getOrigination();						
			reasonViewer = new TransposedReasonViewer( origination);
    		Composite reasonViewerComposite = reasonViewer.createControl(dataComposite, "origin of configuration");
    		reasonViewerComposite.setLayoutData(  new GridData( SWT.FILL, SWT.FILL, true, true, 4, 4));
			
			
			repositoryViewer = new RepositoryViewer(repositoryConfiguration);
    		Composite repositoryViewerComposite = repositoryViewer.createControl(dataComposite, "active compiled configuration");
    		repositoryViewerComposite.setLayoutData(  new GridData( SWT.FILL, SWT.FILL, true, true, 4, 10));
				
		}
		composite.pack();
		return composite;
	}

	

	@Override
	public void dispose() {
		if (reasonViewer != null) {
			reasonViewer.dispose();				
		}
		if (repositoryViewer != null) {
			repositoryViewer.dispose();
		}
		if (bigFont != null) {
			bigFont.dispose();
		}
	}
	
	
}
