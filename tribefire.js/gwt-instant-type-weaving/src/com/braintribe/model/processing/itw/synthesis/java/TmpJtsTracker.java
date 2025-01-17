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
package com.braintribe.model.processing.itw.synthesis.java;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;

import com.braintribe.model.generic.reflection.GenericModelException;
import com.braintribe.model.weaving.ProtoGmEntityType;

/**
 * Temporary code to keep track of instances of GMTS/JTS, for better analysis.
 * 
 * @author peter.gazdik
 */
public class TmpJtsTracker {

	public static final TmpJtsTracker INSTANCE = new TmpJtsTracker();

	private TmpJtsTracker() {
	}

	private final Map<Exception, Object> exceptions = new ConcurrentHashMap<>();

	public synchronized void onNewJts() {
		Exception e = new Exception();
		exceptions.put(e, e);
	}

	public synchronized <T> T handleClassCastException(ClassCastException e) {
		String traces = getTracesForJtsInstantiations();

		throw new GenericModelException("We had our good old ClassCasstException. <JTS_INFO>\n" + traces + "\n</JTS_INFO>", e);
	}

	private String getTracesForJtsInstantiations() {
		if (exceptions.size() == 1)
			return "[There is only one instance of JTS]";

		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);

		int i = 1;
		for (Exception e : exceptions.keySet()) {
			pw.write(i++ + " - ");
			e.printStackTrace(pw);
			pw.write("\n\n");
		}

		return sw.toString();
	}

	public void checkCreatinoStackIsEmpty(Stack<ProtoGmEntityType> entityCreationStack, ProtoGmEntityType typeToWeave, Throwable t) {
		if (!entityCreationStack.isEmpty())
			throw new GenericModelException("ITW is in inconsistent state. Cannot weave " + typeToWeave
					+ " as the creation stack is not empty. Stack: " + entityCreationStack + ". Original error:" + toString(t)
					+ " TROUBLESHOOTING: This might happen as a followup error to a previous one, please check to logs."
					+ " Another case when this could happen is related to lazy-loading (LL) within the woven model. ITW should only be called with fully loaded models."
					+ " If there are absent properties and LL is triggered, unmarshalling the LL query result might trigger ITW again, whch leads to this exception.");
	}

	private String toString(Throwable t) {
		if (t == null)
			return null;

		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		t.printStackTrace(pw);
		return sw.toString();
	}

}
