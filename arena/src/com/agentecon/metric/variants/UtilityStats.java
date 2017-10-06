package com.agentecon.metric.variants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import com.agentecon.ISimulation;
import com.agentecon.market.IStatistics;
import com.agentecon.metric.SimStats;
import com.agentecon.metric.series.AveragingTimeSeries;
import com.agentecon.metric.series.Chart;
import com.agentecon.metric.series.TimeSeries;

public class UtilityStats extends SimStats {

	private TimeSeries tot, min, max;

	public UtilityStats() {
		this.tot = new TimeSeries("Average");
		this.min = new TimeSeries("Min");
		this.max = new TimeSeries("Max");
	}

	@Override
	public void notifyDayEnded(IStatistics stats) {
		this.tot.set(stats.getDay(), stats.getAverageUtility().getAverage());
		this.min.set(stats.getDay(), stats.getAverageUtility().getMin());
		this.max.set(stats.getDay(), stats.getAverageUtility().getMax());
	}

	@Override
	public Collection<? extends Chart> getCharts(String simId) {
		Collection<TimeSeries> list = Arrays.asList(tot, min, max);
		Chart ch = new Chart(simId, "Average Utility", "Average daily utility per consumer in each iteration", list);
		return Collections.singleton(ch);
	}
	
	@Override
	public String toString(){
		return tot.toString();
	}

	@Override
	public Collection<TimeSeries> getTimeSeries() {
		return Arrays.asList(tot, min, max);
	}
	
}
