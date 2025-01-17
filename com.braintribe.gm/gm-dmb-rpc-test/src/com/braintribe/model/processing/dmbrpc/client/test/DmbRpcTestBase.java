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
package com.braintribe.model.processing.dmbrpc.client.test;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.experimental.categories.Category;

import com.braintribe.model.processing.rpc.test.RpcTestBase;
import com.braintribe.model.processing.rpc.test.wire.contract.DmbRpcTestContract;
import com.braintribe.model.processing.rpc.test.wire.contract.RpcTestContract;
import com.braintribe.testing.category.KnownIssue;
import com.braintribe.wire.api.context.WireContext;

/**
 * {@link RpcTestBase} for DMB RPC tests.
 * 
 */
@Category(KnownIssue.class)
public abstract class DmbRpcTestBase extends RpcTestBase {

	protected static WireContext<DmbRpcTestContract> context;

	// ============================= //
	// ======== LIFE CYCLE ========= //
	// ============================= //

	@BeforeClass
	public static void initialize() throws Exception {
		context = DmbRpcTestContract.context();
	}

	@AfterClass
	public static void destroy() throws Exception {
		if (context != null) {
			context.shutdown();
		}
	}

	// ============================= //
	// =========== BEANS =========== //
	// ============================= //

	@Override
	public RpcTestContract rpcTestBeans() {
		return context.contract();
	}

}
