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
package com.braintribe.utils.junit.core.rules;

import java.util.concurrent.CountDownLatch;

import org.junit.Before;
import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

/**
 * Similar to {@link LoopRule}, but the test is run concurrently. Source mostly copied from
 * <a href="http://blog.mycila.com/2009/11/writing-your-own-junit-extensions-using.html">this</a>.
 *
 * If the evaluation of the underlying {@link Statement} ends with an exception for any of the threads, this exception is then re-thrown after all
 * threads are finished. If more threads end with an exception, the rule tries to re-throw the first exception that occurred (since that may the most
 * helpful one for finding the bug), but there is no guarantee of that. All unexpected exceptions should be printed to standard error output, so this
 * is not such a big deal anyway.
 *
 * Note that one may use the {@link org.junit.Test#expected()} parameter only iff all threads are expected to throw a given exception. This exception
 * would be processed inside the {@linkplain Statement} evaluation (i.e. inside the method {@link Statement#evaluate()} and would therefore not be
 * re-thrown. However, if some of the threads did not throw given exception, {@linkplain Statement} evaluation would end with {@link AssertionError}
 * which would then be re-thrown, causing the test to fail.
 *
 * Note regarding {@link Before} annotation: This is executed by each thread, so beware of that. UPDATE: Recently I read somewhere, that the behavior
 * of this annotation is different for higher versions of junit (4.7+), so if someone wants to update to a newer version of junit, it would also be
 * good to create a new version of JUnitTools.
 */
public class ConcurrentRule implements MethodRule {
	private final int threadsCount;

	public ConcurrentRule(final int threadsCount) {
		if (threadsCount < 1) {
			throw new IllegalArgumentException("Number of threads must be a positive number!");
		}

		this.threadsCount = threadsCount;
	}

	@Override
	public Statement apply(final Statement base, final FrameworkMethod method, final Object target) {
		return new Statement() {

			private volatile Throwable throwable = null;

			@Override
			public void evaluate() throws Throwable {
				final int nThreads = getThreadsCount();

				final String name = method.getName();
				final Thread[] threads = new Thread[nThreads];
				final CountDownLatch go = new CountDownLatch(1);

				for (int i = 0; i < nThreads; i++) {
					threads[i] = new Thread(new Runnable() {
						@Override
						public void run() {
							try {
								go.await();
								base.evaluate();

							} catch (final InterruptedException e) {
								Thread.currentThread().interrupt();

							} catch (final Throwable t) {
								if (throwable == null) {
									notifyException(t);
								}

								rethrow(t);
							}
						}

						private void rethrow(final Throwable t) {
							if (t instanceof RuntimeException) {
								throw (RuntimeException) t;
							}

							if (t instanceof Error) {
								throw (Error) t;
							}

							final RuntimeException r = new RuntimeException(t.getMessage(), t);
							r.setStackTrace(t.getStackTrace());
							throw r;
						}

					}, name + "_Thread_" + i);
					threads[i].start();
				}

				go.countDown();

				for (final Thread t : threads) {
					t.join();
				}

				if (this.throwable != null) {
					throw this.throwable;
				}
			}

			private synchronized void notifyException(final Throwable t) {
				if (this.throwable == null) {
					this.throwable = t;
				}
			}

			private int getThreadsCount() {
				final Concurrent concurrent = method.getAnnotation(Concurrent.class);

				final int nThreads = concurrent == null ? ConcurrentRule.this.threadsCount : concurrent.value();

				if (nThreads < 1) {
					throw new IllegalArgumentException("Number of threads must be a positive number!");
				}

				return nThreads;
			}

		};

	}

}
