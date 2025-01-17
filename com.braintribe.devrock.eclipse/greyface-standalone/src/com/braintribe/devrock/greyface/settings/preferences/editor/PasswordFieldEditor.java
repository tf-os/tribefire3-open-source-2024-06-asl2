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
package com.braintribe.devrock.greyface.settings.preferences.editor;

import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import com.braintribe.logging.Logger;

/**
 * an implementation of a password string editor that displays its values only as
 * masked text.
 * 
 * @author pit 
 *
 */
public class PasswordFieldEditor extends StringFieldEditor {

	private static Logger log = Logger.getLogger(PasswordFieldEditor.class);
		
	private Text textField;
	private int validateStrategy = VALIDATE_ON_KEY_STROKE;
	private int textLimit = UNLIMITED;

	public PasswordFieldEditor(String name, String labelText, Composite parent) {
		super(name, labelText, parent);	
	}

	public PasswordFieldEditor(String name, String labelText, int width, Composite parent) {
		super(name, labelText, width, parent);
		
	}

	public PasswordFieldEditor(String name, String labelText, int width, int strategy, Composite parent) {
		super(name, labelText, width, strategy, parent);
		
	}

	
	@Override
	public Text getTextControl(Composite parent) {
		if (textField == null) {
			textField = new Text(parent, SWT.SINGLE | SWT.BORDER | SWT.PASSWORD);
			textField.setFont(parent.getFont());
			switch (validateStrategy) {
				case VALIDATE_ON_KEY_STROKE:
					textField.addKeyListener(new KeyAdapter() {

						/*
						 * (non-Javadoc)
						 * 
						 * @see
						 * org.eclipse.swt.events.KeyAdapter#keyReleased(org
						 * .eclipse.swt.events.KeyEvent)
						 */
						@Override
						public void keyReleased(KeyEvent e) {
							valueChanged();
						}
					});

					break;
				case VALIDATE_ON_FOCUS_LOST:
					textField.addKeyListener(new KeyAdapter() {
						@Override
						public void keyPressed(KeyEvent e) {
							clearErrorMessage();
						}
					});
					textField.addFocusListener(new FocusAdapter() {
						@Override
						public void focusGained(FocusEvent e) {
							refreshValidState();
						}

						@Override
						public void focusLost(FocusEvent e) {
							valueChanged();
							clearErrorMessage();
						}
					});
					break;
				default:
					log.warn("Unknown validation strategy [" + validateStrategy + "]");
					break;
			}
			textField.addDisposeListener(new DisposeListener() {
				@Override
				public void widgetDisposed(DisposeEvent event) {
					textField = null;
				}
			});
			if (textLimit > 0) {// Only set limits above 0 - see SWT spec
				textField.setTextLimit(textLimit);
			}
		} else {
			checkParent(textField, parent);
		}
		return textField;
	}

}
