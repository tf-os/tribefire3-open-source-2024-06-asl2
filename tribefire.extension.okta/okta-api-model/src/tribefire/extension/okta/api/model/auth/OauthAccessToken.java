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
package tribefire.extension.okta.api.model.auth;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

public interface OauthAccessToken extends GenericEntity {

	EntityType<OauthAccessToken> T = EntityTypes.T(OauthAccessToken.class);

	String token_type = "token_type";
	String expires_in = "expires_in";
	String access_token = "access_token";
	String scope = "scope";

	String getToken_type();
	void setToken_type(String token_type);

	Integer getExpires_in();
	void setExpires_in(Integer expires_in);

	String getAccess_token();
	void setAccess_token(String access_token);

	String getScope();
	void setScope(String scope);
}
