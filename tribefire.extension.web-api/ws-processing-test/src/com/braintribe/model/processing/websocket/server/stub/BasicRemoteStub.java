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
package com.braintribe.model.processing.websocket.server.stub;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.websocket.EncodeException;
import javax.websocket.RemoteEndpoint.Basic;

import com.braintribe.codec.marshaller.api.Marshaller;
import com.braintribe.model.service.api.ServiceRequest;

public class BasicRemoteStub implements Basic {

	private List<ServiceRequest> sentServiceRequests = new ArrayList<>();
	private boolean simulateError = false;
	private Marshaller marshaller;
	
	public BasicRemoteStub(Marshaller marshaller) {
		this.marshaller = marshaller;
	}
	
	public BasicRemoteStub(Marshaller marshaller, boolean simulateError) {
		this.marshaller = marshaller;
		this.simulateError = simulateError;
	}
	
	public List<ServiceRequest> getSentServiceRequests() {
		return sentServiceRequests;
	}

	public void setSentServiceRequests(List<ServiceRequest> sentServiceRequests) {
		this.sentServiceRequests = sentServiceRequests;
	}

	@Override
	public void setBatchingAllowed(boolean allowed) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean getBatchingAllowed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void flushBatch() throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sendPing(ByteBuffer applicationData) throws IOException, IllegalArgumentException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sendPong(ByteBuffer applicationData) throws IOException, IllegalArgumentException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sendText(String text) throws IOException {
		if(simulateError) {
			throw new IOException();
		}
		this.sentServiceRequests.add((ServiceRequest) marshaller.unmarshall(new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8))));
		
	}

	@Override
	public void sendBinary(ByteBuffer data) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sendText(String partialMessage, boolean isLast) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sendBinary(ByteBuffer partialByte, boolean isLast) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public OutputStream getSendStream() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Writer getSendWriter() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void sendObject(Object data) throws IOException, EncodeException {
		// TODO Auto-generated method stub
		
	}

}
