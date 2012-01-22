package com.rs.renamer.tree;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;

import java.util.*;
/**
 * @author Mark
 *
 */
public class InterfaceNode implements ClassVisitor {

	public String name;
	public int access;
	public String superName;
	public boolean external;

	public final List<String> parents = new ArrayList<String>();
	public final List<String> children = new ArrayList<String>();

	public final Set<MemberNode> fields = new HashSet<MemberNode>();
	public final Set<MemberNode> methods = new HashSet<MemberNode>();

	public InterfaceNode(boolean external) {
		this.external = external;
	}

	public void visit(
			final int version,
			final int access,
			final String name,
			final String signature,
			final String superName,
			final String[] interfaces) {
		this.name = name;
		this.access = access;
		this.superName = superName;
		this.parents.clear();
		if (superName != null) {
			this.parents.add(superName);
		}
		this.parents.addAll(Arrays.asList(interfaces));
		this.fields.clear();
		this.methods.clear();
		this.children.clear();
	}

	public void visitSource(final String file, final String debug) {

	}

	public void visitOuterClass(
			final String owner,
			final String name,
			final String desc) {

	}

	public void visitInnerClass(
			final String name,
			final String outerName,
			final String innerName,
			final int access) {

	}

	public FieldVisitor visitField(
			final int access,
			final String name,
			final String desc,
			final String signature,
			final Object value) {
		fields.add(new MemberNode(name, desc, access));
		return null;
	}

	public MethodVisitor visitMethod(
			final int access,
			final String name,
			final String desc,
			final String signature,
			final String[] exceptions) {
		methods.add(new MemberNode(name, desc, access));
		return null;
	}

	public AnnotationVisitor visitAnnotation(
			final String desc,
			final boolean visible) {
		return null;
	}

	public void visitAttribute(final Attribute attr) {

	}

	public void visitEnd() {
		
	}

}
