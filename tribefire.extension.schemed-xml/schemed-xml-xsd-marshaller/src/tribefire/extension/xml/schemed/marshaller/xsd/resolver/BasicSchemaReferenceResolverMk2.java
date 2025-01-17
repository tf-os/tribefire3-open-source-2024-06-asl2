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
package tribefire.extension.xml.schemed.marshaller.xsd.resolver;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import javax.xml.stream.XMLStreamException;

import com.braintribe.logging.Logger;
import com.braintribe.model.resource.Resource;
import com.braintribe.utils.archives.Archives;
import com.braintribe.utils.archives.ArchivesException;

import tribefire.extension.xml.schemed.marshaller.xsd.SchemedXmlXsdMarshaller;
import tribefire.extension.xml.schemed.model.api.xsd.analyzer.api.model.ReferencedSchema;
import tribefire.extension.xml.schemed.model.api.xsd.analyzer.api.model.ReferencedSchemata;
import tribefire.extension.xml.schemed.model.xsd.Schema;

public class BasicSchemaReferenceResolverMk2 implements SchemaReferenceResolver, ConfigurableSchemaReferenceResolver {
	private static Logger log = Logger.getLogger(BasicSchemaReferenceResolverMk2.class);
	private Map<String, Schema> uriToSchemaMap = new HashMap<>();
	private Map<Schema, String> schemaToUriMap = new HashMap<>();
	private SchemedXmlXsdMarshaller marshaller = new SchemedXmlXsdMarshaller();
	private Map<String, Schema> shortNameToSchemaMap = new HashMap<>();
	private ReferencedSchemata referencedSchemata;
	private Resource containerResource;
	private File containerDirectory;
	
	private Map<String,File> uriToAbsolutePathMap = new HashMap<>();
	
	@Override
	public void setReferencedSchemata(ReferencedSchemata schemata) {
		referencedSchemata = schemata;
	}
	
	@Override
	public void setContainerResource(Resource resource) {
		containerResource = resource;		
	}

	@Override
	public String getUriOfSchema(Schema schema) {
		return schemaToUriMap.get(schema);
	}

	@Override
	public Schema getSchemaOfUri(String uri) {
		return uriToSchemaMap.get( uri);		
	}
	

	@Override
	public Schema resolve(String uri, Resource resource) {
		
		uri = uri.replace( '\\', '/');
		
		Schema schema = uriToSchemaMap.get( uri);
		String msg = "returning cached schema for [" + uri + "]";
		if (schema != null) {
			if (log.isDebugEnabled()) {
				log.info(msg);
			}
			return schema;
		}
		schema = shortNameToSchemaMap.get( uri);
		if (schema != null) {
			if (log.isDebugEnabled()) {
				log.info(msg);
			}
			return schema;
		}
		
		InputStream inputStream = resource.openStream();
		
		try {
			return resolve( uri, inputStream);
		} catch (XMLStreamException e) {
			throw  new IllegalStateException("resolve referenced schema [" + uri + "]", e);
		}
		finally {			
			try {
				inputStream.close();
			} catch (IOException e) {
				throw  new IllegalStateException("cannot close stream of [" + uri + "]", e);
			}
		}
	}


	@Override
	public Schema resolve(Schema parent, String uri) {
		
		
		if (log.isDebugEnabled()) {
			String msg = "Resolving [" + uri + "]";
			log.debug(msg);
		}	
		
		uri = uri.replace( '\\', '/');
		
		Schema schema = uriToSchemaMap.get( uri);
		String msg = "returning cached schema for [" + uri + "]";
		if (schema != null) {
			if (log.isDebugEnabled()) {
				log.debug(msg);
			}
			return schema;
		}
		
		schema = shortNameToSchemaMap.get( uri);
		if (schema != null) {
			if (log.isDebugEnabled()) {
				log.debug(msg);
			}
			return schema;
		}
				
		InputStream in = getInputStreamForUri(parent, uri);
		
		try {
			return resolve( uri, in);
		} catch (XMLStreamException e) {
			throw  new IllegalStateException("cannot unmarshall stream of [" + uri + "]", e);
		}
		finally {			
			try {
				in.close();
			} catch (IOException e) {
				throw  new IllegalStateException("cannot close stream of [" + uri + "]", e);
			}
		}
	}
	

	/**
	 * @param uri
	 * @param inputStream
	 * @return
	 * @throws XMLStreamException
	 */
	private Schema resolve( String uri, InputStream inputStream) throws XMLStreamException {			
		Schema schema = marshaller.unmarshall( inputStream);
		if (schema == null) {
			throw  new IllegalStateException("unmarshalling the stream of [" + uri + "] returns no schema");
		}
		uriToSchemaMap.put(uri, schema);
		schemaToUriMap.put(schema, uri);
		
		int i = uri.lastIndexOf( '/');
		String shortName = uri.substring(i+1);
		shortNameToSchemaMap.put(shortName, schema);
		
		return schema;				
	}
	
	/**
	 * @param parent 
	 * @param uri
	 * @return
	 */
	private InputStream getInputStreamForUri( Schema parent, String uri) {		
		if (referencedSchemata != null) {
			for (ReferencedSchema rSchema : referencedSchemata.getReferencedSchemata()) {
				if (rSchema.getUri().equalsIgnoreCase(uri)) {
					return rSchema.getSchema().openStream();
				}
			}
		}
		if (containerResource != null) {
			if (containerDirectory == null) {
				try {
					containerDirectory = Files.createTempDirectory("schemed-xml-").toFile();
					Archives.zip().from( containerResource.openStream()).unpack( containerDirectory);
				} catch (IOException e) {
					throw  new IllegalStateException("cannot create temporary working directory", e);
				} catch (ArchivesException e) {
					throw  new IllegalStateException("cannot unpack zip resource to temporary working directory", e);
				}
			}
			// 
			File location;
			if (parent == null) {
				location =  containerDirectory;
			}
			else {
				String parentUri = schemaToUriMap.get( parent);				
				location = uriToAbsolutePathMap.get(parentUri);				
			}			
			File file = new File( location, uri);
			uriToAbsolutePathMap.put(uri, file.getParentFile());
			
			try {
				return new FileInputStream( file);
			} catch (FileNotFoundException e) {
				throw  new IllegalStateException("cannot open stream to [" + file.getAbsolutePath() + "]", e);
			}						
		}
		try {
			File file = new File( uri);
			if (file.exists()) {
				return new FileInputStream(file);
			}
		} catch (Exception e1) {
			System.out.println("no file");
		}
		
		try {
			URL url = new URL( uri);
			try {
				return url.openStream();			
			} catch (IOException e) {
				throw  new IllegalStateException("cannot open stream to [" + uri + "]", e);
			}
		} catch (MalformedURLException e) {			
			throw  new IllegalStateException("cannot open stream to [" + uri + "]", e);			
		}
	}

	
	
}
