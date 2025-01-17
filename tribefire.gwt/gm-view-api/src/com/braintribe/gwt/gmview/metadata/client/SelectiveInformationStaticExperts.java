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
package com.braintribe.gwt.gmview.metadata.client;

import java.util.function.Function;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.meta.GmCustomType;
import com.braintribe.model.meta.GmListType;
import com.braintribe.model.meta.GmMapType;
import com.braintribe.model.meta.GmMetaModel;
import com.braintribe.model.meta.GmSetType;
import com.braintribe.model.meta.GmType;
import com.braintribe.model.meta.override.GmCustomTypeOverride;
import com.braintribe.model.packaging.Artifact;
import com.braintribe.model.packaging.Dependency;
import com.braintribe.model.packaging.Packaging;
import com.braintribe.model.processing.core.expert.api.DenotationMap;
import com.braintribe.model.processing.core.expert.api.MutableDenotationMap;
import com.braintribe.model.processing.core.expert.impl.PolymorphicDenotationMap;

/**
 * @author peter.gazdik
 */
public class SelectiveInformationStaticExperts {

	private static MutableDenotationMap<GenericEntity, Function<GenericEntity, String>> experts = new PolymorphicDenotationMap<>(false);

	public static DenotationMap<GenericEntity, Function<GenericEntity, String>> experts() {
		return experts;
	}

	static {
		addExpert(GmMetaModel.T, SelectiveInformationStaticExperts::metaModelSi);

		addExpert(GmType.T, SelectiveInformationStaticExperts::typeSi);
		addExpert(GmCustomType.T, SelectiveInformationStaticExperts::customSi);
		addExpert(GmCustomTypeOverride.T, SelectiveInformationStaticExperts::customOverrideSi);
		addExpert(GmListType.T, SelectiveInformationStaticExperts::listSi);
		addExpert(GmSetType.T, SelectiveInformationStaticExperts::setSi);
		addExpert(GmMapType.T, SelectiveInformationStaticExperts::mapSi);
		
		addExpert(Packaging.T, SelectiveInformationStaticExperts::packagingSi);
		addExpert(Dependency.T, SelectiveInformationStaticExperts::dependencySi);
		addExpert(Artifact.T, SelectiveInformationStaticExperts::artifactSi);
	}

	public static <T extends GenericEntity> void addExpert(EntityType<T> entityType, Function<? super T, String> expert) {
		experts.put(entityType, (Function<GenericEntity, String>) expert);
	}

	private static String metaModelSi(GmMetaModel gmMetaModel) {
		return replaceAll(gmMetaModel.getName(), "^.*\\:(.*)$", "$1", "GmMetaModel");
	}

	private static String typeSi(GmType gmType) {
		return gmType.getTypeSignature();
	}

	private static String customSi(GmCustomType gmType) {
		if (gmType.getTypeSignature() != null)
			return replaceAll(gmType.getTypeSignature(), "^.*\\.(.*)$", "$1", null);
		else
			return gmType.entityType().getShortName();
	}

	private static String customOverrideSi(GmCustomTypeOverride gmTypeOverride) {
		GmCustomType relatedType = gmTypeOverride.addressedType();
		if (relatedType == null)
			return "(" + gmTypeOverride.entityType().getShortName() + ")";
		else
			return customSi(relatedType) + " (override)";
	}

	private static String listSi(GmListType gmType) {
		return replaceAll(gmType.getTypeSignature(), "^list<.*\\.(.*)>$", "list<$1>", "list");
	}

	private static String setSi(GmSetType gmType) {
		return replaceAll(gmType.getTypeSignature(), "^set<.*\\.(.*)>$", "set<$1>", "set");
	}

	private static String mapSi(GmMapType gmType) {
		return replaceAll(gmType.getTypeSignature(), "^map<([^,]+\\.)?([^,]*),([^>]+\\.)?([^>]*)>?$", "map<$2,$4>", "map");
	}
	
	private static String packagingSi(Packaging packaging) {
		return packaging.getTerminalArtifact().getArtifactId();
	}
	
	private static String dependencySi(Dependency dependency) {
		return dependency.getGroupId() + ":" + dependency.getArtifactId() + "-" + dependency.getVersion();
	}
	
	private static String artifactSi(Artifact artifact) {
		return artifact.getArtifactId() + "-" + artifact.getVersion();
	}

	private static String replaceAll(String s, String regex, String replacement, String defaultValue) {
		return s == null ? defaultValue : s.replaceAll(regex, replacement);
	}

}
