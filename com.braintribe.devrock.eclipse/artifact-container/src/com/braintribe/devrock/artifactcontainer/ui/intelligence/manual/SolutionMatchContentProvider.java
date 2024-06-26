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
package com.braintribe.devrock.artifactcontainer.ui.intelligence.manual;

import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.braintribe.model.artifact.Solution;

public class SolutionMatchContentProvider implements IStructuredContentProvider {

	private List<Solution> results = null;

	
	public List<Solution> getResults() {
		return results;
	}

	public void setResults(List<Solution> pairings) {
		this.results = pairings;
	}

	@Override
	public void dispose() {
	}

	@Override
	public void inputChanged(Viewer arg0, Object arg1, Object arg2) {	
	}

	@Override
	public Object[] getElements(Object arg0) {
		return results.toArray();
		//return results.stream().map( s -> NameParser.buildName(s)).collect(Collectors.toList()).toArray( new String[0]);		
	}


}