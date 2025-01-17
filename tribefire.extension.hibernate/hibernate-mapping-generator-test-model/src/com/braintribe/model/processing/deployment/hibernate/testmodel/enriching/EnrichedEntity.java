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
package com.braintribe.model.processing.deployment.hibernate.testmodel.enriching;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.braintribe.model.generic.StandardIdentifiable;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

/**
 * Test entity to be mapped based on {@code PropertyMetaData}(s) ({@code MaxLength} and {@code PropertyMapping}) and {@code PropertyHint}(s)
 * 
 */

public interface EnrichedEntity extends StandardIdentifiable {

	EntityType<EnrichedEntity> T = EntityTypes.T(EnrichedEntity.class);

	// @formatter:off
	/** string with no MaxLength nor PropertyTypeHint applied */
	String getRegularString();
	void setRegularString(String regularString);

	/**
	 * string enriched with MaxLength meta data 
	 * e.g. MaxLength length = new MaxLength();
	 * 	    length.setLength(1000L);
	 */
	String getMaxLengthString();
	void setMaxLengthString(String maxLengthString);

	/**
	 * string hinted as text with PropertyTypeHint
	 * e.g. com.braintribe.model.processing.deployment.hibernate.testmodel.enriching.EnrichedEntity:typeHintString:string(1000),
	 */
	String getTextHintString();
	void setTextHintString(String textHintString);

	/**
	 * string hinted as clob with PropertyTypeHint
	 * e.g. com.braintribe.model.processing.deployment.hibernate.testmodel.enriching.EnrichedEntity:clobHintString:clob,
	 */
	String getClobHintString();
	void setClobHintString(String clobHintString);

	/**
	 * string enriched with MaxLength meta data and hinted as text with PropertyTypeHint
	 * e.g. MaxLength length = new MaxLength();
	 * 	    length.setLength(2000L);
	 * 
	 * com.braintribe.model.processing.deployment.hibernate.testmodel.enriching.EnrichedEntity:maxLengthAndTextHintString:string(3000),
	 */
	String getMaxLengthAndTextHintString();
	void setMaxLengthAndTextHintString(String maxLengthAndTextHintString);

	/**
	 * string enriched with MaxLength meta data and hinted as clob with PropertyTypeHint
	 * e.g. com.braintribe.model.processing.deployment.hibernate.testmodel.enriching.EnrichedEntity:maxLengthAndClobHintString:clob(1000),
	 */
	String getMaxLengthAndClobHintString();
	void setMaxLengthAndClobHintString(String maxLengthAndClobHintString);

	/**
	 * set hinted as text on its elements with PropertyTypeHint
	 * e.g. com.braintribe.model.processing.deployment.hibernate.testmodel.enriching.EnrichedEntity:typeHintStringSet:string(2000),
	 */
	Set<String> getTextHintStringSet();
	void setTextHintStringSet(Set<String> textHintStringSet);

	/**
	 * set enriched with MaxLength meta data 
	 * e.g. MaxLength length = new MaxLength();
	 * 	    length.setLength(3000L);
	 */
	Set<String> getMaxLengthSet();
	void setMaxLengthSet(Set<String> maxLengthSet);

	/**
	 * list hinted as text on its elements with PropertyTypeHint
	 * e.g. com.braintribe.model.processing.deployment.hibernate.testmodel.enriching.EnrichedEntity:typeHintStringList:string(2000),
	 */
	List<String> getTextHintStringList();
	void setTextHintStringList(List<String> textHintStringList);

	/**
	 * list enriched with MaxLength meta data 
	 * e.g. MaxLength length = new MaxLength();
	 * 	    length.setLength(3000L);
	 */
	List<String> getMaxLengthList();
	void setMaxLengthList(List<String> maxLengthList);

	/**
	 * map enriched with MaxLength meta data 
	 * e.g. MaxLength length = new MaxLength();
	 * 	    length.setLength(2000L);
	 */
	Map<String, String> getMaxLengthMap();
	void setMaxLengthMap(Map<String, String> maxLengthMap);

	/**
	 * map hinted as text on its key with MapPropertyTypeHint
	 * e.g. com.braintribe.model.processing.deployment.hibernate.testmodel.enriching.EnrichedEntity:typeHintStringMapKey:[k]string(1000),
	 */
	Map<String, Integer> getTextHintStringMapKey();
	void setTextHintStringMapKey(Map<String, Integer> textHintStringMapKey);

	/**
	 * map hinted as text on its value with MapPropertyTypeHint
	 * e.g. com.braintribe.model.processing.deployment.hibernate.testmodel.enriching.EnrichedEntity:typeHintStringMapValue:string(4000),
	 */
	Map<Integer, String> getTextHintStringMapValue();
	void setTextHintStringMapValue(Map<Integer, String> textHintStringMapValue);

	/**
	 * map hinted as text on its key and value with MapPropertyTypeHint
	 * e.g. com.braintribe.model.processing.deployment.hibernate.testmodel.enriching.EnrichedEntity:typeHintStringMapKeyAndValue:clob:[k]string(2000),
	 */
	Map<String, String> getTextHintStringMapKeyAndValue();
	void setTextHintStringMapKeyAndValue(Map<String, String> textHintStringMapKeyAndValue);

	/**
	 * string hinted as unique
	 */
	String getUniqueHintedString();
	void setUniqueHintedString(String uniqueHintedString);

	/**
	 * number hinted as unique
	 */
	Long getUniqueHintedNumber();
	void setUniqueHintedNumber(Long uniqueHintedNumber);

	/**
	 * string hinted as explicitly not-unique
	 */
	String getNonUniqueHintedString();
	void setNonUniqueHintedString(String nonUniqueHintedString);

	/**
	 * number hinted as explicitly not-unique
	 */
	Long getNonUniqueHintedNumber();
	void setNonUniqueHintedNumber(Long nonUniqueHintedNumber);

	/**
	 * double to be hinted with scale and precision attributes
	 */
	Double getDoubleWithPrecisionScale();
	void setDoubleWithPrecisionScale(Double doubleWithPrecisionScale);

	/**
	 * two properties to share a common index
	 */
	String getSharedIndexA();
	void setSharedIndexA(String sharedIndexA);

	String getSharedIndexB();
	void setSharedIndexB(String sharedIndexB);

	/**
	 * two properties to share a common unique index
	 */
	String getSharedUniqueIndexA();
	void setSharedUniqueIndexA(String sharedUniqueIndexA);

	String getSharedUniqueIndexB();
	void setSharedUniqueIndexB(String sharedUniqueIndexB);
	// @formatter:on

}
