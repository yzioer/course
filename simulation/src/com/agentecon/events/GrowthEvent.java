package com.agentecon.events;

import com.agentecon.world.ICountry;

public abstract class GrowthEvent extends SimEvent {
	
	private double probPerConsumer;
	
	public GrowthEvent(int start, double probPerConsumer) {
		super(start, 1, 1);
		this.probPerConsumer = probPerConsumer;
	}

	@Override
	public void execute(int day, ICountry sim) {
		double probability = sim.getAgents().getConsumers().size() * probPerConsumer;
		while (probability >= 1.0){
			execute(sim);
			probability -= 1.0;
		}
		if (sim.getRand().nextDouble() <= probability){
			execute(sim);
		}
	}

	protected abstract void execute(ICountry sim);

}
