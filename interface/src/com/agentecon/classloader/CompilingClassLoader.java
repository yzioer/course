// Created on May 29, 2015 by Luzius Meisser

package com.agentecon.classloader;

import java.io.IOException;
import java.net.SocketTimeoutException;

public class CompilingClassLoader extends RemoteLoader {

	private AgentCompiler compiler;

	public CompilingClassLoader(RemoteJarLoader simulationJar, SimulationHandle source) throws SocketTimeoutException, IOException {
		super(simulationJar == null ? CompilingClassLoader.class.getClassLoader() : simulationJar, source);
		this.compiler = new AgentCompiler(simulationJar, source);
	}

	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		byte[] data = this.compiler.findClass(name);
		if (data == null) {
			throw new ClassNotFoundException(name + " could not be found on " + getSource());
		} else {
			return super.defineClass(name, data, 0, data.length);
		}
	}

}
