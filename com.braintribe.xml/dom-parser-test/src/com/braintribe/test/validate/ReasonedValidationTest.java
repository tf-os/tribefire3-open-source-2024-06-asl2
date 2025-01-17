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
package com.braintribe.test.validate;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Assert;
import org.junit.Test;

import com.braintribe.utils.xml.parser.DomParser;
import com.braintribe.utils.xml.parser.DomParserException;

public class ReasonedValidationTest {
	private static File contents = new File("res/maven");
	
	private static File xsd = new File( contents, "maven-4.0.0.xsd");
	
	
	
	private boolean test(File pomFile) {
	
		// validate against schema (for some reason, validating while loading wants a DTD?) 
		try (
				InputStream pomStream = new FileInputStream( pomFile);
				InputStream schemaStream = new FileInputStream( xsd); 
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				) {
			
			boolean isValid = DomParser.validate().from( pomStream).schema(schemaStream).makeItSo( out);			
			String msg = new String( out.toByteArray());
			
			if (!isValid) {
				System.out.println( "validation message : " + msg);
				return false;
			}
			return true;
			
		} catch (FileNotFoundException e) {
			Assert.fail( e.getMessage());
			return false;
		} catch (IOException e) {			
			Assert.fail( e.getMessage());
			return false;		
		} catch (DomParserException e) {
			Assert.fail( e.getMessage());
			return false;
		}	
	}
	
	@Test
	public void testSuccess() {
		boolean retval = test( new File( contents, "valid.pom.xml"));
		Assert.assertTrue( "expected test to succeed, but it failed", retval);
		
	}
	@Test
	public void testFail() {
		boolean retval = test( new File( contents, "invalid.pom.xml"));
		Assert.assertTrue( "expected test to fail, but it succeeded", !retval);
	}
}
