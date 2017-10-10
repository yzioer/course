/**
 * Created by Luzius Meisser on Jun 18, 2017
 * Copyright: Meisser Economics AG, Zurich
 * Contact: luzius@meissereconomics.com
 *
 * Feel free to reuse this code under the MIT License
 * https://opensource.org/licenses/MIT
 */
package com.agentecon.metric.variants;

import java.net.URL;

import com.agentecon.agent.Agent;

public class Rank implements Comparable<Rank> {
	
	private String type;
	private String version;
	private String url;
	private double averageUtility;
	private transient int instances;

	public Rank(String type, Agent agent){
		this.type = type;
		this.version = agent.getVersion();
		URL source = agent.getSourceUrl();
		if (source == null) {
			url = "local";
//		} else if (source.getProtocol().equals("file")) {
//			url = source.getPath();
		} else {
			url = source.toExternalForm();
		}
	}
	
	public String getType(){
		return type;
	}
	
	public void add(double score){
		this.averageUtility *= instances++;
		this.averageUtility += score;
		this.averageUtility /= instances;
	}
	
	@Override
	public int compareTo(Rank o) {
		return -Double.compare(averageUtility, o.averageUtility);
	}
	
	@Override
	public String toString() {
		return getType() + "\t" + averageUtility;
	}
	
}