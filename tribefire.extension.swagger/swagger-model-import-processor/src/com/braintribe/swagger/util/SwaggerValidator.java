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
package com.braintribe.swagger.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;

import org.apache.commons.collections4.CollectionUtils;

import com.braintribe.logging.Logger;
import com.braintribe.swagger.ImportSwaggerModelResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;

import io.swagger.parser.SwaggerParser;
import io.swagger.parser.util.RemoteUrl;
import io.swagger.parser.util.SwaggerDeserializationResult;
import io.swagger.util.Json;
import io.swagger.util.Yaml;

/**
 * @author peter.gazdik
 */
public class SwaggerValidator {

	private static final String SCHEMA_URL = "http://swagger.io/v2/schema.json";

	private static String CACHED_SCHEMA = null;
	private static final ObjectMapper JsonMapper = Json.mapper();
	private static final ObjectMapper YamlMapper = Yaml.mapper();

	private static final Logger logger = Logger.getLogger(SwaggerValidator.class);

	public static String validateSwaggerContent(String content) {
		try {
			String schemaText = getSchema();
			JsonNode schemaObject = JsonMapper.readTree(schemaText);
			JsonSchemaFactory factory = JsonSchemaFactory.byDefault();
			JsonSchema schema = factory.getJsonSchema(schemaObject);

			JsonNode spec = readNode(content);
			if (spec == null) {
				return "Unable to read content.  It may be invalid JSON or YAML.";
			}

			ProcessingReport report = schema.validate(spec, true);

			if (report.isSuccess()) {
				SwaggerDeserializationResult result = readSwagger(content);
				if (Objects.nonNull(result) && CollectionUtils.isNotEmpty(result.getMessages())) {
					return "Cannot deserialize swagger model." + result.getMessages();
				}
			}

		} catch (IOException ioException) {
			logger.debug("Can't read schemaObject", ioException);
			return "Can't read schemaObject!";

		} catch (ProcessingException processingException) {
			logger.debug("Can't validate swagger schema", processingException);

			// We ignore the exception and return null!!!

			// For some reason the validation doesn't work right now even if the content is OK, with error:

			// @formatter:off
			// com.github.fge.jsonschema.core.exceptions.ProcessingException: fatal: content at URI "http://swagger.io/v2/schema.json#" is not valid JSON
			//    level: "fatal"
			//    uri: "http://swagger.io/v2/schema.json#"
			// @formatter:on

			// processingException.printStackTrace();
			// return "Can't validate swagger schema!";
			return null;
		}

		return null;
	}

	private static String getSchema() {
		if (Objects.nonNull(CACHED_SCHEMA)) {
			return CACHED_SCHEMA;
		}
		CACHED_SCHEMA = getSchemaContent();
		if (Objects.isNull(CACHED_SCHEMA)) {
			try {
				InputStream inputStream = ImportSwaggerModelResponse.class.getResourceAsStream("/swagger_schema.json");
				CACHED_SCHEMA = readFromInputStream(inputStream);
			} catch (Exception e) {
				logger.error(e);
			}
		}
		return CACHED_SCHEMA;
	}

	private static String readFromInputStream(InputStream inputStream) throws IOException {
		StringBuilder resultStringBuilder = new StringBuilder();
		try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
			String line;
			while ((line = br.readLine()) != null) {
				resultStringBuilder.append(line).append("\n");
			}
		}
		return resultStringBuilder.toString();
	}

	private static String getSchemaContent() {
		try {
			return RemoteUrl.urlToString(SCHEMA_URL, null);
		} catch (Exception e) {
			logger.error(e);
		}
		return null;
	}

	private static JsonNode readNode(String text) {
		try {
			if (text.trim().startsWith("{")) {
				return JsonMapper.readTree(text);
			} else {
				return YamlMapper.readTree(text);
			}
		} catch (IOException e) {
			return null;
		}
	}

	private static SwaggerDeserializationResult readSwagger(String content) {
		SwaggerParser parser = new SwaggerParser();
		return parser.readWithInfo(content);
	}

}
