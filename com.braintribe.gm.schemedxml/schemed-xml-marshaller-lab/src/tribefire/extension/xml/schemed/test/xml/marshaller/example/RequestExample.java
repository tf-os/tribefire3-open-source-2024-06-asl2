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
package tribefire.extension.xml.schemed.test.xml.marshaller.example;

import java.io.File;

import com.braintribe.codec.marshaller.stax.StaxMarshaller;
import com.braintribe.model.generic.GenericEntity;

import tribefire.extension.xml.schemed.marshaller.xml.SchemedXmlMarshallingRequestProcessor;
import tribefire.extension.xml.schemed.model.api.xml.marshaller.api.model.SchemedXmlMarshallerMarshallRequest;
import tribefire.extension.xml.schemed.model.api.xml.marshaller.api.model.SchemedXmlMarshallerMarshallResponse;
import tribefire.extension.xml.schemed.model.api.xml.marshaller.api.model.SchemedXmlMarshallerUnmarshallRequest;
import tribefire.extension.xml.schemed.model.api.xml.marshaller.api.model.SchemedXmlMarshallerUnmarshallResponse;
import tribefire.extension.xml.schemed.test.commons.commons.SchemedXmlXsdMarshallerRequestBuilder;
import tribefire.extension.xml.schemed.test.commons.xsd.test.resource.ResourceProvidingSession;

/**
 * a NON FUNCTIONAL example of how to use the marshaller (it actually needs the JAR of types of the mapping model in the classpath!)
 * 
 * @author pit
 *
 */
public class RequestExample {
	private SchemedXmlXsdMarshallerRequestBuilder requestBuilder;
	private SchemedXmlMarshallingRequestProcessor marshallingProcessor = new SchemedXmlMarshallingRequestProcessor();
	private StaxMarshaller staxMarshaller = new StaxMarshaller();

	
	public RequestExample() {
		ResourceProvidingSession session = new ResourceProvidingSession();
		requestBuilder = new SchemedXmlXsdMarshallerRequestBuilder();
		requestBuilder.setSession(session);		
		requestBuilder.setModelMarshaller( staxMarshaller);
	}
	
	/**
	 * using the request processor, i.e. how the service works
	 * @param args
	 */
	public void runRequestExample( String [] args) {
		String inputDirectory = args[0];
		String xmlName = args[1];
		String modelName = args[2];
		
		File input = new File( inputDirectory);
		
		// unmarshall, aka decode
		SchemedXmlMarshallerUnmarshallRequest unmarshallRequest = requestBuilder.buildRequest(input, xmlName, modelName);		
		SchemedXmlMarshallerUnmarshallResponse unmarshallResponse = marshallingProcessor.process(unmarshallRequest);
		
		// get result
		GenericEntity assembly = unmarshallResponse.getAssembly();
		System.out.println( assembly.getGlobalId());
		
		// marshall, aka encode
		SchemedXmlMarshallerMarshallRequest marshallRequest = requestBuilder.buildRequest(input, assembly, modelName);
		SchemedXmlMarshallerMarshallResponse marshallResponse = marshallingProcessor.process(marshallRequest);
		
		// get result
		String xmlContent = marshallResponse.getExpression();		
		System.out.println( xmlContent);
	}
	
	

	
	public static void main( String [] args) {
		RequestExample example = new RequestExample();
		
		example.runRequestExample(args);
		
	}
	
}
