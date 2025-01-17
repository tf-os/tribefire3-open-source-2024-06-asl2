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
package com.braintribe.execution;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import com.braintribe.execution.priority.PrioritizedThreadPoolExecutor;

public class ThreadPoolBuilder {

	private Integer corePoolSize = null;
	private Integer maximumPoolSize = null;
	private long keepAliveTime = 60L;
	private TimeUnit timeUnit = TimeUnit.MILLISECONDS;
	private BlockingQueue<Runnable> workQueue = null;
	private ThreadFactory threadFactory = null;
	private RejectedExecutionHandler rejectionHandler = null;
	private Boolean allowCoreThreadTimeOut = null;
	private Boolean waitForTasksToCompleteOnShutdown = null;
	private String threadNamePrefix = null;
	private Boolean addThreadContextToNdc = null;
	private String description = null;

	private ThreadPoolBuilder() {
		// 
	}
	
	public static ThreadPoolBuilder newPool() {
		return new ThreadPoolBuilder();
	}

	public ThreadPoolBuilder poolSize(int corePoolSize, int maximumPoolSize) {
		this.corePoolSize = corePoolSize;
		this.maximumPoolSize = maximumPoolSize;
		return this;
	}
	
	public ThreadPoolBuilder keepAliveTime(long keepAliveTime, TimeUnit timeUnit) {
		this.keepAliveTime = keepAliveTime;
		this.timeUnit = timeUnit;
		if (allowCoreThreadTimeOut == null) {
			this.allowCoreThreadTimeOut = Boolean.TRUE;
		}
		return this;
	}
	
	public ThreadPoolBuilder allowCoreThreadTimeOut(boolean allowCoreThreadTimeOut) {
		this.allowCoreThreadTimeOut = allowCoreThreadTimeOut;
		return this;
	}
	
	public ThreadPoolBuilder workQueue(BlockingQueue<Runnable> workQueue) {
		this.workQueue = workQueue;
		return this;
	}
	
	public ThreadPoolBuilder threadFactory(ThreadFactory threadFactory) {
		this.threadFactory = threadFactory;
		return this;
	}
	
	public ThreadPoolBuilder rejectionHandler(RejectedExecutionHandler rejectionHandler) {
		this.rejectionHandler = rejectionHandler;
		return this;
	}
	
	public ThreadPoolBuilder waitForTasksToCompleteOnShutdown(boolean waitForTasksToCompleteOnShutdown) {
		this.waitForTasksToCompleteOnShutdown = waitForTasksToCompleteOnShutdown;
		return this;
	}
	
	public ThreadPoolBuilder threadNamePrefix(String threadNamePrefix) {
		this.threadNamePrefix  = threadNamePrefix;
		return this;
	}

	public ThreadPoolBuilder setAddThreadContextToNdc(boolean addThreadContextToNdc) {
		this.addThreadContextToNdc  = addThreadContextToNdc;
		return this;
	}
	
	public ThreadPoolBuilder description(String desc) {
		this.description = desc;
		return this;
	}
	
	public ExtendedThreadPoolExecutor build() {
		if (corePoolSize == null || maximumPoolSize == null) {
			throw new IllegalArgumentException("Core pool size "+corePoolSize+" and/or maximum pool size "+maximumPoolSize+" is not set");
		}
		if (workQueue == null) {
			throw new IllegalArgumentException("The work queue has not been specified.");
		}
		
		ExtendedThreadPoolExecutor result = new ExtendedThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, timeUnit, workQueue);
		
		applyConfiguration(result);
		
		return result;
	}
	
	public PrioritizedThreadPoolExecutor buildWithPriority() {
		if (corePoolSize == null || maximumPoolSize == null) {
			throw new IllegalArgumentException("Core pool size "+corePoolSize+" and/or maximum pool size "+maximumPoolSize+" is not set");
		}
		if (workQueue != null) {
			throw new IllegalArgumentException("The work queue must not be specified when building a prioritized thread pool executor.");
		}
		
		PrioritizedThreadPoolExecutor result = new PrioritizedThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, timeUnit);
		
		applyConfiguration(result);
		
		return result;
	}
	
	private void applyConfiguration(ExtendedThreadPoolExecutor result) {
		if (allowCoreThreadTimeOut != null) {
			result.allowCoreThreadTimeOut(allowCoreThreadTimeOut);
		}
		if (threadFactory != null) {
			result.setThreadFactory(threadFactory);
		}
		if (rejectionHandler != null) {
			result.setRejectedExecutionHandler(rejectionHandler);
		}
		if (waitForTasksToCompleteOnShutdown != null) {
			result.setWaitForTasksToCompleteOnShutdown(waitForTasksToCompleteOnShutdown);
		}
		if (threadNamePrefix != null) {
			result.setThreadNamePrefix(threadNamePrefix);
		}
		if (addThreadContextToNdc != null) {
			result.setAddThreadContextToNdc(addThreadContextToNdc);
		}
		result.setDescription(description);
		
		result.postConstruct();
	}
}
