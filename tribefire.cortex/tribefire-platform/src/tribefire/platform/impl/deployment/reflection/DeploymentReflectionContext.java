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
package tribefire.platform.impl.deployment.reflection;

import static com.braintribe.model.generic.typecondition.TypeConditions.isAssignableTo;
import static com.braintribe.model.generic.typecondition.TypeConditions.isKind;
import static com.braintribe.model.generic.typecondition.TypeConditions.not;

import java.util.ArrayList;
import java.util.List;

import com.braintribe.model.deployment.Deployable;
import com.braintribe.model.deployment.HardwiredDeployable;
import com.braintribe.model.deployment.Module;
import com.braintribe.model.deploymentreflection.request.GetDeploymentSummary;
import com.braintribe.model.deploymentreflection.request.GetDeploymentSummaryPlain;
import com.braintribe.model.deploymentreflection.request.UnitFilter;
import com.braintribe.model.deploymentreflection.request.WireKind;
import com.braintribe.model.generic.pr.criteria.TraversingCriterion;
import com.braintribe.model.generic.pr.criteria.matching.StandardMatcher;
import com.braintribe.model.generic.processing.pr.fluent.TC;
import com.braintribe.model.generic.reflection.StandardCloningContext;
import com.braintribe.model.generic.reflection.StrategyOnCriterionMatch;
import com.braintribe.model.generic.typecondition.TypeCondition;
import com.braintribe.model.generic.typecondition.TypeConditions;
import com.braintribe.model.generic.typecondition.basic.IsAssignableTo;
import com.braintribe.model.generic.typecondition.basic.TypeKind;
import com.braintribe.model.generic.typecondition.logic.TypeConditionConjunction;
import com.braintribe.model.processing.tfconstants.TribefireConstants;
import com.braintribe.model.service.api.InstanceId;
import com.braintribe.utils.lcd.CommonTools;

/**
 * Utility class used by the {@link DeploymentReflectionProcessor}.
 * 
 * @author christina.wilpernig
 */
public class DeploymentReflectionContext {

	public static GetDeploymentSummary createGetDeploymentSummaryRequest(GetDeploymentSummaryPlain request) {
		GetDeploymentSummary summary = GetDeploymentSummary.T.create();
	
		UnitFilter unitFilter = buildUnitFilter(request);
		summary.setUnitFilter(unitFilter);

		// multicast
		InstanceId instanceId = createInstanceId(request.getNodeId());
		summary.setMulticastFilter(instanceId);

		return summary;
	}

	private static InstanceId createInstanceId(String nodeId) {
		InstanceId instanceId = null;
		if (!CommonTools.isEmpty(nodeId)) {
			instanceId = InstanceId.T.create();
			instanceId.setNodeId(nodeId);
			// this used to be cartridgeId
			instanceId.setApplicationId(TribefireConstants.TRIBEFIRE_SERVICES_APPLICATION_ID);
		}
		return instanceId;
	}

	private static UnitFilter buildUnitFilter(GetDeploymentSummaryPlain request) {
		UnitFilter unitFilter = null;

		TypeCondition deployableFilter = null;

		String typeSignature = request.getTypeSignature();
		if (!CommonTools.isEmpty(typeSignature)) {
			TypeCondition typeCondition = null;
			if (request.getIsAssignableTo()) {
				typeCondition = TypeConditions.isAssignableTo(typeSignature);
			} else {
				typeCondition = TypeConditions.isType(typeSignature);
			}
			deployableFilter = appendCondition(deployableFilter, typeCondition);
		}

		String externalIdPattern = request.getExternalIdPattern();
		if(!CommonTools.isEmpty(externalIdPattern)) {
			unitFilter = UnitFilter.T.create();
			unitFilter.setExternalIdPattern(externalIdPattern);
		}
		
		String wireKind = request.getWireKind();
		if (!CommonTools.isEmpty(wireKind)) {
			WireKind wk = null;
			try {
				wk = WireKind.valueOf(wireKind);
			} catch (IllegalArgumentException e) {
				throw new IllegalArgumentException("Invalid passed parameter wireKind: '" + wireKind + "'", e);
			}
			IsAssignableTo iat = TypeConditions.isAssignableTo(HardwiredDeployable.T.getTypeSignature());
			switch (wk) {
				case hardwired:
					deployableFilter = appendCondition(deployableFilter, iat);
					break;
				case dynamic:
					deployableFilter = appendCondition(deployableFilter, TypeConditions.not(iat));
					break;
				default:
					break;
			}
		}

		if (deployableFilter != null) {
			if (unitFilter == null) {
				unitFilter = UnitFilter.T.create();
			}
			
			unitFilter.setDeployableFilter(deployableFilter);
		}

		return unitFilter;
	}

	private static TypeCondition appendCondition(TypeCondition qualifiedCondition, TypeCondition condition) {
		if (qualifiedCondition == null) {
			return condition;
		} else {
			TypeConditionConjunction conjunction = TypeConditionConjunction.T.create();

			if (qualifiedCondition instanceof TypeConditionConjunction) {
				conjunction = (TypeConditionConjunction) qualifiedCondition;
				conjunction.getOperands().add(condition);
			} else {
				List<TypeCondition> operands = new ArrayList<TypeCondition>();
				operands.add(qualifiedCondition);
				operands.add(condition);

				conjunction.setOperands(operands);
			}
			return conjunction;
		}
	}
	
	public static Deployable cloneDeployable(Deployable deployable) {
		// For streamlining, complex properties are absent except module information
		// @formatter:off
		TraversingCriterion tc = TC.create()
				.conjunction()
					.typeCondition(not(isKind(TypeKind.simpleType)))
					.typeCondition(not(isAssignableTo(Module.T)))
				.close()
			.done();
		// @formatter:on

		StandardMatcher matcher = new StandardMatcher();
		matcher.setCriterion(tc);

		StandardCloningContext cloningContext = new StandardCloningContext();
		cloningContext.setMatcher(matcher);
		cloningContext.setStrategyOnCriterionMatch(StrategyOnCriterionMatch.partialize);
		Deployable clone = (Deployable) deployable.clone(cloningContext);

		return clone;
	}
	
}
