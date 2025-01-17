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
package com.braintribe.model.processing.session.impl.managed.history;

import static com.braintribe.model.generic.manipulation.util.ManipulationBuilder.compound;
import static com.braintribe.model.processing.manipulation.basic.tools.ManipulationTools.createInverse;

import java.util.ArrayList;

import com.braintribe.model.generic.manipulation.CompoundManipulation;
import com.braintribe.model.generic.manipulation.Manipulation;
import com.braintribe.model.processing.session.api.managed.ManagedGmSession;
import com.braintribe.model.processing.session.api.transaction.NestedTransaction;
import com.braintribe.model.processing.session.api.transaction.TransactionException;

public class BasicNestedTransaction extends AbstractTransactionFrame implements NestedTransaction {
	private BasicTransaction transaction;
	private AbstractTransactionFrame parentFrame;
	
	public BasicNestedTransaction(ManagedGmSession session, BasicTransaction transaction, AbstractTransactionFrame parentFrame) {
		super(session);
		this.transaction = transaction;
		this.parentFrame = parentFrame;
	}

	@Override
	public AbstractTransactionFrame getParentFrame() {
		return parentFrame;
	}
	
	@Override
	protected void pushHibernation() {
		transaction.pushHibernation();
	}
	
	@Override
	protected void popHibernation() {
		transaction.popHibernation();
	}
	
	@Override
	public NestedTransaction beginNestedTransaction() {
		return transaction.beginNestedTransaction();
	}
	
	@Override
	protected void onManipulationUndone(Manipulation manipulation) {
		transaction.onManipulationUndone(manipulation);
	}
	
	@Override
	protected void onManipulationDone(Manipulation manipulation) {
		transaction.onManipulationDone(manipulation);
	}
	
	@Override
	public void commit() {
		// first commit child frame recursively
		if (childFrame != null)
			childFrame.commit();
		
		// commit this frame then by placing a resulting manipulation if there is one
		switch (doneManipulations.size()) {
		case 0:
			break;
		case 1:
			parentFrame.appendManipulation(doneManipulations.getFirst());
			break;
		default:
			CompoundManipulation compoundManipulation = compound(new ArrayList<Manipulation>(doneManipulations));
			compoundManipulation.linkInverse(createInverse(compoundManipulation));

			parentFrame.appendManipulation(compoundManipulation);
		}
		
		transaction.endNestedTransaction();
		
		clear();
	}
	
	@Override
	public void rollback() throws TransactionException {
		// first undo childFrames recursively
		if (childFrame != null) {
			childFrame.rollback();
		}
		
		// really rollback the manipulations
		undo(doneManipulations.size());
		
		transaction.endNestedTransaction();
		
		clear();
	}
}
