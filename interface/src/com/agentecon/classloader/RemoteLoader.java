/**
 * Created by Luzius Meisser on Jun 18, 2017
 * Copyright: Meisser Economics AG, Zurich
 * Contact: luzius@meissereconomics.com
 *
 * Feel free to reuse this code under the MIT License
 * https://opensource.org/licenses/MIT
 */
package com.agentecon.classloader;

import java.io.IOException;
import java.util.HashMap;

public abstract class RemoteLoader extends ClassLoader {
	
	private String version;
	protected SimulationHandle source;
	protected HashMap<SimulationHandle, RemoteLoader> subloaders;
	
	public RemoteLoader(ClassLoader parent, SimulationHandle source) throws IOException{
		super(parent);
		this.version = source.getVersion();
		this.source = source;
		this.subloaders = new HashMap<>();
	}
	
	public void registerSubloader(SimulationHandle handle, RemoteLoader loader) {
		RemoteLoader prev = this.subloaders.put(handle, loader);
		assert prev == null;
	}

	public RemoteLoader getSubloader(SimulationHandle handle) {
		return this.subloaders.get(handle);
	}
	
	public boolean isUptoDate() throws IOException{
		return source.getVersion().equals(version);
	}
	
	public String getOwner() {
		return source.getOwner();
	}

	public SimulationHandle getSource() {
		return source;
	}

}
