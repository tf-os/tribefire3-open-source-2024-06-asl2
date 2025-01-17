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
package com.braintribe.devrock.greyface.process.scan;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.eclipse.core.runtime.IProgressMonitor;

import com.braintribe.build.artifact.name.NameParser;
import com.braintribe.cfg.Configurable;
import com.braintribe.cfg.Required;
import com.braintribe.devrock.greyface.process.notification.ScanContext;
import com.braintribe.devrock.greyface.process.notification.ScanProcessListener;
import com.braintribe.devrock.greyface.process.retrieval.CompoundDependencyResolver;
import com.braintribe.devrock.greyface.scope.GreyfaceScope;
import com.braintribe.model.artifact.Dependency;

public class AsynchronousScannerImpl extends AbstractScannerImpl {
	
	private class Taker extends Thread {
		
		private BlockingQueue<ScanTuple> queue;
		private ThreadPoolExecutor taskExecutor = new ThreadPoolExecutor(5, 100, 10, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(10000));
		private ScanContext context;
		private IProgressMonitor monitor;
		private CompoundDependencyResolver compoundDependencyResolver;
		
		@Required
		public void setQueue(BlockingQueue<ScanTuple> queue) {
			this.queue = queue;
		}
		@Required
		public void setContext(ScanContext context) {
			this.context = context;
		}
		@Required
		public void setMonitor(IProgressMonitor monitor) {
			this.monitor = monitor;
		}
		
		@Configurable @Required
		public void setCompoundDependencyResolver(CompoundDependencyResolver compoundDependencyResolver) {
			this.compoundDependencyResolver = compoundDependencyResolver;
		}

		@Override
		public void run() {
			boolean justAddedOne = false;
			Set<String> scannedDependencies = new HashSet<String>();
			do {
					ScanTuple scanTuple = queue.poll();					
					if (scanTuple != null) {
						String dependency = NameParser.buildName( scanTuple.dependency);
						if (scannedDependencies.contains(dependency)) {
							System.out.println("duplicate dependency run avoided on [" + dependency + "]");
							continue;
						}
						scannedDependencies.add(dependency);
						justAddedOne = true;
						AsynchronousScanExpert scanExpert = new AsynchronousScanExpert();
						scanExpert.setScanTuple(scanTuple);
						scanExpert.setQueue(queue);
						//scanExpert.setCompoundDependencyResolver(compoundDependencyResolver);
						scanExpert.setSources(context.getSourceRepositories());
						scanExpert.setContext(context);								
						scanExpert.setProgressMonitor(monitor);
						scanExpert.setDependencyCache(dependencyCache);
						
						GreyfaceScope.getScope().resetScope(compoundDependencyResolver);
						
						for (ScanProcessListener listener : listeners) {						
							scanExpert.addScanProcessListener(listener);
						}
											
						taskExecutor.execute(scanExpert);						
						
					}
					else {
						try {
							sleep(1000);
							justAddedOne = false;
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				
			} while (justAddedOne || taskExecutor.getActiveCount() != 0 || !queue.isEmpty());
			
			taskExecutor.shutdown();
			try {
				taskExecutor.awaitTermination(2000, TimeUnit.MILLISECONDS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}

	
	@Override
	protected void runScan(IProgressMonitor progressMonitor, ScanContext context) {		
		List<Dependency> dependencies = extractDependenciesFromNameList(context.getCondensedNames());
		
		// add dependencies to queue 
		BlockingQueue<ScanTuple> queue = new LinkedBlockingQueue<ScanTuple>();
		for (Dependency dependency : dependencies) {
			ScanTuple tuple = new ScanTuple(dependency, 0, 0, null);
			queue.offer( tuple);
		}		
		for (ScanProcessListener listener : listeners) {
			listener.acknowledgeStartScan();		
		}		
		
		CompoundDependencyResolver resolver = new CompoundDependencyResolver();
		resolver.setSources( context.getSourceRepositories());
		// start workers
		Taker taker = new Taker();
		taker.setQueue(queue);
		taker.setCompoundDependencyResolver(resolver);
		taker.setContext(context);
		taker.setMonitor(progressMonitor);
		taker.run();
		try {
			taker.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (ScanProcessListener listener : listeners) {
			listener.acknowledgeStopScan();		
		}				

	}

}
