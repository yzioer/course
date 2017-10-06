package com.agentecon.exercise3;

import java.util.Collection;

import com.agentecon.Simulation;
import com.agentecon.exercises.FarmingConfiguration;
import com.agentecon.sim.Event;

public class MoneyConfiguration extends FarmingConfiguration {
	
	public MoneyConfiguration() {
		super(Farmer.class);
	}

	@Override
	public Collection<Event> getEvents() {
		Collection<Event> eventsFromFarmingConfig = super.getEvents();
//		eventsFromFarmingConfig.add(new HelicopterMoneyEvent(1000, 1, 100));
//		eventsFromFarmingConfig.add(new InterestEvent(0.01, 100));
		return eventsFromFarmingConfig;
	}

	public static void main(String[] args) {
		MoneyConfiguration config = new MoneyConfiguration();
		Simulation sim = new Simulation(config);
		sim.run();
		config.diagnoseResult(System.out, sim);
	}
	
}
