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
package com.braintribe.model.processing.smart.query.planner2._base;

import static com.braintribe.model.processing.query.smart.test2._common.SmartModelTestSetupBuilder.newAccess;
import static com.braintribe.model.processing.query.smart.test2._common.SmartTestSetupConstants.accessIdA;
import static com.braintribe.model.processing.query.smart.test2._common.SmartTestSetupConstants.accessIdB;
import static com.braintribe.model.processing.query.smart.test2._common.SmartTestSetupConstants.accessIdS;
import static com.braintribe.utils.lcd.CollectionTools2.asMap;
import static com.braintribe.utils.lcd.CollectionTools2.newMap;

import java.util.Map;

import org.junit.Before;

import com.braintribe.model.accessdeployment.IncrementalAccess;
import com.braintribe.model.accessdeployment.smart.SmartAccess;
import com.braintribe.model.processing.meta.cmd.CmdResolver;
import com.braintribe.model.processing.meta.cmd.CmdResolverImpl;
import com.braintribe.model.processing.meta.oracle.BasicModelOracle;
import com.braintribe.model.processing.query.fluent.SelectQueryBuilder;
import com.braintribe.model.processing.query.smart.test.check.SmartEntityAssemblyChecker;
import com.braintribe.model.processing.query.smart.test.debug.SmartTupleSetViewer;
import com.braintribe.model.processing.query.smart.test.model.deployment.MoodAccess;
import com.braintribe.model.processing.query.smart.test2._base.AbstractSmartTest;
import com.braintribe.model.processing.query.smart.test2._common.SmartModelTestSetup;
import com.braintribe.model.processing.smart.query.planner.SmartQueryPlanner;
import com.braintribe.model.processing.smart.query.planner.base.ConversionExperts;
import com.braintribe.model.processing.smart.query.planner.base.FunctionExperts;
import com.braintribe.model.processing.smart.query.planner.base.TestAccess;
import com.braintribe.model.processing.smart.query.planner.structure.ModelExpert;
import com.braintribe.model.processing.smart.query.planner.structure.StaticModelExpert;
import com.braintribe.model.query.SelectQuery;
import com.braintribe.model.smartqueryplan.SmartQueryPlan;

public abstract class AbstractSmartQueryPlannerTests extends AbstractSmartTest {

	private final SmartQueryPlanner queryPlanner = newQueryPlanner();
	private ModelExpert modelExpert;
	private static Map<SmartModelTestSetup, ModelExpert> modelExpertForSetup = newMap();

	protected int i;

	@Before
	public void preparePlanner() {
		SmartModelTestSetup setup = getSmartModelTestSetup();

		modelExpert = modelExpertForSetup.computeIfAbsent(setup, this::newModelExpert);
	}

	private SmartQueryPlanner newQueryPlanner() {
		SmartQueryPlanner queryPlanner = new SmartQueryPlanner();
		queryPlanner.setFunctionExperts(FunctionExperts.DEFAULT_FUNCTION_EXPERTS);
		queryPlanner.setConversionExperts(ConversionExperts.DEFAULT_CONVERSION_EXPERTS);

		return queryPlanner;
	}

	private ModelExpert newModelExpert(SmartModelTestSetup setup) {
		BasicModelOracle modelOracleS = new BasicModelOracle(setup.modelS);

		CmdResolver cmdResolver = new CmdResolverImpl(modelOracleS);
		StaticModelExpert sme = new StaticModelExpert(modelOracleS);
		SmartAccess accessSDenotation = newAccess(SmartAccess.T, accessIdS, setup.modelS);

		return new ModelExpert(cmdResolver, sme, accessSDenotation, accessMapping(setup));
	}

	private static Map<IncrementalAccess, com.braintribe.model.access.IncrementalAccess> accessMapping(SmartModelTestSetup setup) {
		MoodAccess accessADenotation = newAccess(MoodAccess.T, accessIdA, setup.modelA);
		MoodAccess accessBDenotation = newAccess(MoodAccess.T, accessIdB, setup.modelB);

		TestAccess accessA = new TestAccess(accessIdA);
		TestAccess accessB = new TestAccess(accessIdB);

		return asMap(accessADenotation, accessA, accessBDenotation, accessB);
	}

	protected abstract SmartModelTestSetup getSmartModelTestSetup();

	protected SmartQueryPlan queryPlan;

	protected SelectQueryBuilder query() {
		return new SelectQueryBuilder();
	}

	protected void runTest(SelectQuery selectQuery) {
		queryPlan = queryPlanner.buildQueryPlan(selectQuery, modelExpert);

		SmartTupleSetViewer.view(getTestName(), selectQuery, queryPlan);
	}

	protected SmartEntityAssemblyChecker assertQueryPlan(int totalComponentCount) {
		return new SmartEntityAssemblyChecker(queryPlan) //
				.whereProperty("totalComponentCount").is_(totalComponentCount) //
				.whereProperty("tupleSet");
	}

	protected SmartEntityAssemblyChecker assertQueryPlan() {
		return new SmartEntityAssemblyChecker(queryPlan.getTupleSet());
	}

}
