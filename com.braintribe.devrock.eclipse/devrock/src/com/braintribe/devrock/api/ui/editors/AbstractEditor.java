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
package com.braintribe.devrock.api.ui.editors;

import java.util.HashSet;
import java.util.Set;

import com.braintribe.devrock.api.ui.listeners.ModificationNotificationBroadcaster;
import com.braintribe.devrock.api.ui.listeners.ModificationNotificationListener;

public class AbstractEditor implements ModificationNotificationBroadcaster{
	protected Set<ModificationNotificationListener> listeners = new HashSet<ModificationNotificationListener>();

	@Override
	public void addListener(ModificationNotificationListener listener) {
		listeners.add( listener);
	}

	@Override
	public void removeListener(ModificationNotificationListener listener) {
		listeners.remove(listener);
	}

	protected void broadcast( String value) {
		for (ModificationNotificationListener listener : listeners) {
			listener.acknowledgeChange( this, value);
		}		
	}
	

}