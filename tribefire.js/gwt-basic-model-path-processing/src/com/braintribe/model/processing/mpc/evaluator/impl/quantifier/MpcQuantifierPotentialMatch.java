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
package com.braintribe.model.processing.mpc.evaluator.impl.quantifier;

import java.util.ArrayList;
import java.util.List;

import com.braintribe.logging.Logger;
import com.braintribe.model.generic.path.api.IModelPathElement;
import com.braintribe.model.mpc.quantifier.MpcQuantifierStrategy;
import com.braintribe.model.processing.mpc.evaluator.api.MpcMatch;
import com.braintribe.model.processing.mpc.evaluator.api.multievaluation.MpcEvaluationResumptionStrategy;
import com.braintribe.model.processing.mpc.evaluator.api.multievaluation.MpcPotentialMatch;
import com.braintribe.model.processing.mpc.evaluator.impl.MpcMatchImpl;

public class MpcQuantifierPotentialMatch extends MpcMatchImpl implements MpcPotentialMatch {

	private static Logger logger = Logger.getLogger(MpcQuantifierPotentialMatch.class);
	private static boolean trace = logger.isTraceEnabled();
	
	public List<MpcMatch> matchesList;
	public MpcQuantifierStrategy quantifierStrategy;
	public int matchesCounter;
	public IModelPathElement currentPath;
	
	
	private MpcEvaluationResumptionStrategy resumptionStrategy ;
	
	public MpcQuantifierPotentialMatch(IModelPathElement element) {
		super(element); 
		matchesList = new ArrayList<MpcMatch>();
		quantifierStrategy = MpcQuantifierStrategy.greedy;
		matchesCounter = 0;
		currentPath = null;
	}

	public MpcQuantifierPotentialMatch(IModelPathElement element, List<MpcMatch> matchesList, int matchesCounter, IModelPathElement currentPath, MpcQuantifierStrategy quantifierStrategy) {
		super(element); 
		this.matchesList = matchesList;
		this.matchesCounter = matchesCounter;
		this.currentPath = currentPath;
		this.quantifierStrategy = quantifierStrategy;
	}
	
	@SuppressWarnings("incomplete-switch")
	@Override
	public boolean hasAnotherAttempt() {
		boolean backTrackingPossible = false;
		switch (quantifierStrategy) {
			case greedy:
				if (trace) logger.trace("if there are any previous matches, then we can backtrack and use them :" + matchesList + " size "+ (matchesList != null? matchesList.size() : "NO"));
				backTrackingPossible = (matchesList != null && matchesList.size() > 0) ? true : false;
				break;
			case reluctant:
				if (trace) logger.trace("if there is unconsumed path, it can be used for further processing :" + currentPath );
				backTrackingPossible = currentPath != null ? true : false;
				break;
		}
		return backTrackingPossible;
	}

	@Override
	public void setResumptionStrategy(MpcEvaluationResumptionStrategy resumptionStrategy){
		this.resumptionStrategy = resumptionStrategy;
	}
	
	@Override
	public MpcEvaluationResumptionStrategy getResumptionStrategy() {
		return resumptionStrategy;
	}


}
