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

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.graphics.Image;

import com.braintribe.model.malaclypse.cfg.preferences.ve.EnvironmentOverride;

public class ActiveColumnLabelProvider extends ColumnLabelProvider {
	
	private Image activeImage;
	private Image inactiveImage;
	
	public ActiveColumnLabelProvider() {
		ImageDescriptor imageDescriptor = ImageDescriptor.createFromFile(ActiveColumnLabelProvider.class, "task-active.gif");
		activeImage = imageDescriptor.createImage();
		
		imageDescriptor = ImageDescriptor.createFromFile(ActiveColumnLabelProvider.class, "task-inactive.gif");
		inactiveImage = imageDescriptor.createImage();		
	}

	@Override
	public Image getImage(Object element) {
		EnvironmentOverride override = (EnvironmentOverride) element;		
		return override.getActive() ? activeImage : inactiveImage;
	}

	@Override
	public String getText(Object element) {
		EnvironmentOverride override = (EnvironmentOverride) element;		
		return override.getActive() ? "active" : "inactive"; 
	}

	@Override
	public void dispose() {	
		activeImage.dispose();
		inactiveImage.dispose();
		super.dispose();
	}

}