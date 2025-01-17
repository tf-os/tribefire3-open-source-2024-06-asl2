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
package com.braintribe.model.processing.resource.server.test.wire.contract;

import java.util.List;

import javax.servlet.Filter;

import com.braintribe.codec.marshaller.api.ConfigurableMarshallerRegistry;
import com.braintribe.model.processing.resource.server.WebStreamingServer;
import com.braintribe.model.processing.resource.server.test.commons.TestAuthenticatingUserSessionProvider;
import com.braintribe.model.processing.resource.server.test.commons.TestAuthorizationContext;
import com.braintribe.model.processing.resource.server.test.commons.TestPersistenceGmSessionFactory;
import com.braintribe.model.processing.resource.server.test.commons.TestResourceAccessFactory;
import com.braintribe.model.processing.resource.streaming.access.RemoteResourceAccessFactory;
import com.braintribe.model.processing.session.impl.persistence.BasicPersistenceGmSession;
import com.braintribe.model.processing.smood.Smood;
import com.braintribe.model.usersession.UserSession;
import com.braintribe.provider.ThreadLocalStackedHolder;
import com.braintribe.wire.api.context.WireContext;
import com.braintribe.wire.api.space.WireSpace;

public interface MainContract extends WireSpace {

	static WireContext<MainContract> context() {
		// @formatter:off
		WireContext<MainContract> wireContext = 
				com.braintribe.wire.api.Wire
					.context(MainContract.class)
						.bindContracts(MainContract.class.getName().replace(".contract."+MainContract.class.getSimpleName(), ""))
					.build();
		return wireContext;
		// @formatter:on
	}

	TestAuthenticatingUserSessionProvider userSessionProvider();

	TestAuthorizationContext userSessionIdProvider();

	RemoteResourceAccessFactory remoteResourceAccessFactory();

	ThreadLocalStackedHolder<UserSession> userSessionHolder();

	List<Filter> filters();

	WebStreamingServer servlet();

	Smood access();

	BasicPersistenceGmSession gmSession();

	TestResourceAccessFactory resourceAccessFactory();

	TestPersistenceGmSessionFactory sessionFactory();

	ConfigurableMarshallerRegistry marshallerRegistry();

}
