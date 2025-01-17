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

import com.braintribe.codec.Codec;
import com.braintribe.codec.CodecException;
import com.braintribe.gwt.ioc.client.Configurable;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.i18n.client.constants.NumberConstants;

/**
 * This Double Codec may be configure with decimal and thousand separators.
 * @author michel.docouto
 *
 */
public class ExtendedDoubleCodec implements Codec<Double, String> {
	private static final NumberConstants numberConstants = LocaleInfo.getCurrentLocale().getNumberConstants();
	private static final NumberFormat format = NumberFormat.getDecimalFormat();
	
	private String decimalSeparator = ".";
	private String thousandSeparator = null;
	private int decimalCount = 2;
	private boolean removeThousandSeparatorOnEncoding = false;
	
	/**
	 * Configures the decimal separator. Defaults to "."
	 */
	@Configurable
	public void setDecimalSeparator(String decimalSeparator) {
		this.decimalSeparator = decimalSeparator;
	}

	/**
	 * Configures the thousand separator. Defaults to null.
	 */
	@Configurable
	public void setThousandSeparator(String thousandSeparator) {
		this.thousandSeparator = thousandSeparator;
	}
	
	/**
	 * Configures the decimal count (used only while encoding). Defaults to 2.
	 */
	@Configurable
	public void setDecimalCount(int decimalCount) {
		this.decimalCount = decimalCount;
	}
	
	/**
	 * Configures whether we should remove the thousand separator on encoding.
	 * Defaults to false.
	 */
	@Configurable
	public void setRemoveThousandSeparatorOnEncoding(boolean removeThousandSeparatorOnEncoding) {
		this.removeThousandSeparatorOnEncoding = removeThousandSeparatorOnEncoding;
	}
	
	@Override
	public Double decode(String encodedValue) throws CodecException {
		if (encodedValue == null || encodedValue.trim().isEmpty()) {
			return null;
		}
		
		if (thousandSeparator != null) {
			while (encodedValue.contains(thousandSeparator)) {
				encodedValue = encodedValue.replace(thousandSeparator, "");
			}
		}
		if (!decimalSeparator.equals(".")) {
			encodedValue = encodedValue.replace(decimalSeparator, ".");
		}
		
		int decimalSeparatorPosition = encodedValue.indexOf(".");
		if (decimalSeparatorPosition != -1) {
			int afterSeparatorSize = encodedValue.substring(decimalSeparatorPosition + 1).length();
			
			/*while (afterSeparatorSize < decimalCount) {
				encodedValue += "0";
				afterSeparatorSize++;
			}*/
			
			if (afterSeparatorSize > decimalCount) {
				encodedValue = encodedValue.substring(0, encodedValue.length() - (afterSeparatorSize - decimalCount));
			}
		}
		
		return new Double(encodedValue.trim());
	}
	
	@Override
	public String encode(Double value) throws CodecException {
		if (value == null) {
			return "";
		}
		
		String result = format.format(value);
		int decimalSeparatorPosition = result.indexOf(numberConstants.decimalSeparator());
		
		String separator = numberConstants.decimalSeparator();
		if (separator.equals(".")) {
			separator = "\\.";
		}
		String[] numberParts = result.split(separator);
		
		String number = numberParts.length > 0 ? numberParts[0] : result;
		if (!numberConstants.groupingSeparator().equals(thousandSeparator) && thousandSeparator != null) {
			number = number.replace(numberConstants.groupingSeparator(), thousandSeparator);
		}
		
		String fraction = "";
		if (decimalSeparatorPosition != -1) {
			fraction = numberParts[1];
			int afterSeparatorSize = fraction.length();
			while (afterSeparatorSize < decimalCount) {
				fraction += "0";
				afterSeparatorSize++;
			}
			if (afterSeparatorSize > decimalCount) {
				fraction = fraction.substring(0, fraction.length() - (afterSeparatorSize - decimalCount));
			}
		} else {
			for (int i = 0; i < decimalCount; i++) {
				fraction += "0";
			}
		}
		
		result = number + decimalSeparator + fraction;
		
		if (removeThousandSeparatorOnEncoding) {
			separator = thousandSeparator != null ? thousandSeparator : numberConstants.groupingSeparator();
			while (result.contains(separator)) {
				result = result.replace(separator, "");
			}
		}
		
		return result;
	}

	@Override
	public Class<Double> getValueClass() {
		return Double.class;
	}

}
