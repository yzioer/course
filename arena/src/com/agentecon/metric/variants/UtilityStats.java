package com.agentecon.metric.variants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

import com.agentecon.ISimulation;
import com.agentecon.consumer.IConsumer;
import com.agentecon.market.IStatistics;
import com.agentecon.metric.SimStats;
import com.agentecon.metric.series.Chart;
import com.agentecon.metric.series.TimeSeries;
import com.agentecon.util.InstantiatingHashMap;

public class UtilityStats extends SimStats {

	private TimeSeries tot, min, max;
	private HashMap<String, TimeSeries> utilities;

	public UtilityStats(ISimulation sim) {
		super(sim);
		this.tot = new TimeSeries("Average");
		this.min = new TimeSeries("Min");
		this.max = new TimeSeries("Max");
		this.utilities = new InstantiatingHashMap<String, TimeSeries>() {

			@Override
			protected TimeSeries create(String key) {
				return new TimeSeries(key);
			}
		};
	}

	@Override
	public void notifyDayEnded(IStatistics stats) {
		this.tot.set(stats.getDay(), stats.getAverageUtility().getAverage());
		this.min.set(stats.getDay(), stats.getAverageUtility().getMin());
		this.max.set(stats.getDay(), stats.getAverageUtility().getMax());
		for (IConsumer consumer: getAgents().getConsumers()) {
			utilities.get(consumer.getName()).set(stats.getDay(), consumer.getUtilityFunction().getLatestExperiencedUtility());
		}
	}

	@Override
	public Collection<? extends Chart> getCharts() {
		Chart ch = new Chart("Average Utility", "Average daily utility per consumer in each iteration", getTimeSeries());
		return Collections.singleton(ch);
	}
	
	@Override
	public String toString(){
		return tot.toString();
	}

	@Override
	public Collection<TimeSeries> getTimeSeries() {
		Collection<TimeSeries> list = new ArrayList<>(Arrays.asList(tot, min, max));
		ArrayList<TimeSeries> individuals = new ArrayList<>(utilities.values());
		Collections.sort(individuals);
		list.addAll(individuals);
		return list;
	}
	
}
