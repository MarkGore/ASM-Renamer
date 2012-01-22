package com.rs.renamer.provider;

import com.rs.renamer.tree.InterfaceNode;

import org.objectweb.asm.ClassReader;

import java.io.IOException;
/**
 * @author Mark
 *
 */
public class SCLClassProvider extends ClassProvider {

	public SCLClassProvider() {
		super(null);
	}

	public SCLClassProvider(ClassProvider delegate) {
		super(delegate);
	}

	public InterfaceNode getClass(String name) {
		try {
			InterfaceNode cn = new InterfaceNode(true);
			ClassReader cr = new ClassReader(name);
			cr.accept(cn, 0);
			return cn;
		} catch (IOException ex) {
			if (cp != null) {
				return cp.getClass(name);
			}
		}
		return null;
	}

}
