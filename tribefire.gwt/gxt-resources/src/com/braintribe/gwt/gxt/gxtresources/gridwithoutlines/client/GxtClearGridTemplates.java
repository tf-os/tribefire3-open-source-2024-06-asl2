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
package com.braintribe.gwt.gxt.gxtresources.gridwithoutlines.client;

import com.google.gwt.safecss.shared.SafeStyles;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.sencha.gxt.widget.core.client.grid.GridView.GridTemplates;

/**
 * Templates prepared with gxtReset. This way, the use of gxt's reset.css is not needed.
 * @author michel.couto
 *
 */
public interface GxtClearGridTemplates extends GridTemplates {
	@Override
	@Template("<table cellpadding=\"0\" cellspacing=\"0\" class=\"{0}\" style=\"{1};table-layout: fixed\"><tbody>{3}</tbody><tbody>{2}</tbody></table>")
	SafeHtml table(String classes, SafeStyles tableStyles, SafeHtml contents, SafeHtml sizingHeads);

	@Override
	@Template("<td cellindex=\"{0}\" class=\"gxtReset {1}\" style=\"{2}\" tabindex=\"-1\"><div class=\"{3}\" style=\"{4}\">{5}</div></td>")
	SafeHtml td(int cellIndex, String cellClasses, SafeStyles cellStyles, String textClasses, SafeStyles textStyles, SafeHtml contents);

	@Override
	@Template("<td cellindex=\"{0}\" class=\"gxtReset {1}\" style=\"{2}\" rowspan=\"{3}\"><div class=\"{4}\">{5}</div></td>")
	SafeHtml tdRowSpan(int cellIndex, String classes, SafeStyles styles, int rowSpan, String cellInnerClasses, SafeHtml contents);

	@Override
	@Template("<td cellindex=\"{0}\" class=\"gxtReset {1}\" style=\"{2}\" tabindex=\"-1\"><div class=\"{3}\" style=\"{4}\" unselectable=\"on\">{5}</div></td>")
	SafeHtml tdUnselectable(int cellIndex, String cellClasses, SafeStyles cellStyles, String textClasses, SafeStyles textStyles,
			SafeHtml contents);

	@Override
	@Template("<td colspan=\"{0}\" class=\"gxtReset {1}\"><div class=\"{2}\">{3}</div></td>")
	SafeHtml tdWrap(int colspan, String cellClasses, String textClasses, SafeHtml content);

	@Override
	@Template("<td colspan=\"{0}\" class=\"gxtReset {1}\"><div class=\"{2}\" unselectable=\"on\">{3}</div></td>")
	SafeHtml tdWrapUnselectable(int colspan, String cellClasses, String textClasses, SafeHtml content);

	@Override
	@Template("<th class=\"{0}\" style=\"{1}\"></th>")
	SafeHtml th(String classes, SafeStyles cellStyles);

	@Override
	@Template("<tr class=\"{0}\">{1}</tr>")
	SafeHtml tr(String classes, SafeHtml contents);
}
