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
package tribefire.extension.okta.model;

import java.util.List;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

public interface OktaError extends GenericEntity {

	EntityType<OktaError> T = EntityTypes.T(OktaError.class);

	String error = "error";
	String error_description = "error_description";

	String errorCode = "errorCode";
	String errorSummary = "errorSummary";
	String errorLink = "errorLink";
	String errorId = "errorId";
	String errorCauses = "errorCauses";

	String getErrorCode();
	void setErrorCode(String errorCode);

	String getErrorSummary();
	void setErrorSummary(String errorSummary);

	String getErrorLink();
	void setErrorLink(String errorLink);

	String getErrorId();
	void setErrorId(String errorId);

	List<OktaError> getErrorCauses();
	void setErrorCauses(List<OktaError> errorCauses);

	String getError();
	void setError(String error);

	String getError_description();
	void setError_description(String error_description);

	default String errorMessage() {
		StringBuilder sb = new StringBuilder();
		addErrorText(sb, "Error", getError());
		addErrorText(sb, "Error Description", getError_description());
		addErrorText(sb, "Error Code", getErrorCode());
		addErrorText(sb, "Error Summary", getErrorSummary());
		addErrorText(sb, "Error ID", getErrorId());
		return sb.toString();
	}

	private void addErrorText(StringBuilder sb, String label, String text) {
		if (text != null && text.trim().length() > 0) {
			if (sb.length() > 0) {
				sb.append("\n");
			}
			sb.append(label + ": " + text);
		}
	}
}
