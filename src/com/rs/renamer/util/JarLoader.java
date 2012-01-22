package com.rs.renamer.util;

import com.rs.renamer.tree.InterfaceNode;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.commons.Remapper;
import org.objectweb.asm.tree.ClassNode;

import java.io.*;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
/**
 * @author Mark
 *
 */
public class JarLoader {

	private File file;
	private Map<String, byte[]> data;
	private Map<String, ClassNode> classes;
	private Map<String, InterfaceNode> interfaces;
	public JarLoader(File file) {
		this.file = file;
	}

	public void load() throws IOException {
		Map<String, InterfaceNode> interfaces = new HashMap<String, InterfaceNode>();
		Map<String, ClassNode> classes = new HashMap<String, ClassNode>();
		this.data = new HashMap<String, byte[]>();
		if (file.getName().endsWith(".jar")) {
			ZipFile zf = new ZipFile(file);
			Enumeration<?> e = zf.entries();
			while (e.hasMoreElements()) {
				ZipEntry ze = (ZipEntry) e.nextElement();
				if (ze.isDirectory()) {
					continue;
				}
				String name = ze.getName();
				if (name.endsWith(".class")) {
					name = name.substring(0, name.lastIndexOf(".class"));
					ClassReader cr = new ClassReader(zf.getInputStream(ze));
					InterfaceNode in = new InterfaceNode(false);
					ClassNode cn = new ClassNode();
					cr.accept(cn, 0);
					cn.accept(in);
					classes.put(name, cn);
					interfaces.put(name, in);
				} else {
					InputStream in = zf.getInputStream(ze);
					ByteArrayOutputStream out = new ByteArrayOutputStream();
					final int BUF_SIZE = 1 << 8;
					byte[] buffer = new byte[BUF_SIZE];
					int bytesRead;
					while ((bytesRead = in.read(buffer)) >= 0) {
						out.write(buffer, 0, bytesRead);
					}
					in.close();
					data.put(ze.getName(), out.toByteArray());
				}
			}
			zf.close();
		}
		this.classes = classes;
		this.interfaces = interfaces;
	}

	public Map<String, ClassNode> getClassNodes() {
		return classes;
	}

	public Map<String, InterfaceNode> getInterfaceNodes() {
		return interfaces;
	}

	public void writeClasses(Remapper remapper) {
		interfaces = null;
		for (Map.Entry<String, ClassNode> entry : classes.entrySet()) {
			ClassWriter writer = new ClassWriter(0);
			entry.getValue().accept(writer);
			data.put(remapper.map(entry.getKey()) + ".class", writer.toByteArray());
		}
		classes = null;
	}

	public void dump(File f) throws IOException {
		FileOutputStream stream = new FileOutputStream(f);
		JarOutputStream out = new JarOutputStream(stream);
		for (String s : data.keySet()) {
			if(s.endsWith(".class")) {
				out.putNextEntry(new JarEntry(s));
				out.write(data.get(s));
			}
		}

		out.close();
		stream.close();
	}
}
