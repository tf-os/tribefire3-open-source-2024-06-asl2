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

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import com.braintribe.logging.Logger;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.reflection.SimpleType;
import com.braintribe.model.meta.GmEnumConstant;
import com.braintribe.model.meta.GmEnumType;

import tribefire.extension.xml.schemed.mapper.api.MapperInfoRegistry;
import tribefire.extension.xml.schemed.mapping.metadata.EnumConstantMappingMetaData;
import tribefire.extension.xml.schemed.marshaller.xml.SchemedXmlMarshallingException;

/**
 * a decoder for {@link SimpleType} values (as represented by {@link tribefire.extension.xml.schemed.model.xsd.SimpleType}
 * 
 * @author pit
 *
 */
public class ValueDecoder {
	private static Logger log = Logger.getLogger(ValueDecoder.class);

	/**
	 * convert the object to the correct type .. 
	 * @param type - the {@link GenericModelType}
	 * @param stringValue - the values represented as a {@link String} 
	 * @return - the actual type name as in the XML
	 */
	public static Object decode(GenericModelType type, String stringValue, String mappedType) throws SchemedXmlMarshallingException {
		if (stringValue == null)
			return stringValue;
		Object value = null;
		switch (type.getTypeCode()) {
		case dateType:			
			if (
					mappedType == null ||
					mappedType.equalsIgnoreCase( "dateTime")
					) {
				value = new DateInterpreter().parseDateTime( stringValue);
			} 
			else if (mappedType.equalsIgnoreCase("time")) {
				value = new DateInterpreter().parseTime( stringValue);
			} 
			else {
				value = new DateInterpreter().parseDate( stringValue);	
			}

			break;
		case integerType:
			if (stringValue == null || stringValue.length() == 0) {
				value = Integer.valueOf(0);
			}
			else {
				value = Integer.valueOf(stringValue);
			}
			break;
		case longType:
			if (stringValue == null || stringValue.length() == 0) {
				value = Long.valueOf(0);
			}
			else {
				value = Long.valueOf( stringValue);
			}
			break;
		case floatType:
			if (stringValue == null || stringValue.length() == 0) {
				value = Float.valueOf(0);
			}
			else  {
				value = Float.valueOf( stringValue);
			}
			break;
		case doubleType:
			if (stringValue == null || stringValue.length() == 0) {
				value = Double.valueOf(0);
			}
			else {
				value = Double.valueOf( stringValue);
			}
			break;
		case decimalType:
			if (stringValue == null || stringValue.length() == 0) {
				value = BigDecimal.valueOf(0);
			}
			else {
				value = BigDecimal.valueOf( Double.valueOf(stringValue));
			}
			break;
		case stringType:
			value = stringValue;
			break;			
		case booleanType:
			if (stringValue == null || stringValue.length() == 0) {
				value = Boolean.valueOf(false);
			}
			value = Boolean.valueOf( stringValue);
			break;
		default:
			log.warn("unsupported type [" + type.getTypeSignature() + ":" + type.getTypeCode() + "]");
			break;
		}

		return value;
	}
	
	@SuppressWarnings("rawtypes")
	public static Enum decodeEnum(MapperInfoRegistry mapper, Map<String,String> namespaceMap, String xsdType,  GenericModelType modelType, String childValue) throws SchemedXmlMarshallingException {
		/*
		String signature = mapper.getMappedTypeSignature( xsdType, namespaceMap);
		if (signature == null)
			signature = xsdType;
		if (signature == null) {
			String msg = "no corresponding enum type found for [" + xsdType + "]"; 
			log.error( msg);
			throw new SchemedXmlMarshallingException( msg);
		}
		*/
		String signature = modelType.getTypeSignature();
		GmEnumType enumType = mapper.getMatchingEnumType( signature);
		// build a map of possible values - there might a translation setup in the mapping meta model
		Map<String,String> values = new HashMap<String,String>();
		for (GmEnumConstant constant : enumType.getConstants()) {
			EnumConstantMappingMetaData constantMappingData = mapper.getEnumConstantMetaData(constant);
			if (constantMappingData != null) {
				values.put( constantMappingData.getXsdName(), constant.getName());
			} else  {
				values.put( constant.getName(), constant.getName());
			}
		}
		// 
		String actualValue = values.get( childValue);
		@SuppressWarnings("unchecked")
		Class<Enum> enumClass = (Class<Enum>) modelType.getJavaType();
		@SuppressWarnings("unchecked")
		Enum enumValue = Enum.valueOf(enumClass, actualValue);
		return enumValue;
	}

	
}
