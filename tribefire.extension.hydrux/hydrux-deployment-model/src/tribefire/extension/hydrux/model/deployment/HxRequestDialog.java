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
package tribefire.extension.hydrux.model.deployment;

import com.braintribe.model.generic.annotation.Abstract;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

/**
 * This is an abstract supertype for an {@link HxView} that serves a very special purpose - evaluating a service request by asking a user for input
 * (in a form of a dialog window).
 * 
 * <h3>Simple example</h3>
 * 
 * Imagine we have a service request called FormatText, with a property "text" of type String. Typically, this would be evaluated on a server, where a
 * processor is registered which modifies the text, e.g. toUpperCase.
 * <p>
 * That's fine, but static. What if we wanted to ask the user what kind of effect we want to apply, e.g. toUpperCase, toLowerCase or say lIkE tHiS?
 * <p>
 * For this purpose we can create a view ({@code FormatTextDialog extends HxRequestDialog}) that shows three buttons for these three options plus a
 * "Cancel" button.
 * <p>
 * We then bind a special service-processor for our FormatText request like this: {@code bindDialogProcessor(FormatText.T, formatTextDialogInstance)}
 * <p>
 * Note this bind method is available in hydrux api on {@code IHxModuleBindingContext.serviceProcessorBinder}.
 * <p>
 * This bind method creates a special service-processor internally which
 * 
 * @author peter.gazdik
 */
@Abstract
public interface HxRequestDialog extends HxView {

	EntityType<HxRequestDialog> T = EntityTypes.T(HxRequestDialog.class);

	String getTitle();
	void setTitle(String title);

	HxWindowCustomizability getWindowCustomizability();
	void setWindowCustomizability(HxWindowCustomizability windowCustomizability);

	/**
	 * If true, the element is rendered as a <a href="https://en.wikipedia.org/wiki/Modal_window">modal window</a>, i.e. it blocks any interaction
	 * with the rest of the application.
	 */
	boolean getModal();
	void setModal(boolean modal);

}
