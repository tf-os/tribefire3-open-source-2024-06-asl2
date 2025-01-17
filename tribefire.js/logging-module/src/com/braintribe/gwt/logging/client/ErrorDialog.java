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
package com.braintribe.gwt.logging.client;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

import com.braintribe.gwt.ioc.client.Configurable;

public class ErrorDialog {	
	private static ErrorUI errorUI;
	private static LogEventBuffer logEventBuffer;
	private static Formatter<String> formatter = new StandardStringFormatter();
	
	private static Map<Predicate<Throwable>, Consumer<Throwable>> exceptionFilterAndActionMap = new HashMap<>();
	public static final String EXCEPTION_PROPERTY = "exception";
	
	/**
	 * Configures a filter for filtering exceptions. If a exception is filtered out by using the given action, then the action is performed. 
	 */
	@Configurable
	public static void addExceptionFilterAction(Predicate<Throwable> exceptionFilter, Consumer<Throwable> exceptionAction) {
		exceptionFilterAndActionMap.put(exceptionFilter, exceptionAction);
	}
	
	public static void setFormatter(Formatter<String> formatter) {
		ErrorDialog.formatter = formatter;
	}
	
	public static Formatter<String> getFormatter() {
		return formatter;
	}

	
	public static void setLogEventBuffer(LogEventBuffer logEventBuffer) {
		ErrorDialog.logEventBuffer = logEventBuffer;
	}
	
	public static LogEventBuffer getLogEventBuffer() {
		return logEventBuffer;
	}

	public static void setErrorUI(ErrorUI errorUI) {
		ErrorDialog.errorUI = errorUI;
	}
	
	/**
	 * If a filter matches the given {@link Throwable}, then its configured action is called and true is returned.
	 * Otherwise, false is returned.
	 */
	public static boolean handleIfExceptionFilter(Throwable t) {
		boolean result = false;
		for (Map.Entry<Predicate<Throwable>, Consumer<Throwable>> entry : exceptionFilterAndActionMap.entrySet()) {
			if (ExceptionUtil.getSpecificCause(t, entry.getKey()) != null) {
				entry.getValue().accept(t);
				result = true;
			}
		}
		
		return result;
	}

	public static void show(String message, Throwable t, boolean details) {
		if (handleIfExceptionFilter(t))
			return;
		
		if (details && errorUI instanceof ExtendedErrorUI)
			((ExtendedErrorUI) errorUI).showDetails(message, t);
		else
			errorUI.show(message, t);
	}
	
	public static void show(String message, Throwable t) {
		show(message, t, false);
	}
	
	public static void show(String message, String details) {
		if (errorUI instanceof ExtendedErrorUI)
			((ExtendedErrorUI) errorUI).showDetails(message, details);
		else
			errorUI.show(message);
	}
	
	public static void showMessage(String message) {
		errorUI.show(message);
	}
}
