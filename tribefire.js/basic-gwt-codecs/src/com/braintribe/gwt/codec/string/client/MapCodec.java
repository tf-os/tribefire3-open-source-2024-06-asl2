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
package com.braintribe.gwt.codec.string.client;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import com.braintribe.codec.Codec;
import com.braintribe.codec.CodecException;
import com.braintribe.gwt.ioc.client.Configurable;



public class MapCodec<K, V> implements Codec<Map<K, V>, String> {
	
	@SuppressWarnings("rawtypes")
	private Codec<V,String> passThroughCodecInstance = new PassThroughCodec(null);
	private Function<K, Codec<V, String>> valueCodecProvider = k -> passThroughCodecInstance;
	@SuppressWarnings("rawtypes")
	private Codec<K, String> keyCodec = new PassThroughCodec(null);
	private Codec<String, String> escapeCodec = new PassThroughCodec<>(String.class);
	private String delimiter = ",";
	private String associationDelimiter = "=";

	public MapCodec() {
		super();
	}
	
	@Configurable
	public void setValueCodecProvider(Function<K, Codec<V, String>> valueCodecProvider) {
		this.valueCodecProvider = valueCodecProvider;
	}
	
	@Configurable
	public void setKeyCodec(Codec<K, String> keyCodec) {
		this.keyCodec = keyCodec;
	}
	
	@Configurable
	public void setEscapeCodec(Codec<String, String> escapeCodec) {
		this.escapeCodec = escapeCodec;
	}
	
	@Configurable
	public void setAssociationDelimiter(String associationDelimiter) {
		this.associationDelimiter = associationDelimiter;
	}
	
	@Configurable
	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}
	
	public Function<K, Codec<V, String>> getValueCodecProvider() {
		return valueCodecProvider;
	}
	
	@Override
	public Map<K, V> decode(String s) throws CodecException {
		if (s == null || s.trim().length() == 0)
			return new HashMap<K, V>();

		String[] associations = s.split(delimiter);
		Map<K, V> map = new HashMap<K, V>();
		
		for (int i = 0; i < associations.length; i++) {
			String association = associations[i];
			int index = association.indexOf(associationDelimiter);
			if (index == -1)
				throw new CodecException("expected association delimiter " + associationDelimiter + " but did not found it");
			
			String escapedKey = association.substring(0, index); 
			String escapedValue = association.substring(index + associationDelimiter.length());
			
			String encodedKey = escapeCodec.decode(escapedKey);
			String encodedValue = escapeCodec.decode(escapedValue);
			
			K key = keyCodec.decode(encodedKey);
			V value;
			try {
				value = getValueCodecProvider().apply(key).decode(encodedValue);
			} catch (RuntimeException e) {
				throw new CodecException(e);
			}
			
			map.put(key, value);
		}
		return map;
	}

	@Override
	public String encode(Map<K, V> obj) throws CodecException {
		if (obj == null) return "";
		
		StringBuilder encodedMap = new StringBuilder();

		for (Map.Entry<K, V> entry: obj.entrySet()) {
			K key = entry.getKey();
			V value = entry.getValue();
			
			String encodedValue = null;
			try {
				encodedValue = getValueCodecProvider().apply(key).encode(value);
			} catch (RuntimeException e) {
				throw new CodecException(e);
			}
			
			String encodedKey = keyCodec.encode(key);
			
			
			String escapedKey = escapeCodec.encode(encodedKey);
			String escapedValue = escapeCodec.encode(encodedValue);
			
			if (encodedMap.length() > 0) 
				encodedMap.append(delimiter);
			
			encodedMap.append(escapedKey);
			encodedMap.append(associationDelimiter);
			encodedMap.append(escapedValue);
		}

		return encodedMap.toString();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Class<Map<K, V>> getValueClass() {
		return (Class) Map.class;
	}
}
