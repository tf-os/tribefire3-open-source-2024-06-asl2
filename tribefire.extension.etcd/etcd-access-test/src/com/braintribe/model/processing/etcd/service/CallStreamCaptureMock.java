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
package com.braintribe.model.processing.etcd.service;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.CloningContext;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.generic.reflection.PropertyValueReceiver;
import com.braintribe.model.generic.reflection.TraversingContext;
import com.braintribe.model.generic.session.GmSession;
import com.braintribe.model.generic.session.OutputStreamProvider;
import com.braintribe.model.generic.value.EntityReference;
import com.braintribe.model.generic.value.ValueDescriptor;
import com.braintribe.model.resource.CallStreamCapture;

public class CallStreamCaptureMock implements CallStreamCapture {

	protected ByteArrayOutputStream baos;
	
	@Override
	public OutputStream openStream() {
		baos = new ByteArrayOutputStream();
		return baos;
	}
	
	public byte[] getData() {
		return baos.toByteArray();
	}
	
	@Override
	public <T> T getId() {
		return null;
	}

	@Override
	public void setId(Object id) {
		//Intentionally left empty
	}

	@Override
	public String getPartition() {
		return null;
	}

	@Override
	public void setPartition(String partition) {
		//Intentionally left empty
	}

	@Override
	public String getGlobalId() {
		return null;
	}

	@Override
	public void setGlobalId(String globalId) {
		//Intentionally left empty
	}

	@Override
	public void write(Property p, Object value) {
		//Intentionally left empty
	}

	@Override
	public Object read(Property p) {
		return null;
	}

	@Override
	public void writeVd(Property p, ValueDescriptor value) {
		//Intentionally left empty
	}

	@Override
	public ValueDescriptor readVd(Property p) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void read(Property p, PropertyValueReceiver pvr) {
		//Intentionally left empty
	}

	@Override
	public String toSelectiveInformation() {
		return null;
	}

	@Override
	public GenericModelType type() {
		return null;
	}

	@Override
	public <T extends GenericEntity> EntityType<T> entityType() {
		return null;
	}

	@Override
	public <T extends EntityReference> T reference() {
		return null;
	}

	@Override
	public <T extends EntityReference> T globalReference() {
		return null;
	}

	@Override
	public boolean isEnhanced() {
		return false;
	}

	@Override
	public boolean isVd() {
		return false;
	}

	@Override
	public long runtimeId() {
		return 0;
	}

	@Override
	public GmSession session() {
		return null;
	}

	@Override
	public void attach(GmSession session) {
		//Intentionally left empty
	}

	@Override
	public GmSession detach() {
		return null;
	}

	@Override
	public <T> T clone(CloningContext cloningContext) {
		return null;
	}

	@Override
	public void traverse(TraversingContext traversingContext) {		
		//Intentionally left empty
	}

	@Override
	public OutputStreamProvider getOutputStreamProvider() {
		return null;
	}

	@Override
	public void setOutputStreamProvider(OutputStreamProvider outputStreamProvider) {
		//Intentionally left empty
	}

}
