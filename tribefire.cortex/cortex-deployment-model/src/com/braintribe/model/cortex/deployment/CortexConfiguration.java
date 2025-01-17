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
package com.braintribe.model.cortex.deployment;

import com.braintribe.model.accessdeployment.IncrementalAccess;
import com.braintribe.model.cortex.deployment.cors.CorsConfiguration;
import com.braintribe.model.crypto.configuration.encryption.AsymmetricEncryptionConfiguration;
import com.braintribe.model.crypto.configuration.encryption.SymmetricEncryptionConfiguration;
import com.braintribe.model.crypto.configuration.hashing.HashingConfiguration;
import com.braintribe.model.generic.StandardStringIdentifiable;
import com.braintribe.model.generic.annotation.SelectiveInformation;
import com.braintribe.model.generic.annotation.meta.Description;
import com.braintribe.model.generic.annotation.meta.Name;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.lockingdeployment.Locking;
import com.braintribe.model.messagingdeployment.Messaging;

import tribefire.cortex.model.deployment.mimetypedetection.MimeTypeDetector;
import tribefire.cortex.model.deployment.usersession.cleanup.CleanupUserSessionsProcessor;
import tribefire.cortex.model.deployment.usersession.service.UserSessionService;

@SelectiveInformation("Cortex Configuration")
public interface CortexConfiguration extends StandardStringIdentifiable {

	EntityType<CortexConfiguration> T = EntityTypes.T(CortexConfiguration.class);

	String CORTEX_CONFIGURATION_GLOBAL_ID = "config:cortex";

	String asymmetricEncryptionConfiguration = "asymmetricEncryptionConfiguration";
	String authenticationAccess = "authenticationAccess";
	String corsConfiguration = "corsConfiguration";
	String hashingConfiguration = "hashingConfiguration";
	String locking = "locking";
	String messaging = "messaging";
	String symmetricEncryptionConfiguration = "symmetricEncryptionConfiguration";
	String transientMessagingAccess = "transientMessagingAccess";
	String uploadRepository = "uploadRepository";
	String userSessionsAccess = "userSessionsAccess";
	String userSessionService = "userSessionService";
	String cleanupUserSessionsProcessor = "cleanupUserSessionsProcessor";
	String userStatisticsAccess = "userStatisticsAccess";

	@Name("CORS Configuration")
	@Description("The CORS configuration of this server.")
	CorsConfiguration getCorsConfiguration();
	void setCorsConfiguration(CorsConfiguration corsConfiguration);

	@Name("Messaging")
	@Description("A configurable messaging supplier.")
	Messaging getMessaging();
	void setMessaging(Messaging messaging);

	@Name("Locking")
	@Description("A configurable Locking.")
	Locking getLocking();
	void setLocking(Locking locing);
	
	@Name("Authentication Access")
	IncrementalAccess getAuthenticationAccess();
	void setAuthenticationAccess(IncrementalAccess authenticationAccess);

	@Name("User Sessions")
	@Description("A configurable alternative authentication Access.")
	IncrementalAccess getUserSessionsAccess();
	void setUserSessionsAccess(IncrementalAccess userSessionsAccess);

	@Name("Cleanup User Sessions Processor")
	@Description("Processor for cleaning up user sessions. Must be compatible with userSessionsAccess.")
	CleanupUserSessionsProcessor getCleanupUserSessionsProcessor();
	void setCleanupUserSessionsProcessor(CleanupUserSessionsProcessor cleanupUserSessionsProcessor);

	@Name("User Session Service")
	@Description("Service for managing sesssions. Must be compatible with userSessionsAccess.")
	UserSessionService getUserSessionService();
	void setUserSessionService(UserSessionService userSessionService);

	@Name("Authentication Access")
	IncrementalAccess getUserStatisticsAccess();
	void setUserStatisticsAccess(IncrementalAccess userStatisticsAccess);

	@Name("Transient Messaging Access")
	@Description("Access for temporarily storing binary data.")
	IncrementalAccess getTransientMessagingAccess();
	void setTransientMessagingAccess(IncrementalAccess transientMessagingAccess);

	@Name("MIME Type Detector")
	MimeTypeDetector getMimeTypeDetector();
	void setMimeTypeDetector(MimeTypeDetector mimeTypeDetector);

	@Name("Hashing Configuration")
	@Description("The system's default hashing algorithm.")
	HashingConfiguration getHashingConfiguration();
	void setHashingConfiguration(HashingConfiguration hashingConfiguration);

	@Name("Symmetric Encryption")
	@Description("The system's default symmetric encryption keys.")
	SymmetricEncryptionConfiguration getSymmetricEncryptionConfiguration();
	void setSymmetricEncryptionConfiguration(SymmetricEncryptionConfiguration symmetricEncryptionConfiguration);

	@Name("Asymmetric Encryption")
	@Description("The system's default asymmetric encryption keys.")
	AsymmetricEncryptionConfiguration getAsymmetricEncryptionConfiguration();
	void setAsymmetricEncryptionConfiguration(AsymmetricEncryptionConfiguration asymmetricEncryptionConfiguration);

}
