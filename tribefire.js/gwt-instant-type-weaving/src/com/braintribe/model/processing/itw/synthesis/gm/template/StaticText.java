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
package com.braintribe.model.processing.itw.synthesis.gm.template;

import java.util.Set;

import com.braintribe.asm.MethodVisitor;
import com.braintribe.asm.Opcodes;

public class StaticText implements TemplateNode {
	private final String text;

	public StaticText(String text) {
		this.text = text;
	}

	@Override
	public void merge(MethodVisitor mv, VariableResolver vr) {
		mergeText(mv, text);
	}

	public static void mergeText(MethodVisitor mv, String text) {
		mv.visitLdcInsn(text);
		mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/braintribe/model/processing/itw/tools/ItwStringBuilder", "append",
				"(Ljava/lang/String;)Lcom/braintribe/model/processing/itw/tools/ItwStringBuilder;", false);
	}

	@Override
	public void collectVariables(Set<Variable> variables) {
		// no variables expected
	}

}
