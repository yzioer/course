/**
 * Created by Luzius Meisser on Jun 18, 2017
 * Copyright: Meisser Economics AG, Zurich
 * Contact: luzius@meissereconomics.com
 *
 * Feel free to reuse this code under the MIT License
 * https://opensource.org/licenses/MIT
 */
package com.agentecon.classloader;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.function.BiConsumer;

public abstract class RemoteLoader extends ClassLoader {

	private String version;
	private HashMap<String, ByteCodeSource> bytecode;

	protected SimulationHandle source;
	protected HashMap<SimulationHandle, RemoteLoader> subLoaderCache;

	public RemoteLoader(ClassLoader parent, SimulationHandle source) throws IOException {
		super(parent);
		this.version = source.getVersion();
		this.source = source;
		this.bytecode = new HashMap<>();
		this.subLoaderCache = new HashMap<>();
	}

	protected byte[] loadBytecode(String classname) throws ClassNotFoundException {
		throw new ClassNotFoundException(classname);
	}

	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		byte[] data = getByteCodeSource(name).getData();
		return super.defineClass(name, data, 0, data.length);
	}

	public boolean usesRepository(String repo) {
		if (source.getRepo().equals(repo)) {
			return true;
		} else {
			for (SimulationHandle handle : subLoaderCache.keySet()) {
				if (handle.getRepo().equals(repo)) {
					return true;
				}
			}
			return false;
		}
	}
	
	public boolean refreshSubloaders() throws IOException {
		Iterator<RemoteLoader> iter = subLoaderCache.values().iterator();
		boolean changed = false;
		while (iter.hasNext()) {
			if (!iter.next().isUptoDate()) {
				iter.remove();
				changed = true;
			}
		}
		return changed;
	}

	public Collection<RemoteLoader> getCachedSubloaders() {
		return subLoaderCache.values();
	}
	
	public void registerSubloader(RemoteLoader loader) {
		registerSubloader(loader.getSource(), loader);
	}

	public void registerSubloader(SimulationHandle source, RemoteLoader loader) {
		RemoteLoader prev = this.subLoaderCache.put(source, loader);
		assert prev == null;
	}

	public RemoteLoader getSubloader(SimulationHandle handle) {
		return this.subLoaderCache.get(handle);
	}

	public String getVersionString() {
		return version;
	}

	public boolean isUptoDate() throws IOException {
		return source.getVersion().equals(version);
	}

	public String getOwner() {
		return source.getRepo();
	}

	public SimulationHandle getSource() {
		return source;
	}
	
	public synchronized ByteCodeSource getByteCodeSource(String name){
		ByteCodeSource data = this.bytecode.get(name);
		if (data == null) {
			data = new ByteCodeSource(name){
				@Override
				protected byte[] loadData() throws ClassNotFoundException{
					return RemoteLoader.this.loadBytecode(name);
				}
			};
			this.bytecode.put(name, data);
		}
		return data;
	}

	public void forEach(String packageName, BiConsumer<String, ByteCodeSource> biConsumer) throws IOException {
		bytecode.forEach(new BiConsumer<String, ByteCodeSource>() {
			
			private final boolean recurse = false;

			@Override
			public void accept(String name, ByteCodeSource u) {
				if (name.startsWith(packageName)) {
					if (recurse || name.substring(packageName.length() + 1).indexOf('.') == -1) {
						biConsumer.accept(name, u);
					}
				}
			}
		});
	}

	@Override
	public String toString() {
		return "Class loader that loads from " + source;
	}

}
