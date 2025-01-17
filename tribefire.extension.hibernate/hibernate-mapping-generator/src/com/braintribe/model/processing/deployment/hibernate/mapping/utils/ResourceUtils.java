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
package com.braintribe.model.processing.deployment.hibernate.mapping.utils;

import static com.braintribe.utils.lcd.CollectionTools2.newList;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

import com.braintribe.logging.Logger;
import com.braintribe.utils.IOTools;

public class ResourceUtils {

	private static Logger log = Logger.getLogger(ResourceUtils.class);
	private static final String classpathPrefix = "classpath:";

	private ResourceUtils() {
	}

	public static List<String> loadResourceToStrings(String xmlSnippetUrl) {
		try (BufferedReader reader = getReader(xmlSnippetUrl)) {
			List<String> result = newList();
			while (reader.ready()) {
				String line = reader.readLine();
				if (line.contains("<!DOCTYPE hibernate-mapping PUBLIC") || line.contains("Hibernate/Hibernate Mapping DTD")
						|| line.contains("http://www.hibernate.org/dtd/hibernate-mapping-3.0.dtd")) {
					continue;
				}
				result.add(line);
			}

			return result;

		} catch (IOException e) {
			throw new UncheckedIOException("XML could not be loaded: '" + xmlSnippetUrl + "'", e);
		}
	}

	public static String loadResourceToString(String xmlSnippetUrl) {

		String res = null;
		try (Reader newBufferedReader = getReader(xmlSnippetUrl)) {
			res = IOTools.slurp(newBufferedReader);
		} catch (IOException e) {
			throw new UncheckedIOException("XML could not be loaded: '" + xmlSnippetUrl + "'", e);
		}

		return res;
	}

	private static BufferedReader getReader(String xmlSnippetUrl) throws IOException {

		Objects.requireNonNull(xmlSnippetUrl, "URL must not be null");

		if (xmlSnippetUrl.startsWith(classpathPrefix)) {

			xmlSnippetUrl = xmlSnippetUrl.substring(classpathPrefix.length());

			// leading slashes must be removed when loading resources from the ClassLoader
			if (xmlSnippetUrl.startsWith("/") && xmlSnippetUrl.length() > 1) {
				xmlSnippetUrl = xmlSnippetUrl.substring(1);
			}

			InputStream stream = ResourceUtils.class.getClassLoader().getResourceAsStream(xmlSnippetUrl);

			if (stream == null) {
				throw new IOException("URL couldn't be loaded from classpath: '" + xmlSnippetUrl + "'");
			}

			if (log.isTraceEnabled())
				log.trace("Loaded from classpath: " + xmlSnippetUrl);

			return new BufferedReader(new InputStreamReader(stream, "UTF-8"));

		} else {

			Path path = Paths.get(xmlSnippetUrl);

			return Files.newBufferedReader(path);

		}

	}

}
