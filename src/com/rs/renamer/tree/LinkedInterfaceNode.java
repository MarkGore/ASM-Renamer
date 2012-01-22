package com.rs.renamer.tree;

import org.objectweb.asm.Opcodes;

import java.util.*;
/**
 * @author Mark
 *
 */
public class LinkedInterfaceNode {

	public final String name;
	public final int access;
	public final String superName;
	public final boolean external;

	private String newName;

	private final List<LinkedInterfaceNode> parents;
	private final List<LinkedInterfaceNode> children;

	private final Set<MemberNode> fields;
	private final Set<MemberNode> methods;

	private final Map<MemberNode, String> fieldMap;
	private final Map<MemberNode, String> methodMap;

	public LinkedInterfaceNode(String name,
							   int access,
							   String superName,
							   boolean external,
							   Set<MemberNode> fields,
							   Set<MemberNode> methods) {
		this.name = name;
		this.access = access;
		this.superName = superName;
		this.external = external;
		this.fields = fields;
		this.methods = methods;
		this.fieldMap = new HashMap<MemberNode, String>();
		this.methodMap = new HashMap<MemberNode, String>();
		this.parents = new ArrayList<LinkedInterfaceNode>();
		this.children = new ArrayList<LinkedInterfaceNode>();
	}

	public void addParent(LinkedInterfaceNode node) {
		parents.add(node);
	}

	public void addChild(LinkedInterfaceNode node) {
		children.add(node);
	}

	public Set<MemberNode> getFields() {
		return Collections.unmodifiableSet(fields);
	}

	public Set<MemberNode> getMethods() {
		return Collections.unmodifiableSet(methods);
	}

	public String getNewName() {
		return newName;
	}

	/**
	 * Specifies this node to rename the class it
	 * represents. If package is excluded, this
	 * class will be left in its current package,
	 * otherwise (if the '/' character exists to
	 * specify a package other than default) the
	 * class will be moved to that package. If you
	 * want to ensure that the class moves to the
	 * default package, prefix the name parameter
	 * with a single '/' character.
	 *
	 * @param name The new class name.
	 * @return <tt>true</tt> if this class was renamed.
	 */
	public boolean rename(String name) {
		if (this.name.equals("client") || this.name.startsWith("jag")) {
			return false;
		}
		if (!name.contains("/")) {
			name = this.name.substring(0,
					this.name.lastIndexOf('/') + 1) + name;
		} else if (name.startsWith("/")) {
			name = name.substring(1);
		}
		if (newName == null && !this.name.equals("java/lang/Object")) {
			if (!external && superName.equals("java/lang/Object")) {
				newName = name;
			} else {
				newName = this.name;
				name = this.name.substring(this.name.lastIndexOf("/") + 1);
			}
			int idx = 0;
			for (LinkedInterfaceNode child : children) {
				if (child.superName.equals(this.name)) {
					child.renameSub(name.substring(
							name.lastIndexOf('/') + 1) + "_Sub" + ++idx);
				}
			}
			return true;
		}
		return false;
	}

	protected void renameSub(String name) {
		if (this.name.equals("client") || this.name.startsWith("jag")) {
			return;
		}
		if (!external) {
			newName = name;
		} else {
			name = this.name.substring(this.name.lastIndexOf('/') + 1);
		}
		int idx = 0;
		for (LinkedInterfaceNode child : children) {
			if (child.superName.equals(this.name)) {
				child.renameSub(name.substring(
						name.lastIndexOf('/') + 1) + "_Sub" + ++idx);
			}
		}
	}

	public boolean renameField(MemberNode node, String name) {
		if (!fieldMap.containsKey(node)
				&& fields.contains(node)
				&& isFieldRenamable(node)) {
			renameFieldTree(new LinkedList<LinkedInterfaceNode>(), node, name);
			return true;
		}
		return false;
	}

	public boolean renameMethod(MemberNode node, String name) {
		if (!methodMap.containsKey(node)
				&& methods.contains(node)
				// TODO: providesignlink in rules obv.
				&& !node.name.matches("(<(cl)?init>)|(main)|(providesignlink)")
				&& isMethodRenamable(node)) {
			renameMethodTree(new LinkedList<LinkedInterfaceNode>(), node, name);
			return true;
		}
		return false;
	}

	public String getFieldName(String name, String desc) {
		String nn = fieldMap.get(new MemberNode(name, desc, 0));
		return nn == null ? name : nn;
	}

	public String getMethodName(String name, String desc) {
		String nn = methodMap.get(new MemberNode(name, desc, 0));
		return nn == null ? name : nn;
	}

	public List<LinkedInterfaceNode> getParents() {
		return Collections.unmodifiableList(parents);
	}

	boolean isFieldRenamable(MemberNode node) {
		MemberNode myNode = null;
		for (MemberNode m : fields) {
			if (m.equals(node)) {
				myNode = m;
				break;
			}
		}
		if (myNode != null && (myNode.access &
				Opcodes.ACC_PRIVATE) == 0) {
			for (LinkedInterfaceNode parent : parents) {
				if (parent.fields.contains(node)) {
					return false;
				}
			}
		}
		return true;
	}

	boolean isMethodRenamable(MemberNode node) {
		MemberNode myNode = null;
		for (MemberNode m : methods) {
			if (m.equals(node)) {
				myNode = m;
				break;
			}
		}
		if (myNode != null && (myNode.access &
				Opcodes.ACC_PRIVATE) == 0) {
			for (LinkedInterfaceNode parent : parents) {
				if (parent.external &&
						parent.methods.contains(node)) {
					return false;
				}
			}
		}
		return true;
	}

	void renameFieldTree(List<LinkedInterfaceNode> visited,
						 MemberNode node, String name) {
		if (!external && !visited.contains(this)) {
			fieldMap.put(node, name);
			visited.add(this);
			for (LinkedInterfaceNode parent : parents) {
				parent.renameFieldTree(visited, node, name);
			}
			for (LinkedInterfaceNode child : children) {
				child.renameFieldTree(visited, node, name);
			}
		}
	}

	void renameMethodTree(List<LinkedInterfaceNode> visited,
						  MemberNode node, String name) {
		if (!external && !visited.contains(this)) {
			methodMap.put(node, name);
			visited.add(this);
			for (LinkedInterfaceNode parent : parents) {
				parent.renameMethodTree(visited, node, name);
			}
			for (LinkedInterfaceNode child : children) {
				child.renameMethodTree(visited, node, name);
			}
		}
	}

}
