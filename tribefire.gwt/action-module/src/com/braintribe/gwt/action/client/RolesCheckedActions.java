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
package com.braintribe.gwt.action.client;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

import com.braintribe.gwt.ioc.client.Configurable;
import com.braintribe.gwt.ioc.client.Required;


/**
 * Set of actions that are checked against roles.
 * @author Dirk
 *
 */
public class RolesCheckedActions implements Iterable<Action> {
	private Set<Action> actions = new LinkedHashSet<Action>();
	private Supplier<Set<String>> rolesProvider;
	private boolean checkInverted = false;

	/**
	 * Required set of roles for checking the configured action against.
	 */
	@Configurable @Required
	public void setRolesProvider(Supplier<Set<String>> rolesProvider) {
		this.rolesProvider = rolesProvider;
	}
	
	/**
	 * Configures the action(s) that are checked against the roles.
	 */
	@Configurable
	public void addAction(Action action) {
		actions.add(action);
	}
	
	/**
	 * If true (defaults to false), the filter will work inverted: instead of checking for the presence of the configured roles,
	 * it will check for the NON presence of them.
	 */
	@Configurable
	public void setCheckInverted(boolean checkInverted) {
		this.checkInverted = checkInverted;
	}

	protected boolean isAllowed(Action action, Set<String> givenRoles) {
		Set<String> roles = action.getRoles();
		if (roles != null) {
			for (String role: roles) {
				if (givenRoles.contains(role)) {
					return checkInverted ? false : true;
				}
			}
			return checkInverted ? true : false;
		}
		return true;
	}
	
	protected Action filterAction(Action action, Set<String> givenRoles) {
		if (action instanceof ActionMenu) {
			ActionMenu menu = (ActionMenu)action;
			List<Action> actions = menu.getActions();

			Iterator<Action> it = actions.iterator();
			while (it.hasNext()) {
				Action subAction = it.next();
				subAction = filterAction(subAction, givenRoles);
				if (subAction == null)
					it.remove();
			}
			
			if (!actions.isEmpty()) return menu;
			else return null;
		}
		else {
			if (isAllowed(action, givenRoles))
				return action;
			else
				return null;
		}
	}
	
	/**
	 * This method returns the allowed Set of actions.
	 */
	public Set<Action> getFilteredActions() {
		Set<Action> filteredActions = new LinkedHashSet<Action>();
		
		try {
			Set<String> givenRoles = rolesProvider.get();
			for (Action action: actions) {
				action = filterAction(action, givenRoles);
				if (action != null) 
					filteredActions.add(action);
			}
		} catch (RuntimeException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		
		return filteredActions;
	}
	
	/**
	 * Iterator for the allowed set of actions.
	 */
	@Override
	public Iterator<Action> iterator() {
		return getFilteredActions().iterator();
	}
}
