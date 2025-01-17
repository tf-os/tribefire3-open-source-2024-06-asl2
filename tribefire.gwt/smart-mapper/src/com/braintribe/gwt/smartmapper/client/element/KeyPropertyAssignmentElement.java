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
package com.braintribe.gwt.smartmapper.client.element;

import java.util.HashSet;
import java.util.Set;

import com.braintribe.gwt.smartmapper.client.PropertyAssignmentInput;
import com.braintribe.gwt.smartmapper.client.PropertyAssignmentPropertyInput;
import com.braintribe.gwt.smartmapper.client.PropertyAssignmentTypeInput;
import com.braintribe.gwt.smartmapper.client.experts.AbstractMappingElementsProvider;
import com.braintribe.gwt.smartmapper.client.experts.AbstractMappingElementsProvider.MappingElementKind;
import com.braintribe.gwt.smartmapper.client.experts.KeyPropertyElementsProvider;
import com.braintribe.gwt.smartmapper.client.experts.KeyPropertyElementsProvider.Mode;
import com.braintribe.gwt.smartmapper.client.util.TypeAndPropertyInfo;
import com.braintribe.model.meta.GmEntityType;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

public class KeyPropertyAssignmentElement extends PropertyAssignmentElement{
	
	protected boolean inverse = false;
	
	FlowPanel main;
	FlowPanel wrapper;
	PropertyAssignmentTypeInput propertyTypeInput;
	PropertyAssignmentPropertyInput propertyInput;
	PropertyAssignmentTypeInput keyPropertyTypeInput;
	PropertyAssignmentPropertyInput keyPropertyInput;
	
	KeyPropertyElementsProvider keyPropertyPropertiesProvider;
	KeyPropertyElementsProvider keyPropertyTypesProvider;
	KeyPropertyElementsProvider propertiesProvider;
	KeyPropertyElementsProvider typesProvider;
	
	public KeyPropertyAssignmentElement(boolean inverse) {
		this.inverse = inverse;
		
		keyPropertyPropertiesProvider = new KeyPropertyElementsProvider(MappingElementKind.properties, Mode.keyProperty, this.inverse);
		keyPropertyTypesProvider = new KeyPropertyElementsProvider(MappingElementKind.types, Mode.keyProperty, this.inverse);
		propertiesProvider = new KeyPropertyElementsProvider(MappingElementKind.properties, Mode.property, this.inverse);
		typesProvider = new KeyPropertyElementsProvider(MappingElementKind.types, Mode.property, this.inverse);
	}
	
	@Override
	public Widget getWidget() {
		return getMain();
	}
	
	@Override
	public void handleAdaption() {		
//		getKeyPropertyInput().getElement().setAttribute("placeholder", "entityType");
		
		GmEntityType keyPropertyEntityType = !inverse ? 
				TypeAndPropertyInfo.getMappedEntityTypeOfProperty(propertyAssignmentContext.smartMapper.cmdResolver, TypeAndPropertyInfo.getPropertyType(propertyAssignmentContext.parentProperty))
					: propertyAssignmentContext.mappedToEntityType;
		
		String keyPropertyEntityTypeName = keyPropertyEntityType != null ? getTypeName(keyPropertyEntityType.getTypeSignature()) : "???";
		
		getKeyPropertyTypeInput().getElement().setAttribute("placeholder", keyPropertyEntityTypeName);
		
//		getPropertyInput().getElement().setAttribute("placeholder", propertyEntityType);
		
		GmEntityType propertyEntityType = !inverse 
				? propertyAssignmentContext.mappedToEntityType 
						: TypeAndPropertyInfo.getMappedEntityTypeOfProperty(propertyAssignmentContext.smartMapper.cmdResolver, TypeAndPropertyInfo.getPropertyType(propertyAssignmentContext.parentProperty));
				
		String propertyEntityTypeName = propertyEntityType != null ? getTypeName(propertyEntityType.getTypeSignature()) : "???";
		
		getPropertyTypeInput().getElement().setAttribute("placeholder", propertyEntityTypeName);
	}
	
	@Override
	public void handleRender() {
		//NOP
	}
	
	@Override
	public void handleDisposal() {
		//NOP
	}
	
	@Override
	public void handleInitialisation() {
		//NOP
	}
	
	@Override
	public void validate() {
		//NOP
	}
	
	@Override
	public void handleNoticeManipulation() {
		//NOP
	}
	
	@Override
	public Set<PropertyAssignmentInput> getInputElements() {		
		if(inputElements == null){
			inputElements = new HashSet<>();
			inputElements.add(getPropertyTypeInput());
			inputElements.add(getPropertyInput());
			inputElements.add(getKeyPropertyTypeInput());
			inputElements.add(getKeyPropertyInput());
		}
		return inputElements;
	}
	
	@Override
	public Set<AbstractMappingElementsProvider> getTypesOrPropertiesProviders() {
		Set<AbstractMappingElementsProvider> providers = new HashSet<>();
		providers.add(keyPropertyPropertiesProvider);
		providers.add(keyPropertyTypesProvider);
		providers.add(propertiesProvider);
		providers.add(typesProvider);
		return providers;
	}
	
	public FlowPanel getMain() {
		if(main == null){
			main = new FlowPanel();
			main.addStyleName("keyPropertyAssignment");
			main.add(getWrapper());
		}
		return main;
	}
	
	public FlowPanel getWrapper() {
		if(wrapper == null){
			wrapper = new FlowPanel();
//			wrapper.add(PropertyAssignmentTypeInput.clear(getPropertyTypeInput()));
			wrapper.add(getPropertyTypeInput());
			wrapper.add(getSeparatorElement("."));
			wrapper.add(getPropertyInput());
//			wrapper.add(PropertyAssignmentTypeInput.clear(getPropertyInput()));
			wrapper.add(getSeparatorElement("="));
//			wrapper.add(PropertyAssignmentTypeInput.clear(getKeyPropertyTypeInput()));
			wrapper.add(getKeyPropertyTypeInput());
			wrapper.add(getSeparatorElement("."));
			wrapper.add(getKeyPropertyInput());
//			wrapper.add(PropertyAssignmentTypeInput.clear(getKeyPropertyInput()));
			wrapper.addStyleName("propertyAssignmentElementWrapper");
		}
		return wrapper;
	}
	
	public PropertyAssignmentTypeInput getPropertyTypeInput() {
		if(propertyTypeInput == null){
			propertyTypeInput = new PropertyAssignmentTypeInput();
			propertyTypeInput.setPropertyNameOfAssignment("property");
			propertyTypeInput.setTypesProvider(typesProvider);
			propertyTypeInput.getElement().setAttribute("placeholder", "???");
		}
		return propertyTypeInput;
	}
	
	public PropertyAssignmentPropertyInput getPropertyInput() {
		if(propertyInput == null){
			propertyInput = new PropertyAssignmentPropertyInput();
			propertyInput.setPropertyNameOfAssignment("property");
			propertyInput.setPropertiesProvider(propertiesProvider);
			propertyInput.getElement().setAttribute("placeholder", "property");
		}
		return propertyInput;
	}
	
	public PropertyAssignmentTypeInput getKeyPropertyTypeInput() {
		if(keyPropertyTypeInput == null){
			keyPropertyTypeInput = new PropertyAssignmentTypeInput();
			keyPropertyTypeInput.setPropertyNameOfAssignment("keyProperty");
			keyPropertyTypeInput.setTypesProvider(keyPropertyTypesProvider);
			keyPropertyTypeInput.getElement().setAttribute("placeholder", "???");
		}
		return keyPropertyTypeInput;
	}
	
	public PropertyAssignmentPropertyInput getKeyPropertyInput() {
		if(keyPropertyInput == null){
			keyPropertyInput = new PropertyAssignmentPropertyInput();
			keyPropertyInput.setPropertyNameOfAssignment("keyProperty");
			keyPropertyInput.setPropertiesProvider(keyPropertyPropertiesProvider);
			keyPropertyInput.getElement().setAttribute("placeholder", "keyProperty");
		}
		return keyPropertyInput;
	}

}
