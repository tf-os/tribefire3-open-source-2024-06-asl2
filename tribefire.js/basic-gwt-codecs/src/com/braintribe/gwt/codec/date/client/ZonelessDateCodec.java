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
package com.braintribe.gwt.codec.date.client;

import java.util.Date;

import com.braintribe.codec.Codec;
import com.braintribe.codec.CodecException;

public class ZonelessDateCodec implements Codec<Date, Date> {
	/*private static final String datePatternWithZone = "yyyy-MM-dd HH:mm:ss SSS Z";
	private static final String datePatternWithoutZone = "yyyy-MM-dd HH:mm:ss SSS";
	private DateTimeFormat formatWithZone = DateTimeFormat.getFormat(datePatternWithZone);
	private DateTimeFormat formatWithoutZone = DateTimeFormat.getFormat(datePatternWithoutZone);*/
	
	public static ZonelessDateCodec INSTANCE = new ZonelessDateCodec();

	/**
	 * convert from zoneless (UTC) to local zone
	 */
	@SuppressWarnings("deprecation")
	@Override
	public Date encode(Date zonedDate) throws CodecException {
		if (zonedDate == null)
			return null;
		
		int minutesOffset = zonedDate.getTimezoneOffset();
		
		long shiftedTime = zonedDate.getTime() - minutesOffset * 60_000;
		
		Date zonelessDate = new Date(shiftedTime);
		return zonelessDate;
		
		/*String formattedZonedDate = formatWithoutZone.format(zonedDate);
		formattedZonedDate += " +0000"; 
		
		Date zonelessDate = formatWithZone.parse(formattedZonedDate);
		return zonelessDate;*/
	}

	/**
	 * convert from local zone to zoneless (UTC)
	 */
	@SuppressWarnings("deprecation")
	@Override
	public Date decode(Date zonelessDate) throws CodecException {
		if (zonelessDate == null)
			return null;
		
		int minutesOffset = zonelessDate.getTimezoneOffset();
		
		long shiftedTime = zonelessDate.getTime() + minutesOffset * 60_000;
		
		Date zonedDate = new Date(shiftedTime);
		return zonedDate;
		
		/*String formattedZonelessDate = formatWithZone.format(zonelessDate, TimeZone.createTimeZone(0));
		formattedZonelessDate = formattedZonelessDate.substring(0, datePatternWithoutZone.length());
		
		Date zonedDate = formatWithoutZone.parse(formattedZonelessDate);
		return zonedDate;*/
	}

	@Override
	public Class<Date> getValueClass() {
		return Date.class;
	}
	
}