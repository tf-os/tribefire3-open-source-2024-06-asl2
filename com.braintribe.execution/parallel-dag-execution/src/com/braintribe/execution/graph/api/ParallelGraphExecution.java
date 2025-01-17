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
package com.braintribe.execution.graph.api;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Function;
import java.util.function.Supplier;

import com.braintribe.common.lcd.function.CheckedConsumer;
import com.braintribe.common.lcd.function.CheckedFunction;
import com.braintribe.execution.graph.impl.PgeBuilderImpl;

/**
 * @author peter.gazdik
 */
public interface ParallelGraphExecution {

	static <N> PgeBuilder<N> foreach(String name, Iterable<? extends N> nodes) {
		return new PgeBuilderImpl<N>(name, nodes);
	}

	public interface PgeBuilder<N> extends PgeExecutor<N> {

		// Graph edges

		/**
		 * Configures an "edge function", which for each item i returns all the items which have to be processed first.
		 * <p>
		 * Note that even if the iterable returned by the functions provides the same item multiple times, it is processed only
		 * once by the parallel executor.
		 */
		PgeBuilder<N> itemsToProcessFirst(Function<N, Iterable<? extends N>> dependencyResolver);

		/**
		 * Configures an "edge function", which for each item i returns all the items which cannot be processed first.
		 * <p>
		 * Note that even if the iterable returned by the functions provides the same item multiple times, it is processed only
		 * once by the parallel executor.
		 */
		PgeBuilder<N> itemsToProcessAfter(Function<N, Iterable<? extends N>> dependenderResolver);

		// Thread pool

		PgeBuilder<N> withThreadPool(int nThreads);

		/** Equivalent to {@code withThreadPoolExecutorSupplier(() -> threadPoolExecutor)} */
		default PgeBuilder<N> withThreadPoolExecutor(ExecutorService threadPoolExecutor) {
			return withThreadPoolExecutorSupplier(() -> threadPoolExecutor);
		}

		/**
		 * Configures a {@link ThreadPoolExecutor} supplier for the underlying parallel execution. The method's suffix
		 * "Supplier" suggests given executor was only borrowed (rather than created specifically for this use-case) and is thus
		 * not closed at the end of execution.
		 * <p>
		 * Use {@link #withThreadPoolExecutorFactory(Supplier)} to make sure the returned executor is also closed.
		 */
		PgeBuilder<N> withThreadPoolExecutorSupplier(Supplier<ExecutorService> threadPoolExecutorSupplier);

		/**
		 * Similar to {@link #withThreadPoolExecutorSupplier(Supplier)} but closes the used {@link ThreadPoolExecutor} when the
		 * parallel execution ends.
		 */
		PgeBuilder<N> withThreadPoolExecutorFactory(Supplier<ExecutorService> threadPoolExecutorFactory);

		PgeExecutor<N> prepareRunner();

	}

	// Terminals

	public interface PgeExecutor<N> {

		default PgeResult<N, Boolean> run(CheckedConsumer<? super N, Exception> processor) {
			return compute(item -> {
				processor.accept(item);
				return true;
			});
		}

		default <R> PgeResult<N, R> iJustMetYouAndThisIsCrazyButHeresAFunctionSoCallItMaybe(
				CheckedFunction<? super N, ? extends R, Exception> processor) {
			return compute(processor);
		}

		<R> PgeResult<N, R> compute(CheckedFunction<? super N, ? extends R, Exception> processor);
	}

	public interface PgeResult<N, R> extends Iterable<PgeItemResult<N, R>> {

		/**
		 * Indicates whether an error occurred during parallel computation. If the value is true, look inside individual
		 * {@link PgeItemResult#getError() item results} to find the actual errors.
		 * <p>
		 * Note there can be more that one error if multiple nodes were being executed at the moment the first node failed. But
		 * once a failure occurs, no new nodes are submitted for execution, so this cannot be used to detect all problematic
		 * items.
		 */
		boolean hasError();

		Map<N, PgeItemResult<N, R>> itemResulsts();

		@Override
		default Iterator<PgeItemResult<N, R>> iterator() {
			return itemResulsts().values().iterator();
		}

	}

	public interface PgeItemResult<N, R> {

		N getItem();

		R getResult();

		Throwable getError();

		PgeItemStatus status();
	}

	public enum PgeItemStatus {
		created,
		submitted,
		finished,
		failed,
		cancelled,
	}
}
