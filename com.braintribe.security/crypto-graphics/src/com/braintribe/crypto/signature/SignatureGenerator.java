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
package com.braintribe.crypto.signature;

import java.io.File;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;

import com.braintribe.crypto.CryptoServiceException;
import com.braintribe.crypto.base64.Base64;
import com.braintribe.crypto.key.AsymmetricKeyGenerator;
import com.braintribe.crypto.utils.TextUtils;

/**
 * handles signatures of the type SHA1withDSA provided by Sun
 * 
 * @author pit
 *
 */
public class SignatureGenerator {
	
	private static String SIGNATURE_INSTANCE = "SHA1withRSA";

	/**
	 * generates a signature 
	 * @param privKey - private key to use
	 * @param toSign - string to sign
	 * @return - the signature in Base64 encoding 
	 * @throws CryptoServiceException
	 */
	public static String generateSignature( PrivateKey privKey, byte [] toSignBytes) throws CryptoServiceException {
		try {
			Signature dsa = Signature.getInstance( SIGNATURE_INSTANCE); 
			dsa.initSign( privKey);
			
			
			dsa.update( toSignBytes);
			
			byte [] signatureBytes = dsa.sign();
			
			String signature = Base64.encodeBytes( signatureBytes, Base64.DONT_BREAK_LINES);			
			return signature;
			
		} catch (InvalidKeyException e) {
			throw new CryptoServiceException( "cannot sign data as " + e, e);			
		} catch (NoSuchAlgorithmException e) {
			throw new CryptoServiceException( "cannot sign data as " + e, e);
		} catch (SignatureException e) {
			throw new CryptoServiceException( "cannot sign data as " + e, e);
		}    
	}
	
	/**
	 * @param privKey
	 * @param toSign
	 * @return
	 * @throws CryptoServiceException
	 */
	public static String generateSignature( PrivateKey privKey, String toSign) throws CryptoServiceException {
		byte [] toSignBytes = toSign.getBytes();
		return generateSignature(privKey, toSignBytes);
	}
	
	/**
	 * verifies a signature 
	 * @param pubKey - the public key 
	 * @param signature - the signature's bytes in Base64 encoding 
	 * @param toVerify - the string the verify 
	 * @return
	 * @throws CryptoServiceException
	 */
	public static boolean verifySignature( PublicKey pubKey, String signature, String toVerify) throws CryptoServiceException {
		
		try {
			byte [] signatureBytes = Base64.decode( signature);
			
			byte [] toVerifyBytes = toVerify.getBytes();
			
			 Signature sig = Signature.getInstance( SIGNATURE_INSTANCE);
			 sig.initVerify(pubKey);
			 
			 sig.update( toVerifyBytes);
			 
			 boolean result = sig.verify( signatureBytes);
			 
			 return result;
		} catch (InvalidKeyException e) {
			throw new CryptoServiceException( "cannot verify signature as " + e, e);
		} catch (NoSuchAlgorithmException e) {
			throw new CryptoServiceException( "cannot verify signature as " + e, e);
		} catch (SignatureException e) {
			throw new CryptoServiceException( "cannot verify signature as " + e, e);
		}
	}
		
	
	/**
	 * main : can sign a file or verify the file 
	 * -g : generates a signature
	 * 		-g <input> <private key> -> <input>.signature
	 * -v : validates a signature
	 * 		-v <input> <signature> <public key> 
	 * 
	 * @param args
	 */
	public static void main( String [] args) {
	
			try {
				if (args[0].equalsIgnoreCase( "-g")) {								
					String input = TextUtils.readContentsFromFile( new File( args[1]));				
					String privateKeyAsString = TextUtils.readContentsFromFile( new File( args[2]));			
					PrivateKey privateKey = AsymmetricKeyGenerator.importPrivateKey( privateKeyAsString);																		
					String signature = generateSignature( privateKey, input);				
					TextUtils.writeContentsToFile( signature, new File( args[1] + ".signature"));
					return;															
				}
				if (args[0].equalsIgnoreCase( "-v")) {
					String text = TextUtils.readContentsFromFile( new File( args[1]));
					String signature = TextUtils.readContentsFromFile( new File(args[2]));			
					String publicKeyAsString = TextUtils.readContentsFromFile( new File( args[3]));
					PublicKey publicKey = AsymmetricKeyGenerator.importPublicKey( publicKeyAsString);
					 
					boolean validates = verifySignature( publicKey, signature, text);
					System.out.println( validates ? "validates ok" : "doesn't validate");
					return;				
				}
			} catch (CryptoServiceException e) {			
				e.printStackTrace();
			}
						
	    	    	    	
	}
}
