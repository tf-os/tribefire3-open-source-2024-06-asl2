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
package com.braintribe.gwt.gmview.codec.client;

import com.braintribe.codec.Codec;
import com.braintribe.codec.CodecException;
import com.braintribe.model.workbench.KeyConfiguration;
import com.google.gwt.event.dom.client.KeyCodes;

/**
 * This codec is responsible for transforming a {@link KeyConfiguration} into String,
 * for visualization only purpose.
 * 
 */
public class KeyConfigurationRendererCodec implements Codec<KeyConfiguration, String> {
	private static final String EMPTY_STRING = "";

	@Override
	public KeyConfiguration decode(String encodedValue) throws CodecException {
		throw new CodecException("Decode is not supported");
	}

	@Override
	public String encode(KeyConfiguration keyConfiguration) throws CodecException {
		return getStringFromKeyConfiguration(keyConfiguration);
	}

	private static String getStringFromKeyConfiguration(KeyConfiguration keyConfiguration) {
		if (keyConfiguration == null)
			return EMPTY_STRING;
		
		StringBuilder builder = new StringBuilder();
		if (keyConfiguration.getCtrl())
			builder.append("CTRL+");
		if (keyConfiguration.getShift())
			builder.append("SHIFT+");
		if (keyConfiguration.getAlt())
			builder.append("ALT+");
		if (keyConfiguration.getMeta())
			builder.append("META+");
		
		Integer keyCode = keyConfiguration.getKeyCode();		
		if ( keyCode != null) {
			if (KeyCodes.KEY_SPACE == keyCode)
				builder.append("SPACE");
			if (KeyCodes.KEY_ENTER == keyCode)
				builder.append("ENTER");
			if (KeyCodes.KEY_END == keyCode)
				builder.append("END");
			if (KeyCodes.KEY_HOME == keyCode)
				builder.append("HOME");
			if (KeyCodes.KEY_INSERT == keyCode)
				builder.append("INSERT");
			if (KeyCodes.KEY_TAB == keyCode)
				builder.append("TAB");
			else if (KeyCodes.KEY_BACKSPACE == keyCode)
				builder.append("BACKSPACE");
			else if (KeyCodes.KEY_DELETE == keyCode)
				builder.append("DELETE");
			else if (KeyCodes.KEY_DOWN == keyCode)
				builder.append("DOWN");
			else if (KeyCodes.KEY_UP == keyCode)
				builder.append("UP");
			else if (KeyCodes.KEY_LEFT == keyCode)
				builder.append("LEFT");
			else if (KeyCodes.KEY_RIGHT == keyCode)
				builder.append("RIGHT");
			else if (KeyCodes.KEY_F1 == keyCode)
				builder.append("F1");
			else if (KeyCodes.KEY_F2 == keyCode)
				builder.append("F2");
			else if (KeyCodes.KEY_F3 == keyCode)
				builder.append("F3");
			else if (KeyCodes.KEY_F4 == keyCode)
				builder.append("F4");
			else if (KeyCodes.KEY_F5 == keyCode)
				builder.append("F5");
			else if (KeyCodes.KEY_F6 == keyCode)
				builder.append("F6");
			else if (KeyCodes.KEY_F7 == keyCode)
				builder.append("F7");
			else if (KeyCodes.KEY_F8 == keyCode)
				builder.append("F8");
			else if (KeyCodes.KEY_F9 == keyCode)
				builder.append("F9");
			else if (KeyCodes.KEY_F10 == keyCode)
				builder.append("F10");
			else if (KeyCodes.KEY_F11 == keyCode)
				builder.append("F11");
			else if (KeyCodes.KEY_F12 == keyCode)
				builder.append("F12");						
			else {
				builder.append(Character.toString ((char) keyCode.intValue()));
			}
		}		
		return builder.toString();
	}

	@Override
	public Class<KeyConfiguration> getValueClass() {
		return KeyConfiguration.class;
	}

	public static String encodeKeyConfiguration(KeyConfiguration keyConfiguration) {
		return getStringFromKeyConfiguration(keyConfiguration);
	}
}
