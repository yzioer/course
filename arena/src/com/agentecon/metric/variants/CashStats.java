package com.agentecon.metric.variants;

import java.util.Collection;
import java.util.Collections;

import com.agentecon.ISimulation;
import com.agentecon.agent.IAgent;
import com.agentecon.metric.SimStats;
import com.agentecon.metric.series.Chart;
import com.agentecon.metric.series.TimeSeries;
import com.agentecon.metric.series.TimeSeriesCollector;

public class CashStats extends SimStats {

	private TimeSeriesCollector collector;

	public CashStats(ISimulation agents) {
		super(agents);
		this.collector = new TimeSeriesCollector();
	}

	@Override
	public void notifyDayEnded(int day) {
		for (IAgent a : getAgents().getAgents()) {
			collector.record(day, a, a.getMoney().getAmount());
		}
		collector.flushDay(day, false);
	}

	@Override
	public Collection<? extends Chart> getCharts() {
		Chart ch = new Chart("Cash", "Overnight cash holdings by agent type", collector.getTypeTimeSeries());
		ch.setStacking("normal");
		return Collections.singleton(ch);
	}

	@Override
	public Collection<TimeSeries> getTimeSeries() {
		return collector.getTimeSeries();
	}

}
