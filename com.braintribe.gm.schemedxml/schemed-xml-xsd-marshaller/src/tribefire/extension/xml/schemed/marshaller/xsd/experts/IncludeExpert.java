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
package tribefire.extension.xml.schemed.marshaller.xsd.experts;

import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import tribefire.extension.xml.schemed.marshaller.xsd.HasSchemaTokens;
import tribefire.extension.xml.schemed.model.xsd.Annotation;
import tribefire.extension.xml.schemed.model.xsd.Include;
import tribefire.extension.xml.schemed.model.xsd.Namespace;
import tribefire.extension.xml.schemed.model.xsd.Schema;

public class IncludeExpert extends AbstractSchemaExpert implements HasSchemaTokens {

	public static Include read(Schema declaringSchema,  XMLStreamReader reader) throws XMLStreamException {
		Include include = Include.T.create();
		attach(include, declaringSchema);
		
		Map<QName,String> attributes = readAttributes(reader);
		include.setSchemaLocation( attributes.get( new QName(SCHEMA_LOCATION)));
		include.setId( attributes.get( new QName(ID)));
		
		readAnyAttributes( include.getAnyAttributes(), attributes, ID, SCHEMA_LOCATION);

		reader.next();
		while (reader.hasNext()) {
			switch (reader.getEventType()) {
				case XMLStreamConstants.START_ELEMENT : {
					
					String tag = reader.getName().getLocalPart();
					switch (tag) {
					case ANNOTATION:
						Annotation annotation = AnnotationExpert.read( declaringSchema, reader);
						include.setAnnotation(annotation);					
						break;
					default:
						skip(reader);
					}					
				}
				break;
				case XMLStreamConstants.END_ELEMENT : {
					return include;
				}
				default: 
					break;
				}
			reader.next();
		}
		return include;
	}
	
	public static void write( XMLStreamWriter writer, Namespace namespace, Include suspect) throws XMLStreamException {
		if (suspect == null)
			return;
		String prefix = namespace.getPrefix();
		writer.writeStartElement( prefix != null ? prefix + ":" + INCLUDE : INCLUDE);
		writer.writeAttribute( SCHEMA_LOCATION, suspect.getSchemaLocation());
		writeAnyAttributes(writer, suspect.getAnyAttributes());
		AnnotationExpert.write(writer, namespace, suspect.getAnnotation());
		writer.writeEndElement();
	}

}
