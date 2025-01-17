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
package com.braintribe.gwt.thumbnailpanel.client.expert;

import java.util.ArrayList;
import java.util.List;

import com.braintribe.gwt.thumbnailpanel.client.ImageResourceModelData;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.Timer;

public class ImageLoader implements Comparable<ImageLoader> {
	
	@Override
	public int compareTo(ImageLoader o) {
		return imageElementId.compareTo(o.getImageElementId());
	}
	
	private String src;
	private String defaultSrc; //watermark, defaultIcon
	private String imageElementId;
	private ImageLoadingStatus imageLoadingStatus = ImageLoadingStatus.UNLOADED;
	private List<ImageLoaderListener> imageLoaderListeners = new ArrayList<ImageLoaderListener>();
	private ImageLoadingChain parentLoadingChaing;
	
	private static int timeOutIntervall = 20000;
	
	public void setParentLoadingChaing(ImageLoadingChain parentLoadingChaing) {
		this.parentLoadingChaing = parentLoadingChaing;
	}
	
	public ImageLoadingChain getParentLoadingChaing() {
		return parentLoadingChaing;
	}

	public void setSrc(String src) {
		this.src = src;
	}
	
	public String getSrc() {
		return src;
	}

	public void setDefaultSrc(String defaultSrc) {
		this.defaultSrc = defaultSrc;
	}
	
	public String getDefaultSrc() {
		return defaultSrc;
	}
	
	public void setImageElementId(String imageElementId) {
		this.imageElementId = imageElementId;
	}
	
	public String getImageElementId() {
		return imageElementId;
	}
	
	public void setImageLoadingStatus(ImageLoadingStatus imageLoadingStatus) {
		this.imageLoadingStatus = imageLoadingStatus;
		fireOnLoadingStatusChanged();
	}
	
	public ImageLoadingStatus getImageLoadingStatus() {
		return imageLoadingStatus;
	}
	
	public void addImageLoaderListener(ImageLoaderListener listener){
		imageLoaderListeners.add(listener);
	}
	
	public void removeImageLoaderListener(ImageLoaderListener listener){
		imageLoaderListeners.remove(listener);
	}
	
	public void fireOnLoadingStatusChanged(){
		for(ImageLoaderListener listener : new ArrayList<ImageLoaderListener>(imageLoaderListeners))
			listener.onLoadingStatusChanged(this);
	}
	
	public void fireOnVisibilityChanged(ImageResourceModelData imageResourceModelData){
		for(ImageLoaderListener listener : new ArrayList<ImageLoaderListener>(imageLoaderListeners))
			listener.onVisibiltyChanged(imageResourceModelData);
	}	

	public void load(LoadAndErrorHandler loader/*AsyncCallback<ImageElement> asyncCallback*/) {		
		setImageLoadingStatus(ImageLoadingStatus.LOADING);
		//ImageElement imageElement = com.google.gwt.dom.client.Document.get().getElementById(imageElementId).cast();
		Element element = com.google.gwt.dom.client.Document.get().getElementById(imageElementId);
		if (element == null)
			return;
		
		if (loader == null){			
			DOM.sinkEvents(element, Event.ONLOAD | Event.ONERROR);
			loader = new LoadAndErrorHandler(/*asyncCallback,*/ element, this);
			DOM.setEventListener(element, loader);
		}
		// this starts the actual loading
		//imageElement.setSrc(src);
		if (src != null && !src.isEmpty())
			element.setAttribute("src", src);
		else
			setImageLoadingStatus(ImageLoadingStatus.FAILURE);
	}	

	private static class LoadAndErrorHandler implements EventListener {
		private Element imageElement;
		private ImageLoader loader;
		private Timer timer = new Timer() {			
			@Override
			public void run() {
				onError();
				cancel();
			}
		};
		
		public LoadAndErrorHandler(/*AsyncCallback<ImageElement> asyncCallback,*/ Element imageElement, ImageLoader loader) {
			//this.asyncCallback = asyncCallback;
			this.imageElement = imageElement;
			this.loader = loader;
			timer.schedule(timeOutIntervall);
		}
		
		@Override
		public void onBrowserEvent(Event event) {
			switch (event.getTypeInt()) {
			case Event.ONLOAD:
				onLoad();
				break;
			case Event.ONERROR:
				onError();
				break;
			}			
		}
		
		public void onError() {
			imageElement.setAttribute("src", "");
			loader.setImageLoadingStatus(ImageLoadingStatus.FAILURE);
			timer.cancel();
			//removeEventListeners();
			//asyncCallback.onFailure(new ImageLoadingException());
		}
		
		public void onLoad() {
			loader.setImageLoadingStatus(ImageLoadingStatus.LOADED);
			timer.cancel();
			//removeEventListeners();
			//asyncCallback.onSuccess(imageElement);
		}
		
		/*private void removeEventListeners(){
			DOM.sinkEvents((com.google.gwt.user.client.Element)imageElement.cast(), 0);
			DOM.setEventListener((com.google.gwt.user.client.Element)imageElement.cast(), null);
		}*/
	}
}
