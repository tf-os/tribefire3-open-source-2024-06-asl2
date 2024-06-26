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
package com.braintribe.devrock.artifactcontainer.plugin.preferences.project;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;

import com.braintribe.model.malaclypse.cfg.preferences.svn.SourceRepositoryKind;
import com.braintribe.model.malaclypse.cfg.preferences.svn.SourceRepositoryPairing;

public class RepositoryKindColumnEditingSupport extends EditingSupport {
	
	private TableViewer viewer;

	public RepositoryKindColumnEditingSupport(TableViewer viewer) {
		super(viewer);
		this.viewer = viewer;
	}

	@Override
	protected boolean canEdit(Object arg0) {		
		return true;
	}

	@Override
	protected CellEditor getCellEditor(Object arg0) {
		return new CheckboxCellEditor( viewer.getTable());
	}

	@Override
	protected Object getValue(Object element) {
		SourceRepositoryPairing pairing = (SourceRepositoryPairing) element;	
		return pairing.getRemoteRepresentationKind();
	}

	@Override
	protected void setValue(Object element, Object value) {
		SourceRepositoryPairing pairing = (SourceRepositoryPairing) element;
		pairing.setRemoteRepresentationKind( (SourceRepositoryKind) value);
		viewer.refresh(element);

	}

}