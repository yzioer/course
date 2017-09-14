package com.agentecon.classloader;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collection;

public abstract class SimulationHandle {
	
	public static final String JAVA_SUFFIX = ".java";
	public static final String JAR_PATH = "simulation/jar/simulation.jar";

	private String owner, name;

	public SimulationHandle(String owner, String name) {
		this.owner = owner;
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public String getOwner() {
		return owner;
	}
	
	public abstract String getPath();
	
	public abstract long getJarDate() throws IOException;
	
	public abstract InputStream openJar() throws IOException;
	
	public abstract URL getBrowsableURL(String classname);
	
	@Override
	public String toString(){
		return name;
	}

	public abstract InputStream openInputStream(String classname) throws IOException;

	public abstract Collection<String> listSourceFiles(String packageName) throws IOException;

	public abstract String getVersion() throws IOException;
	
	@Override
	public int hashCode(){
		return owner.hashCode() ^ name.hashCode();
	}
	
	@Override
	public boolean equals(Object o){
		SimulationHandle other = (SimulationHandle) o;
		return other.owner.equals(owner) && other.name.equals(name);
	}

}
