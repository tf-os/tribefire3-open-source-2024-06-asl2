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
package com.braintribe.model.processing.library.service.libdoc;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.util.Matrix;

import com.braintribe.logging.Logger;

public class IndexGenerator {

	private final static Logger logger = Logger.getLogger(IndexGenerator.class);
	
	protected static float titleFontSize = 14.0f;
	protected static float tableFontSize = 10.0f;
	protected static Color gray = new Color(125, 125, 125);

	public static InMemoryPdf createIndexPages(String title, LinkedHashMap<String,Integer> indexMap) throws Exception {

		PDDocument document = new PDDocument();
		PDFont font = PDType1Font.HELVETICA;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		PDPage page = createPage(document, font, title);
		
		PDRectangle pageSize = page.getMediaBox();
		float pageWidth = pageSize.getWidth();
		float pageHeight = pageSize.getHeight();
		float y = pageHeight - (titleFontSize + 60);
		PDPageContentStream contentStream = null;
		
		int pageCount = 1;
		
		for (Map.Entry<String,Integer> entry : indexMap.entrySet()) {

			if (contentStream == null) {
				contentStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true, true);
			}
			
			String key = entry.getKey();
			Integer pageNumber = entry.getValue();
			
			if (y < (20 + tableFontSize)) {
				page = createPage(document, font, title);
				pageCount++;
				y = pageHeight - (titleFontSize + 60);
				
				contentStream.close();
				contentStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true, true);
			}

			addRow(key, pageNumber, font, pageWidth, y, contentStream);
			y -= (tableFontSize+10);
		}
		
		if (contentStream != null) {
			contentStream.close();
		}

		// Save the results and ensure that the document is properly closed:
		document.save(baos);
		document.close();

		final int totalPages = pageCount;
		logger.debug(() -> "Created a "+totalPages+" index PDF for "+title);
		
		return new InMemoryPdf(baos.toByteArray(), pageCount);
	}
	
	private static void addRow(String key, Integer pageNumber, PDFont font, float pageWidth, float y, PDPageContentStream contentStream) throws Exception {
		contentStream.beginText();
		// set font and font size
		contentStream.setFont(font, tableFontSize);
		// set text color to red
		contentStream.setNonStrokingColor(Color.BLACK);
		contentStream.setTextMatrix(Matrix.getTranslateInstance(20f, y));

		contentStream.showText(key);
		contentStream.endText();
		
		String message = ""+pageNumber;
		float stringWidth = font.getStringWidth(message)*tableFontSize/1000f;
		contentStream.beginText();
		// set font and font size
		contentStream.setFont(font, tableFontSize);
		// set text color to red
		contentStream.setNonStrokingColor(Color.BLACK);
		contentStream.setTextMatrix(Matrix.getTranslateInstance(pageWidth-stringWidth-40, y));

		contentStream.showText(message);
		contentStream.endText();	

	}

	protected static PDPage createPage(PDDocument document, PDFont font, String title) throws Exception {
		
		PDPage page = new PDPage(PDRectangle.A4);
		document.addPage(page);
		
		PDRectangle pageSize = page.getMediaBox();
		float pageHeight = pageSize.getHeight();
		
		
		//Add title
		
		PDPageContentStream contentStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true, true);
		contentStream.beginText();
		// set font and font size
		contentStream.setFont(font, titleFontSize);
		// set text color to red
		contentStream.setNonStrokingColor(Color.BLACK);
		contentStream.setTextMatrix(Matrix.getTranslateInstance(20f, pageHeight-40f));

		contentStream.showText(title);
		contentStream.endText();
		contentStream.close();
		
		return page;
	}
}
