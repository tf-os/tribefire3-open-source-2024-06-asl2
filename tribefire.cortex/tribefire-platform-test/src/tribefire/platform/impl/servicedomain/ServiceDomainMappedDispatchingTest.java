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
package tribefire.platform.impl.servicedomain;

import static com.braintribe.testing.junit.assertions.assertj.core.api.Assertions.assertThat;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.braintribe.model.generic.eval.Evaluator;
import com.braintribe.model.processing.deployment.api.DeploymentException;
import com.braintribe.model.service.api.ExecuteInDomain;
import com.braintribe.model.service.api.ServiceRequest;
import com.braintribe.wire.api.Wire;
import com.braintribe.wire.api.context.WireContext;

import tribefire.cortex.model.sdmt.DomainAwareRequest;
import tribefire.cortex.model.sdmt.TestRequest1;
import tribefire.cortex.model.sdmt.TestRequest2;
import tribefire.cortex.model.sdmt.TestRequest3;
import tribefire.platform.impl.servicedomain.wire.ServiceDomainMappedDispatchingTestWireModule;
import tribefire.platform.impl.servicedomain.wire.contract.ServiceDomainMappedDispatchingTestContract;

public class ServiceDomainMappedDispatchingTest implements ServiceDomainMappedDispatchingTestCommons {
	protected static WireContext<ServiceDomainMappedDispatchingTestContract> context;
	protected static Evaluator<ServiceRequest> evaluator;
	protected static ServiceDomainMappedDispatchingTestContract testContract;
	
	@BeforeClass
	public static void beforeClass() {
		context = Wire.context(ServiceDomainMappedDispatchingTestWireModule.INSTANCE);
		testContract = context.contract();
		evaluator = testContract.evaluator();
	}
	
	@AfterClass
	public static void afterClass() {
		context.shutdown();
	}
	
	@Test
	public void testProcessWithMapping() {
		TestRequest1 testRequest1 = TestRequest1.T.create();
		testRequest1.setDomainId(SERVICE_DOMAIN1);
		String value1 = testRequest1.eval(evaluator).get();
		
		TestRequest2 testRequest2 = TestRequest2.T.create();
		testRequest2.setDomainId(SERVICE_DOMAIN1);
		String value2 = testRequest2.eval(evaluator).get();

		TestRequest3 testRequest3 = TestRequest3.T.create();
		testRequest3.setDomainId(SERVICE_DOMAIN1);
		String value3 = testRequest3.eval(evaluator).get();
		
		assertThat(value1).isEqualTo(RETVAL_SERVICE_PROCESSOR1);
		assertThat(value2).isEqualTo(RETVAL_SERVICE_PROCESSOR2);
		assertThat(value3).isEqualTo(RETVAL_HARDWIRED_SERVICE_PROCESSOR);
	}
	
	@Test
	public void testPreProcessWithMapping() {
		TestRequest1 testRequest1 = TestRequest1.T.create();
		testRequest1.setDomainId(SERVICE_DOMAIN2);
		String value1 = testRequest1.eval(evaluator).get();

		TestRequest3 testRequest3 = TestRequest3.T.create();
		testRequest3.setDomainId(SERVICE_DOMAIN2);
		String value3 = testRequest3.eval(evaluator).get();
		
		assertThat(value1).isEqualTo(RETVAL_SERVICE_PROCESSOR1_ALT);
		assertThat(value3).isEqualTo(RETVAL_HARDWIRED_SERVICE_PROCESSOR_ALT);
	}
	
	@Test
	public void testPostProcessWithMapping() {
		TestRequest1 testRequest1 = TestRequest1.T.create();
		testRequest1.setDomainId(SERVICE_DOMAIN3);
		String value1 = testRequest1.eval(evaluator).get();
		
		TestRequest3 testRequest3 = TestRequest3.T.create();
		testRequest3.setDomainId(SERVICE_DOMAIN3);
		String value3 = testRequest3.eval(evaluator).get();
		
		assertThat(value1).isEqualTo(RETVAL_SERVICE_PROCESSOR1.toUpperCase());
		assertThat(value3).isEqualTo(RETVAL_HARDWIRED_SERVICE_PROCESSOR.toUpperCase());
	}
	
	@Test
	public void testAroundProcessWithMapping() {
		TestRequest1 testRequest1 = TestRequest1.T.create();
		testRequest1.setDomainId(SERVICE_DOMAIN4);
		String value1 = testRequest1.eval(evaluator).get();
		
		assertThat(value1).isEqualTo(RETVAL_SERVICE_PROCESSOR1_ALT.toUpperCase());
	}
	
	@Test(expected=DeploymentException.class)
	public void testProcessWithMappingToUndeployedProcessor() {
		TestRequest2 testRequest2 = TestRequest2.T.create();
		testRequest2.setDomainId(SERVICE_DOMAIN2);
		testRequest2.eval(evaluator).get();
	}
	
	@Test
	public void testExecuteInDomain() {
		DomainAwareRequest domainAwareRequest = DomainAwareRequest.T.create();

		ExecuteInDomain executeInDomain = ExecuteInDomain.T.create();
		executeInDomain.setServiceRequest(domainAwareRequest);
		executeInDomain.setDomainId(SERVICE_DOMAIN1);
		
		String value1 = (String)executeInDomain.eval(evaluator).get();
		
		executeInDomain.setDomainId(SERVICE_DOMAIN2);
		
		String value2 = (String)executeInDomain.eval(evaluator).get();
		
		assertThat(value1).isEqualTo(RETVAL_PREFIX_DOMAIN_AWARE_PROCESSOR + SERVICE_DOMAIN1);
		assertThat(value2).isEqualTo(RETVAL_PREFIX_DOMAIN_AWARE_PROCESSOR + SERVICE_DOMAIN2);
	}
}
