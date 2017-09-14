package com.agentecon.configuration;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.SocketTimeoutException;

import com.agentecon.IAgentFactory;
import com.agentecon.agent.Endowment;
import com.agentecon.agent.IAgentIdGenerator;
import com.agentecon.classloader.CompilingClassLoader;
import com.agentecon.classloader.GitSimulationHandle;
import com.agentecon.classloader.LocalSimulationHandle;
import com.agentecon.classloader.RemoteJarLoader;
import com.agentecon.classloader.RemoteLoader;
import com.agentecon.classloader.SimulationHandle;
import com.agentecon.consumer.IConsumer;
import com.agentecon.consumer.IUtility;

public class CompilingAgentFactory implements IAgentFactory {
	
	private String classname;
	private RemoteLoader loader;
	
	public CompilingAgentFactory(String classname, String owner, String repo) throws SocketTimeoutException, IOException {
		this(classname, owner, repo, "master");
	}

	public CompilingAgentFactory(String classname, String owner, String repo, String branch) throws SocketTimeoutException, IOException {
		this(classname, new GitSimulationHandle(owner, repo, branch));
	}
	
	public CompilingAgentFactory(String classname, File basePath) throws SocketTimeoutException, IOException {
		this(classname, new LocalSimulationHandle(basePath));
	}

	public CompilingAgentFactory(String classname, SimulationHandle handle) throws SocketTimeoutException, IOException {
		this.classname = classname;
		RemoteJarLoader parent = getSimulationJarLoader();
		if (parent == null){
			this.loader = new CompilingClassLoader(getSimulationJarLoader(), handle);
		} else {
			RemoteLoader loader = parent.getSubloader(handle);
			if (loader == null){
				this.loader = new CompilingClassLoader(getSimulationJarLoader(), handle);
				parent.registerSubloader(handle, this.loader);
			} else {
				this.loader = loader;
			}
		}
	}

	private RemoteJarLoader getSimulationJarLoader() {
		ClassLoader loader = getClass().getClassLoader();
		return loader instanceof RemoteJarLoader ? (RemoteJarLoader)loader : null;
	}

	@Override
	public IConsumer createConsumer(IAgentIdGenerator id, Endowment endowment, IUtility utilityFunction) {
		try {
			@SuppressWarnings("unchecked")
			Class<? extends IConsumer> clazz = (Class<? extends IConsumer>) loader.loadClass(classname);
			Constructor<? extends IConsumer> constructor = clazz.getConstructor(IAgentIdGenerator.class, Endowment.class, IUtility.class);
			assert clazz.getClassLoader() == loader;
			return constructor.newInstance(id, endowment, utilityFunction);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		} catch (SecurityException e) {
			throw new RuntimeException(e);
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

}
