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
package tribefire.extension.metrics.connector.api;

import com.braintribe.cfg.Configurable;
import com.braintribe.cfg.LifecycleAware;
import com.braintribe.cfg.Required;
import com.braintribe.logging.Logger;
import com.braintribe.model.check.service.CheckResultEntry;
import com.braintribe.model.deployment.Deployable;

import io.micrometer.core.instrument.MeterRegistry;

public abstract class AbstractMetricsConnector implements MetricsConnector, LifecycleAware {

	private final static Logger logger = Logger.getLogger(AbstractMetricsConnector.class);

	private MeterRegistry registry;

	// -----------------------------------------------------------------------
	// LifecycleAware
	// -----------------------------------------------------------------------

	// TODO: need to close registry - the problem is that the registry comes from wire, since it is managed it then
	// cannot recover....

	@Override
	public void postConstruct() {
		initialize();

		// TODO: config of compoundRegistry
		// TODO: dynamically add/remove meters??? Is this really necesary. I mean, when having it running you have the
		// intention to use the metrics - and not to check if can disable for example a bunch of meters - maybe it make
		// sense, let's see
		// TODO: use all/most of the about page metrics
		// TODO: define strategy with tags

		// ------------------
		// TODO: remove examples
		//@formatter:off
//		Counter counter = compoundRegistry.counter("", "");
//		Timer timer = compoundRegistry.timer("");
//		Integer gauge = compoundRegistry.gauge("", 1);
//		DistributionSummary summary = compoundRegistry.summary("");
		//@formatter:on
		// ------------------

	}

	@Override
	public void preDestroy() {
		registry.clear();
		registry.close();

		logger.info(() -> "Cleared registry of connector: '" + name() + "'");
	}

	// -----------------------------------------------------------------------
	// HEALTH
	// -----------------------------------------------------------------------

	@Override
	public CheckResultEntry health() {
		CheckResultEntry entry = actualHealth();
		return entry;
	}

	protected abstract CheckResultEntry actualHealth();

	// -----------------------------------------------------------------------
	// METHODS
	// -----------------------------------------------------------------------

	@Override
	public MeterRegistry registry() {
		return registry;
	}

	// -----------------------------------------------------------------------
	// HELPER METHODS FOR ADAPTING CONFIGURATION AT RUNTIME
	// -----------------------------------------------------------------------

	// -----------------------------------------------------------------------
	// HELPERS
	// -----------------------------------------------------------------------

	protected String defaultName(Deployable deployable) {
		return deployable.getName() + "[" + deployable.getExternalId() + "]";
	}

	// -----------------------------------------------------------------------
	// GETTER & SETTER
	// -----------------------------------------------------------------------

	@Required
	@Configurable
	public void setRegistry(MeterRegistry registry) {
		this.registry = registry;
	}
}