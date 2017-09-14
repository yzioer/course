// Created on May 29, 2015 by Luzius Meisser

package com.agentecon.runner;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Iterator;

import com.agentecon.ISimulation;
import com.agentecon.classloader.RemoteJarLoader;
import com.agentecon.classloader.RemoteLoader;
import com.agentecon.classloader.SimulationHandle;

	
public class SimulationLoader extends RemoteJarLoader {

	public static final String SIM_CLASS = "com.agentecon.Simulation";

	private Checksum checksum;

	public SimulationLoader(SimulationHandle handle) throws SocketTimeoutException, IOException {
		super(handle);
	}

	public Checksum getChecksum(){
		return checksum;
	}
	
	public SimulationLoader refresh(boolean[] changed) throws SocketTimeoutException, IOException{
		Iterator<RemoteLoader> iter = subloaders.values().iterator();
		while (iter.hasNext()){
			RemoteLoader next = iter.next();
			if (!next.isUptoDate()){
				iter.remove();
				changed[0] = true;
			}
		}
		if (isUptoDate()){
			return this;
		} else {
			changed[0] = true;
			SimulationLoader newLoader = new SimulationLoader(source);
			newLoader.subloaders.putAll(this.subloaders);
			return newLoader;
		}
	}

	@SuppressWarnings("unchecked")
	public Class<? extends ISimulation> loadSimClass() {
		try {
			return (Class<? extends ISimulation>) loadClass(SIM_CLASS);
		} catch (ClassNotFoundException e) {
			throw new java.lang.RuntimeException(e);
		}
	}

	public ISimulation loadSimulation() throws IOException {
		try {
			return (ISimulation) loadClass(SIM_CLASS).newInstance();
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			throw new IOException("Failed to load simulation" , e);
		}
	}

}
