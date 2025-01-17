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
package com.braintribe.model.processing.resource.server.request;

import java.util.Set;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.resource.source.ResourceSource;
import com.braintribe.model.resource.specification.ResourceSpecification;

/**
 * <p>
 * Type-safe holder of request parameters handled on upload operations.
 * 
 */
public class ResourceUploadRequest extends ResourceStreamingRequest {

	private String responseMimeType;
	private String useCase;
	private String mimeType;
	private String md5;
	private Set<String> tags;

	private EntityType<? extends ResourceSource> sourceType;
	private ResourceSpecification specification;


	public ResourceUploadRequest() {
	}

	public String getResponseMimeType() {
		return responseMimeType;
	}

	public void setResponseMimeType(String responseMimeType) {
		this.responseMimeType = responseMimeType;
	}

	public String getUseCase() {
		return useCase;
	}

	public void setUseCase(String useCase) {
		this.useCase = useCase;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public String getMd5() {
		return md5;
	}

	public void setMd5(String md5) {
		this.md5 = md5;
	}

	public void setSpecification(ResourceSpecification specification) {
		this.specification = specification;
	}

	public ResourceSpecification getSpecification() {
		return specification;
	}

	public EntityType<? extends ResourceSource> getSourceType() {
		return sourceType;
	}

	public void setSourceType(EntityType<? extends ResourceSource> sourceType) {
		this.sourceType = sourceType;
	}

	public Set<String> getTags() {
		return tags;
	}

	public void setTags(Set<String> tags) {
		this.tags = tags;
	}

}
