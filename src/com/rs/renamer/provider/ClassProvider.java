package com.rs.renamer.provider;

import com.rs.renamer.tree.InterfaceNode;
/**
 * @author Mark
 *
 */
public abstract class ClassProvider {

	protected ClassProvider cp;

	public ClassProvider(ClassProvider delegate) {
		this.cp = delegate;
	}

	public abstract InterfaceNode getClass(String name);

}
