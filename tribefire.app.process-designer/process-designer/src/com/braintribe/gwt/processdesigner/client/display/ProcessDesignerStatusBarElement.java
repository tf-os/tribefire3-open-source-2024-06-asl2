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
package com.braintribe.gwt.processdesigner.client.display;

import org.vectomatic.dom.svg.OMSVGGElement;
import org.vectomatic.dom.svg.OMSVGRectElement;
import org.vectomatic.dom.svg.OMSVGTextElement;
import org.vectomatic.dom.svg.utils.OMSVGParser;

import com.braintribe.gwt.gmview.client.GmSelectionListener;
import com.braintribe.model.generic.tracking.ManipulationListener;

public abstract class ProcessDesignerStatusBarElement extends OMSVGGElement implements ManipulationListener, GmSelectionListener{

	protected int width = 75;
	protected int height = 75;
	private OMSVGRectElement rect;
	//private ImageResource icon;
	//private OMSVGImageElement iconElement;
	private String name;
	//private String tooltip;
	private OMSVGTextElement text;
	private OMSVGTextElement value;
	
	public ProcessDesignerStatusBarElement() {
	
	}

	public void setName(String name) {
		this.name = name;
	}
	
	/*public void setTooltip(String tooltip) {
		this.tooltip = tooltip;
	}*/
	
	public void init(){
		getRect().setAttribute("x", getX()+"");
		getRect().setAttribute("y", getY()+"");
		
		getValue().setAttribute("x", (getX()+width/2)+"");
		getValue().setAttribute("y", (getY()+height/2)+"");
		getValue().getElement().setInnerText("");
		
		getText().setAttribute("x", (getX()+width/2)+"");
		getText().setAttribute("y", (getY()+10)+"");
		getText().getElement().setInnerText(name);
		
		appendChild(getRect());
		appendChild(getValue());
		appendChild(getText());
		configure();
	}
	
	public OMSVGRectElement getRect() {
		if(rect == null){
			rect = new OMSVGRectElement(0, 0, width, height, 0, 0);
			rect.setAttribute("style", "fill:none;stroke:none;stroke-width:0;stroke-dasharray:0,0");
		}
		return rect;
	}
	
	public OMSVGTextElement getText() {
		if(text == null){
			text = OMSVGParser.currentDocument().createSVGTextElement();
			text.setAttribute("text-anchor","middle");
			text.setAttribute("dominant-baseline","middle");
			text.setAttribute("font-family", "Open Sans");
			text.setAttribute("font-weight", "bold");
			text.setAttribute("opacity", "1.0");
			text.setAttribute("fill", "black");
			text.setAttribute("font-size", "10px");	
		}
		return text;
	}
	
	public OMSVGTextElement getValue() {
		if(value == null){
			value = OMSVGParser.currentDocument().createSVGTextElement();
			value.setAttribute("text-anchor","middle");
			value.setAttribute("dominant-baseline","middle");
			value.setAttribute("font-family", "Open Sans");
			value.setAttribute("font-weight", "bold");
			value.setAttribute("opacity", "1.0");
			value.setAttribute("fill", "black");
			value.setAttribute("font-size", "12px");	
		}
		return value;
	}
	
	public double getX(){
		try{
			return Double.parseDouble(getAttribute("x"));
		}catch(Exception ex){
			return 0;
		}
	}
	
	public double getY(){
		try{
			return Double.parseDouble(getAttribute("y"));
		}catch(Exception ex){
			return 0;
		}
	}

	public abstract void handleDipose();
	
	public abstract void configure();
}
