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
package tribefire.extension.spreadsheet.processing.importing.common;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalQueries;
import java.util.Date;
import java.util.Locale;
import java.util.function.Function;

import com.braintribe.model.meta.data.constraint.DateClipping;

public class DateParser extends DateClipper implements Function<String, Date> {

	private DateTimeFormatter formatter;
	private String pattern;
	private Locale locale;
	private boolean emptyStringsAreNullDates;

	public DateParser(String pattern, ZoneId defaultZone, Locale locale, DateClipping dateClipping, boolean emptyStringsAreNullDates) {
		super(dateClipping, defaultZone);
		this.pattern = pattern;
		this.locale = locale;
		this.emptyStringsAreNullDates = emptyStringsAreNullDates;
		formatter = DateTimeFormatter.ofPattern(pattern) //
				.withZone(defaultZone).withLocale(locale);
	}

	@Override
	public Date apply(String s) {
		if (emptyStringsAreNullDates && s.isEmpty())
			return null;

		try {
			TemporalAccessor accessor = formatter.parse(s);
			LocalTime time = accessor.query(TemporalQueries.localTime());
			LocalDate date = accessor.query(TemporalQueries.localDate());
			ZoneId zone = accessor.query(TemporalQueries.zone());
			if (time == null)
				time = LocalTime.of(0, 0);
			if (date == null)
				date = LocalDate.of(0, 1, 1);
			Date result = Date.from(ZonedDateTime.of(date, time, zone).toInstant());
			result = clip(result);
			return result;
		} catch (Exception e) {
			throw new IllegalArgumentException("Date [" + s + "] could not be parsed with the meta-data mapped format: pattern [" + pattern
					+ "], defaultTimeZone [" + getZoneId() + "], locale [" + locale.toLanguageTag() + "]", e);
		}
	}
}
