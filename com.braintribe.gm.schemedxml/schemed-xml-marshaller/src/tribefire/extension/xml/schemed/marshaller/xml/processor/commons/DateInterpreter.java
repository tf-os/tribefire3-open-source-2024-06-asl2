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
package tribefire.extension.xml.schemed.marshaller.xml.processor.commons;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import com.braintribe.logging.Logger;

import tribefire.extension.xml.schemed.marshaller.xml.SchemedXmlMarshallingException;

/**
 * helper to support the two xsd date related types, from and to {@link Date}
 * <br> 
 * <ul>
 * <li>xsd:dateTime</li>
 * <li>xsd:date</li>
 * <li>xsd:time</li>
 * </ul>
 *  it uses jaxp's {@link DatatypeFactory}, and the {@link XMLGregorianCalendar} of it
 * </br>
 * @author pit
 *
 */
public class DateInterpreter {
	
	private static Logger log = Logger.getLogger(DateInterpreter.class);	
	private static DatatypeFactory df; 
	
	public DateInterpreter() {
		if (df == null) {
			try {
				df = DatatypeFactory.newInstance();
			} catch (DatatypeConfigurationException e) {
				log.error( "cannot generate DatatypeFactory", e);
			}
		}
	}
	
	/**
	 * parse a string in xsd:dateTime format 
	 * @param dateAsString - the string 
	 * @return - the resulting {@link Date}
	 * @throws SchemedXmlMarshallingException
	 */
	public Date parseDateTime( String dateAsString) throws SchemedXmlMarshallingException {
		if (dateAsString == null || dateAsString.length() == 0) {
			return null;
		}
		try {		
			XMLGregorianCalendar dateTime = df.newXMLGregorianCalendar(dateAsString);			
			return dateTime.toGregorianCalendar().getTime();
		} catch (Exception e) {
			String msg = "cannot parse datetime from [" + dateAsString + "]";
			log.error( msg, e);
			throw new SchemedXmlMarshallingException( msg, e);
		}
	}
	
	/**
	 * format a {@link Date} as an xsd:dateTime
	 * @param date - the {@link Date}
	 * @return - the resulting string 
	 * @throws SchemedXmlMarshallingException
	 */
	public String formatDateTime( Date date) throws SchemedXmlMarshallingException {
		if (date == null)
			return "";
		try {
			GregorianCalendar calendar = new GregorianCalendar();
			calendar.setTime(date);			
			XMLGregorianCalendar dateTime = df.newXMLGregorianCalendar(calendar);
			return dateTime.toString();
			
		} catch (Exception e) {
			String msg = "cannot print dateTime from [" + date + "]";
			log.error( msg, e);
			throw new SchemedXmlMarshallingException( msg, e);
		}
	}
	
	/**
	 * parse a string in xsd:date format
	 * @param dateAsString - the string
	 * @return - the resulting {@link Date}
	 * @throws SchemedXmlMarshallingException
	 */
	public Date parseDate( String dateAsString) throws SchemedXmlMarshallingException {
		if (dateAsString == null || dateAsString.length() == 0) {
			return null;
		}
		try {
			XMLGregorianCalendar dateTime = df.newXMLGregorianCalendar(dateAsString);			
			return dateTime.toGregorianCalendar().getTime();
		} catch (Exception e) {
			String msg = "cannot parse date from [" + dateAsString + "]";
			log.error( msg, e);
			throw new SchemedXmlMarshallingException( msg, e);
		}
	}
	
	/**
	 * format a {@link Date} as an xsd:date
	 * @param date - the {@link Date}
	 * @return - the result string 
	 * @throws SchemedXmlMarshallingException
	 */
	public String formatDate( Date date) throws SchemedXmlMarshallingException {
		if (date == null)
			return "";
		try {
			GregorianCalendar calendar = new GregorianCalendar();
			calendar.setTime(date);		
			
			XMLGregorianCalendar dateTime = df.newXMLGregorianCalendarDate(calendar.get( Calendar.YEAR), calendar.get( Calendar.MONDAY) + 1, calendar.get( Calendar.DAY_OF_MONTH), DatatypeConstants.FIELD_UNDEFINED);
			return dateTime.toString();
		} catch (Exception e) {
			String msg = "cannot print date from [" + date + "]";
			log.error( msg, e);
			throw new SchemedXmlMarshallingException( msg, e);
		}
	}
	
	/**
	 * parse an xsd:time
	 * @param timeAsString - the string
	 * @return - the resulting {@link Date}
	 * @throws SchemedXmlMarshallingException
	 */
	public Date parseTime( String timeAsString) throws SchemedXmlMarshallingException {
		if (timeAsString == null || timeAsString.length() == 0) {
			return null;
		}
		try {			
			XMLGregorianCalendar dateTime = df.newXMLGregorianCalendar(timeAsString);			
			return dateTime.toGregorianCalendar().getTime();
		} catch (Exception e) {
			String msg = "cannot parse date from [" + timeAsString + "]";
			log.error( msg, e);
			throw new SchemedXmlMarshallingException( msg, e);
		}
	}
	
	/**
	 * format a {@link Date} as an xsd:time
	 * @param date - the {@link Date}
	 * @return - the resulting String 
	 * @throws SchemedXmlMarshallingException
	 */
	public String formatTime( Date date) throws SchemedXmlMarshallingException {
		if (date == null)
			return "";
		try {
			GregorianCalendar calendar = new GregorianCalendar();
			calendar.setTime(date);
						
			XMLGregorianCalendar dateTime = df.newXMLGregorianCalendarTime(calendar.get( Calendar.HOUR), calendar.get( Calendar.MINUTE), calendar.get( Calendar.SECOND), DatatypeConstants.FIELD_UNDEFINED, DatatypeConstants.FIELD_UNDEFINED);
			return dateTime.toString();
		} catch (Exception e) {
			String msg = "cannot print date from [" + date + "]";
			log.error( msg, e);
			throw new SchemedXmlMarshallingException( msg, e);
		}
	}
	
	
	
}
