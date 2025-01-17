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
package com.braintribe.utils.encryption;

import java.nio.ByteBuffer;
import java.security.AlgorithmParameters;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import com.braintribe.exception.Exceptions;
import com.braintribe.utils.lcd.StringTools;

public class Cryptor {

	public static final String DECRYPTION_PREFIX = "Decrypt:";
	private static SecureRandom random;

	public static String decrypt(String secret, String algorithm, String keyFactoryAlgorithm, Integer keyLength, String contentEncryptedAndEncoded) {
		if (secret == null) {
			throw new IllegalArgumentException("The secret must not be null.");
		}
		if (algorithm == null) {
			algorithm = "AES/CBC/PKCS5Padding";
		}
		if (keyFactoryAlgorithm == null) {
			keyFactoryAlgorithm = "PBKDF2WithHmacSHA1";
		}
		if (keyLength == null) {
			keyLength = 128;
		}

		try {
			Cipher cipher = Cipher.getInstance(algorithm);

			ByteBuffer buffer = ByteBuffer.wrap(Base64.getDecoder().decode(contentEncryptedAndEncoded));

			byte[] saltBytes = new byte[20];
			buffer.get(saltBytes, 0, saltBytes.length);
			byte[] ivBytes1 = new byte[cipher.getBlockSize()];
			buffer.get(ivBytes1, 0, ivBytes1.length);
			byte[] encryptedTextBytes = new byte[buffer.capacity() - saltBytes.length - ivBytes1.length];

			buffer.get(encryptedTextBytes);

			// Deriving the key
			SecretKeyFactory factory = SecretKeyFactory.getInstance(keyFactoryAlgorithm);

			PBEKeySpec spec = new PBEKeySpec(secret.toCharArray(), saltBytes, 65556, keyLength);

			SecretKey secretKey = factory.generateSecret(spec);
			SecretKeySpec secretSpec = new SecretKeySpec(secretKey.getEncoded(), "AES");

			cipher.init(Cipher.DECRYPT_MODE, secretSpec, new IvParameterSpec(ivBytes1));

			byte[] decryptedTextBytes = cipher.doFinal(encryptedTextBytes);

			String text = new String(decryptedTextBytes, "UTF-8");
			return text;
		} catch(Exception e) {
			throw new RuntimeException("Could not decrypt the String "+StringTools.simpleObfuscatePassword(contentEncryptedAndEncoded)+" with key "+StringTools.simpleObfuscatePassword(secret), e);
		}

	}

	public static String encrypt(String secret, String algorithm, String keyFactoryAlgorithm, Integer keyLength, String value) {
		if (secret == null) {
			throw new IllegalArgumentException("The secret must not be null.");
		}
		if (algorithm == null) {
			algorithm = "AES/CBC/PKCS5Padding";
		}
		if (keyFactoryAlgorithm == null) {
			keyFactoryAlgorithm = "PBKDF2WithHmacSHA1";
		}
		if (keyLength == null) {
			keyLength = 128;
		}

		try {
			byte[] ivBytes;

			SecureRandom random = getRandom();
			byte bytes[] = new byte[20];
			random.nextBytes(bytes);
			byte[] saltBytes = bytes;

			// Derive the key
			SecretKeyFactory factory = SecretKeyFactory.getInstance(keyFactoryAlgorithm);

			int maxAllowedKeyLength = Cipher.getMaxAllowedKeyLength("AES");
			if (maxAllowedKeyLength > 0 && maxAllowedKeyLength < keyLength) {
				throw new IllegalArgumentException("The requested key length "+keyLength+" is greater than allowed: "+maxAllowedKeyLength);
			}

			PBEKeySpec spec = new PBEKeySpec(secret.toCharArray(),saltBytes,65556,keyLength);

			SecretKey secretKey = factory.generateSecret(spec);
			SecretKeySpec secretSpec = new SecretKeySpec(secretKey.getEncoded(), "AES");

			//encrypting the word

			Cipher cipher = Cipher.getInstance(algorithm);
			cipher.init(Cipher.ENCRYPT_MODE, secretSpec);
			AlgorithmParameters params = cipher.getParameters();
			ivBytes =   params.getParameterSpec(IvParameterSpec.class).getIV();

			byte[] encryptedTextBytes = cipher.doFinal(value.getBytes("UTF-8"));

			//prepend salt and vi

			byte[] buffer = new byte[saltBytes.length + ivBytes.length + encryptedTextBytes.length];

			System.arraycopy(saltBytes, 0, buffer, 0, saltBytes.length);
			System.arraycopy(ivBytes, 0, buffer, saltBytes.length, ivBytes.length);

			System.arraycopy(encryptedTextBytes, 0, buffer, saltBytes.length + ivBytes.length, encryptedTextBytes.length);

			String encoded = new String(Base64.getEncoder().encode(buffer), "UTF-8");

			return encoded;
		} catch (Exception e) {
			throw Exceptions.unchecked(e, "Error while encrypting value");
		}
	}
	
	private static SecureRandom getRandom() {
		if (random != null)
			return random;
		
		random = new SecureRandom();
		return random;
	}

}
