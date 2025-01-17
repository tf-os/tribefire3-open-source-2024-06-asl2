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
package com.braintribe.common.concurrent;

import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Scheduler for periodic tasks which waits for the tasks to finish when being shut down.
 * <p>
 * The timeout for waiting until the task finishes, and whether or not the task should be interrupted, is configured per individual task.
 * 
 * @author peter.gazdik
 */
public interface TaskScheduler {

	/**
	 * Returns a builder to schedules a task (runnable).
	 * <p>
	 * IMPORTANT: for the task to be scheduled you need to call {@link TaskScheduleBuilder#done()} on the returned builder.
	 * <p>
	 * Semantics is the same as {@link ScheduledExecutorService#scheduleAtFixedRate(Runnable, long, long, TimeUnit)}, and calling this method in the
	 * underlying executor is the natural implementation.
	 */
	TaskScheduleBuilder scheduleAtFixedRate(String name, Runnable command, long initialDelay, long period, TimeUnit unit);

	/**
	 * {@link ScheduledTask#cancel() cancels} all tasks and ensures no other task can be scheduled from now on, meaning
	 * {@link TaskScheduleBuilder#done()} throws {@link RejectedExecutionException} from now on.
	 */
	boolean shutdown();

	/** @return {@code true} if this scheduler has been shut down */
	boolean isShutdown();

	/**
	 * Shuts down the scheduler, cancels all tasks and any further attempts to schedule a new task would result in an {@link IllegalStateException}
	 * throw by {@link TaskScheduleBuilder#done()}.
	 * <p>
	 * This method cannot time out, it waits as long as necessary for the tasks to finish. 
	 */
	void awaitTermination();

	/**
	 * Similar to {@link #awaitTermination()}, but uses the max timeout of all the tasks that are still available to cancel.
	 */
	boolean awaitTerminationUsingTaskTimeout();

	boolean awaitTermination(long timeout, TimeUnit timeUnit);

}
