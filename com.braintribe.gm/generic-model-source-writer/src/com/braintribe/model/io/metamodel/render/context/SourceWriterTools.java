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
package com.braintribe.model.io.metamodel.render.context;

import java.util.Collection;
import java.util.stream.Collectors;

import com.braintribe.model.generic.annotation.GlobalId;
import com.braintribe.model.meta.GmModelElement;
import com.braintribe.model.meta.GmModels;

/**
 * @author peter.gazdik
 */
public class SourceWriterTools {

	public static boolean elmentNeedsGlobalId(GmModelElement element) {
		String globalId = element.getGlobalId();
		return globalId != null && !globalId.equals(GmModels.naturalGlobalId(element));
	}

	public static String join(Collection<String> strings, String separator) {
		return strings.stream() //
				.collect(Collectors.joining(separator));
	}

	// used from within context builders only
	public static String getGlobalIdAnnotationSource(String globalId) {
		return GlobalId.class.getSimpleName() + "(\"" + globalId + "\")";
	}

}