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
package com.braintribe.devrock.mc.core.commons;

import java.util.ArrayList;
import java.util.List;

/**
 * helper to retrieve file listing (parts) from a HTML page (as maven standard repositories do)
 * (ported from mc-the-older)
 * @author pit
 *
 */
public class HtmlContentParser {
	private static String HREF_TOKEN_BEGIN = "href=\"";
	private static String HREF_TOKEN_END = "\"";
	
	/**
	 * clears chaff from a href statement 
	 * @param value - the expression 
	 * @return - the cleaned expression
	 */
	private static String sanitizeHrefExpression( String value) {
		//
		StringBuilder buffer = new StringBuilder();
		for (int i = 0; i < value.length(); i++) {
			char c = value.charAt(i);
			switch (c) {
				case ':':
					break;
				default:
					buffer.append(c);
				}
		}
		return buffer.toString();
	}
	/**
	 * parse file names from a html page - actually, hrefs are interpreted as pointing to files 
	 * @param filesInHtml - the contents of the html page   
	 * @return - a {@link List} of files (as {@link String})
	 */
	public static List<String> parseFilenamesFromHtml( String filesInHtml) {
		   List<String> result = new ArrayList<String>();
		   if (filesInHtml == null)
			   return result;
		   List<String> hrefs = extractHrefs(filesInHtml);
			
			for (String href : hrefs) {
				if (href.endsWith( "/"))
					continue;
				int p = href.lastIndexOf( "/");
				String name = null;
				if (p > 0) {
					name = href.substring( p+1);
				} else {
					name = href;
				}
				name = sanitizeHrefExpression(name);				
				result.add( name);
			}
		   return result;
	}
	
	/**
	 * returns a list of all href targets of a html code 
	 * @param html - the html code
	 * @return - a list of all href targets found 
	 */
	public static List<String> extractHrefs( String html) {
		
		List<String> result = new ArrayList<String>();
		int p = html.indexOf( HREF_TOKEN_BEGIN);
		if (p < 0)
			return result;
		do {
			int p2 = p + HREF_TOKEN_BEGIN.length();
			int q = html.indexOf( HREF_TOKEN_END, p2);
			String part = html.substring( p2, q);
			if (part.startsWith("\"")) {
				part = part.substring(1);
			}
			if (part.endsWith("\"")) {
				part = part.substring( 0, part.length()-1);
			}			
			result.add( part);
			p = html.indexOf( HREF_TOKEN_BEGIN, q+1);			
		} while (p > 0);
		
		return result;
	}
}
