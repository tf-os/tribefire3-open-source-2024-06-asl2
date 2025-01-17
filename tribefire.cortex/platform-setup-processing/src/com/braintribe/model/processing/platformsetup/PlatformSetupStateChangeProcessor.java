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
package com.braintribe.model.processing.platformsetup;

import java.util.Collections;
import java.util.List;

import com.braintribe.model.asset.PlatformAsset;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.processing.sp.api.AfterStateChangeContext;
import com.braintribe.model.processing.sp.api.StateChangeProcessor;
import com.braintribe.model.processing.sp.api.StateChangeProcessorException;
import com.braintribe.model.processing.sp.api.StateChangeProcessorMatch;
import com.braintribe.model.processing.sp.api.StateChangeProcessorRule;
import com.braintribe.model.processing.sp.api.StateChangeProcessorSelectorContext;

/**
 * TODO: Find out how to track deep-structure changes as well.
 */
public class PlatformSetupStateChangeProcessor implements StateChangeProcessor<PlatformAsset, GenericEntity>, StateChangeProcessorRule, StateChangeProcessorMatch {

	private static Property hasUnsavedChangesProperty = PlatformAsset.T.getProperty(PlatformAsset.hasUnsavedChanges);
	private static Property resolvedRevisionProperty = PlatformAsset.T.getProperty(PlatformAsset.resolvedRevision);
	
	@Override
	public String getRuleId() {
		return getClass().getName();
	}

	@Override
	public StateChangeProcessor<? extends GenericEntity, ?> getStateChangeProcessor(String processorId) {
		return this;
	}
	
	@Override
	public String getProcessorId() {
		return getRuleId();
	}
	
	@Override
	public StateChangeProcessor<?, ?> getStateChangeProcessor() {
		return this;
	}

	@Override
	public List<StateChangeProcessorMatch> matches(StateChangeProcessorSelectorContext context) {
		if (context.isForProperty() && context.getEntityType() == PlatformAsset.T && context.getProperty() != hasUnsavedChangesProperty
				&& context.getProperty() != resolvedRevisionProperty) {
			return Collections.singletonList(this);
		} else {
			return Collections.emptyList();
		}
	}

	
	@Override
	public void onAfterStateChange(AfterStateChangeContext<PlatformAsset> context, GenericEntity customContext) throws StateChangeProcessorException {
		context.getProcessEntity().setHasUnsavedChanges(true);
	}

}
