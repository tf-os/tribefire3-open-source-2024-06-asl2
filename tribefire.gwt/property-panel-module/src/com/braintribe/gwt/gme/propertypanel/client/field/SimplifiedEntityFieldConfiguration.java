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
package com.braintribe.gwt.gme.propertypanel.client.field;

import java.util.ArrayList;
import java.util.List;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.processing.session.api.persistence.PersistenceGmSession;
import com.braintribe.model.query.EntityQuery;

public class SimplifiedEntityFieldConfiguration {

	private EntityType<? extends GenericEntity> entityType;
	private List<SimplifiedEntityFieldTabConfiguration> tabsConfig;
	private PersistenceGmSession gmSession;
	private String useCase;
	private boolean useQueryTabAsDefault;
	private String title;
	private Integer dialogWidth;
	private Integer dialogHeight;

	public SimplifiedEntityFieldConfiguration(EntityType<? extends GenericEntity> entityType, PersistenceGmSession gmSession, String useCase, String title) {
		this(entityType, (List<SimplifiedEntityFieldTabConfiguration>) null, gmSession, useCase, false, title);
	}

	public SimplifiedEntityFieldConfiguration(EntityType<? extends GenericEntity> entityType, List<SimplifiedEntityFieldTabConfiguration> tabsConfig,
			PersistenceGmSession gmSession, String useCase, boolean useQueryTabAsDefault, String title) {
		super();
		this.entityType = entityType;
		this.tabsConfig = tabsConfig;
		this.gmSession = gmSession;
		this.useCase = useCase;
		this.useQueryTabAsDefault = useQueryTabAsDefault;
		this.title = title;
	}

	public SimplifiedEntityFieldConfiguration(EntityType<? extends GenericEntity> entityType, EntityQuery entityQuery, PersistenceGmSession gmSession,
			String useCase, boolean useQueryTabAsDefault, String title) {
		this(entityType, (List<SimplifiedEntityFieldTabConfiguration>) null, gmSession, useCase, useQueryTabAsDefault, title);
		setEntityQuery(entityQuery);
	}

	public EntityType<? extends GenericEntity> getEntityType() {
		return this.entityType;
	}

	public void setEntityType(EntityType<? extends GenericEntity> entityType) {
		this.entityType = entityType;
	}

	public PersistenceGmSession getGmSession() {
		return this.gmSession;
	}

	public void setGmSession(PersistenceGmSession gmSession) {
		this.gmSession = gmSession;
	}

	public String getUseCase() {
		return this.useCase;
	}

	public void setUseCase(String useCase) {
		this.useCase = useCase;
	}
	
	public String getTitle() {
		return title;
	}

	public boolean isUseQueryTabAsDefault() {
		return this.useQueryTabAsDefault;
	}

	public void setUseQueryTabAsDefault(boolean useQueryTabAsDefault) {
		this.useQueryTabAsDefault = useQueryTabAsDefault;
	}

	public void setEntityQuery(EntityQuery entityQuery) {
		if (entityQuery == null) {
			this.tabsConfig = null;
		} else {
			if (this.tabsConfig == null)
				this.tabsConfig = new ArrayList<>();

			this.tabsConfig.add(new SimplifiedEntityFieldTabConfiguration(entityQuery));
		}
	}

	public EntityQuery getEntityQuery() {
		return (this.tabsConfig != null && this.tabsConfig.size() > 0 ? this.tabsConfig.get(0).getEntityQuery() : null);
	}

	public void setTabsConfig(List<SimplifiedEntityFieldTabConfiguration> tabsConfig) {
		this.tabsConfig = tabsConfig;
	}

	public List<SimplifiedEntityFieldTabConfiguration> getTabsConfig() {
		return this.tabsConfig;
	}
	
	public void setDialogWidth(Integer dialogWidth) {
		this.dialogWidth = dialogWidth;
	}
	
	public Integer getDialogWidth() {
		return dialogWidth;
	}
	
	public void setDialogHeight(Integer dialogHeight) {
		this.dialogHeight = dialogHeight;
	}
	
	public Integer getDialogHeight() {
		return dialogHeight;
	}
}
