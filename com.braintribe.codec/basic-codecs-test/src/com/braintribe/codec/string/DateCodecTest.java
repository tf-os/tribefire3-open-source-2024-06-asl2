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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.text.SimpleDateFormat;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.junit.Test;

public class DateCodecTest {

	@Test
	public void testParser() throws Exception {
		
		DateTimeFormatter[] formatters = DateCodec.ALL_FORMATTERS;

		Date date = new Date();
		ZonedDateTime dateTime = ZonedDateTime.ofInstant(date.toInstant(), ZoneOffset.UTC);
		
		for (DateTimeFormatter dtf : formatters) {

			String nowString = dtf.format(dateTime);
			System.out.println("Parsing "+nowString+" with "+dtf.toString());

			Date resultDate = DateCodec.failsafeDateParse(nowString);
			assertThat(resultDate).describedAs("Parsed "+nowString+" with "+dtf.toString(), new Object[0]).isNotNull();
			
			ZonedDateTime dateTimeToVerify = ZonedDateTime.ofInstant(resultDate.toInstant(), ZoneOffset.UTC);
			String formattedDateToVerify = dtf.format(dateTimeToVerify);
			
			assertThat(formattedDateToVerify).isEqualTo(nowString);
			
		}
		
		assertThat(date).isEqualTo(DateCodec.failsafeDateParse("" + date.getTime()));
		
		
	}
	
	@Test
	public void compareToSimpleDateFormat() throws Exception {
		String[] formats = new String[] {
				"yyyy.MM.dd HH:mm:ss",
				"yyyy-MM-dd'T'HH:mm:ss.SSSZ"
		};
		
		Date now = new Date();
		
		for (String f : formats) {
			
			SimpleDateFormat sdf = new SimpleDateFormat(f);
			String sdfResult = sdf.format(now);
			
			System.out.println("Checking: "+sdfResult);
			
			DateCodec dc = new DateCodec(f);
			String dcResult = dc.encode(now);
			
			assertThat(dcResult).isEqualTo(sdfResult);
		}
	}
	
	@Test
	public void testLeniency() throws Exception {
		
		GregorianCalendar gc = new GregorianCalendar();
		Date now = gc.getTime();
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		String input = sdf.format(now);

		System.out.println("Input: "+input);
		
		DateCodec dcWithoutLeniency = new DateCodec("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		
		assertThatThrownBy(() -> dcWithoutLeniency.decode(input));

		DateCodec dcWithLeniency = new DateCodec("yyyy-MM-dd'T'HH:mm:ss.SSSZ", true);
		
		Date actual = dcWithLeniency.decode(input);
		
		GregorianCalendar actualGc = new GregorianCalendar();
		actualGc.setTime(actual);
		
		assertThat(actualGc.get(Calendar.DAY_OF_MONTH)).isEqualTo(gc.get(Calendar.DAY_OF_MONTH));
		assertThat(actualGc.get(Calendar.MONTH)).isEqualTo(gc.get(Calendar.MONTH));
		assertThat(actualGc.get(Calendar.YEAR)).isEqualTo(gc.get(Calendar.YEAR));
		assertThat(actualGc.get(Calendar.HOUR_OF_DAY)).isEqualTo(0);

	}
	
	@Test
	public void testTimezoneSupport() throws Exception {
		String dateString = "2018-03-28T17:16:56.084+0200";
		
		DateCodec dc = new DateCodec("yyyy-MM-dd'T'HH:mm:ss.SSSZ", false);

		GregorianCalendar gc = new GregorianCalendar();
		Date decoded = dc.decode(dateString);
		gc.setTime(decoded);
		gc.setTimeZone(TimeZone.getTimeZone("GMT"));
		
		assertThat(gc.get(Calendar.DAY_OF_MONTH)).isEqualTo(28);
		assertThat(gc.get(Calendar.MONTH)).isEqualTo(2);
		assertThat(gc.get(Calendar.YEAR)).isEqualTo(2018);
		assertThat(gc.get(Calendar.HOUR_OF_DAY)).isEqualTo(15); //timezone is set to GMT; hence 2 less
		assertThat(gc.get(Calendar.MINUTE)).isEqualTo(16);
		assertThat(gc.get(Calendar.SECOND)).isEqualTo(56);
		assertThat(gc.get(Calendar.MILLISECOND)).isEqualTo(84);
		assertThat(gc.get(Calendar.ZONE_OFFSET)).isEqualTo(0);
	}
	
}
