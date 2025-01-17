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

import com.braintribe.model.accessdeployment.jpa.meta.JpaEmbeddable;
import com.braintribe.model.generic.annotation.Initializer;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.meta.data.EntityTypeMetaData;

/** Information need for Hibernate mapping of given entity. */
public interface EntityMapping extends EntityTypeMetaData {

	EntityType<EntityMapping> T = EntityTypes.T(EntityMapping.class);

	@Initializer("true")
	Boolean getMapToDb();
	void setMapToDb(Boolean mapToDb);

	/** @deprecated use {@link JpaEmbeddable} instead */
	@Deprecated
	boolean getIsEmbeddable();
	@Deprecated
	void setIsEmbeddable(boolean isEmbeddable);

	/**
	 * Xml code to be used as "content" of the {@code <class>} tag (excluding the {@code <class>} tag). May be something
	 * like e.g.
	 * 
	 * {@code
	 * <id name="id"><generator class="native" /></id>
	 * <property name="type" column_id="some_name_for_type" />
	 * <set name="properties" lazy="true" inverse="true" cascade="all-delete-orphan">
	 * 		<key column="USER_ID" />
	 * 		<one-to-many class="org.applabs.base.UserProp" />
	 * </set>	 
	 * } Note that setting this value means that only this value (and nothing more) will be used for mapping.
	 */
	String getXml();
	void setXml(String xml);

	String getXmlFileUrl();
	void setXmlFileUrl(String xmlFileUrl);

	String getTableName();
	void setTableName(String tableName);

	String getDiscriminatorColumnName();
	void setDiscriminatorColumnName(String discriminatorColumnName);

	String getDiscriminatorFormula();
	void setDiscriminatorFormula(String discriminatorFormula);
	
	String getDiscriminatorType();
	void setDiscriminatorType(String discriminatorType);

	String getDiscriminatorValue();
	void setDiscriminatorValue(String discriminatorValue);

	String getSchema();
	void setSchema(String schema);

	String getCatalog();
	void setCatalog(String catalog);

	boolean getForceMapping();
	void setForceMapping(boolean forceMapping);

}
