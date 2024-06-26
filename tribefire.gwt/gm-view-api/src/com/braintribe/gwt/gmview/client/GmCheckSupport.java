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
package com.braintribe.gwt.gmview.client;

import java.util.List;

import com.braintribe.gwt.gmview.client.js.interop.InteropConstants;
import com.braintribe.model.generic.path.ModelPath;

import jsinterop.annotations.JsType;

@SuppressWarnings("unusable-by-js")
@JsType(namespace = InteropConstants.VIEW_NAMESPACE)
public interface GmCheckSupport {
	
	public void addCheckListener(GmCheckListener cl);

    public void removeCheckListener(GmCheckListener cl);

    public ModelPath getFirstCheckedItem();

    public List<ModelPath> getCurrentCheckedItems();

    public boolean isChecked(Object element);
    
    public boolean uncheckAll();
    
}