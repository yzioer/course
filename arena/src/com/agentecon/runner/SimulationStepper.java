package com.agentecon.runner;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Collection;

import com.agentecon.ISimulation;
import com.agentecon.classloader.LocalSimulationHandle;
import com.agentecon.classloader.SimulationHandle;
import com.agentecon.util.LogClock;
import com.agentecon.web.SoftCache;
import com.agentecon.web.methods.Rank;
import com.agentecon.web.methods.UtilityRanking;

public class SimulationStepper {

	private SimulationCache simulation;
	private SimulationLoader loader;
	private SoftCache<Object, Object> cachedData;

	private SimulationStepper successor;

	public SimulationStepper(SimulationHandle handle) throws IOException {
		this(new SimulationLoader(handle));
	}

	public SimulationStepper(SimulationLoader loader) throws IOException {
		this.loader = loader;
		this.simulation = new SimulationCache(loader);
		this.cachedData = refreshCache(loader);
		this.successor = null;
	}

	public boolean isObsolete() {
		return successor != null;
	}

	public SimulationStepper getSuccessor() {
		return successor;
	}

	private SoftCache<Object, Object> refreshCache(SimulationLoader loader) throws IOException {
		SoftCache<Object, Object> cache = new SoftCache<>();
		cache.put(UtilityRanking.class, createRanking(loader.loadSimulation()));
		return cache;
	}

	public Recyclable<ISimulation> getSimulation() throws IOException {
		return this.simulation.getAny();
	}

	public Recyclable<ISimulation> getSimulation(int day) throws IOException {
		Recyclable<ISimulation> rec = this.simulation.borrow(day);
		ISimulation simulation = rec.getItem();
		assert day <= simulation.getConfig().getRounds();
		assert simulation.getDay() <= day;
		simulation.forwardTo(day);
		assert simulation.getDay() == day;
		return rec;
	}

	public SimulationStepper refreshSimulation(String repo) throws SocketTimeoutException, IOException, NothingChangedException {
		SimulationLoader loader = SimulationStepper.this.loader;
		if (loader.usesRepository(repo)) {
			SimulationStepper newstepper = new SimulationStepper(new SimulationLoader(loader));
			System.out.println("Refreshed " + this);
			return newstepper;
		} else {
			throw new NothingChangedException();
		}
	}

	private UtilityRanking createRanking(ISimulation sim) {
		UtilityRanking ranking = new UtilityRanking();
		sim.addListener(ranking);
		sim.run();
		return ranking;
	}

	public Collection<Rank> getRanking() {
		UtilityRanking ranking = (UtilityRanking) cachedData.get(UtilityRanking.class);
		return ranking.getRanking();
	}

	public static void main(String[] args) throws IOException, InterruptedException {
		LogClock clock = new LogClock();
		// SimulationHandle local = new GitSimulationHandle("meisserecon", "agentecon",
		// "master");
		SimulationHandle local = new LocalSimulationHandle();
		clock.time("Created handle");
		SimulationStepper stepper = new SimulationStepper(local);
		stepper.getSimulation(100);
		stepper.getSimulation(50);
	}

	public Object getCachedItem(Object key) {
		return cachedData.get(key);
	}

	public void putCached(Object string, Object chart) {
		cachedData.put(string, chart);
	}

	@Override
	public String toString() {
		return "Simulation stepper for " + loader;
	}

}
