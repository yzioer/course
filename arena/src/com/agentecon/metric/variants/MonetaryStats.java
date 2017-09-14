package com.agentecon.metric.variants;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

import com.agentecon.agent.IAgent;
import com.agentecon.agent.IAgents;
import com.agentecon.metric.SimStats;
import com.agentecon.metric.series.AveragingTimeSeries;
import com.agentecon.metric.series.Chart;
import com.agentecon.metric.series.Line;
import com.agentecon.metric.series.TimeSeries;
import com.agentecon.util.InstantiatingHashMap;

public class MonetaryStats extends SimStats {

	private static final int SKIP = 10;

	private IAgents agents;
	private HashMap<String, AveragingTimeSeries> cashByType;

	public MonetaryStats(IAgents agents) {
		this.agents = agents;
		this.cashByType = new InstantiatingHashMap<String, AveragingTimeSeries>() {

			@Override
			protected AveragingTimeSeries create(String key) {
				return new AveragingTimeSeries(key, new Line());
			}
		};
	}

	@Override
	public void notifyDayEnded(int day) {
		for (IAgent a : agents.getAgents()) {
			double money = a.getMoney().getAmount();
			cashByType.get(a.getType()).add(money / SKIP);
		}
		if (day % SKIP == 0) {
			for (AveragingTimeSeries ats : cashByType.values()) {
				ats.pushSum(day);
			}
		}
	}

	@Override
	public Collection<? extends Chart> getCharts(String simId) {
		Chart ch = new Chart(simId, "Cash", "Overnight cash holdings by agent type", AveragingTimeSeries.unwrap(cashByType.values()));
		ch.setStacking("normal");
		return Collections.singleton(ch);
	}

	@Override
	public Collection<TimeSeries> getTimeSeries() {
		ArrayList<TimeSeries> list = new ArrayList<>();
		list.addAll(TimeSeries.prefix("Overnight cash", AveragingTimeSeries.unwrap(cashByType.values())));
		return list;
	}

}
