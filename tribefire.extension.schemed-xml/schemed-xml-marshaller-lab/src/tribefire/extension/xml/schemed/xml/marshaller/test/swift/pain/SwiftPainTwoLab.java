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
package tribefire.extension.xml.schemed.xml.marshaller.test.swift.pain;

import java.io.File;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.utils.IOTools;

import tribefire.extension.xml.schemed.model.api.xml.marshaller.api.model.SchemedXmlMarshallerMarshallRequest;
import tribefire.extension.xml.schemed.model.api.xml.marshaller.api.model.SchemedXmlMarshallerMarshallResponse;
import tribefire.extension.xml.schemed.model.api.xml.marshaller.api.model.SchemedXmlMarshallerUnmarshallRequest;
import tribefire.extension.xml.schemed.model.api.xml.marshaller.api.model.SchemedXmlMarshallerUnmarshallResponse;
import tribefire.extension.xml.schemed.test.commons.xsd.test.util.TestUtil;
import tribefire.extension.xml.schemed.xml.marshaller.test.AbstractXmlMarshallerLab;

public class SwiftPainTwoLab extends AbstractXmlMarshallerLab {
	private static final String TEST_XML = "kleiderfirma_pain.001.001.02.xml";
	private static final String TEST_XSD = "pain.001.001.02.ISO.xsd";
	private static final String MAPPING_MODEL_XML = "com.braintribe.xsd.swift.PainTwoFlattenedModel-mapping.model.xml";
	private File contents = new File(res, "swift");
	private File simple = new File( contents, "pain/flat");
	private File input = new File( simple, "input");
	private File output = new File( simple, "output");
	
	@Before
	public void before() {
		TestUtil.ensure(output);
	}
	
	
	public void test(String xml) {
		try {
			SchemedXmlMarshallerUnmarshallRequest umRequest = buildRequest(input, xml, MAPPING_MODEL_XML);
			SchemedXmlMarshallerUnmarshallResponse umResponse = process( umRequest);
			GenericEntity result = umResponse.getAssembly();
			System.out.println(result);
			
			SchemedXmlMarshallerMarshallRequest mRequest = buildRequest(input, result, MAPPING_MODEL_XML);
			SchemedXmlMarshallerMarshallResponse mResponse = process( mRequest);

			File xmlOutputFile = new File( output, xml);
			IOTools.spit( xmlOutputFile, mResponse.getExpression(), "UTF-8", false);
			
			File xsdInputFile = new File( input, TEST_XSD);
			
			boolean validationResult = validate(xsdInputFile, xmlOutputFile);
			Assert.assertTrue( "output doesn't validate", validationResult);;
			
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail( "Exception [" + e.getMessage() + "] thrown");
		}
	}
	
	@Test
	public void test1() {
			test( TEST_XML);
	}
	


}
