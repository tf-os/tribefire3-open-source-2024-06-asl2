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
package com.braintribe.model.processing.query.eval.tools;

import java.util.Iterator;

import com.braintribe.model.processing.query.eval.api.Tuple;
import com.braintribe.model.processing.query.eval.tuple.OneDimensionalTuple;

/**
 * 
 */
public class PopulationAsTupleIterator implements Iterator<Tuple> {

	protected final Iterator<?> populationIterator;
	protected final OneDimensionalTuple singletonTuple;
	protected final int index;

	public PopulationAsTupleIterator(Iterable<?> population, int index) {
		this.populationIterator = population.iterator();
		this.singletonTuple = new OneDimensionalTuple(index);
		this.index = index;
	}

	@Override
	public boolean hasNext() {
		return populationIterator.hasNext();
	}

	@Override
	public Tuple next() {
		singletonTuple.setValueDirectly(index, populationIterator.next());

		return singletonTuple;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException("Cannot remove a tuple from a tuple set!");
	}

}
