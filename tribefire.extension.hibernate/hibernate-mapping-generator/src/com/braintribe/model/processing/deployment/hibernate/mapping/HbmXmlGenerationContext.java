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
package com.braintribe.model.processing.deployment.hibernate.mapping;

import static com.braintribe.utils.lcd.NullSafe.nonNull;

import java.io.File;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.braintribe.model.access.hibernate.meta.aspects.HibernateDialectAspect;
import com.braintribe.model.accessdeployment.hibernate.HibernateDialect;
import com.braintribe.model.meta.GmEntityType;
import com.braintribe.model.meta.GmMetaModel;
import com.braintribe.model.meta.GmProperty;
import com.braintribe.model.processing.deployment.hibernate.mapping.hints.EntityHintProvider;
import com.braintribe.model.processing.deployment.hibernate.mapping.render.context.MappingMetaDataResolver;
import com.braintribe.model.processing.meta.cmd.CmdResolver;
import com.braintribe.model.processing.meta.cmd.CmdResolverBuilder;
import com.braintribe.model.processing.meta.cmd.CmdResolverImpl;
import com.braintribe.model.processing.meta.cmd.builders.EntityMdResolver;
import com.braintribe.model.processing.meta.cmd.builders.ModelMdResolver;
import com.braintribe.model.processing.meta.cmd.builders.PropertyMdResolver;
import com.braintribe.model.processing.meta.oracle.BasicModelOracle;
import com.braintribe.model.processing.meta.oracle.ModelOracle;

/**
 * 
 */
public class HbmXmlGenerationContext {

	private GmMetaModel gmMetaModel;
	private List<GmEntityType> entityTypes;
	private CmdResolver cmdResolver;
	private Function<GmMetaModel, CmdResolverBuilder> cmdResolverFactory = m -> CmdResolverImpl.create(new BasicModelOracle(gmMetaModel));
	private HibernateDialect dialect;
	private MappingMetaDataResolver metaDataResolver;
	private EntityHintProvider entityHintProvider;

	public File outputFolder;
	public String tablePrefix;
	public String foreignKeyNamePrefix;
	public String uniqueKeyNamePrefix;
	public String indexNamePrefix;
	public boolean allUppercase = false;
	public String targetDb = "";
	public String typeHints;
	public File typeHintsFile;
	public File typeHintsOutputFile;
	public String defaultSchema;
	public String defaultCatalog;

	private static final String JPA_USE_CASE = "jpa";

	public GmMetaModel getGmMetaModel() {
		return this.gmMetaModel;
	}

	public List<GmEntityType> getEntityTypes() {
		if (entityTypes == null)
			entityTypes = getModelOracle().getTypes().onlyEntities().<GmEntityType> asGmTypes().collect(Collectors.toList());

		return entityTypes;
	}

	public void setGmMetaModel(GmMetaModel model) {
		this.gmMetaModel = nonNull(model, "model");
	}

	public void setCmdResolver(CmdResolver cmdResolver) {
		this.cmdResolver = cmdResolver;
	}

	public void setCmdResolverFactory(Function<GmMetaModel, CmdResolverBuilder> cmdResolverFactory) {
		if (cmdResolverFactory != null)
			this.cmdResolverFactory = cmdResolverFactory;
	}

	public void setDialect(HibernateDialect dialect) {
		this.dialect = dialect;
	}

	public void overwriteGmMetaModel(GmMetaModel newGmMetaModel) {
		this.gmMetaModel = newGmMetaModel;
		// forces re-initialization based on new GmMetaModel:
		this.cmdResolver = null;
	}

	public MappingMetaDataResolver getMappingMetaDataResolver() {
		if (metaDataResolver == null || cmdResolver == null)
			metaDataResolver = new MappingMetaDataResolver(this, false);

		return metaDataResolver;
	}

	public PropertyMdResolver propertyMd(GmEntityType gmEntityType, GmProperty gmProperty) {
		return entityMd(gmEntityType).property(gmProperty);
	}

	public EntityMdResolver entityMd(GmEntityType gmEntityType) {
		return getMetaData().entityType(gmEntityType);
	}
	
	public ModelMdResolver getMetaData() {
		ModelMdResolver result = getCmdResolver().getMetaData().useCase(JPA_USE_CASE);
		if (dialect != null)
			result = result.with(HibernateDialectAspect.class, dialect);

		return result;
	}

	private CmdResolver getCmdResolver() {
		if (cmdResolver == null)
			cmdResolver = cmdResolverFactory.apply(gmMetaModel).done();

		return cmdResolver;
	}

	public ModelOracle getModelOracle() {
		return getCmdResolver().getModelOracle();
	}

	public EntityHintProvider getEntityHintProvider() {
		if (entityHintProvider == null)
			entityHintProvider = new EntityHintProvider(typeHints, typeHintsFile);

		return entityHintProvider;
	}

}
