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
package com.braintribe.wire.lab;

import com.braintribe.asm.AnnotationVisitor;
import com.braintribe.asm.ClassReader;
import com.braintribe.asm.ClassWriter;
import com.braintribe.asm.FieldVisitor;
import com.braintribe.asm.Label;
import com.braintribe.asm.MethodVisitor;
import com.braintribe.asm.Opcodes;
import com.braintribe.asm.Type;
import com.braintribe.asm.tree.ClassNode;
import com.braintribe.wire.api.annotation.Enriched;

public class EnrichedCheckLab implements Opcodes {
	private static class ExtraClassLoader extends ClassLoader {
		
		public ExtraClassLoader(ClassLoader parent) {
			super(parent);
			// TODO Auto-generated constructor stub
		}

		public Class<?> deploy(String name, byte[] data) {
			return defineClass(name, data, 0, data.length);
		}
	}
	
	
	private static class ClassNodeEx extends ClassNode {
		
		private static Type type = Type.getType(Enriched.class);
		
		public ClassNodeEx() {
			super(ASM5);
		}

		@Override
		public void visit(int version, int access, String name, String signature, String superName,
				String[] interfaces) {
			System.out.println(name);
			System.out.println(superName);
			super.visit(version, access, name, signature, superName, interfaces);
		}
		@Override
		public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
			if (desc.equals(type.getDescriptor())) {
				System.out.println(desc);
			}
			return super.visitAnnotation(desc, visible);
		}
	}
	
	
	public static void main(String[] args) {
		try {
			ClassReader reader = new ClassReader(EnrichedCheckLab.class.getResourceAsStream("/" + Space1Enriched.class.getName().replace('.', '/')+ ".class"));
			
			byte data[] = reader.b;
			ClassNodeEx classNode = new ClassNodeEx();
		    reader.accept(classNode, 0);
		    
		    System.out.println(classNode);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public static byte[] buildA() {
		ClassWriter cw = new ClassWriter(0);
		FieldVisitor fv;
		MethodVisitor mv;
		AnnotationVisitor av0;

		cw.visit(52, ACC_PUBLIC + ACC_SUPER, "com/braintribe/wire/lab/A", null, "java/lang/Object", null);

		cw.visitSource("A.java", null);

		{
		fv = cw.visitField(ACC_PUBLIC, "b", "Lcom/braintribe/wire/lab/B;", null, null);
		fv.visitEnd();
		}
		{
		mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
		mv.visitCode();
		Label l0 = new Label();
		mv.visitLabel(l0);
		mv.visitLineNumber(3, l0);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
		mv.visitInsn(RETURN);
		Label l1 = new Label();
		mv.visitLabel(l1);
		mv.visitLocalVariable("this", "Lcom/braintribe/wire/lab/A;", null, l0, l1, 0);
		mv.visitMaxs(1, 1);
		mv.visitEnd();
		}
		cw.visitEnd();

		return cw.toByteArray();
	}
	
	public static byte[] buildB() {
		ClassWriter cw = new ClassWriter(0);
		FieldVisitor fv;
		MethodVisitor mv;
		AnnotationVisitor av0;
		
		cw.visit(52, ACC_PUBLIC + ACC_SUPER, "com/braintribe/wire/lab/B", null, "java/lang/Object", null);
		
		cw.visitSource("A.java", null);
		
		{
			fv = cw.visitField(ACC_PUBLIC, "a", "Lcom/braintribe/wire/lab/A;", null, null);
			fv.visitEnd();
		}
		{
			mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
			mv.visitCode();
			Label l0 = new Label();
			mv.visitLabel(l0);
			mv.visitLineNumber(3, l0);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
			mv.visitInsn(RETURN);
			Label l1 = new Label();
			mv.visitLabel(l1);
			mv.visitLocalVariable("this", "Lcom/braintribe/wire/lab/B;", null, l0, l1, 0);
			mv.visitMaxs(1, 1);
			mv.visitEnd();
		}
		cw.visitEnd();
		
		return cw.toByteArray();
	}
}
