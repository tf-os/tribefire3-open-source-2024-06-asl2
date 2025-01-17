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

import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import com.braintribe.gwt.thumbnailpanel.client.ImageResourceModelData;
import com.google.gwt.core.client.Scheduler;

public class ImageLoadingChain implements ImageLoaderListener{
	
	//private boolean startLoading = true;
	//private boolean limited = true;
	private int limit = 4;
	
	public void setLimit(int limit) {
		this.limit = limit;
	}
	
	private Set<ImageLoader> loadingChain = new HashSet<ImageLoader>();
	private SortedSet<ImageLoader> waitingChain = new TreeSet<ImageLoader>();

	@Override
	public void onLoadingStatusChanged(ImageLoader imageLoader) {
		switch(imageLoader.getImageLoadingStatus()){
		case FAILURE:
			removeImageLoader(imageLoader);
			break;
		case LOADED:
			removeImageLoader(imageLoader);
			break;
		case LOADING:
			break;
		case UNLOADED:
			break;
		case WAITING:
			break;
		default:
			break;
		}		
	}
	
	@Override
	public void onVisibiltyChanged(ImageResourceModelData imageResourceModelData) {
		if(imageResourceModelData.isVisible()){
			switch(imageResourceModelData.getImageLoader().getImageLoadingStatus()){
			case FAILURE:
				addImageLoader(imageResourceModelData.getImageLoader());
				break;
			case LOADED:
				break;
			case LOADING:
				break;
			case UNLOADED:
				addImageLoader(imageResourceModelData.getImageLoader());
				break;
			case WAITING:
				break;
			default:
				break;
			}
		}else{
			switch(imageResourceModelData.getImageLoader().getImageLoadingStatus()){
			case FAILURE:
				break;
			case LOADED:
				break;
			case LOADING:
				break;
			case UNLOADED:
				break;
			case WAITING:
				waitingChain.remove(imageResourceModelData.getImageLoader());
				imageResourceModelData.getImageLoader().setImageLoadingStatus(ImageLoadingStatus.UNLOADED);
				break;
			default:
				break;
			}
		}
	}
	
	public void addImageLoader(ImageLoader imageLoader){		
		if(loadingChain.size() + 1 <= limit){
			//System.err.println("added " + imageLoader.getImageElementId() + " to loading queue");
			imageLoader.setParentLoadingChaing(this);
			loadingChain.add(imageLoader);
			Scheduler.get().scheduleDeferred(() -> {
				imageLoader.load(null);
			});			
		}
		else{
			//System.err.println("added " + imageLoader.getImageElementId() + " to waiting queue");
			imageLoader.setParentLoadingChaing(this);
			waitingChain.add(imageLoader);
			imageLoader.setImageLoadingStatus(ImageLoadingStatus.WAITING);
		}
	}
	
	public void removeImageLoader(ImageLoader imageLoader){
		//imageLoader.setParentLoadingChaing(null);
		if(loadingChain.contains(imageLoader)){
			//System.err.println("removed " + imageLoader.getImageElementId() + " from loading queue");
			loadingChain.remove(imageLoader);
			if(waitingChain.size() > 0){
				ImageLoader waitingLoader = waitingChain.first();
				removeImageLoader(waitingLoader);
				addImageLoader(waitingLoader);				
			}				
		}
		if(waitingChain.contains(imageLoader)){
			//System.err.println("removed " + imageLoader.getImageElementId() + " from waiting queue");
			waitingChain.remove(imageLoader);		
		}
	}

	public void clear() {
		loadingChain.clear();
		waitingChain.clear();		
	}
	
	public boolean isLoadingChainEmpty(){
		return loadingChain.isEmpty();
	}
	
	public boolean isWaitingChainEmpty(){
		return waitingChain.isEmpty();
	}

}
