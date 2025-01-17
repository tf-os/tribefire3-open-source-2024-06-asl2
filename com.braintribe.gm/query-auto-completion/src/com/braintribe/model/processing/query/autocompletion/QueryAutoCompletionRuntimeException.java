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
package com.braintribe.model.processing.query.autocompletion;

public class QueryAutoCompletionRuntimeException extends RuntimeException {
	private static final long serialVersionUID = -1139774778244065260L;

	public QueryAutoCompletionRuntimeException() {
		super();
	}

	public QueryAutoCompletionRuntimeException(final String message) {
		super(message);
	}

	public QueryAutoCompletionRuntimeException(final Throwable cause) {
		super(cause);
	}

	public QueryAutoCompletionRuntimeException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public QueryAutoCompletionRuntimeException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
