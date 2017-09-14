package com.agentecon.oldarena;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import com.agentecon.classloader.GitSimulationHandle;
import com.agentecon.classloader.SimulationHandle;
import com.agentecon.classloader.WebUtil;

public class GitBasedSimulationList {

	private static final String NAME = "\"name\":\"";

	private String account, repo;
	private HashMap<String, GitSimulationHandle> sims;

	public GitBasedSimulationList(String account, String repo) throws IOException, InterruptedException {
		this.sims = new HashMap<String, GitSimulationHandle>();
		this.account = account;
		this.repo = repo;
		this.refresh();
	}

	/**
	 * Todo: more efficient refresh by looking at repository events
	 * https://api.github.com/repos/meisserecon/Agentecon/events
	 */
	protected void refresh() throws IOException, InterruptedException {
		String content = WebUtil.readHttp("https://api.github.com/repos/" + account + "/" + repo + "/tags");
		int pos = content.indexOf(NAME);
		while (pos >= 0) {
			int nameEnd = content.indexOf('"', pos + NAME.length());
			String name = content.substring(pos + NAME.length(), nameEnd);
			GitSimulationHandle handle = sims.get(name);
			if (handle == null) {
				handle = new GitSimulationHandle(account, repo, name);
				sims.put(name, handle);
			}
			pos = content.indexOf(NAME, pos + NAME.length());
		}
	}

	public ArrayList<GitSimulationHandle> getSims() {
		return new ArrayList<>(this.sims.values());
	}

	public SimulationHandle getSimulation(String name) {
		return sims.get(name);
	}
	
	@Override
	public String toString() {
		return sims.toString();
	}

	public static void main(String[] args) throws IOException, InterruptedException {
		GitBasedSimulationList list = new GitBasedSimulationList("meisserecon", "Agentecon");
		System.out.println(list);
	}

}
