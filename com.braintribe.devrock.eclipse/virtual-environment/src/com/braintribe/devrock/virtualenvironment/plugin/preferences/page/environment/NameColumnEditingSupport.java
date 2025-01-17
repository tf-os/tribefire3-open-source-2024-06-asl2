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
package com.braintribe.devrock.virtualenvironment.plugin.preferences.page.environment;

import org.eclipse.jface.viewers.TableViewer;

import com.braintribe.model.malaclypse.cfg.preferences.ve.EnvironmentOverride;

public class NameColumnEditingSupport extends AbstractOverrideColumnEditingSupport {

	public NameColumnEditingSupport(TableViewer viewer) {
		super(viewer);	
	}
	
	@Override
	protected Object getValue(Object element) {
		EnvironmentOverride override = (EnvironmentOverride) element;
		return override.getName();
	}

	@Override
	protected void setValue(Object element, Object value) {
		EnvironmentOverride override = (EnvironmentOverride) element;
		override.setName( (String) value);
		refresh(element);
	}

}
