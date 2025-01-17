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
package com.braintribe.devrock.api.ui.editors;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;

import com.braintribe.cfg.Configurable;
import com.braintribe.devrock.api.ui.properties.VirtualPropertyResolver;
import com.braintribe.devrock.api.ve.listeners.VirtualEnvironmentNotificationListener;


/**
 * scan for a directory 
 * 
 * @author pit
 *
 */
public class DirectoryEditor extends AbstractEditor implements ModifyListener, VirtualEnvironmentNotificationListener {
	
	private Shell shell;
	private String start;
	private Text text;
	private Button scanButton;
	private boolean startEnabled = true;
	private ModifyListener listener;
	private VirtualPropertyResolver propertyResolver;

	public DirectoryEditor( Shell shell) {
		this.shell = shell;			
	}
	
	@Configurable
	public void setSelection( File selection) {
		start = selection.getAbsolutePath();
		if (text != null) {
			text.setText( start);
		}
	}

	@Configurable
	public void setSelection( String selection) {
		start = selection;
		if (text != null) {
			text.setText( start);
		}
	}

	@Configurable
	public void setEnable( boolean enable){
		startEnabled = enable;
		if (text != null)
			text.setEnabled(enable);
		if (scanButton != null)
			scanButton.setEnabled(enable);
	}
	@Configurable
	public void setListener( ModifyListener listener) {
		this.listener = listener;
		if (text != null) {
			text.addModifyListener( this.listener);
		}
	}
	
	@Configurable
	public void setResolver( VirtualPropertyResolver resolver) {
		propertyResolver = resolver;
	}
	
	public String scanForDirectory(Shell shell) {
		DirectoryDialog dialog = new DirectoryDialog(shell);
		String directory = propertyResolver != null ? propertyResolver.resolve(text.getText()) : text.getText();
		if (directory != null) {
			dialog.setFilterPath( directory);
		}
		String name = dialog.open();
		if (name == null)
			return null;
		return name;
	}
	
	public Composite createControl( Composite parent, String tag) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 4;
		composite.setLayout(layout);
		
		Label label = new Label( composite, SWT.NONE);
		label.setText( tag);
		label.setLayoutData( new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
				
		text = new Text( composite, SWT.NONE);
		text.setLayoutData( new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		if (start != null) {
			text.setText( start);
		}
		text.setToolTipText( propertyResolver != null ? propertyResolver.resolve(start) : start);
		text.setEnabled(startEnabled);
		if (listener != null)
			text.addModifyListener(listener);

		text.addModifyListener( this);
		
		scanButton = new Button(composite, SWT.NONE);
		scanButton.setText("..");
		scanButton.setLayoutData( new GridData( SWT.LEFT, SWT.CENTER, false, false, 1,1));
		scanButton.addSelectionListener( new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				String value = scanForDirectory( shell);
				if (value != null) {
					text.setText( value);
					setSelection( new File( value));
				}
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {				
			}
		});		
		scanButton.setEnabled(startEnabled);
		return composite;
	}
	
	
	public String getSelection() {
		if (text != null)			
			return text.getText();
		else
			return start;
	}

	@Override
	public void modifyText(ModifyEvent event) {
		text.setToolTipText( propertyResolver != null ? propertyResolver.resolve( text.getText()) : text.getText());
		broadcast(text.getText());
	}

	@Override
	public void acknowledgeOverrideChange() {
		modifyText(null);
	}
	
	public Widget getWidget() {
		return text;
	}
	

}
