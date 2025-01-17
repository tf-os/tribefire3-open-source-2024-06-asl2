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
package com.braintribe.model.processing.deployment.hibernate;

import static com.braintribe.utils.lcd.StringTools.isEmpty;

import java.io.File;
import java.util.function.Function;
import java.util.function.Supplier;

import com.braintribe.cfg.DestructionAware;
import com.braintribe.cfg.Required;
import com.braintribe.logging.Logger;
import com.braintribe.model.accessdeployment.hibernate.HibernateDialect;
import com.braintribe.model.meta.GmMetaModel;
import com.braintribe.model.processing.deployment.hibernate.mapping.HbmXmlGeneratingService;
import com.braintribe.model.processing.meta.cmd.CmdResolver;
import com.braintribe.model.processing.meta.cmd.CmdResolverBuilder;
import com.braintribe.utils.FileTools;

public class HibernateMappingsDirectorySupplier implements Supplier<File>, DestructionAware {

	private GmMetaModel metaModel;
	private String defaultSchema;
	private String defaultCatalog;
	private String objectNamePrefix;
	private String tableNamePrefix;
	private String foreignKeyNamePrefix;
	private String uniqueKeyNamePrefix;
	private String indexNamePrefix;
	private CmdResolver cmdResolver;

	private File tempDir;
	private boolean keepTempDir;

	private static final Logger log = Logger.getLogger(HibernateMappingsDirectorySupplier.class);
	private HibernateDialect dialect;
	private Function<GmMetaModel, CmdResolverBuilder> cmdResolverFactory;

	@Required
	public void setMetaModel(GmMetaModel metaModel) {
		this.metaModel = metaModel;
	}

	public void setDefaultSchema(String defaultSchema) {
		this.defaultSchema = defaultSchema;
	}

	public void setDefaultCatalog(String defaultCatalog) {
		this.defaultCatalog = defaultCatalog;
	}

	public void setObjectNamePrefix(String objectNamePrefix) {
		this.objectNamePrefix = objectNamePrefix;
	}

	public void setTableNamePrefix(String tableNamePrefix) {
		this.tableNamePrefix = tableNamePrefix;
	}

	public void setForeignKeyNamePrefix(String foreignKeyNamePrefix) {
		this.foreignKeyNamePrefix = foreignKeyNamePrefix;
	}

	public void setUniqueKeyNamePrefix(String uniqueKeyNamePrefix) {
		this.uniqueKeyNamePrefix = uniqueKeyNamePrefix;
	}

	public void setIndexNamePrefix(String indexNamePrefix) {
		this.indexNamePrefix = indexNamePrefix;
	}

	public void setCmdResolver(CmdResolver cmdResolver) {
		this.cmdResolver = cmdResolver;
	}

	public void setCmdResolverFactory(Function<GmMetaModel, CmdResolverBuilder> cmdResolverFactory) {
		this.cmdResolverFactory = cmdResolverFactory;
	}

	public void setDialect(HibernateDialect dialect) {
		this.dialect = dialect;
	}

	protected String getTableNamePrefix() {
		return (tableNamePrefix != null && !tableNamePrefix.isEmpty()) ? tableNamePrefix : objectNamePrefix;
	}

	protected String getForeignKeyNamePrefix() {
		return (foreignKeyNamePrefix != null && !foreignKeyNamePrefix.isEmpty()) ? foreignKeyNamePrefix : objectNamePrefix;
	}

	protected String getUniqueKeyNamePrefix() {
		return (uniqueKeyNamePrefix != null && !uniqueKeyNamePrefix.isEmpty()) ? uniqueKeyNamePrefix : objectNamePrefix;
	}

	protected String getIndexNamePrefix() {
		return (indexNamePrefix != null && !indexNamePrefix.isEmpty()) ? indexNamePrefix : objectNamePrefix;
	}

	/**
	 * Provides a {@link File} which represents a folder containing hibernate mapping xml files.
	 * 
	 * Note that the folder is deleted when this factory is being destroyed (see {@link #preDestroy()}).
	 */
	@Override
	public File get() {
		if (tempDir == null)
			tempDir = newResource();

		return tempDir;
	}

	private File newResource() {
		tempDir = FileTools.createNewTempDir("tf_hbm", modelNamePart(metaModel.getName()) + "_" + System.nanoTime());
		
		HbmXmlGeneratingService hbmXmlGenerator = new HbmXmlGeneratingService(metaModel, tempDir) //
			.defaultSchema(defaultSchema) //
			.defaultCatalog(defaultCatalog) //
			.tablePrefix(getTableNamePrefix()) //
			.foreignKeyNamePrefix(getForeignKeyNamePrefix()) //
			.uniqueKeyNamePrefix(getUniqueKeyNamePrefix()) //
			.indexNamePrefix(getIndexNamePrefix()) //
			.cmdResolver(cmdResolver) //
			.cmdResolverFactory(cmdResolverFactory) //
			.dialect(dialect);
		hbmXmlGenerator.renderMappings();

		keepTempDir = hbmXmlGenerator.keepHbmXmlDir();
		
		return tempDir;
	}

	private static String modelNamePart(String modelName) {
		return isEmpty(modelName) ? "" : modelName.replace(".", "_").replace(":", "-").replace("#", "-").replaceAll("[^\\w_-]", "");
	}
	

	@Override
	public void preDestroy() {
		if (tempDir == null)
			return;
		
		if (keepTempDir) {
			log.info("Folder '" + tempDir.getAbsolutePath() + "' with Hibernate mappings for model '" + metaModel.getName()
					+ "' is configured per MD not to be deleted. So we'll keep it...");
			return;
		}

		try {
			FileTools.deleteDirectoryRecursively(tempDir);

		} catch (Exception e) {
			log.error("Unabled to delete temp file containing hbm xml mappings [" + tempDir.getAbsolutePath() + "]");
		}
	}

}
