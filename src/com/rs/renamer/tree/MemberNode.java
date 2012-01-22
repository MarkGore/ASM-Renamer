package com.rs.renamer.tree;
/**
 * @author Mark
 *
 */
public class MemberNode {

	public final String name;
	public final String desc;
	public final int access;

	public MemberNode(String name, String desc, int access) {
		this.name = name;
		this.desc = desc;
		this.access = access;
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (o != null && o instanceof MemberNode) {
			MemberNode n = (MemberNode) o;
			return n.name.equals(name) && n.desc.equals(desc);
		}
		return false;
	}

	@Override
	public String toString() {
		return "MemberNode[name=" + name + ",desc=" +
				desc + ",access=" + access + "]";
	}

}
