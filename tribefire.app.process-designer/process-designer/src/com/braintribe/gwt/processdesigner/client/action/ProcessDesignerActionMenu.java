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
package com.braintribe.gwt.processdesigner.client.action;

import java.util.List;

import org.vectomatic.dom.svg.OMSVGGElement;
import org.vectomatic.dom.svg.OMSVGRectElement;

import com.braintribe.gwt.processdesigner.client.animation.SvgElementAnimation;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;

public class ProcessDesignerActionMenu extends OMSVGGElement implements MouseOverHandler, MouseOutHandler{
	
	private int width = 75;
	private int height = 100;
	private OMSVGRectElement rect = new OMSVGRectElement(0, 0, 0, height, 0, 0);
	private List<ProcessDesignerActionMenuElement> menuElements;
	
	public void setMenuElements(List<ProcessDesignerActionMenuElement> menuElements) {
		this.menuElements = menuElements;
	}
	
	public ProcessDesignerActionMenu() {
//		setAttribute("opacity", "0");
		setAttribute("id", "actionMenu");
		appendChild(rect);
		
//		addMouseOverHandler(this);
//		addMouseOutHandler(this);
	}
	
	public void init(){
		rect.setAttribute("x", getX() + "");
		rect.setAttribute("y", getY() + "");
		rect.setAttribute("width", (menuElements.size() *  width) + "");
		rect.setAttribute("height", height+"");
		rect.setAttribute("style", "fill:white;stroke:silver;stroke-width:0;stroke-dasharray:2,3;stroke-linecap:round");
		int i = 0;
		for(ProcessDesignerActionMenuElement element : menuElements){
			element.setAttribute("x", (getX() + (i * width)) + "");
			element.setAttribute("y", getY() + "");
			element.init();
			i++;
			appendChild(element);
		}
	}
	
	public int getHeight() {
		return height;
	}
	
	public double getX(){
		return Double.parseDouble(getAttribute("x"));
	}
	
	public double getY(){
		return Double.parseDouble(getAttribute("y"));
	}
	
	@Override
	public void onMouseOut(MouseOutEvent event) {
		SvgElementAnimation.getInstance().startAnimation(this, "opacity", 1, 0, 500, 250);
	}
	
	@Override
	public void onMouseOver(MouseOverEvent event) {
		SvgElementAnimation.getInstance().startAnimation(this, "opacity", 0, 1, 500, 250);
	}

}
