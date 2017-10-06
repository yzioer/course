package com.agentecon.exercise3;

import com.agentecon.Simulation;
import com.agentecon.exercises.FarmingConfiguration;

public class MoneyConfiguration extends FarmingConfiguration {
	
	public MoneyConfiguration() {
		super(Farmer.class);
		
//		addEvent(new HelicopterMoneyEvent(1000, 1, 1, 100));
//		addEvent(new InterestEvent(0.01, 100));
	}

	public static void main(String[] args) {
		MoneyConfiguration config = new MoneyConfiguration();
		Simulation sim = new Simulation(config);
		sim.run();
		config.diagnoseResult(System.out, sim);
	}
	
}
