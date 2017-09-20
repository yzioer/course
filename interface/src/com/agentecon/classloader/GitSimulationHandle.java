package com.agentecon.classloader;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

public class GitSimulationHandle extends SimulationHandle {

	private String branch;
	private HashMap<String, HashSet<String>> cachedTree;

	public GitSimulationHandle(String owner, String repo) throws IOException {
		this(owner, repo, "master");
	}

	public GitSimulationHandle(String owner, String repo, String branch) throws IOException {
		super(owner, repo);
		this.branch = branch;
		this.cachedTree = new HashMap<>();
		WebUtil.checkAuthorizationCode();
	}

	public String getPath() {
		return super.getOwner() + "/" + getRepo() + "/" + branch;
	}

	@Override
	public String getBranch() {
		return branch;
	}

	@Override
	public URL getBrowsableURL(String classname) {
		try {
			return new URL("https://github.com/" + getOwner() + "/" + getRepo() + "/blob/" + branch + "/simulation/src/" + classname.replace(".", "/") + ".java");
		} catch (MalformedURLException e) {
			throw new java.lang.RuntimeException(e);
		}
	}

	public URLConnection getJarURLConnection() throws IOException {
		// only works when the jar is publicly available. Cannot use
		// openContentConnection because that only works up to 1 MB.
		// Alternative would be to use blob api, but that api only works with file
		// hashes, not with pathes.
		URL url = new URL("https://raw.githubusercontent.com/" + getOwner() + "/" + getRepo() + "/" + branch + "/" + JAR_PATH);
		return url.openConnection();
	}

	private URLConnection openContentConnection(String path) throws IOException {
		try {
			URL url = new URL(WebUtil.addSecret("https://api.github.com/repos/" + getOwner() + "/" + getRepo() + "/contents/" + path + "?ref=" + branch));
			URLConnection conn = url.openConnection();
			conn.setRequestProperty("Accept", "application/vnd.github.VERSION.raw");
			return conn;
		} catch (MalformedURLException e) {
			throw new java.lang.RuntimeException(e);
		}
	}

	@Override
	public boolean isPresent() throws IOException {
		try {
			WebUtil.readHttp("https://api.github.com/repos/" + getOwner() + "/" + getRepo());
			return true;
		} catch (FileNotFoundException e) {
			return false;
		}
	}

	@Override
	public long getJarDate() throws IOException {
		return getJarURLConnection().getDate();
	}

	@Override
	public InputStream openJar() throws IOException {
		return getJarURLConnection().getInputStream();
	}

	@Override
	public InputStream openInputStream(String classname) throws IOException {
		String path = classname.replace('.', '/');
		int dollar = path.indexOf('$');
		if (dollar >= 0) {
			path = path.substring(0, dollar);
		}
		URLConnection url = openContentConnection("exercises/src/" + path + ".java");
		return url.getInputStream();
	}

	@Override
	public Collection<String> listSourceFiles(String packageName) throws IOException {
		if (cachedTree.containsKey(packageName)) {
			return cachedTree.get(packageName);
		} else if (couldExist(packageName)) {
			ArrayList<String> names = new ArrayList<>();
			HashSet<String> subfolders = new HashSet<>();
			try {
				String answer = WebUtil.readGitApi(getOwner(), getRepo(), "contents", "exercises/src/" + packageName.replace('.', '/'), branch);
				int[] pos = new int[] { 0 };
				while (true) {
					String name = WebUtil.extract(answer, "name", pos);
					if (name == null) {
						break;
					} else if (name.endsWith(JAVA_SUFFIX)) {
						name = name.substring(0, name.length() - JAVA_SUFFIX.length());
						names.add(packageName + "." + name);
					} else if (!name.contains(".")) {
						subfolders.add(name);
					}
				}
			} catch (FileNotFoundException e) {
				// ignore, return empty list
			}
			cachedTree.put(packageName, subfolders);
			return names;
		} else {
			return Collections.emptyList();
		}
	}

	private boolean couldExist(String packageName) {
		while (true) {
			int dot = packageName.lastIndexOf('.');
			if (dot == -1) {
				return true;
			} else {
				String parent = packageName.substring(0, dot);
				HashSet<String> children = cachedTree.get(parent);
				if (children != null && !children.contains(packageName.substring(dot + 1))) {
					return false;
				}
				packageName = parent;
			}
		}
	}

	@Override
	public String getVersion() throws IOException {
		String commitUrl = "https://api.github.com/repos/" + getOwner() + "/" + getRepo() + "/commits/" + branch;
		String commitDesc = WebUtil.readHttp(commitUrl);
//		String hash = WebUtil.extract(commitDesc, "sha", new int[] { 0 });
		 String name = WebUtil.extract(commitDesc, "name", new int[]{0});
		 String date = WebUtil.extract(commitDesc, "date", new int[]{0});
		// String email = WebUtil.extract(commitDesc, "email", new int[]{0});
		return name + " on " + date;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof GitSimulationHandle) {
			return super.equals(o) && ((GitSimulationHandle) o).branch.equals(branch);
		} else {
			return false;
		}
	}

}
