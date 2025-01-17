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

import com.braintribe.model.generic.GenericEntity;

import tribefire.extension.xml.schemed.model.xsd.All;
import tribefire.extension.xml.schemed.model.xsd.Annotation;
import tribefire.extension.xml.schemed.model.xsd.Element;
import tribefire.extension.xml.schemed.model.xsd.Namespace;
import tribefire.extension.xml.schemed.model.xsd.Schema;

public class AllExpert extends AbstractSchemaExpert {

	public static All read( Schema declaringSchema, XMLStreamReader reader) throws XMLStreamException {
		Map<QName, String> attributes = readAttributes(reader);		
	
		All all = All.T.create();
		attach(all, declaringSchema);
	
		String value = attributes.get( new QName(MIN_OCCURS));
		if (value != null) {
			all.setMinOccurs(Integer.valueOf(value));
			all.setMinOccursSpecified( true);
		}
	
		value = attributes.get( new QName(MAX_OCCURS));
		if (value != null) {
			if (value.equalsIgnoreCase( UNBOUNDED)) {
				all.setMaxOccurs(-1);
				all.setMaxOccursSpecified( true);
			}
			else {
			all.setMaxOccurs(Integer.valueOf(value));
			}
		}
		
		
		readAnyAttributes( all.getAnyAttributes(), attributes, ID, MAX_OCCURS, MIN_OCCURS);
		
		reader.next();
		
		while (reader.hasNext()) {
			
			switch (reader.getEventType()) {
			
				case XMLStreamConstants.START_ELEMENT :												
					String tag = reader.getName().getLocalPart();
					switch (tag) {
						case ELEMENT:	
							Element element = ElementExpert.read(declaringSchema, reader);
							all.getElements().add( element);
							all.getNamedItemsSequence().add( element);
							break;
						case ANNOTATION:
							Annotation annotation = AnnotationExpert.read(declaringSchema, reader);
							all.setAnnotation(annotation);
							all.getNamedItemsSequence().add(annotation);
							break;
						default:
								skip(reader);
							break;
					}				
				break;
				
				case XMLStreamConstants.END_ELEMENT : {
					return all;
				}		
			
				default: 
					break;
			}
			reader.next();
		}
		return all;
	}

	
	public static void write(XMLStreamWriter writer, Namespace namespace, All all) throws XMLStreamException{
		if (all == null)
			return;
		String prefix = namespace.getPrefix();
		writer.writeStartElement( prefix != null ? namespace.getPrefix() + ":" + ALL : ALL);
		// write attributes
		int min = all.getMinOccurs();
		if (min != 1 || all.getMinOccursSpecified()) {
			writer.writeAttribute( MIN_OCCURS, "" + min);
		}
		int max = all.getMaxOccurs();
		if (max < 0) {
			writer.writeAttribute( MAX_OCCURS, UNBOUNDED);
		}
		else if (max != 1 || all.getMaxOccursSpecified()) {
			writer.writeAttribute( MAX_OCCURS, "" + max);
		}
		
		writeAnyAttributes( writer, all.getAnyAttributes());
		for (GenericEntity ge : all.getNamedItemsSequence()) {
			if (ge instanceof Element) {
				ElementExpert.write(writer, namespace, (Element) ge);				
			}
			else if (ge instanceof Annotation) {
				AnnotationExpert.write(writer, namespace, (Annotation) ge);
			}
		}
		writer.writeEndElement();		
	}
}
