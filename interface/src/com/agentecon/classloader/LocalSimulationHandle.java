package com.agentecon.classloader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.function.BiConsumer;

public class LocalSimulationHandle extends SimulationHandle {

	private File basePath;
	private HashMap<String, Long> touchedFiles;

	public LocalSimulationHandle() {
		this(new File(".."));
	}

	public LocalSimulationHandle(File basePath) {
		super(System.getProperty("user.name").toLowerCase(), "local");
		this.basePath = basePath;
		this.touchedFiles = new HashMap<>();
		assert this.basePath.isDirectory() : this.basePath.getAbsolutePath() + " is not a folder";
		// assert getJarfile().isFile() : getJarfile().getAbsolutePath() + "
		// does not exist";
	}

	public String getDescription() {
		return "Simulation loader from local file system";
	}

	public String getAuthor() {
		return getOwner();
	}
	
	public boolean isPresent(){
		return getJarfile().exists();
	}

	private File getJarfile() {
		return new File(basePath, JAR_PATH.replace('/', File.separatorChar));
	}

	@Override
	public String getPath() {
		return getOwner() + "/local/local";
	}

	@Override
	public URL getBrowsableURL(String classname) {
		try {
			return new URL("file://" + basePath.getAbsolutePath() + File.separatorChar + "src" + File.separatorChar + classname.replace('.', File.separatorChar) + ".java");
		} catch (MalformedURLException e) {
			throw new java.lang.RuntimeException(e);
		}
	}

	@Override
	public long getJarDate() throws IOException {
		return getJarfile().lastModified();
	}

	@Override
	public InputStream openJar() throws IOException {
		File file = getJarfile();
		notifyTouched(file);
		return new FileInputStream(file);
	}

	private void notifyTouched(File file) {
		touchedFiles.put(file.getPath(), file.lastModified());
	}

	@Override
	public InputStream openInputStream(String classname) throws IOException {
		String fileName = classname.replace('.', '/') + ".java";
		File file = new File(basePath, fileName);
		notifyTouched(file);
		return new FileInputStream(file);
	}

	@Override
	public Collection<String> listSourceFiles(String packageName) throws IOException {
		File file = new File(basePath, packageName.replace('.', '/'));
		File[] children = file.listFiles();
		ArrayList<String> names = new ArrayList<>();
		if (children != null) {
			for (File f : children) {
				String name = f.getName();
				if (name.endsWith(JAVA_SUFFIX)) {
					name = name.substring(0, name.length() - JAVA_SUFFIX.length());
					names.add(packageName + "." + name);
				}
			}
		}
		return names;
	}

	@Override
	public String getVersion() {
		long[] version = new long[]{0};
		touchedFiles.forEach(new BiConsumer<String, Long>() {

			@Override
			public void accept(String t, Long u) {
				if (new File(t).lastModified() != u){
					version[0]++;
				}
			}
		});
		return version[0] + " modified files";
	}

}
