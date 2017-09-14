package com.agentecon.metric.variants;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

import com.agentecon.agent.IAgents;
import com.agentecon.firm.IFirm;
import com.agentecon.firm.IFirmListener;
import com.agentecon.metric.SimStats;
import com.agentecon.metric.series.AveragingTimeSeries;
import com.agentecon.metric.series.Chart;
import com.agentecon.metric.series.TimeSeries;
import com.agentecon.util.InstantiatingHashMap;

public class DividendStats extends SimStats {

	private HashMap<String, AveragingTimeSeries> dividends;

	public DividendStats(IAgents agents) {
		super(agents);
		this.dividends = new InstantiatingHashMap<String, AveragingTimeSeries>() {

			@Override
			protected AveragingTimeSeries create(String key) {
				return new AveragingTimeSeries(key);
			}
		};
	}

	@Override
	public void notifyFirmCreated(IFirm firm) {
		firm.addFirmMonitor(new IFirmListener() {

			@Override
			public void reportDividend(IFirm comp, double amount) {
				DividendStats.this.reportDividend(comp.getName(), amount);
			}

		});
	}

	protected void reportDividend(String type, double amount) {
		dividends.get(type).add(amount);
	}

	@Override
	public void notifyDayEnded(int day) {
//		String line = day + " dividends";
		for (AveragingTimeSeries ts : dividends.values()) {
			ts.push(day);
//			line += ", " + ts.getTimeSeries().getName() + " " + Numbers.toString(value);
		}
		// System.out.println(line);
	}

	@Override
	public Collection<? extends Chart> getCharts(String simId) {
		return Collections.singleton(new Chart(simId, "Dividends", "Average daily dividend per firm type", AveragingTimeSeries.unwrap(dividends.values())));
	}

	@Override
	public ArrayList<TimeSeries> getTimeSeries() {
		return TimeSeries.prefix("Dividends", AveragingTimeSeries.unwrap(dividends.values()));
	}

}
