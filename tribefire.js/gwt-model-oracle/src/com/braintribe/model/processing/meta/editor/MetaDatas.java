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
package com.braintribe.model.processing.meta.editor;

import java.util.function.Consumer;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.meta.data.MetaData;
import com.braintribe.model.meta.selector.MetaDataSelector;

/**
 * @author peter.gazdik
 */
public interface MetaDatas {

	static <M extends MetaData> M md(EntityType<M> mdType) {
		return mdType.create();
	}

	static <M extends MetaData> M md(EntityType<M> mdType, MetaDataSelector selector) {
		M result = mdType.create();
		result.setSelector(selector);
		return result;
	}
	
	static <M extends MetaData> M md(EntityType<M> mdType, Double priority) {
		M result = mdType.create();
		result.setConflictPriority(priority);
		return result;
	}
	
	static <M extends MetaData> M md(EntityType<M> mdType, Consumer<? super M> mdConsumer) {
		M result = mdType.create();
		mdConsumer.accept(result);
		return result;
	}

}
