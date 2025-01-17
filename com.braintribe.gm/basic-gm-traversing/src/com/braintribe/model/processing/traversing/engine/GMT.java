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
package com.braintribe.model.processing.traversing.engine;

import com.braintribe.model.generic.value.ValueDescriptor;
import com.braintribe.model.processing.traversing.api.GmTraversingException;
import com.braintribe.model.processing.traversing.api.GmTraversingVisitor;
import com.braintribe.model.processing.traversing.engine.api.ClonerConfigurer;
import com.braintribe.model.processing.traversing.engine.api.TraversingConfigurer;
import com.braintribe.model.processing.traversing.engine.api.customize.ModelWalkerCustomization;
import com.braintribe.model.processing.traversing.engine.impl.TraversingConfigurerImpl;
import com.braintribe.model.processing.traversing.engine.impl.clone.Cloner;
import com.braintribe.model.processing.traversing.engine.impl.clone.ClonerConfigurerImpl;
import com.braintribe.model.processing.traversing.engine.impl.walk.ModelWalker;
import com.braintribe.model.processing.traversing.engine.impl.walk.VdeModelWalkerCustomization;
import com.braintribe.model.processing.vde.evaluator.api.VdeEvaluationMode;
import com.braintribe.model.processing.vde.evaluator.api.builder.VdeContextBuilder;

/**
 * Main Access point for all traversing actions.
 * 
 * All methods are declared as static.
 * 
 */
public class GMT {

	/**
	 * Performs standard cloning for an object
	 * 
	 * @param value
	 *            Object that needs to be cloned
	 * @return A cloned instance of the provided object
	 */
	public static <V> V clone(Object value) throws GmTraversingException {
		Cloner cloner = new Cloner();
		doClone().visitor(cloner).doFor(value);

		return cloner.getClonedValue();
	}

	/**
	 * Allows a customised setup for cloning operation via
	 * {@link ClonerConfigurer}
	 * 
	 */
	public static ClonerConfigurer<? extends ClonerConfigurer<?>> doClone() {
		return new ClonerConfigurerImpl();
	}

	/**
	 * Perform a standard traversing of an assembly with default settings {@link ModelWalker} in addition to another provided visitor
	 * 
	 * @param visitor
	 *            Visitor that should be used
	 * @param value
	 *            Object that will be traversed
	 */
	public static void traverse(GmTraversingVisitor visitor, Object value) throws GmTraversingException {
		traverse().visitor(visitor).doFor(value);
	}

	/**
	 * Allows a customized setup for traversing operation via
	 * {@link TraversingConfigurer}
	 * 
	 */
	public static TraversingConfigurer<? extends TraversingConfigurer<?>> traverse() {
		return new TraversingConfigurerImpl();
	}

	/**
	 * Provides a clone of an assembly, where all {@link ValueDescriptor} are
	 * evaluated. The extent of the evaluation depends on:
	 * <ul>
	 * <li>If last layer: All ValueDescriptors must be evaluated, which means
	 * that all corresponding experts and aspects must be provided, otherwise an
	 * exception will be thrown</li>
	 * <li>If NOT last layer: All ValueDescriptors that could be evaluated would
	 * be, otherwise they will be simply cloned normally</li>
	 * </ul>
	 * 
	 * @param vdeContext
	 *            A {@link VdeContextBuilder} that holds the settings for VDE
	 *            evaluation
	 * @param value
	 *            Object that needs to requires evaluation
	 * @param isLastLayer
	 *            boolean indicating if this is the last layer of an evaluation
	 *            process
	 * @return A cloned assembly where all the value descriptors have been
	 *         evaluated based on the the last layer status
	 */
	public static <V> V evaluteTemplate(VdeContextBuilder vdeContext, Object value, boolean isLastLayer) throws GmTraversingException {
		if (isLastLayer) {
			vdeContext.withEvaluationMode(VdeEvaluationMode.Final);
		} else {
			vdeContext.withEvaluationMode(VdeEvaluationMode.Preliminary);
		}
		Cloner cloner = new Cloner();
		ModelWalkerCustomization walkerCustomization = new VdeModelWalkerCustomization(vdeContext, !isLastLayer);
		doClone().customizeDefaultWalker(walkerCustomization).visitor(cloner).doFor(value);
		return cloner.getClonedValue();
	}

}
