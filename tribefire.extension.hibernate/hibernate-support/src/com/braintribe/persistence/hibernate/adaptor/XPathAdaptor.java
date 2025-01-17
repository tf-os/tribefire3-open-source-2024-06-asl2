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
package com.braintribe.persistence.hibernate.adaptor;

import java.io.File;
import java.util.Map;

import org.apache.commons.jxpath.JXPathContext;
import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;

import com.braintribe.cfg.Configurable;
import com.braintribe.cfg.Required;
import com.braintribe.logging.Logger;
import com.braintribe.utils.xml.XmlTools;

/**
 * Implementation of the {@link HibernateConfigurationAdaptor} that is able to change attribute
 * values in arbitrary XML files. The attributes are identified by XPath expressions.
 * A regular expression to identify the target files can (and should) also be configured.
 * 
 * Reason for this implementation is the need to change an attribute value in an
 * hibernate mapping file (e.g., change the type of an attribute from clob to text)
 * 
 * @author roman.kurmanowytsch
 */
public class XPathAdaptor implements HibernateConfigurationAdaptor {

	private static Logger logger = Logger.getLogger(XPathAdaptor.class);

	protected Map<String,String> valueMap = null;
	protected boolean disableXMLValidation = true;
	protected EntityResolver entityResolver = null;

	@Override
	public void cleanup() {
		//do nothing
	}
	
	@Override
	public void adaptEhCacheConfigurationResource(File configFile) throws Exception {

		if (configFile.exists()) {
			logger.debug("Adapting file "+configFile);

			this.adaptResourceFile(configFile);
		}

		return;
	}

	/**
	 * Adapts a single file
	 * 
	 * @param resourceFile The file to be adapted
	 * @throws Exception If the file adaptation fails
	 */
	protected void adaptResourceFile(File resourceFile) throws Exception {

		boolean trace = logger.isTraceEnabled();

		try {
			if (trace) {
				logger.trace("Loading file "+resourceFile.getAbsolutePath());
			}
			// Load the XML into the memory
			Document doc = XmlTools.parseXMLFile(resourceFile, (String) null, this.disableXMLValidation, this.entityResolver);

			// Apply the XPath expression to get a pointer to the right place

			JXPathContext context = JXPathContext.newContext(doc);

			boolean contentChanged = false;

			// Note: the current implementation only replaces the value of the first entry identified by the XPath
			for (Map.Entry<String,String> mapEntry : this.valueMap.entrySet()) {

				String xpath = mapEntry.getKey();
				String value = mapEntry.getValue();

				logger.debug("Setting value "+value+" at path "+xpath+" in file "+resourceFile.getAbsolutePath());

				String oldValue = (String) context.getValue(xpath);
				if (oldValue != null) {
					if (value.equals(oldValue)) {
						continue;
					}
				}

				contentChanged = true;

				context.setValue(xpath, value);
			}

			if (contentChanged) {
				// Write the XML back into the file
				XmlTools.writeXml(doc, resourceFile, "UTF-8");
			}

		} catch(Exception e) {
			throw new Exception("Error while trying to apply xpath on " + resourceFile.getAbsolutePath(), e);
		}
	}

	public boolean isDisableXMLValidation() {
		return disableXMLValidation;
	}
	@Configurable
	public void setDisableXMLValidation(boolean disableXMLValidation) {
		this.disableXMLValidation = disableXMLValidation;
	}

	public EntityResolver getEntityResolver() {
		return entityResolver;
	}
	@Configurable
	public void setEntityResolver(EntityResolver entityResolver) {
		this.entityResolver = entityResolver;
	}

	public static String getBuildVersion() {
		return "$Build_Version$ $Id: XPathAdaptor.java 102500 2017-12-13 19:17:52Z andre.goncalves $";
	}

	@Required
	public void setValueMap(Map<String, String> valueMap) {
		this.valueMap = valueMap;
	}

}
