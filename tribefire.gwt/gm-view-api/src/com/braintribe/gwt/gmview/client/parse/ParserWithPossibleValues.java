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
package com.braintribe.gwt.gmview.client.parse;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.braintribe.gwt.gmview.action.client.ParserResult;
import com.braintribe.gwt.gmview.metadata.client.SelectiveInformationResolver;
import com.braintribe.model.generic.GMF;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EnumType;
import com.braintribe.model.generic.reflection.SimpleType;
import com.braintribe.model.generic.typecondition.TypeCondition;
import com.braintribe.model.generic.value.EnumReference;
import com.braintribe.model.meta.GmEnumConstant;
import com.braintribe.model.meta.GmEnumType;
import com.braintribe.model.processing.session.api.persistence.PersistenceGmSession;

/**
 * Implementation similar to the {@link SimpleTypeParser}, but it accepts a list of possible values.
 * Plus, it also supports entities and enum constants.
 * @author michel.docouto
 *
 */
public class ParserWithPossibleValues extends SimpleTypeParser {
	
	private List<Object> possibleValues;
	private boolean ignoreSimpleTypes = false;
	private PersistenceGmSession gmSession;

	/**
	 * Configures the possible values and the data session.
	 */
	public void configurePossibleValues(List<Object> possibleValues, PersistenceGmSession gmSession) {
		this.possibleValues = possibleValues;
		this.gmSession = gmSession;
		
		for (Object possibleValue : possibleValues) {
			if (possibleValue instanceof GenericEntity || possibleValue instanceof Enum) {
				ignoreSimpleTypes = true;
				break;
			}
		}
	}
	
	public ParserWithPossibleValues() {
		super();
	}
	
	@Override
	protected void addResultForEmptyValue(List<ParserResult> result, ParserArgument parserArgument) {
		if (!ignoreSimpleTypes) {
			for (Map.Entry<SimpleType, Function<String, ?>> parserEntry : valueParsers.entrySet()) {
				SimpleType type = parserEntry.getKey();
				
				if (matchingType(parserArgument, type)) {
					String typeSignature = type.getTypeSignature();
					
					for (Object possibleValue : possibleValues)
						result.add(new ParserResult(typeSignature, typeSignature, possibleValue));
					return;
				}
			}
		}
		
		for (Object possibleValue : possibleValues) {
			if (!(possibleValue instanceof GenericEntity || possibleValue instanceof Enum))
				continue;
			
			if (possibleValue instanceof GmEnumConstant) {
				GmEnumConstant enumConstant = (GmEnumConstant) possibleValue;
				GmEnumType gmEnumType = enumConstant.getDeclaringType();
				if (matchingEnumType(parserArgument.getTypeCondition(), gmEnumType)) {
					result.add(new ParserResult(enumConstant.getName(), gmEnumType.getTypeSignature(),
							prepareEnumReference(enumConstant)));
				}
			} else if (possibleValue instanceof GenericEntity) {
				String typeSignature = ((GenericEntity) possibleValue).entityType().getTypeSignature();
				result.add(new ParserResult(typeSignature, typeSignature, possibleValue));
			} else if (possibleValue instanceof Enum) {
				Enum<?> theEnum = (Enum<?>) possibleValue;
				Class<? extends Enum<?>> enumClass = (Class<? extends Enum<?>>) theEnum.getClass();
				EnumType enumType = GMF.getTypeReflection().getEnumType(enumClass);
				result.add(new ParserResult(theEnum.name(), enumType.getTypeSignature(), theEnum));
			}
		}
	}
	
	@Override
	protected void addResultForNonEmptyValue(List<ParserResult> result, ParserArgument parserArgument) {
		String parserArgumentValue = parserArgument.getValue().trim().toLowerCase();
		
		if (!ignoreSimpleTypes) {
			for (Map.Entry<SimpleType, Function<String, ?>> parserEntry : valueParsers.entrySet()) {
				SimpleType type = parserEntry.getKey();
	
				if (matchingType(parserArgument, type)) {
					Function<String, ?> parser = parserEntry.getValue();
	
					Object parsedValue = safeParse(parser, parserArgumentValue);
	
					if (parsedValue != null) {
						for (Object possibleValue : possibleValues) {
							if (possibleValue.toString().contains(parserArgumentValue)) {
								String typeSignature = type.getTypeSignature();
								result.add(new ParserResult(typeSignature, typeSignature, possibleValue));
							}
						}
					}
					
					return;
				}
			}
		}
		
		for (Object possibleValue : possibleValues) {
			if (!(possibleValue instanceof GenericEntity || possibleValue instanceof Enum))
				continue;
			
			if (possibleValue instanceof GmEnumConstant) {
				GmEnumConstant enumConstant = (GmEnumConstant) possibleValue;
				GmEnumType gmEnumType = enumConstant.getDeclaringType();
				String typeSignature = gmEnumType.getTypeSignature();
				String description = typeSignature + "." + enumConstant.getName();
				if (matchingEnumType(parserArgument.getTypeCondition(), gmEnumType) && description.toLowerCase().contains(parserArgumentValue))
					result.add(new ParserResult(enumConstant.getName(), typeSignature, prepareEnumReference(enumConstant)));
			} else if (possibleValue instanceof GenericEntity) {
				String possibleValueString = SelectiveInformationResolver.resolve((GenericEntity) possibleValue, null);
				
				if (possibleValueString.contains(parserArgumentValue)) {
					String typeSignature = ((GenericEntity) possibleValue).entityType().getTypeSignature();
					result.add(new ParserResult(typeSignature, typeSignature, possibleValue));
				}
			} else if (possibleValue instanceof Enum) {
				Enum<?> theEnum = (Enum<?>) possibleValue;
				Class<? extends Enum<?>> enumClass = (Class<? extends Enum<?>>) theEnum.getClass();
				EnumType enumType = GMF.getTypeReflection().getEnumType(enumClass);
				String typeSignature = enumType.getTypeSignature();
				String description = typeSignature + "." + theEnum.name();
				if (matchingEnumType(parserArgument.getTypeCondition(), enumType) && description.toLowerCase().contains(parserArgumentValue))
					result.add(new ParserResult(theEnum.name(), enumType.getTypeSignature(), theEnum));
			}
		}
	}
	
	private boolean matchingEnumType(TypeCondition tc, GmEnumType enumType) {
		return tc == null || tc.matches(enumType);
	}
	
	private boolean matchingEnumType(TypeCondition tc, EnumType enumType) {
		return tc == null || tc.matches(enumType);
	}
	
	private EnumReference prepareEnumReference(GmEnumConstant enumConstant) {
		EnumReference ref = gmSession.create(EnumReference.T);
		ref.setTypeSignature(enumConstant.getDeclaringType().getTypeSignature());
		ref.setConstant(enumConstant.getName());
		return ref;
	}

}
