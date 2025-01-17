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
package com.braintribe.gwt.metadataeditor.client.view;

import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

import com.braintribe.gwt.async.client.Future;
import com.braintribe.gwt.gmview.client.GmSessionHandler;
import com.braintribe.gwt.gmview.client.InstanceSelectionData;
import com.braintribe.gwt.gmview.client.SelectionConfig;
import com.braintribe.gwt.gmview.client.UseCaseHandler;
import com.braintribe.gwt.metadataeditor.client.MetaDataEditorPanel;
import com.braintribe.gwt.metadataeditor.client.experts.MetaDataEditorBaseExpert;
import com.braintribe.model.generic.path.ModelPath;
import com.braintribe.model.generic.path.ModelPathElement;
import com.braintribe.model.processing.session.api.persistence.PersistenceGmSession;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.IsWidget;
import com.sencha.gxt.widget.core.client.event.RowClickEvent.RowClickHandler;
import com.sencha.gxt.widget.core.client.menu.Menu;

public interface MetaDataEditorProvider extends IsWidget, GmSessionHandler, UseCaseHandler {

	String getCaption();

	void setFilter(Set<String> set);
	
	public void setCaption(String caption);
	
	public Boolean isActionManipulationAllowed();
	
	public void setContextMenu(Menu menu);
	
	public ModelPath getFirstSelectedItem();
	
	public ModelPath getExtendedSelectedItem();
	
	public boolean isSelected(Object element);
	
	public boolean isSelectionActive();

	public HandlerRegistration addRowClickHandler(RowClickHandler handler);

	//void setContent(ModelPathElement pathElement, ModelPath rootModelPath);
	void setContent(ModelPath rootModelPath);
	
	public boolean getEditorVisible (ModelPathElement pathElement);
	
	public boolean getUseSessionResolver();	
	
	public MetaDataEditorBaseExpert getModelExpert();
	
	public Set<ModelPath> getContent();
	
	public void setNeedUpdate();
	
	public void doRefresh();
	
	public void setMetaDataEditorPanel(MetaDataEditorPanel panel);
	
	public MetaDataEditorPanel getMetaDataEditorPanel();
	
	public void applySearchFilter(String searchText, Boolean useDeclaredTypesOnly);
	
	public String getSearchDeclaredText();

	public Boolean isSearchMode();

	void setMetaDataResolverProvider(MetaDataResolverProvider metaDataResolverProvider);
	
	public void configureWorkbenchSession(PersistenceGmSession gmSession);	
	
	public void setSelectionFutureProvider(Supplier<? extends Function<SelectionConfig, ? extends Future<InstanceSelectionData>>> selectionFutureProvider);
	
	public void setSpecialFlowClasses(Set<Class<?>> specialFlowClasses);	
}
