package com.agentecon.metric.variants;

import java.util.ArrayList;
import java.util.Collection;

import com.agentecon.ISimulation;
import com.agentecon.firm.IFirm;
import com.agentecon.firm.IFirmListener;
import com.agentecon.metric.SimStats;
import com.agentecon.metric.series.TimeSeries;
import com.agentecon.metric.series.TimeSeriesCollector;
import com.agentecon.web.query.AgentQuery;

public class DividendStats extends SimStats {

	private TimeSeriesCollector collector;

	public DividendStats(ISimulation agents, ArrayList<AgentQuery> selection) {
		super(agents);
		this.collector = new TimeSeriesCollector();
	}

	@Override
	public void notifyFirmCreated(IFirm firm) {
		firm.addFirmMonitor(new IFirmListener() {

			@Override
			public void reportDividend(IFirm comp, double amount) {
				collector.record(getDay(), comp, amount);
			}

		});
	}

	@Override
	public void notifyDayEnded(int day) {
		collector.flushDay(day, true);
	}

	@Override
	public Collection<TimeSeries> getTimeSeries() {
		return collector.getTimeSeries();
	}

}
