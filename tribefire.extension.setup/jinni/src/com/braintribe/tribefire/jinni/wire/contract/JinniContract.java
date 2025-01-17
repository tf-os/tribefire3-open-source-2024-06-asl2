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
package com.braintribe.tribefire.jinni.wire.contract;

import java.util.Map;
import java.util.function.Function;

import com.braintribe.codec.marshaller.api.MarshallerRegistry;
import com.braintribe.codec.marshaller.yaml.YamlMarshaller;
import com.braintribe.model.generic.eval.Evaluator;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.jinni.api.JinniOptions;
import com.braintribe.model.processing.session.api.managed.ModelAccessory;
import com.braintribe.model.service.api.ServiceRequest;
import com.braintribe.tribefire.jinni.support.request.alias.JinniAlias;
import com.braintribe.tribefire.jinni.support.request.history.JinniHistory;
import com.braintribe.wire.api.space.WireSpace;

public interface JinniContract extends WireSpace {

	Evaluator<ServiceRequest> evaluator();

	Map<String, EntityType<?>> shortcuts();

	Map<EntityType<?>, Function<JinniOptions, ? extends ServiceRequest>> defaultRequests();

	JinniHistory history();

	JinniAlias alias();

	ModelAccessory modelAccessory();

	MarshallerRegistry marshallerRegistry();

	YamlMarshaller yamlMarshaller();

}
