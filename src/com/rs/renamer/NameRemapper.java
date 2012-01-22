package com.rs.renamer;

import com.rs.renamer.tree.LinkedInterfaceNode;

import org.objectweb.asm.commons.Remapper;

import java.util.Map;

public class NameRemapper extends Remapper {

	private Map<String, LinkedInterfaceNode> tree;

	public NameRemapper(Map<String, LinkedInterfaceNode> tree) {
		this.tree = tree;
	}

	@Override
	public String mapFieldName(String owner, String name, String desc) {
		LinkedInterfaceNode node = tree.get(owner);
		if (node != null) {
			return node.getFieldName(name, desc);
		}
		return name;
	}

	@Override
	public String mapMethodName(String owner, String name, String desc) {
		LinkedInterfaceNode node = tree.get(owner);
		if (node != null) {
			return node.getMethodName(name, desc);
		}
		return name;
	}

	@Override
	public String map(String typeName) {
		LinkedInterfaceNode node = tree.get(typeName);
		if (node != null) {
			String name = node.getNewName();
			if (name != null) {
				return name;
			}
		}
		return typeName;
	}

}
