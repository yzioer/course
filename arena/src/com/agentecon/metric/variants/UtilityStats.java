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

	private int iter;
	private AveragingTimeSeries totUtil;
	private AveragingTimeSeries phase1Util;

	public UtilityStats() {
		this.iter = 0;
		this.totUtil = new AveragingTimeSeries("Overall");
		this.phase1Util = new AveragingTimeSeries("From day 250 to end");
	}

	@Override
	public void notifyDayEnded(IStatistics stats) {
		this.totUtil.add(stats.getAverageUtility().getAverage());
		if (stats.getDay() >= 250) {
			this.phase1Util.add(stats.getAverageUtility().getAverage());
		}
	}

	@Override
	public void notifySimEnded(ISimulation sim) {
		this.totUtil.push(iter);
		this.phase1Util.push(iter);
		this.iter++;
	}

	@Override
	public Collection<? extends Chart> getCharts(String simId) {
		Collection<TimeSeries> list = Arrays.asList(totUtil.getTimeSeries(), phase1Util.getTimeSeries());
		Chart ch = new Chart(simId, "Average Utility", "Average daily utility per consumer in each iteration", list);
		return Collections.singleton(ch);
	}
	
	@Override
	public String toString(){
		return phase1Util.toString();
	}

	public AveragingTimeSeries getScore() {
		return phase1Util;
	}
	
	public AveragingTimeSeries getUtil(){
		return totUtil;
	}

	@Override
	public Collection<TimeSeries> getTimeSeries() {
		ArrayList<TimeSeries> list = new ArrayList<>();
		list.add(totUtil.getTimeSeries());
		return list;
	}
	
}
