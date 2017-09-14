/**
 * Created by Luzius Meisser on Jun 18, 2017
 * Copyright: Meisser Economics AG, Zurich
 * Contact: luzius@meissereconomics.com
 *
 * Feel free to reuse this code under the MIT License
 * https://opensource.org/licenses/MIT
 */
package com.agentecon.web.methods;

public class Rank implements Comparable<Rank> {
	
	private String type;
	private double averageUtility;
	private transient int count;

	public Rank(String type){
		this.type = type;
	}
	
	public String getType(){
		return type;
	}
	
	public void add(double lifeTimeUtility){
		this.averageUtility *= count++;
		this.averageUtility += lifeTimeUtility;
		this.averageUtility /= count;
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