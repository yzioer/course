package com.agentecon.oldarena;

import java.util.ArrayList;

import com.agentecon.runner.Checksum;

public class SimulationList extends Persistable {
	
	public static final int DEFAULT_ID = 1;

	private ArrayList<SimulationInfo> sims;

	public SimulationList() {
		this.sims = new ArrayList<>();
		this.setId(DEFAULT_ID);
	}
	
	public void put(SimulationInfo info) {
		for (int i = 0; i < sims.size(); i++) {
			if (sims.get(i).equals(info)) {
				sims.set(i, info);
				return;
			}
		}
		this.sims.add(info);
	}

	public SimulationInfo getSimulation(Checksum checksum) {
		for (SimulationInfo info: sims){
			if (info.hasChecksum(checksum)){
				return info;
			}
		}
		return null;
	}

}
