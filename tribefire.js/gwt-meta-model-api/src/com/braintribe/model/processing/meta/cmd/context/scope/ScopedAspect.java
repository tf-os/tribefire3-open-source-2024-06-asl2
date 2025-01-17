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
package com.braintribe.model.processing.meta.cmd.context.scope;

import com.braintribe.model.processing.meta.cmd.context.SelectorContextAspect;
import com.braintribe.model.processing.meta.cmd.context.aspects.GmEntityTypeAspect;
import com.braintribe.model.processing.meta.cmd.context.aspects.RoleAspect;

public class ScopedAspect<T> implements SelectorContextAspect<T> {

	/* This makes sure there is no way to create a subclass of {@linkplain ScopedAspect} directly, but one must use one
	 * of the static nested classes. */
	private ScopedAspect() {
	}

	/**
	 * Given aspect does not change at all. Example: {@link GmEntityTypeAspect} (for given entity this remains
	 * constant).
	 */
	public static class StaticScopedAspect<T> extends ScopedAspect<T> {
		// blank
	}

	/**
	 * Given aspect remains constant inside a given session. Example: {@link RoleAspect}.
	 */
	public static class SessionScopedAspect<T> extends ScopedAspect<T> {
		// blank
	}

	/**
	 * In general it is like {@link CmdScope#VOLATILE}, but a client may explicitly specify some period of time (by
	 * invoking either of start/stop methods) during which the value of such aspect remains constant.
	 */
	public static class MomentaryScopedAspect<T> extends ScopedAspect<T> {
		// blank
	}

	/**
	 * Aspects with this scope must always be evaluated, their value may change at any time.
	 */
	public static class VolatileScopedAspect<T> extends ScopedAspect<T> {
		// blank
	}
}
