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
package com.braintribe.gwt.simplepropertypanel.client.validation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.braintribe.gwt.utils.client.FastSet;
import com.braintribe.model.meta.GmEntityType;
import com.braintribe.model.meta.GmEnumConstant;
import com.braintribe.model.meta.GmEnumType;
import com.braintribe.model.meta.GmProperty;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

public class SimplePropertyValidationPanel extends FlowPanel {

	private Set<String> tfReservedProperties = new FastSet(Arrays.asList("id", "globalid", "partition"));
	private Set<String> duplicateValues = new FastSet();
	private Set<String> valueCache = new FastSet();
	
	public SimplePropertyValidationPanel() {
		addStyleName("simplePropertyValidationPanel");
	}
	
	private void clearCaches(){
		clear();
		duplicateValues.clear();
		valueCache.clear();
	}
	
	private void addValue(String value){
		if(valueCache.contains(value))
			duplicateValues.add(value);
		valueCache.add(value);
	}

	public void validateEnumType(GmEnumType enumType) {
		setVisible(false);
		clearCaches();	
		if(enumType.getConstants() != null) {
			for(GmEnumConstant constant : enumType.getConstants())
				addValue(constant.getName());
		}
		checkDuplicates();
	}
	
	public void validateEntityType(GmEntityType entityType) {
		setVisible(false);
		clearCaches();
		for(GmProperty property : getAllProperties(entityType/*, false*/))
			addValue(property.getName());
		checkDuplicates();
	}
	
	private void checkDuplicates(){
		if(!duplicateValues.isEmpty()){
			final StringBuilder sb = new StringBuilder();
			
			duplicateValues.forEach((value) -> {
				sb.append(value + ", ");
			});
			
			add(new ValidationEntry("duplicated values : " + sb.toString()));
			setVisible(true);
		}			
	}
	
	class ValidationEntry extends FlowPanel{
		
		public ValidationEntry(String message) {
			addStyleName("simplePropertyValidationEntry");
			add(new Label(message));
		}
		
	}
	
	public List<GmProperty> getAllProperties(GmEntityType entityType/*, boolean isSuperType*/) {
		List<GmProperty> gmProperties = new ArrayList<>();
//		if(isSuperType)
//			gmProperties.addAll(entityType.getProperties());
//		else{
		if (entityType.getProperties() != null) {
			gmProperties.addAll(entityType.getProperties().stream().filter((property) -> {
				return !tfReservedProperties.contains(property.getName().toLowerCase());
			}).collect(Collectors.toList()));
		}
//		}
		if (entityType.getSuperTypes() != null) {
			for(GmEntityType superType : entityType.getSuperTypes())
				gmProperties.addAll(getAllProperties(superType/*, true*/));
		}
		
		return gmProperties;
	}
	
}
