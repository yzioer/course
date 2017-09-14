package com.agentecon.web.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.agentecon.ISimulation;

public class SelectionRecommendation {
	
	private HashSet<String> known;
	
	public SelectionRecommendation(ISimulation simulation, Set<String> agents){
		this.known = new HashSet<>(simulation.getAgents().getFirmTypes());
		this.known.addAll(simulation.getAgents().getConsumerTypes());
		this.known.addAll(agents);
	}

	public Collection<String> getNewNodeSuggestions(ISimulation simulation){
		ArrayList<String> newTypes = new ArrayList<>();
		for (String type: simulation.getAgents().getConsumerTypes()){
			if (!known.contains(type)){
				newTypes.add(type);
			}
		}
		for (String type: simulation.getAgents().getFirmTypes()){
			if (!known.contains(type)){
				newTypes.add(type);
			}
		}
		return newTypes;
	}

}
