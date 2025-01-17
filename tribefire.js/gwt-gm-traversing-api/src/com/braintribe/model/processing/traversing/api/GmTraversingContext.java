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
package com.braintribe.model.processing.traversing.api;

import com.braintribe.model.generic.path.api.IModelPathElement;
import com.braintribe.model.processing.traversing.api.path.TraversingModelPathElement;

/**
 * Context which provides access to the traversing-related data for the {@link GmTraversingVisitor} (well, using the right sub-type).
 * 
 * @see EnterContext
 * @see LeaveContext
 */
public interface GmTraversingContext {
	/**
	 * 
	 * @param predecessor
	 *            the explicit predecessor or null if the new event pair should be appended at the end
	 */
	GmTraversingEnterEvent appendEventPair(GmTraversingEvent predecessor, TraversingModelPathElement pathElement);

	/** Skip all following "enter" nodes. */
	void skipAll(SkipUseCase skipUseCase);

	/** Skip to the "leave" corresponding to the current node (if current node is enter). */
	void skipWalkFrame(SkipUseCase skipUseCase);

	/**
	 * Skip all enqueued nodes which are descendants of current node. This "descendant-relationship" is defined by the walker. For standard
	 * depth-first search this would be equivalent to {@link #skipWalkFrame(SkipUseCase)}. For standard breath-first search (that means
	 * first traverse siblings, only then children) this would mean we still traverse all the siblings, and then skip the children of the
	 * current node and go straight to the children of the next sibling.
	 */
	void skipDescendants(SkipUseCase skipUseCase);

	/** Stops immediately, does not even enter "leaves" for nodes that were entered but not "left" yet. */
	void abort();

	/** @return traversing depth of the current object. This depth is computed based on the */
	int getCurrentDepth();

	<T extends GmTraversingEvent> T getEvent();

	<T> T getSharedCustomValue(IModelPathElement pathElement);

	<T> T getVisitorSpecificCustomValue(IModelPathElement pathElement);

	void setSharedCustomValue(IModelPathElement pathElement, Object value);

	void setVisitorSpecificCustomValue(IModelPathElement pathElement, Object value);

	SkipUseCase getSkipUseCase();

}
