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
package com.braintribe.model.access.hibernate.base.mock;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import javax.transaction.Synchronization;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.resource.transaction.spi.TransactionStatus;

/**
 * A delegating {@link SessionFactory} implementation which doesn't propagate a commit on a session to prevent data from actually being written to the
 * underlying DB. Instead, it does a {@link Session#flush()} on the delegate, thus ensuring subsequent queries return a correct result.
 * 
 * @author peter.gazdik
 */
public interface NonCommittingSessionFactory extends HbmTestSessionFactory {

	static NonCommittingSessionFactory newInstance(SessionFactory delegate) {
		Class<?>[] ifaces = { NonCommittingSessionFactory.class };
		InvocationHandler handler = new NonCommittingSessionFactoryHandler(delegate);

		return (NonCommittingSessionFactory) Proxy.newProxyInstance(NonCommittingSessionFactory.class.getClassLoader(), ifaces, handler);
	}

}

interface NonCommittingSession extends Session {

	void reset();

}

class NonCommittingSessionFactoryHandler implements InvocationHandler {

	private final SessionFactory delegate;
	private final NonCommittingSession nonCommittingSession;

	public NonCommittingSessionFactoryHandler(SessionFactory delegate) {
		this.delegate = delegate;
		this.nonCommittingSession = nonCommittingSession(delegate.openSession());
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		switch (method.getName()) {
			case "reset":
				return reset();
			case "openSession":
				return nonCommittingSession;
			default:
				return method.invoke(delegate, args);
		}
	}

	private Object reset() {
		nonCommittingSession.reset();
		return null;
	}

	private static NonCommittingSession nonCommittingSession(Session delegate) {
		Class<?>[] ifaces = { NonCommittingSession.class };
		InvocationHandler handler = new NonCommittingSessionHandler(delegate);

		return (NonCommittingSession) Proxy.newProxyInstance(NonCommittingSessionHandler.class.getClassLoader(), ifaces, handler);
	}

}

class NonCommittingSessionHandler implements InvocationHandler {

	private final Session delegate;
	private Transaction actualTransaction;

	private final Transaction nonCommittingTransaction;

	public NonCommittingSessionHandler(Session delegate) {
		this.delegate = delegate;
		this.nonCommittingTransaction = new NonCommittingJustFlushingTransaction(delegate);
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		switch (method.getName()) {
			case "reset":
				return reset(); // rollback actual transaction
			case "beginTransaction":
				return beginTransaction(); // start actual transaction if needed but return non-committing transaction instead
			case "close":
				return null;
			default:
				return method.invoke(delegate, args);
		}
	}

	private Transaction beginTransaction() {
		if (actualTransaction == null)
			actualTransaction = delegate.beginTransaction();

		return nonCommittingTransaction;
	}

	private Object reset() {
		if (actualTransaction != null) {
			actualTransaction.rollback();
			actualTransaction = null;
		}
		return null;
	}

}

class NonCommittingJustFlushingTransaction implements Transaction {

	private final Session session;

	public NonCommittingJustFlushingTransaction(Session session) {
		this.session = session;
	}

	// @formatter:off
	@Override public void commit() { session.flush(); } // This ensures the subsequent queries return correct results

	@Override public void begin() { /* NOOP*/	}
	@Override public void rollback() { /* NOOP*/ }
	@Override public void setRollbackOnly() { /* NOOP*/ }
	@Override public boolean getRollbackOnly() { return false; }
	@Override public boolean isActive() { return false;	}
	@Override public TransactionStatus getStatus() { return null; }
	@Override public void registerSynchronization(Synchronization synchronization) throws HibernateException { /* NOOP*/ }
	@Override public void setTimeout(int seconds) { /* NOOP*/ }
	@Override public int getTimeout() { return 0; }
	// @formatter:on

}