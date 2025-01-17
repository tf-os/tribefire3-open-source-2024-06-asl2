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
package com.braintribe.codec.string;

import java.util.ArrayList;
import java.util.List;

import com.braintribe.codec.Codec;
import com.braintribe.codec.CodecException;

public class ListCodec<T> implements Codec<List<T>, String> {
	private Codec<T, String> elementCodec;
	private Codec<String, String> escapeCodec = new PassThroughCodec<String>(String.class);
	private String delimiter = ",";

	public ListCodec(Codec<T, String> elementCodec) {
		super();
		this.elementCodec = elementCodec;
	}
	
	public void setEscapeCodec(Codec<String, String> escapeCodec) {
		this.escapeCodec = escapeCodec;
	}
	
	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}

	@Override
	public List<T> decode(String s) throws CodecException {
		if (s == null || s.trim().length() == 0)
			return new ArrayList<T>();

		String[] escapedValues = s.split(delimiter);
		List<T> values = new ArrayList<T>();

		for (int i = 0; i < escapedValues.length; i++) {
			String encodedValue = escapeCodec.decode(escapedValues[i]);
			values.add(elementCodec.decode(encodedValue));
		}
		return values;
	}

	@Override
	public String encode(List<T> obj) throws CodecException {
		StringBuilder encodedList = new StringBuilder();

		for (T value : obj) {
			if (encodedList.length() > 0) 
				encodedList.append(delimiter);
			String encodedValue = elementCodec.encode(value);
			encodedList.append(escapeCodec.encode(encodedValue));
		}

		return encodedList.toString();
	}

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Class<List<T>> getValueClass() {
		return (Class)List.class;
	}
}
