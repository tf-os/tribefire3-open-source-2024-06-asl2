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
package com.braintribe.model.processing.manipulation.parser.impl;

import static com.braintribe.utils.lcd.CollectionTools2.isEmpty;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.UncheckedIOException;
import java.io.UnsupportedEncodingException;

import com.braintribe.model.processing.manipulation.parser.api.GmmlManipulatorParserConfiguration;
import com.braintribe.model.processing.manipulation.parser.api.GmmlParserConfiguration;
import com.braintribe.model.processing.manipulation.parser.api.ParseResponse;
import com.braintribe.model.processing.manipulation.parser.api.ProblematicEntitiesRegistry;
import com.braintribe.model.processing.manipulation.parser.impl.listener.GmmlManipulatorParserListener;
import com.braintribe.model.processing.manipulation.parser.impl.listener.HomeopathyIgnoringManipulatorParserListener;
import com.braintribe.model.processing.session.api.managed.EntityManager;

public class ManipulatorParser {

	// #######################################
	// ## . . . . . . . . API . . . . . . . ##
	// #######################################

	public static ParseResponse parse(String text, EntityManager em, GmmlManipulatorParserConfiguration config) {
		StringReader reader = new StringReader(text);
		return parse(reader, em, config);
	}

	public static ParseResponse parse(InputStream in, String charset, EntityManager em, GmmlManipulatorParserConfiguration config) {
		Reader reader = newInputStreamReader(in, charset);
		return parse(reader, em, config);
	}

	private static InputStreamReader newInputStreamReader(InputStream in, String charset) {
		try {
			return new InputStreamReader(in, charset);

		} catch (UnsupportedEncodingException e) {
			throw new UncheckedIOException(e);
		}
	}

	public static ParseResponse parse(Reader reader, EntityManager em, GmmlManipulatorParserConfiguration config) {
		return tryParse(reader, em, config);
	}

	// #######################################
	// ## . . . . DEPRECATED API . . . . . .##
	// #######################################

	/**
	 * @deprecated use {@link #parse(String, EntityManager, GmmlManipulatorParserConfiguration)}. If you are getting your configuration via
	 *             {@link Gmml#configuration()}, use {@link Gmml#manipulatorConfiguration()} instead.
	 */
	@Deprecated
	public static ParseResponse parse(String text, EntityManager em, GmmlParserConfiguration config) {
		StringReader reader = new StringReader(text);
		return parse(reader, em, config);
	}

	/**
	 * @deprecated use {@link #parse(String, EntityManager, GmmlManipulatorParserConfiguration)}. If you are getting your configuration via
	 *             {@link Gmml#configuration()}, use {@link Gmml#manipulatorConfiguration()} instead.
	 */
	@Deprecated
	public static ParseResponse parse(InputStream in, String charset, EntityManager em, GmmlParserConfiguration config) {
		Reader reader = newInputStreamReader(in, charset);
		return parse(reader, em, config);
	}

	/**
	 * @deprecated use {@link #parse(Reader, EntityManager, GmmlManipulatorParserConfiguration)}. If you are getting your configuration via
	 *             {@link Gmml#configuration()}, use {@link Gmml#manipulatorConfiguration()} instead.
	 */
	@Deprecated
	public static ParseResponse parse(Reader reader, EntityManager em, GmmlParserConfiguration config) {
		return tryParse(reader, em, (GmmlManipulatorParserConfiguration) config);
	}

	// #######################################
	// ## . . . . . . . Impl . . . . . . . .##
	// #######################################

	private static ParseResponse tryParse(Reader reader, EntityManager em, GmmlManipulatorParserConfiguration config) {
		GmmlManipulatorParserListener listener = newManipulatorListener(em, config);

		ManipulationParser.parse(reader, config, listener);

		updateProblematicEntitiesIfRelevant(config, listener);

		ParseResponse result = new ParseResponse();
		result.variables = listener.getAllUsedVariables();
		result.newVariables = listener.getNewVariables();
		result.lastAssignment = listener.getLastAssignment();

		return result;
	}

	private static GmmlManipulatorParserListener newManipulatorListener(EntityManager em, GmmlManipulatorParserConfiguration config) {
		if (isEmpty(config.homeopathicVariables()))
			return new GmmlManipulatorParserListener(em, config);
		else
			return new HomeopathyIgnoringManipulatorParserListener(em, config);
	}

	private static void updateProblematicEntitiesIfRelevant(GmmlManipulatorParserConfiguration config, GmmlManipulatorParserListener listener) {
		ProblematicEntitiesRegistry registry = config.problematicEntitiesRegistry();
		if (registry == null)
			return;

		registry.recognizeProblematicEntities(listener.getProblematicEntities());
	}

}
