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
package com.braintribe.gwt.gme.tetherbar.client;

import java.util.function.Supplier;

import com.braintribe.gwt.gmview.client.GmContentView;
import com.braintribe.model.generic.path.ModelPath;
import com.braintribe.model.generic.path.ModelPathElement;


/**
 * Model containing info on each element in the TetherBar.
 * @author michel.docouto
 *
 */
public class TetherBarElement {
	
	private String name;
	private String description;
	private ModelPath modelPath;
	private Supplier<? extends GmContentView> contentViewProvider;
	private GmContentView contentView;
	
	public TetherBarElement(ModelPath modelPath, String name, String description, Supplier<? extends GmContentView> contentViewProvider) {
		setName(name);
		setDescription(description);
		setModelPath(modelPath);
		setContentViewProvider(contentViewProvider);
	}
	
	public TetherBarElement(ModelPath modelPath, String name, String description, GmContentView contentView) {
		setName(name);
		setDescription(description);
		setModelPath(modelPath);
		this.contentView = contentView;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public ModelPathElement getModelPathElement() {
		return modelPath == null ? null : modelPath.last();
	}
	
	public ModelPath getModelPath() {
		return modelPath;
	}
	
	public void setModelPath(ModelPath modelPath) {
		this.modelPath = modelPath;
	}
	
	public GmContentView getContentView() throws RuntimeException {
		if (contentView == null) {
			contentView = contentViewProvider.get();
		}
		return contentView;
	}
	
	public GmContentView getContentViewIfProvided() {
		return contentView;
	}
	
	public void setContentViewProvider(Supplier<? extends GmContentView> contentViewProvider) {
		this.contentViewProvider = contentViewProvider;
	}

}
