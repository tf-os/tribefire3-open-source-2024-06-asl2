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
package com.braintribe.gwt.tribefirejs.client.error;

import jsinterop.annotations.JsIgnore;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

@SuppressWarnings({ "unusable-by-js", "serial" })
@JsType
public class TfJsError extends Throwable{
	
	Object t;
	
	public TfJsError(Object t) {
		this.t = t;
	}
	
	@JsProperty(name="message")
	public String getJsMessage() {
		if(isNativeError(t))
			return msg(t);
		else {
			if(t instanceof Throwable)
				return ((Throwable)t).getMessage();
			return "undefined";
		}
	}

	@Override
	public String getMessage() {
		return getJsMessage();	
	}
	
	private native String msg(Object o) /*-{
		return o.message;
	}-*/;

	@JsProperty(name="localizedMessage")
	public String getJsLocalizedMessage() {
		if(isNativeError(t))
			return msg(t);
		else {
			if(t instanceof Throwable)
				return ((Throwable)t).getLocalizedMessage();
			return "undefined";
		}
	}

	@Override
	public String getLocalizedMessage() {
		return getJsLocalizedMessage();
	}

	@JsProperty(name="cause")
	public Throwable getJsCause() {
		if(t instanceof Throwable)
			return new TfJsError(((Throwable) t).getCause());
		else
			return this;
	}

	@Override
	public Throwable getCause() {
		return getJsCause();
	}
	
	@JsMethod
	public void log() {
		_log(getMessage());
	}
	
	@JsMethod
	@Override
	public void printStackTrace() {
		if(!isNativeError(t))		
			((Throwable) t).printStackTrace(new TfJsConsolePrintStream());
		else
			_log(t);
	}

	@JsProperty(name="stackTrace")
	public StackTraceElement[] getJsStackTrace() {
		if(isNativeError(t))
			return trace(t);
		else
			if(t instanceof Throwable)
				return ((Throwable) t).getStackTrace();
			else
				return trace(t);
	}
	
	@Override
	public StackTraceElement[] getStackTrace() {
		return getJsStackTrace();
	}
	
	@JsProperty(name="typeName")
	public String getTypeName() {
		if(isNativeError(t))
			return name(t);
		return t.getClass().getName();
	}
	
	@JsProperty(name="name")
	public String getSimpleName() {
		if(isNativeError(t))
			return name(t);
		return t.getClass().getSimpleName();
	}
	
	@JsProperty(name="stack")
	public String getStack() {
		if(isNativeError(t)){
			return stack(t);
		}else {
			StringBuilder sb = new StringBuilder();
			for(StackTraceElement ste : getStackTrace()) {
				sb.append("at " + ste.getClassName() + "." + ste.getMethodName() + "(" + ste.getFileName() + ":" + ste.getLineNumber() + ")\n");
			}
			return sb.toString();
		}
	}
	
	@JsIgnore
	private native String name(Object o) /*-{
		return o.name;
	}-*/;
	
	@JsIgnore
	private native StackTraceElement[] trace(Object o) /*-{
		return o.trace;
	}-*/;
	
	@JsIgnore
	private native String stack(Object o) /*-{
		return o.stack;
	}-*/;
	
	@JsIgnore
	private native boolean supportsTrace(Object o) /*-{
		if(o.printStackTrace)
			return true;
		else
			return false;
	}-*/;
	
	@JsIgnore
	private native void _log(Object o) /*-{
		console.log(o);
	}-*/;
	
	@JsProperty(name="isNative")
	public boolean isNative() { 
		return isNativeError(t);
	}
	
	@JsIgnore
	private native boolean isNativeError(Object e) /*-{
		return (e != undefined && e.stack != undefined && e.message != undefined);
	}-*/;
	
}
