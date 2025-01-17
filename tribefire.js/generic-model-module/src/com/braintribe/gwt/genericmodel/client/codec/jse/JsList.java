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
package com.braintribe.gwt.genericmodel.client.codec.jse;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import com.google.gwt.core.client.JavaScriptObject;

public class JsList<E> extends JavaScriptObject {
	protected JsList() {
		
	}

	public final native int size() /*-{
		return this.length;
	}-*/;

	public final boolean isEmpty() {
		return size() == 0;
	}

	public final boolean contains(Object o) {
		return indexOf(o) >= 0;
	}
	
	public final int indexOf(Object o) {
		int size = size();
		if (o == null) {
		    for (int i = 0; i < size; i++)
			if (get(i)==null)
			    return i;
		} else {
		    for (int i = 0; i < size; i++)
			if (o.equals(get(i)))
			    return i;
		}
		return -1;
	}
	
	public final int lastIndexOf(Object o) {
		int size = size();
		if (o == null) {
		    for (int i = size-1; i >= 0; i--)
			if (get(i)==null)
			    return i;
		} else {
		    for (int i = size-1; i >= 0; i--)
			if (o.equals(get(i)))
			    return i;
		}
		return -1;
	}
	
	
	public final native E get(int index) /*-{
		return this[index];
	}-*/;

	public final native E set(int index, E element) /*-{
		var oldValue = this[index];
		this[index] = element;
		return oldValue;
	}-*/;

	public final native void add(int index, E element) /*-{
		this.splice(index,0,element);
	}-*/;

	public final native E remove(int index) /*-{
		return this.splice(index,1)[0];
	}-*/;

	public final native void removeIntervall(int s, int e) /*-{
		this.splice(s, e - s);
	}-*/;
	
	public final native void clear() /*-{
		this.length = 0;
	}-*/;


	public final native boolean add(E e) /*-{
		this.push(e);
		return true;
	}-*/;

	public final boolean addAll(Collection<? extends E> c) {
		Iterator<? extends E> e = c.iterator();
		while (e.hasNext()) {
		    add(e.next());
		}
		return true;
	}

	public final boolean addAll(int index, Collection<? extends E> c) {
		Iterator<? extends E> e = c.iterator();
		while (e.hasNext()) {
		    add(index++, e.next());
		}
		return true;
	}

	public final boolean remove(Object o) {
		int index = indexOf(o);
		if (index != -1) {
			remove(index);
			return true;
		}
		else
			return false;
	}

	public final boolean containsAll(Collection<?> c) {
		Iterator<?> e = c.iterator();
		while (e.hasNext())
		    if (!contains(e.next()))
			return false;
		return true;
	}
	
	public final Object[] toArray() {
		int size = size();
		Object array[] = new Object[size];
		for (int i = 0; i < size; i++)
			array[i] = get(i);
		return array;
	}

	public final <T> T[] toArray(T[] a) {
		Object array[] = toArray();
		T[] result = Arrays.asList(array).toArray(a);
		return result;
	}
	
}
