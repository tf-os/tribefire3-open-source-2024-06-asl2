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
package tribefire.extension.scheduling.test;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import tribefire.extension.scheduling.test.wire.SchedulingTestWireModule;
import tribefire.extension.scheduling.test.wire.contract.SchedulingTestContract;
import com.braintribe.model.generic.eval.Evaluator;
import com.braintribe.model.service.api.ServiceRequest;
import com.braintribe.wire.api.Wire;
import com.braintribe.wire.api.context.WireContext;


public abstract class SchedulingTestBase {
	
	protected static WireContext<SchedulingTestContract> context;
	protected static Evaluator<ServiceRequest> evaluator;
	protected static SchedulingTestContract testContract;
	
	@BeforeClass
	public static void beforeClass() {
		context = Wire.context(SchedulingTestWireModule.INSTANCE);
		testContract = context.contract();
		evaluator = testContract.evaluator();
	}
	
	@AfterClass
	public static void afterClass() {
		context.shutdown();
	}

}
