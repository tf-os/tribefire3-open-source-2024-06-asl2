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
package com.braintribe.gwt.async.client;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import com.google.gwt.user.client.rpc.AsyncCallback;

public class MultiLoader implements Loader<MultiLoaderResult> {
	private Map<String, Loader<?>> loaders = new LinkedHashMap<String, Loader<?>>();
	private boolean parallel;
	
	public MultiLoader setParallel(boolean parallel) {
		this.parallel = parallel;
		return this;
	}

	public MultiLoader add(String key, Loader<?> loader) {
		loaders.put(key, loader);
		return this;
	}
	
	public Future<MultiLoaderResult> load() {
		Future<MultiLoaderResult> future = new Future<MultiLoaderResult>();
		load(future);
		return future;
	}
	
	@Override
	public void load(final AsyncCallback<MultiLoaderResult> asyncCallback) {
		if (parallel) loadParallel(asyncCallback);
		else loadSerial(asyncCallback);
	}
	
	protected void loadParallel(final AsyncCallback<MultiLoaderResult> asyncCallback) {
		final MultiLoaderResult multiLoaderResult = new MultiLoaderResult();
		
		for (Map.Entry<String, Loader<?>> entry: loaders.entrySet()) {
			final String key = entry.getKey();
			
			Loader<Object> loader = (Loader<Object>)entry.getValue();
			
			loader.load(new AsyncCallback<Object>() {
				@Override
				public void onSuccess(Object result) {
					multiLoaderResult.set(key, result);
					if (multiLoaderResult.getResults().size() == loaders.size())
						asyncCallback.onSuccess(multiLoaderResult);
				}
				
				@Override
				public void onFailure(Throwable caught) {
					asyncCallback.onFailure(caught);
				}
			});
		}
	}
	
	protected void loadSerial(final AsyncCallback<MultiLoaderResult> asyncCallback) {
		final Iterator<Map.Entry<String, Loader<?>>> it = loaders.entrySet().iterator();
		final MultiLoaderResult multiLoaderResult = new MultiLoaderResult();
		
		new Object() {
			public void loadNext() {
				if (it.hasNext()) {
					Map.Entry<String, Loader<?>> entry = it.next();
					Loader<Object> loader = (Loader<Object>)entry.getValue();
					final String key = entry.getKey();
		
					loader.load(new AsyncCallback<Object>() {
						@Override
						public void onSuccess(Object result) {
							multiLoaderResult.set(key, result);
							loadNext();
						}
						
						@Override
						public void onFailure(Throwable caught) {
							asyncCallback.onFailure(caught);
						}
					});
				}
				else {
					asyncCallback.onSuccess(multiLoaderResult);
				}
			}
		}.loadNext();
	}
}
