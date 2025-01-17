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
package com.braintribe.model.processing.platformsetup.transfer;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import com.braintribe.common.attribute.AttributeContext;
import com.braintribe.common.attribute.TypeSafeAttribute;
import com.braintribe.common.attribute.TypeSafeAttributeEntry;
import com.braintribe.model.asset.PlatformAsset;
import com.braintribe.model.asset.natures.ManipulationPriming;
import com.braintribe.model.generic.eval.EvalContext;
import com.braintribe.model.platformsetup.api.request.TransferAsset;
import com.braintribe.model.processing.accessrequest.api.AccessRequestContext;
import com.braintribe.model.processing.service.api.ServiceRequestContextAspect;
import com.braintribe.model.processing.service.api.ServiceRequestContextBuilder;
import com.braintribe.model.processing.service.api.ServiceRequestSummaryLogger;
import com.braintribe.model.processing.session.api.persistence.PersistenceGmSession;
import com.braintribe.model.service.api.ServiceRequest;

/**
 * @author christina.wilpernig
 */
public class InstallAssetRequestContext implements AccessRequestContext<TransferAsset> {

	@Override
	public String getRequestorAddress() {
		return null;
	}

	@Override
	public String getRequestorId() {
		return null;
	}

	@Override
	public String getRequestorSessionId() {
		return null;
	}

	@Override
	public String getRequestorUserName() {
		return null;
	}

	@Override
	public String getRequestedEndpoint() {
		return null;
	}

	@Override
	public boolean isAuthorized() {
		return false;
	}

	@Override
	public boolean isTrusted() {
		return false;
	}

	@Override
	public ServiceRequestSummaryLogger summaryLogger() {
		return null;
	}

	@Override
	public <T> EvalContext<T> eval(ServiceRequest evaluable) {
		return null;
	}

	@Override
	public PersistenceGmSession getSession() {
		return null;
	}

	@Override
	public PersistenceGmSession getSystemSession() {
		return null;
	}

	@Override
	public TransferAsset getRequest() {
		
		ManipulationPriming priming = ManipulationPriming.T.create();
		priming.setAccessId("cortex");
		
		PlatformAsset asset = PlatformAsset.T.create();
		asset.setGroupId("com.braintribe.test");
		asset.setName("TestAsset");
		asset.setVersion("1.0");
		asset.setResolvedRevision("0");
		
		asset.setNature(priming);
		
		TransferAsset install = TransferAsset.T.create();
		install.setAsset(asset);
		
		return install;
	}

	
	
	@Override
	public TransferAsset getSystemRequest() {
		return null;
	}

	@Override
	public TransferAsset getOriginalRequest() {
		return null;
	}

	@Override
	public void setAutoInducing(boolean autoInducing) {
		// noop
	}

	@Override
	public void notifyResponse(Object response) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ServiceRequestContextBuilder derive() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T, A extends ServiceRequestContextAspect<? super T>> T findAspect(Class<A> aspect) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void transferAttributes(Map<Class<? extends TypeSafeAttribute<?>>, Object> target) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public AttributeContext parent() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Stream<TypeSafeAttributeEntry> streamAttributes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <A extends TypeSafeAttribute<V>, V> Optional<V> findAttribute(Class<A> attribute) {
		// TODO Auto-generated method stub
		return null;
	}

}
