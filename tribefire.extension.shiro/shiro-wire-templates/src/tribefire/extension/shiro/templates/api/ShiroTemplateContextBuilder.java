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
package tribefire.extension.shiro.templates.api;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.braintribe.model.shiro.deployment.FieldEncoding;

import tribefire.extension.templates.api.TemplateContextBuilder;

public interface ShiroTemplateContextBuilder extends TemplateContextBuilder<ShiroTemplateContext> {

	ShiroTemplateContextBuilder setGoogleEnabled(boolean googleEnabled);
	ShiroTemplateContextBuilder setGoogleClientId(String googleClientId);
	ShiroTemplateContextBuilder setGoogleSecret(String googleSecret);

	ShiroTemplateContextBuilder setAzureEnabled(boolean azureEnabled);
	ShiroTemplateContextBuilder setAzureClientId(String azureClientId);
	ShiroTemplateContextBuilder setAzureSecret(String azureSecret);
	ShiroTemplateContextBuilder setAzureTenant(String azureTenant);

	ShiroTemplateContextBuilder setTwitterEnabled(boolean twitterEnabled);
	ShiroTemplateContextBuilder setTwitterKey(String twitterKey);
	ShiroTemplateContextBuilder setTwitterSecret(String twitterSecret);

	ShiroTemplateContextBuilder setFacebookEnabled(boolean facebookEnabled);
	ShiroTemplateContextBuilder setFacebookKey(String facebookKey);
	ShiroTemplateContextBuilder setFacebookSecret(String facebookSecret);

	ShiroTemplateContextBuilder setGithubEnabled(boolean githubEnabled);
	ShiroTemplateContextBuilder setGithubKey(String githubKey);
	ShiroTemplateContextBuilder setGithubSecret(String githubSecret);

	ShiroTemplateContextBuilder setCognitoEnabled(boolean cognitoEnabled);
	ShiroTemplateContextBuilder setCognitoClientId(String cognitoClientId);
	ShiroTemplateContextBuilder setCognitoSecret(String cognitoSecret);
	ShiroTemplateContextBuilder setCognitoRegion(String cognitoRegion);
	ShiroTemplateContextBuilder setCognitoUserPoolId(String cognitoUserPoolId);
	ShiroTemplateContextBuilder setCognitoExclusiveRoleProvider(boolean cognitoExclusiveRoleProvider);

	ShiroTemplateContextBuilder setOktaEnabled(boolean oktaEnabled);
	ShiroTemplateContextBuilder setOktaClientId(String oktaClientId);
	ShiroTemplateContextBuilder setOktaSecret(String oktaSecret);
	ShiroTemplateContextBuilder setOktaDiscoveryUrl(String oktaDiscoveryUrl);
	ShiroTemplateContextBuilder setOktaExclusiveRoleProvider(boolean oktaExclusiveRoleProvider);
	ShiroTemplateContextBuilder setOktaRolesField(String oktaRolesField);
	ShiroTemplateContextBuilder setOktaRolesFieldEncoding(FieldEncoding oktaRolesFieldEncoding);

	ShiroTemplateContextBuilder setInstagramEnabled(boolean instagramEnabled);
	ShiroTemplateContextBuilder setInstagramClientId(String instagramClientId);
	ShiroTemplateContextBuilder setInstagramSecret(String instagramSecret);
	ShiroTemplateContextBuilder setInstagramAuthUrl(String instagramAuthUrl);
	ShiroTemplateContextBuilder setInstagramTokenUrl(String instagramTokenUrl);
	ShiroTemplateContextBuilder setInstagramProfileUrl(String instagramProfileUrl);
	ShiroTemplateContextBuilder setInstagramUserInformationUrl(String instagramUserInformationUrl);
	ShiroTemplateContextBuilder setInstagramUsernamePattern(String instagramUsernamePattern);
	ShiroTemplateContextBuilder setInstagramScope(String instagramScope);

	ShiroTemplateContextBuilder setUserRolesMapField(List<String> userRolesMapField);
	ShiroTemplateContextBuilder setUserRolesMap(Map<Set<String>, Set<String>> userRolesMap);
	ShiroTemplateContextBuilder setAcceptList(Set<String> acceptList);
	ShiroTemplateContextBuilder setBlockList(Set<String> blockList);
	ShiroTemplateContextBuilder setCreateUsers(boolean createUsers);
	ShiroTemplateContextBuilder setPublicServicesUrl(String publicServicesUrl);
	ShiroTemplateContextBuilder setCallbackUrl(String callbackUrl);
	ShiroTemplateContextBuilder setUnauthorizedUrl(String unauthorizedUrl);
	ShiroTemplateContextBuilder setUnauthenticatedUrl(String unauthenticatedUrl);
	ShiroTemplateContextBuilder setRedirectUrl(String redirectUrl);
	ShiroTemplateContextBuilder setFallbackUrl(String fallbackUrl);
	ShiroTemplateContextBuilder setAddSessionParameterOnRedirect(boolean addSessionParameterOnRedirect);
	ShiroTemplateContextBuilder setLoginDomain(String loginDomain);
	ShiroTemplateContextBuilder setCustomParameters(Map<String, String> customParameters);
	ShiroTemplateContextBuilder setShowStandardLoginForm(boolean showStandardLoginForm);
	ShiroTemplateContextBuilder setShowTextLinks(boolean showTextLinks);

	ShiroTemplateContextBuilder setObfuscateLogOutput(boolean obfuscateLogOutput);

}