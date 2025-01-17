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
package tribefire.platform.impl.crypto;

import java.io.File;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.braintribe.cfg.Configurable;
import com.braintribe.codec.marshaller.api.GmSerializationOptions;
import com.braintribe.codec.marshaller.api.Marshaller;
import com.braintribe.codec.marshaller.stax.StaxMarshaller;
import com.braintribe.logging.Logger;
import com.braintribe.model.crypto.configuration.CryptoConfiguration;
import com.braintribe.model.crypto.configuration.encryption.AsymmetricEncryptionConfiguration;
import com.braintribe.model.crypto.configuration.encryption.EncryptionConfiguration;
import com.braintribe.model.crypto.configuration.encryption.SymmetricEncryptionConfiguration;
import com.braintribe.model.crypto.key.keystore.HasKeyStoreEntry;
import com.braintribe.model.crypto.token.EncryptionToken;
import com.braintribe.utils.IOTools;

public class CortexCryptoConfigurationBackupService {

	private static final Logger log = Logger.getLogger(CortexCryptoConfigurationBackupService.class);
	private static final DateFormat df = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");

	private Marshaller marshaller;
	private GmSerializationOptions serializationOptions = GmSerializationOptions.deriveDefaults().build();
	private Path backupBaseDirectory;

	public CortexCryptoConfigurationBackupService() {
	}

	@Configurable
	public void setMarshaller(Marshaller marshaller) {
		this.marshaller = marshaller;
	}

	@Configurable
	public void setSerializationOptions(GmSerializationOptions serializationOptions) {
		this.serializationOptions = serializationOptions;
	}

	@Configurable
	public void setBackupBaseDirectory(File backupBaseDirectory) {
		this.backupBaseDirectory = backupBaseDirectory != null ? backupBaseDirectory.toPath() : null;
	}

	public Marshaller getMarshaller() {
		if (this.marshaller == null) {
			this.marshaller = StaxMarshaller.defaultInstance;
		}
		return this.marshaller;
	}

	public Path getBackupBaseDirectory() {
		if (this.backupBaseDirectory == null) {
			this.backupBaseDirectory = new File(System.getProperty("java.io.tmpdir")).toPath();
		}
		return this.backupBaseDirectory;
	}

	public void backupCryptoConfiguration(CryptoConfiguration cryptoConfiguration, String configurationDescription)
			throws CortexCryptoConfiguratorException {

		Path backupBase = getBackupBaseDirectory();
		Path targetPath = backupBase.resolve("cryptoConfig_" + df.format(new Date()));

		OutputStream os = null;
		try {
			Files.createDirectories(targetPath);
			Path targetGmFile = targetPath.resolve(configurationDescription + ".gm");
			os = Files.newOutputStream(targetGmFile, StandardOpenOption.CREATE_NEW, StandardOpenOption.SYNC);
			getMarshaller().marshall(os, cryptoConfiguration, serializationOptions);
			os.flush();

			if (log.isDebugEnabled()) {
				log.debug("Backup of generated cortex's " + configurationDescription + " entity was saved to: [ " + targetGmFile + " ]");
			}

		} catch (Exception e) {
			throw new CortexCryptoConfiguratorException("Failed to backup configuration", e);
		} finally {
			IOTools.closeCloseable(os, null);
		}

		if (cryptoConfiguration instanceof EncryptionConfiguration) {
			backupReferencedToken((EncryptionConfiguration) cryptoConfiguration, targetPath);
		}

		if (log.isInfoEnabled()) {
			log.info("Backup of generated cortex's " + configurationDescription + " was saved to: [ " + backupBase + " ]");
		}

	}

	protected void backupReferencedToken(EncryptionConfiguration encryptionConfiguration, Path targetPath) throws CortexCryptoConfiguratorException {

		EncryptionToken encryptionToken = null;

		if (encryptionConfiguration instanceof SymmetricEncryptionConfiguration) {
			encryptionToken = ((SymmetricEncryptionConfiguration) encryptionConfiguration).getSymmetricEncryptionToken();
		} else if (encryptionConfiguration instanceof AsymmetricEncryptionConfiguration) {
			encryptionToken = ((AsymmetricEncryptionConfiguration) encryptionConfiguration).getAsymmetricEncryptionToken();
		}

		if (encryptionToken instanceof HasKeyStoreEntry) {
			backupKeyStoreToken((HasKeyStoreEntry) encryptionToken, targetPath);
		}

	}

	protected void backupKeyStoreToken(HasKeyStoreEntry keyStoreEntryToken, Path targetPath) throws CortexCryptoConfiguratorException {

		if (keyStoreEntryToken.getKeyStore() != null) {

			String filePath = keyStoreEntryToken.getKeyStore().getFilePath();
			String systemProperty = keyStoreEntryToken.getKeyStore().getSystemProperty();

			if (systemProperty != null && !systemProperty.trim().isEmpty()) {
				filePath = System.getProperty(systemProperty);
			}

			if (filePath != null && !filePath.trim().isEmpty()) {

				Path source = Paths.get(filePath);

				if (source != null) {

					String newFileName = source.toString().replaceAll("\\W", "_");
					Path target = targetPath.resolve(newFileName);

					try {
						Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);

						if (log.isDebugEnabled()) {
							log.debug("Referenced key store [ " + source + " ] was copied to: [ " + target + " ]");
						}

					} catch (Exception e) {
						throw new CortexCryptoConfiguratorException("Failed to backup keystore file", e);
					}

				}
			}

		}
	}

}
