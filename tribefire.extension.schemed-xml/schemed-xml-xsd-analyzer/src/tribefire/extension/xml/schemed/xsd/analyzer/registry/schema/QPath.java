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
package tribefire.extension.xml.schemed.xsd.analyzer.registry.schema;

import tribefire.extension.xml.schemed.model.xsd.Schema;
import tribefire.extension.xml.schemed.model.xsd.SchemaEntity;
import tribefire.extension.xml.schemed.xsd.api.analyzer.AnalyzerRegistry;
import tribefire.extension.xml.schemed.xsd.api.mapper.type.TypeMapper;

/**
 * a {@link QPath} is a qualified path of a {@link SchemaEntity} with the full tree of interdependent {@link Schema}
 * <br/>
 * it has the following form : <top>.<child>[.<child>] where the actual values are the namespaces of the schema, as determined by the xs:import statements<br/>
 * <br/>
 * consider this:
 * parent.xsd <no namespace><br/>
 * import1.xsd import1 <br/>
 * 	common.xsd common <br/>
 * import2.xsd import2 <br/>
 *  common.xsd common <br/>
 * 
 * a QPath for an {@link SchemaEntity} of parent.xsd will be "" (no value) <br/>
 * a QPath for an {@link SchemaEntity} of import1.xsd will be "import1" <br/>
 * a QPath for an {@link SchemaEntity} of import2.xsd will be "import2" <br/>
 * a QPath for an {@link SchemaEntity} of common.xsd will be "import1.common" if requested via import1.xsd <br/>
 * a QPath for an {@link SchemaEntity} of common.xsd will be "import2.common" if requested via import2.xsd <br/>
 * <br/>
 * see {@link AnalyzerRegistry} for the creation of {@link QPath}, they are used to create unique names for types in the {@link TypeMapper}
 * @author pit
 *
 */
public class QPath {
	private String path = "";

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
		
}
