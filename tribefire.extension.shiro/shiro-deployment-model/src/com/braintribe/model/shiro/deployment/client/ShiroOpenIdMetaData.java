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
package com.braintribe.model.shiro.deployment.client;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

public interface ShiroOpenIdMetaData extends GenericEntity {

	final EntityType<ShiroOpenIdMetaData> T = EntityTypes.T(ShiroOpenIdMetaData.class);

	String getIssuer();
	void setIssuer(String issuer);

	String getAuthorizationEndpoint();
	void setAuthorizationEndpoint(String authorizationEndpoint);

	String getTokenEndpoint();
	void setTokenEndpoint(String tokenEndpoint);

	String getRegistrationEndpoint();
	void setRegistrationEndpoint(String registrationEndpoint);

	String getJwksUri();
	void setJwksUri(String jwksUri);

	String getScopesSupported();
	void setScopesSupported(String scopesSupported);

	String getResponseTypesSupported();
	void setResponseTypesSupported(String responseTypesSupported);

	String getResponseModesSupported();
	void setResponseModesSupported(String responseModesSupported);

	String getGrantTypesSupported();
	void setGrantTypesSupported(String grantTypesSupported);

	String getCodeChallengeMethodsSupported();
	void setCodeChallengeMethodsSupported(String codeChallengeMethodsSupported);

	String getTokenEndpointAuthMethodsSupported();
	void setTokenEndpointAuthMethodsSupported(String tokenEndpointAuthMethodsSupported);

	String getTokenEndpointAuthSigningAlgValuesSupported();
	void setTokenEndpointAuthSigningAlgValuesSupported(String tokenEndpointAuthSigningAlgValuesSupported);

	String getRequestObjectSigningAlgValuesSupported();
	void setRequestObjectSigningAlgValuesSupported(String requestObjectSigningAlgValuesSupported);

	String getRequestObjectEncryptionAlgValuesSupported();
	void setRequestObjectEncryptionAlgValuesSupported(String requestObjectEncryptionAlgValuesSupported);

	String getRequestObjectEncryptionEncValuesSupported();
	void setRequestObjectEncryptionEncValuesSupported(String requestObjectEncryptionEncValuesSupported);

	String getUiLocalesSupported();
	void setUiLocalesSupported(String uiLocalesSupported);

	String getServiceDocumentation();
	void setServiceDocumentation(String serviceDocumentation);

	String getOpPolicyUri();
	void setOpPolicyUri(String opPolicyUri);

	String getOpTosUri();
	void setOpTosUri(String opTosUri);

	String getIntrospectionEndpoint();
	void setIntrospectionEndpoint(String introspectionEndpoint);

	String getIntrospectionEndpointAuthMethodsSupported();
	void setIntrospectionEndpointAuthMethodsSupported(String introspectionEndpointAuthMethodsSupported);

	String getIntrospectionEndpointAuthSigningAlgValuesSupported();
	void setIntrospectionEndpointAuthSigningAlgValuesSupported(String introspectionEndpointAuthSigningAlgValuesSupported);

	String getRevocationEndpoint();
	void setRevocationEndpoint(String revocationEndpoint);

	String getRevocationEndpointAuthMethodsSupported();
	void setRevocationEndpointAuthMethodsSupported(String revocationEndpointAuthMethodsSupported);

	String getRevocationEndpointAuthSigningAlgValuesSupported();
	void setRevocationEndpointAuthSigningAlgValuesSupported(String revocationEndpointAuthSigningAlgValuesSupported);

	String getTlsClientCertificateBoundAccessTokens();
	void setTlsClientCertificateBoundAccessTokens(String tlsClientCertificateBoundAccessTokens);

	String getAuthorizationSigningAlgValuesSupported();
	void setAuthorizationSigningAlgValuesSupported(String authorizationSigningAlgValuesSupported);

	String getAuthorizationEncryptionAlgValuesSupported();
	void setAuthorizationEncryptionAlgValuesSupported(String authorizationEncryptionAlgValuesSupported);

	String getAuthorizationEncryptionEncValuesSupported();
	void setAuthorizationEncryptionEncValuesSupported(String authorizationEncryptionEncValuesSupported);

	String getUserinfoEndpoint();
	void setUserinfoEndpoint(String userinfoEndpoint);

	String getCheckSessionIframe();
	void setCheckSessionIframe(String checkSessionIframe);

	String getEndSessionEndpoint();
	void setEndSessionEndpoint(String endSessionEndpoint);

	String getAcrValuesSupported();
	void setAcrValuesSupported(String acrValuesSupported);

	String getSubjectTypesSupported();
	void setSubjectTypesSupported(String subjectTypesSupported);

	String getIdTokenSigningAlgValuesSupported();
	void setIdTokenSigningAlgValuesSupported(String idTokenSigningAlgValuesSupported);

	String getIdTokenEncryptionAlgValuesSupported();
	void setIdTokenEncryptionAlgValuesSupported(String idTokenEncryptionAlgValuesSupported);

	String getIdTokenEncryptionEncValuesSupported();
	void setIdTokenEncryptionEncValuesSupported(String idTokenEncryptionEncValuesSupported);

	String getUserinfoSigningAlgValuesSupported();
	void setUserinfoSigningAlgValuesSupported(String userinfoSigningAlgValuesSupported);

	String getUserinfoEncryptionAlgValuesSupported();
	void setUserinfoEncryptionAlgValuesSupported(String userinfoEncryptionAlgValuesSupported);

	String getUserinfoEncryptionEncValuesSupported();
	void setUserinfoEncryptionEncValuesSupported(String userinfoEncryptionEncValuesSupported);

	String getDisplayValuesSupported();
	void setDisplayValuesSupported(String displayValuesSupported);

	String getClaimTypesSupported();
	void setClaimTypesSupported(String claimTypesSupported);

	String getClaimsSupported();
	void setClaimsSupported(String claimsSupported);

	String getClaimsLocalesSupported();
	void setClaimsLocalesSupported(String claimsLocalesSupported);

	String getClaimsParameterSupported();
	void setClaimsParameterSupported(String claimsParameterSupported);

	String getRequestParameterSupported();
	void setRequestParameterSupported(String requestParameterSupported);

	String getRequestUriParameterSupported();
	void setRequestUriParameterSupported(String requestUriParameterSupported);

	String getRequireRequestUriRegistration();
	void setRequireRequestUriRegistration(String requireRequestUriRegistration);

	String getBackchannelLogoutSupported();
	void setBackchannelLogoutSupported(String backchannelLogoutSupported);

	String getBackchannelLogoutSessionSupported();
	void setBackchannelLogoutSessionSupported(String backchannelLogoutSessionSupported);

	String getFrontchannelLogoutSupported();
	void setFrontchannelLogoutSupported(String frontchannelLogoutSupported);

	String getFrontchannelLogoutSessionSupported();
	void setFrontchannelLogoutSessionSupported(String frontchannelLogoutSessionSupported);

}
