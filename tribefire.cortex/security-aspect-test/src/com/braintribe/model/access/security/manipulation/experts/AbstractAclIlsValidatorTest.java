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
package com.braintribe.model.access.security.manipulation.experts;

import static com.braintribe.utils.lcd.CollectionTools2.asList;
import static com.braintribe.utils.lcd.SetTools.asSet;

import java.util.Set;

import org.junit.Before;

import com.braintribe.model.access.security.manipulation.ValidatorTestBase;
import com.braintribe.model.access.security.testdata.acl.AclFactory;
import com.braintribe.model.acl.AclOperation;
import com.braintribe.model.processing.security.manipulation.ManipulationSecurityExpert;

public abstract class AbstractAclIlsValidatorTest extends ValidatorTestBase {

	protected AclFactory acls;

	@Override
	@Before
	public void setUp() {
		super.setUp();

		acls = new AclFactory(session);
		acls.setAssignedAclOps(asList(AclOperation.WRITE));
	}

	@Override
	protected final Set<? extends ManipulationSecurityExpert> manipulationSecurityExperts() {
		return asSet(new AclManipulationSecurityExpert());
	}

}
