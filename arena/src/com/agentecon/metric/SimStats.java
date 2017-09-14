package com.agentecon.metric;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;

import com.agentecon.ISimulation;
import com.agentecon.agent.IAgents;
import com.agentecon.metric.series.Chart;
import com.agentecon.metric.series.TimeSeries;
import com.agentecon.sim.SimulationListenerAdapter;

public abstract class SimStats extends SimulationListenerAdapter {

	protected final IAgents agents;

	public SimStats() {
		this(null);
	}

	public SimStats(IAgents agents) {
		this.agents = agents;
	}

	public abstract Collection<? extends Chart> getCharts(String simId);

	public void notifySimStarting(ISimulation sim) {
		sim.addListener(this);
	}

	public void notifySimEnded(ISimulation sim) {
	}

	public String getName() {
		return getClass().getSimpleName();
	}

	public abstract Collection<TimeSeries> getTimeSeries();

	public void print(PrintStream out) {
		try {
			print(out, "\t");
		} catch (NoInterestingTimeSeriesFoundException e) {
		}
	}

	public void print(PrintStream out, String separator) throws NoInterestingTimeSeriesFoundException {
		out.print("Day");
		Collection<TimeSeries> series = getTimeSeries();
		int start = Integer.MAX_VALUE;
		int end = 0;
		ArrayList<TimeSeries> ofInterest = new ArrayList<>();
		for (TimeSeries ts : series) {
			if (ts.isInteresting()) {
				out.print(separator + ts.getName());
				start = Math.min(start, ts.getStart());
				end = Math.max(end, ts.getEnd());
				ofInterest.add(ts);
			}
		}
		if (ofInterest.isEmpty()){
			throw new NoInterestingTimeSeriesFoundException();
		}
		out.println();
		for (int day = start; day <= end; day++) {
			out.print(day);
			for (TimeSeries ts : ofInterest) {
				out.print(separator + ts.get(day));
			}
			out.println();
		}
	}

}
