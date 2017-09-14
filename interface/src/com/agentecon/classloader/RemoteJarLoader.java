// Created on May 29, 2015 by Luzius Meisser

package com.agentecon.classloader;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.function.BiConsumer;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import com.agentecon.util.IOUtils;

public class RemoteJarLoader extends RemoteLoader {

	private static final int ENDING_LEN = ".class".length();

	private long date;
	private HashMap<String, byte[]> data;

	public RemoteJarLoader() throws SocketTimeoutException, IOException {
		this("meisserecon", "agentecon", "master");
	}

	public RemoteJarLoader(String owner, String repo, String branch) throws SocketTimeoutException, IOException {
		this(new GitSimulationHandle(owner, repo, branch));
	}

	public RemoteJarLoader(File basePath) throws IOException {
		this(new LocalSimulationHandle(basePath));
	}

	public RemoteJarLoader(SimulationHandle source) throws SocketTimeoutException, IOException {
		super(RemoteJarLoader.class.getClassLoader(), source);
		System.out.println("Created remote jar loader to read from " + source.getBrowsableURL(""));
		this.date = source.getJarDate();
		this.data = new HashMap<String, byte[]>();
		try (InputStream is = source.openJar()) {
			JarInputStream jis = new JarInputStream(new BufferedInputStream(is, 500000));
			try {
				JarEntry entry = jis.getNextJarEntry();
				while (entry != null) {
					if (!entry.isDirectory()) {
						int size = (int) entry.getSize();
						byte[] data = IOUtils.readData(size, jis);
						this.data.put(toClassName(entry.getName()), data);
						jis.closeEntry();
					}
					entry = jis.getNextJarEntry();
				}
			} finally {
				jis.close();
			}
		} catch (ClassFormatError e) {
			throw new java.lang.RuntimeException(e);
		}
	}

	public long getDate() {
		return date;
	}

	public static byte[] readData(InputStream source) throws IOException {
		int ava = source.available();
		int size = ava == 0 ? 1000 : ava;
		ByteArrayOutputStream out = new ByteArrayOutputStream(size);
		byte[] buffer = new byte[size];
		while (true) {
			int read = source.read(buffer, 0, buffer.length);
			if (read > 0) {
				out.write(buffer, 0, read);
			} else {
				assert read == -1;
				break;
			}
		}
		return out.toByteArray();
	}

	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		byte[] data = this.data.get(name);
		if (data == null) {
			throw new ClassNotFoundException(name);
		} else {
			return super.defineClass(name, data, 0, data.length);
		}
	}

	private String toClassName(String name) {
		return name.substring(0, name.length() - ENDING_LEN).replace('/', '.');
	}

	public void forEach(BiConsumer<String, byte[]> biConsumer) {
		data.forEach(biConsumer);
	}

	public boolean hasClass(String className) {
		return data.containsKey(className);
	}

	public byte[] getByteCode(String className) {
		return data.get(className);
	}

}
