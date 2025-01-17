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
package com.braintribe.model.processing.core.commons.comparison;

import java.util.Comparator;
import java.util.function.Function;

import com.braintribe.model.generic.path.api.IModelPathElement;
import com.braintribe.model.generic.reflection.CollectionType;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.reflection.MapType;
import com.braintribe.model.processing.traversing.api.path.TraversingModelPathElement;

public class AssemblyComparison {

	private final EntityComparator entityComparator;
	private final ObjectComparator objectComparator;
	private final RootComparator rootComparator;
	
	private final EntityComparator internalEntityComparator;
	private final ObjectComparator internalObjectComparator;
	
	private final ComparableComparator comparableComparator;
	private final ComparableComparator internalComparableComparator;
	
	private final boolean trackingEnabled;
	private TraversingModelPathElement lastPathElement;
	private String mismatchDescription;
	
	public AssemblyComparison(boolean trackingEnabled, boolean useGlobalId) {
		this.trackingEnabled = trackingEnabled;
		entityComparator = new EntityComparator(this, false, useGlobalId);
		objectComparator = new ObjectComparator(this, false);
		rootComparator = new RootComparator(this);
		
		internalEntityComparator = new EntityComparator(this, true, useGlobalId);
		internalObjectComparator = new ObjectComparator(this, true);
		comparableComparator = new ComparableComparator(false, this);
		internalComparableComparator = new ComparableComparator(true, this);
	}
	
	boolean isTrackingEnabled() {
		return trackingEnabled;
	}
	
	public void setMismatchDescription(String mismatchDescription) {
		this.mismatchDescription = mismatchDescription;
	}

	void pushElement(Function<TraversingModelPathElement, TraversingModelPathElement> elementSupplier) {
		if (!trackingEnabled)
			return;
		
		lastPathElement  = elementSupplier.apply(lastPathElement);
	}
	
	
	void pushElement(TraversingModelPathElement element) {
		if (!trackingEnabled)
			return;
		
		lastPathElement = element;
	}
	
	TraversingModelPathElement peekElement() {
		return lastPathElement;
	}
	
	void popElement() {
		if (!trackingEnabled)
			return;
		
		if (lastPathElement != null) {
			lastPathElement = lastPathElement.getPrevious();
		}
		else {
			throw new IllegalStateException("Invalid stack pop. Stack is empty");
		}
	}
	
	public Comparator<Object> getInternalComparator(GenericModelType type) {
		switch (type.getTypeCode()) {
		case objectType:
			return internalObjectComparator;
			
		case stringType:
		case booleanType:
		case dateType:
		case decimalType:
		case doubleType:
		case enumType:
		case floatType:
		case integerType:
		case longType:
			return internalComparableComparator;
			
		case entityType:
				return (Comparator<Object>) (Comparator<?>) internalEntityComparator;
			
		default:
			throw new IllegalArgumentException("unsupported type for internal comparator: " + type);
		}
	}
	
	public static boolean equals(Object assembly1, Object assembly2) {
		return new AssemblyComparison(true, false).rootComparator.compare(assembly1, assembly2) == 0;
	}
	
	public static AssemblyComparisonBuilder build() {
		return new AssemblyComparisonBuilder() {
			private boolean useGlobalId;
			private boolean enableTracking;
			@Override
			public AssemblyComparisonBuilder useGlobalId() {
				useGlobalId = true;
				return this;
			}
			
			@Override
			public AssemblyComparisonBuilder enableTracking() {
				enableTracking = true;
				return this;
			}
			
			@Override
			public AssemblyComparisonResult compare(Object assembly1, Object assembly2) {
				AssemblyComparison assemblyComparison = new AssemblyComparison(enableTracking, useGlobalId);
				int res = assemblyComparison.rootComparator.compare(assembly1, assembly2);
				return new AssemblyComparisonResult() {
					
					@Override
					public IModelPathElement firstMismatchPath() {
						return assemblyComparison.lastPathElement;
					}
					
					@Override
					public boolean equal() {
						return res == 0;
					}
					
					@Override
					public String mismatchDescription() {
						return assemblyComparison.mismatchDescription;
					}
				};
			}
			
			@Override
			public Comparator<Object> comparator() {
				return new AssemblyComparison(enableTracking, useGlobalId).rootComparator;
			}
		};
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected Comparator<Object> getComparator(GenericModelType type) {
		switch (type.getTypeCode()) {
		case objectType:
			return objectComparator;
			
		case stringType:
		case booleanType:
		case dateType:
		case decimalType:
		case doubleType:
		case enumType:
		case floatType:
		case integerType:
		case longType:
			return comparableComparator;
			
		case entityType:
			return (Comparator)entityComparator;
					
		case listType:
			CollectionType listType = (CollectionType)type;
			GenericModelType listElementType = listType.getCollectionElementType();
			return new ListComparator(this, listElementType, getComparator(listElementType));
		case setType:
			CollectionType setType = (CollectionType)type;
			GenericModelType setElementType = setType.getCollectionElementType();
			return new SetComparator(this, setElementType, getComparator(setElementType), getInternalComparator(setElementType));
		case mapType:
			MapType mapType = (MapType)type;
			GenericModelType keyType = mapType.getKeyType();
			GenericModelType valueType = mapType.getValueType();
			return new MapComparator(this, keyType, valueType, getComparator(keyType), getComparator(valueType), getInternalComparator(keyType));
		default:
			throw new IllegalArgumentException("unsupported type " + type);
		}
	}
	
	
}

