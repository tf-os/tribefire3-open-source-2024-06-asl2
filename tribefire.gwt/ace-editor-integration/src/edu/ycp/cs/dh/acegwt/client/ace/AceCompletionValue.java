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
package edu.ycp.cs.dh.acegwt.client.ace;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * A completion proposed by an {@link AceCompletionProvider}. 
 * 
 * <strong>Warning</strong>: this is an experimental feature of AceGWT.
 * It is possible that the API will change in an incompatible way
 * in future releases.
 */
public class AceCompletionValue extends AceCompletion {
	
	/**
	 * The caption of the completion (this is the left aligned autocompletion name on the left side of items in the dropdown box. If only a single completion is available in a context, then the caption will not be seen.
	 */
	private final String caption;
	
	/**
	 * The text value of the completion. This does not need to be escaped.
	 */
	private final String value;
	
	
	/**
	 * "meta" means the category of the substitution (this appears right aligned on the dropdown list). This is freeform description and can contain anything but typically a very short category description (9 chars or less) such as "function" or "param" or "template".
	 */
	private final String meta;

	/**
	 * The score is the value assigned to the autocompletion option. Scores with a higher value will appear closer to the top. Items with an identical score are sorted alphbetically by caption in the drop down.
	 */
	private final int score;
	
	/**
	 * The score is the value assigned to the autocompletion option. Scores with a higher value will appear closer to the top. Items with an identical score are sorted alphbetically by caption in the drop down.
	 */
	private final String tooltip;
	
	/**
	 * Constructor. 
	 * 
	 * @param name The caption of the completion (this is the left aligned autocompletion name on the left side of items in the dropdown box. If only a single completion is available in a context, then the caption will not be seen.
	 * @param value  The text value of the completion. This does not need to be escaped.
	 * @param meta "meta" means the category of the substitution (this appears right aligned on the dropdown list). This is freeform description and can contain anything but typically a very short category description (9 chars or less) such as "function" or "param" or "template".
	 * @param score  The score is the value assigned to the autocompletion option. Scores with a higher value will appear closer to the top. Items with an identical score are sorted alphbetically by caption in the drop down.
	 */
	public AceCompletionValue(String name, String value, String meta, int score) {
		this.caption = name;
		this.value = value;
		this.meta = meta;
		this.tooltip = null;
		this.score = score;
	}
	
	/**
	 * Constructor. 
	 * 
	 * @param name The caption of the completion (this is the left aligned autocompletion name on the left side of items in the dropdown box. If only a single completion is available in a context, then the caption will not be seen.
	 * @param value  The text value of the completion. This does not need to be escaped.
	 * @param meta "meta" means the category of the substitution (this appears right aligned on the dropdown list). This is freeform description and can contain anything but typically a very short category description (9 chars or less) such as "function" or "param" or "template".
	 * @param tooltip "tooltip" is an escaped html tooltip to be displayed when the completion option is displayed, this can be null. 
	 * @param score  The score is the value assigned to the autocompletion option. Scores with a higher value will appear closer to the top. Items with an identical score are sorted alphbetically by caption in the drop down.
	 */
	public AceCompletionValue(String name, String value, String meta, String tooltip, int score) {
		this.caption = name;
		this.value = value;
		this.score = score;
		this.tooltip = tooltip;
		this.meta = meta;
	}
	
	/**
	 * Convert to a native JS object in the format expected
	 * by the Ace code completion callback.
	 * 
	 * @return native JS object
	 */
	@Override
	native JavaScriptObject toJsObject() /*-{
		
		
			return {
				caption: this.@edu.ycp.cs.dh.acegwt.client.ace.AceCompletionValue::caption,
				value: this.@edu.ycp.cs.dh.acegwt.client.ace.AceCompletionValue::value,
				score: this.@edu.ycp.cs.dh.acegwt.client.ace.AceCompletionValue::score,
				meta: this.@edu.ycp.cs.dh.acegwt.client.ace.AceCompletionValue::meta,
			    aceGwtHtmlTooltip: this.@edu.ycp.cs.dh.acegwt.client.ace.AceCompletionValue::tooltip
			};

	}-*/;
}
