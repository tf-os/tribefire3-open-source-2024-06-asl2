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
package tribefire.extension.xml.schemed.test.processing.basics;



import java.io.File;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import tribefire.extension.xml.schemed.test.processing.AbstractTest;


public class BasicTest extends AbstractTest {
	private File basic = new File( contents, "basic");
	private File input = new File( basic, "input");
	private File output = new File( basic, "output");
	
	@Before
	public void before() {
		before( output);
	}
	
	@Test
	public void test() {
		
		try {
			runTest( input, output, "basic.xsd", "com.braintribe.xml", "com.braintribe.xml.test:basic#1.0");
		} catch (Exception e) {
			Assert.fail("exception [" + e + "] thrown");
		}
	}

}
