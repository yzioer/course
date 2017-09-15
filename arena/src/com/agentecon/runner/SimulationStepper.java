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
		this.enablePeriodicUpdate();
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

	private SimulationStepper refreshSimulation() throws SocketTimeoutException, IOException {
		SimulationLoader loader = SimulationStepper.this.loader;
		boolean[] changed = new boolean[] { false };
		SimulationLoader newLoader = loader.refresh(changed);
		if (changed[0]) {
			return new SimulationStepper(newLoader);
		} else {
			return null;
		}
	}

	public void enablePeriodicUpdate() {
		Thread t = new Thread() {
			public void run() {
				try {
					while (successor == null) {
						Thread.sleep(60000);
						try {
							successor = refreshSimulation();
						} catch (SocketTimeoutException e) {
							// try again in a minute
						} catch (IOException e) {
							throw new RuntimeException(e);
						}
					}
				} catch (InterruptedException e) {
				}
			}
		};
		t.setDaemon(true);
		t.start();

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

}
