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
package com.braintribe.model.processing.query.shortening;

import static com.braintribe.utils.lcd.CollectionTools2.newMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.braintribe.model.meta.GmType;
import com.braintribe.model.processing.meta.oracle.ModelOracle;
import com.braintribe.model.processing.query.api.shortening.QueryShorteningRuntimeException;
import com.braintribe.model.processing.query.api.shortening.SignatureExpert;

public class SmartShortening implements SignatureExpert {
	private final Map<String, List<String>> shortNameToSignatures;
	private final Map<String, String> shortenCache = newMap();
	private final Map<String, String> expandCache = newMap();

	public SmartShortening(ModelOracle modelOracle) {
		shortNameToSignatures = modelOracle.getTypes().asGmTypes() //
			.map(GmType::getTypeSignature) //
			.collect(Collectors.groupingBy(SmartShortening::getShortName));
	}
	
	private static String getShortName(String typeSignature) {
		int packageSplit = typeSignature.lastIndexOf(".");
		
		return packageSplit < 0 ? typeSignature : typeSignature.substring(packageSplit + 1);
	}

	@Override
	public String shorten(String typeSignature) {
		if (typeSignature == null)
			return null;
		
		return shortenCache.computeIfAbsent(typeSignature, this::shortenHelper);
	}
	
	public String shortenHelper(String typeSignature) {
		int packageSplit = typeSignature.lastIndexOf(".");
		if (packageSplit < 0)
			return typeSignature;
		
		String typeSignaturePart = getShortName(typeSignature);
		
		List<String> list = shortNameToSignatures.get(typeSignaturePart);
		if (list == null)
			//throwException(typeSignature, Collections.emptyList(), false);
			return typeSignature;
		
		return checkResult(list, typeSignature, typeSignaturePart, packageSplit);
	}
	
	private String checkResult(List<String> typeSignatures, String typeSignature, String typeSignaturePart, int packageSplit) {
		while (typeSignatures.size() > 1 && !typeSignature.equals(typeSignaturePart)) {
			packageSplit = typeSignature.lastIndexOf(".", packageSplit - 1);
			typeSignaturePart = typeSignature.substring(packageSplit + 1);

			List<String> matchingSignatures = new ArrayList<>();
			for (String ts : typeSignatures)
				if (ts.endsWith(typeSignaturePart))
					matchingSignatures.add(ts);
			
			typeSignatures = matchingSignatures;
		}
		
		return typeSignaturePart;
	}
	
	@Override
	public String expand(final String shortenedTypeSignature) {
		if (shortenedTypeSignature == null)
			return null;
		
		return expandCache.computeIfAbsent(shortenedTypeSignature, this::expandHelper);
	}
	
	public String expandHelper(String shortenedTypeSignature) {
		int index = shortenedTypeSignature.lastIndexOf(".");
		String shortestTypeSignature = index == -1 ? shortenedTypeSignature : shortenedTypeSignature.substring(index + 1);
		
		List<String> list = shortNameToSignatures.get(shortestTypeSignature);
		if (list == null)
			throwException(shortenedTypeSignature, Collections.emptyList(), true);
		else {
			if (list.size() == 1)
				return list.get(0);
			else {
				List<String> newList = list.stream().filter(signature -> signature.endsWith(shortenedTypeSignature)).collect(Collectors.toList());
				if (newList.size() == 1)
					return newList.get(0);
				else
					throwException(shortenedTypeSignature, newList, true);
			}
		}
		
		return null;
	}
	
	private static void throwException(String typeSignature, List<String> typeSignatures, boolean inExpandMode) {
		StringBuilder msg = new StringBuilder();

		if (inExpandMode) {
			msg.append("Could not find a unique GmCustomType");
			msg.append(" for the shortened TypeSignature: ").append(typeSignature);
		} else {
			msg.append("Could not shorten to a unique GmCustomType");
			msg.append(" for the TypeSignature: ").append(typeSignature);
		}

		if (!typeSignatures.isEmpty()) {
			msg.append("\n").append("Found types:");
			for (String signature : typeSignatures)
				msg.append("\n\t").append(signature);
		}

		throw new QueryShorteningRuntimeException(msg.toString());
	}
}
