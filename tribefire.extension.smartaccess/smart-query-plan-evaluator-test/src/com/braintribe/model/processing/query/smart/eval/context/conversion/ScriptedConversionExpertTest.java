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
package com.braintribe.model.processing.query.smart.eval.context.conversion;

import static com.braintribe.model.processing.smartquery.eval.api.ConversionDirection.delegate2Smart;
import static com.braintribe.model.processing.smartquery.eval.api.ConversionDirection.smart2Delegate;

import java.util.UUID;

import org.junit.Test;

import com.braintribe.gm.model.reason.Maybe;
import com.braintribe.gm.model.reason.Reasons;
import com.braintribe.gm.model.reason.essential.NotFound;
import com.braintribe.model.accessdeployment.smart.meta.conversion.ScriptedConversion;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.query.smart.processing.eval.context.conversion.ScriptedConversionExpert;
import com.braintribe.model.resource.Resource;
import com.braintribe.model.resource.source.StringSource;
import com.braintribe.utils.junit.assertions.BtAssertions;

import tribefire.extension.scripting.api.ScriptingEngine;
import tribefire.extension.scripting.api.ScriptingEngineResolver;
import tribefire.extension.scripting.deployment.model.GroovyScript;
import tribefire.extension.scripting.groovy.GroovyEngine;
import tribefire.extension.scripting.model.deployment.Script;

/**
 * @author peter.gazdik
 */
public class ScriptedConversionExpertTest {

	private final ScriptedConversionExpert expert = expert();

	@Test
	public void addingPrefix() throws Exception {
		// 7 is the length of "PREFIX-" of course
		String source = "$context.getValue() == null ? null : ($context.getIsForward() ? $context.getValue().substring(7) : 'PREFIX-' + $context.getValue())";
		ScriptedConversion conversion = conversionForScript(source);

		test(conversion, null, null);
		test(conversion, "hello", "PREFIX-hello");
	}

	@Test
	public void paddingTo5Chars() throws Exception {
		String pad = "def pad(val){ while (val.length() < 5) { val = '0' + val; }; return val; }\n";
		String unpad = "def unpad(val){ int i = 0; while (i < val.length() && val.charAt(i) == '0') {i++;}; return i > 0 ? val.substring(i) : val; }\n";
		// for whatever reason $.value is not a string, but it is an object. So I do '' + $.value to ensure it is a string
		String base = "$context.getValue() == null ? null : ($context.getIsForward() ? pad('' + $context.getValue()) : unpad('' + $context.getValue()))";
		String source = pad + unpad + base;

		ScriptedConversion conversion = conversionForScript(source);

		test(conversion, null, null);
		test(conversion, "00123", "123");
		test(conversion, "12345", "12345");
	}

	private static ScriptedConversion conversionForScript(String sourceCode) {
		Resource source = Resource.T.create();
		StringSource code = StringSource.T.create();
		code.setContent(sourceCode);
		source.setResourceSource(code);

		GroovyScript script = GroovyScript.T.create();
		script.setSource(source);

		ScriptedConversion conversion = ScriptedConversion.T.create();
		conversion.setScript(script);

		return conversion;
	}

	private void test(ScriptedConversion conversion, String delegateValue, String expectedSmartValue) {
		String smartValue = (String) expert.convertValue(conversion, delegateValue, delegate2Smart);
		BtAssertions.assertThat(smartValue).isEqualTo(expectedSmartValue);

		String convertedDelegateValue = (String) expert.convertValue(conversion, smartValue, smart2Delegate);
		BtAssertions.assertThat(convertedDelegateValue).isEqualTo(delegateValue);
	}

	private ScriptedConversionExpert expert() {
		ScriptedConversionExpert result = new ScriptedConversionExpert();
		result.setScriptingEngineResolver(scriptingEngineResolver());

		return result;
	}

	private ScriptingEngineResolver scriptingEngineResolver() {
		GroovyEngine groovyEngine = new GroovyEngine();

		return new ScriptingEngineResolver() {

			@Override
			public <S extends Script> Maybe<ScriptingEngine<S>> resolveEngine(EntityType<S> scriptType) {
				if (GroovyScript.T.isAssignableFrom(scriptType))
					return (Maybe<ScriptingEngine<S>>) (Maybe<? extends ScriptingEngine<?>>) Maybe.complete(groovyEngine);
				else
					return Reasons.build(NotFound.T).toMaybe();
			}
		};

	}

}
