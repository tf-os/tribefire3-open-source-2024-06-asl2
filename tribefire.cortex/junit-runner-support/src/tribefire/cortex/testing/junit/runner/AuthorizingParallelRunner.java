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
package tribefire.cortex.testing.junit.runner;

import java.util.Deque;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.RecursiveAction;

import org.junit.experimental.theories.PotentialAssignment;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.experimental.theories.internal.Assignments;
import org.junit.internal.AssumptionViolatedException;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.junit.runners.model.TestClass;

import com.braintribe.thread.api.ThreadContextScoping;

/**
 * An extension of the JUnit {@link Theories} runner, which executes all <code>@Test</code> methods concurrently. Furthermore all calls to
 * <code>@Theory</code> methods with different parameter assignments are executes concurrently too. Example:
 * 
 * <pre>
 *     &#64;RunWith(ParallelRunner.class)
 *     <b>public</b> <b>class</b> FooTest {
 *         &#64;Test
 *         <b>public</b> <b>void</b> test1() {
 *             <i>// Will be executed in a worker thread</i>
 *         }
 *         &#64;Test
 *         <b>public</b> <b>void</b> test2() {
 *             <i>// Will be executed concurrently in another worker thread</i>
 *         }
 *     }
 * </pre>
 * 
 * You can specify the maximum number of parallel test threads using the system property <code>maxParallelTestThreads</code>. If this system property
 * is not specified, the maximum number of test threads will be the number of {@link Runtime#availableProcessors() available processors.}
 * 
 * @see <a href="https://github.com/MichaelTamm/junit-toolbox/blob/master/src/main/java/com/googlecode/junittoolbox/ParallelRunner.java">JUnit
 *      Toolbox</a>
 */
public class AuthorizingParallelRunner extends Theories implements ThreadContextScopingAware {

	private AuthorizingParallelScheduler scheduler;

	public AuthorizingParallelRunner(Class<?> klass) throws InitializationError {
        super(klass);
		setScheduler(scheduler = new AuthorizingParallelScheduler());
    }
	
	@Override
	public void setThreadContextScoping(ThreadContextScoping tcs) {
		scheduler.tcs = tcs;
	}

    @Override
    public Statement methodBlock(FrameworkMethod method) {
        return new ParallelTheoryAnchor(method, getTestClass());
    }

    public class ParallelTheoryAnchor extends TheoryAnchor {
        private final Deque<ForkJoinTask<?>> _asyncRuns = new LinkedBlockingDeque<>();
        private final FrameworkMethod _testMethod;
        private volatile boolean _wasRunWithAssignmentCalled;

        public ParallelTheoryAnchor(FrameworkMethod method, TestClass testClass) {
            super(method, testClass);
            _testMethod = method;
        }

        @Override
        protected void runWithAssignment(Assignments assignments) throws Throwable {
            if (_wasRunWithAssignmentCalled) {
                super.runWithAssignment(assignments);
            } else {
                _wasRunWithAssignmentCalled = true;
                super.runWithAssignment(assignments);
                // This is the first call to runWithAssignment, therefore we need to
                // make sure, that all asynchronous runs have finished, before we return ...
                // Note: Because we have added all asynchronous runs via addFirst to _asyncRuns
                // and retrieve them via removeFirst here, task.join() is able to steal tasks,
                // which have not been started yet, from other worker threads ...
                Throwable failure = null;
                while (failure == null && !_asyncRuns.isEmpty()) {
                    ForkJoinTask<?> task = _asyncRuns.removeFirst();
                    try { task.join(); } catch (Throwable t) { failure = t; }
                }
                if (failure != null) {
                    // Cancel all remaining tasks ...
                    while (!_asyncRuns.isEmpty()) {
                        ForkJoinTask<?> task = _asyncRuns.removeFirst();
                        try { task.cancel(true); } catch (Throwable ignored) {/* empty */}
                    }
                    // ... and join them, to prevent interference with other tests ...
                    while (!_asyncRuns.isEmpty()) {
                        ForkJoinTask<?> task = _asyncRuns.removeFirst();
                        try { task.join(); } catch (Throwable ignored) {/* empty */}
                    }
                    throw failure;
                }
            }
        }

        @Override
        protected void runWithIncompleteAssignment(Assignments incomplete) throws Throwable {
            for (PotentialAssignment source : incomplete.potentialsForNextUnassigned()) {
                Assignments nextAssignment = incomplete.assignNext(source);
                ForkJoinTask<?> asyncRun = new RecursiveAction() {
					private static final long serialVersionUID = 80085L;

					@Override
                    protected void compute() {
                        try {
                            ParallelTheoryAnchor.this.runWithAssignment(nextAssignment);
                        } catch (Throwable t) {
                            throw new RuntimeException(t);
                        }
                    }
                };
                _asyncRuns.addFirst(asyncRun.fork());
            }
        }

        /**
         * Overridden to make the method synchronized.
         */
        @Override
        protected synchronized void handleAssumptionViolation(AssumptionViolatedException e) {
            if (_testMethod.getAnnotation(Theory.class) == null) {
            // Behave like BlockJUnit4ClassRunner
                throw e;
            }
            // Behave like Theories runner
            super.handleAssumptionViolation(e);
        }

        /**
         * Overridden to make the method synchronized.
         */
        @Override
        protected synchronized void handleDataPointSuccess() {
            super.handleDataPointSuccess();
        }
    }

}