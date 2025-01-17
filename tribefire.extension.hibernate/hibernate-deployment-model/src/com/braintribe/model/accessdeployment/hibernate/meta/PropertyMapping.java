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
package com.braintribe.model.accessdeployment.hibernate.meta;


import com.braintribe.model.accessdeployment.jpa.meta.JpaPropertyMapping;
import com.braintribe.model.generic.annotation.Initializer;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

/** Information need for Hibernate mapping of given property. */

public interface PropertyMapping  extends JpaPropertyMapping {

	EntityType<PropertyMapping> T = EntityTypes.T(PropertyMapping.class);

	@Initializer("true")
	Boolean getMapToDb();
	void setMapToDb(Boolean mapToDb);

	/**
	 * Xml code to be used for mapping of this property, e.g.:
	 * {@code <property column="modification_date" length="4" name="modificationDate" type="java.util.Date"/>}
	 * 
	 * Note that setting this value means that only this value (and nothing more) will be used for mapping.
	 */
	String getXml();
	void setXml(String xml);

	String getXmlFileUrl();
	void setXmlFileUrl(String xmlFileUrl);

	String getColumnName();
	void setColumnName(String columnName);

	/**
	 * If set to true, given value should not be configured by the user, but is set automatically by Hibernate/Db. Example is an
	 * auto-increment id property.
	 */
	Boolean getAutoAssignable();
	void setAutoAssignable(Boolean autoAssignable);

	String getType();
	void setType(String type);

	/**
	 * By default Hibernate uses the id column for references. For example, if a <code>Person</code> has a property
	 * <code>friend</code>, which references another <code>Person</code>, Hibernate by default creates a column
	 * <code>friend</code> which holds the value of the id property of the other person. Using <code>propertyRef</code>
	 * one can specify the name of the property to be used instead of the id property.
	 */
	String getReferencedProperty();
	void setReferencedProperty(String referencedProperty);

	Long getLength();
	void setLength(Long length);

	Long getPrecision();
	void setPrecision(Long precision);

	Long getScale();
	void setScale(Long scale);

	String getLazy();
	void setLazy(String lazy);

	String getCascade();
	void setCascade(String cascade);

	String getFetch();
	void setFetch(String fetch);

	Boolean getUnique();
	void setUnique(Boolean unique);

	Boolean getNotNull();
	void setNotNull(Boolean notNull);

	String getIdGeneration();
	void setIdGeneration(String idGeneration);

	String getUniqueKey();
	void setUniqueKey(String uniqueKey);

	String getIndex();
	void setIndex(String index);

	String getForeignKey();
	void setForeignKey(String foreignKey);

	String getCollectionTableName();
	void setCollectionTableName(String collectionTableName);

	String getMapKeySimpleType();
	void setMapKeySimpleType(String mapKeySimpleType);

	Long getMapKeyLength();
	void setMapKeyLength(Long mapKeyLength);

	String getCollectionKeyColumn();
	void setCollectionKeyColumn(String collectionKeyColumn);

	Boolean getCollectionKeyColumnNotNull();
	void setCollectionKeyColumnNotNull(Boolean collectionKeyColumnNotNull);

	String getListIndexColumn();
	void setListIndexColumn(String listIndexColumn);

	String getMapKeyColumn();
	void setMapKeyColumn(String mapKeyColumn);

	String getCollectionElementColumn();
	void setCollectionElementColumn(String collectionElementColumn);

	Boolean getOneToMany();
	void setOneToMany(Boolean oneToMany);

	String getCollectionKeyPropertyRef();
	void setCollectionKeyPropertyRef(String collectionKeyPropertyRef);

	String getCollectionElementFetch();
	void setCollectionElementFetch(String collectionElementFetch);

	/**
	 * Whether or not to ignore invalid references. If enabled, invalid references will be ignored (i.e. entity property
	 * will be set to <code>null</code>). If disabled (which is the default), invalid references will cause an
	 * exception.<br/>
	 * Note that fortunately invalid references usually cannot exist (when foreign key constraints are properly set).
	 */
	Boolean getInvalidReferencesIgnored();
	void setInvalidReferencesIgnored(Boolean invalidReferencesIgnored);

	String getCollectionElementForeignKey();
	void setCollectionElementForeignKey(String collectionElementForeignKey);

	String getMapKeyForeignKey();
	void setMapKeyForeignKey(String mapKeyForeignKey);

	@Override
	default boolean isMapped() {
		return Boolean.TRUE.equals(getMapToDb());
	}

}
