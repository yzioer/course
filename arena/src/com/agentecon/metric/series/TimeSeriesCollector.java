package com.agentecon.metric.series;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

import com.agentecon.agent.IAgent;
import com.agentecon.consumer.IConsumer;
import com.agentecon.firm.IFirm;
import com.agentecon.util.InstantiatingHashMap;

public class TimeSeriesCollector {

	private HashMap<String, AveragingTimeSeries> type;
	private HashMap<String, TimeSeries> individual;
	private AveragingTimeSeries firms, consumers;

	public TimeSeriesCollector() {
		this.firms = new AveragingTimeSeries("Firms");
		this.consumers = new AveragingTimeSeries("Consumers");
		this.type = new InstantiatingHashMap<String, AveragingTimeSeries>() {

			@Override
			protected AveragingTimeSeries create(String key) {
				return new AveragingTimeSeries(key, new Line());
			}
		};
		this.individual = new InstantiatingHashMap<String, TimeSeries>() {

			@Override
			protected TimeSeries create(String key) {
				return new TimeSeries(key);
			}
		};
	}

	public void record(int day, IAgent agent, double number) {
		record(day, new IAgentType() {

			@Override
			public String getIndividualKey() {
				return agent.getName();
			}

			@Override
			public String[] getTypeKeys() {
				return new String[] { agent.getType() };
			}

			@Override
			public boolean isConsumer() {
				return agent instanceof IConsumer;
			}

			@Override
			public boolean isFirm() {
				return agent instanceof IFirm;
			}

		}, number);
	}

	public void record(int day, IAgentType agent, double number) {
		if (agent.getIndividualKey() != null) {
			individual.get(agent.getIndividualKey()).set(day, number);
		}
		for (String t : agent.getTypeKeys()) {
			type.get(t).add(number);
		}
		if (agent.isConsumer()) {
			consumers.add(number);
		}
		if (agent.isFirm()) {
			firms.add(number);
		}
	}

	public void flushDay(int day, boolean average) {
		for (AveragingTimeSeries ts : type.values()) {
			flush(day, average, ts);
		}
		flush(day, average, firms);
		flush(day, average, consumers);
	}

	protected void flush(int day, boolean average, AveragingTimeSeries ts) {
		if (average) {
			ts.push(day);
		} else {
			ts.pushSum(day);
		}
	}

	public Collection<TimeSeries> getTimeSeries() {
		ArrayList<TimeSeries> ts = new ArrayList<>();
		if (consumers.getTimeSeries().isInteresting()) {
			ts.add(consumers.getTimeSeries());
		}
		if (firms.getTimeSeries().isInteresting()) {
			ts.add(firms.getTimeSeries());
		}
		ts.addAll(sort(AveragingTimeSeries.unwrap(type.values())));
		ts.addAll(sort(individual.values()));
		return ts;
	}

	private Collection<? extends TimeSeries> sort(Collection<TimeSeries> values) {
		ArrayList<TimeSeries> list = new ArrayList<>(values.size());
		for (TimeSeries ts : values) {
			if (ts.isInteresting()) {
				list.add(ts);
			}
		}
		Collections.sort(list);
		return list;
	}

	public Collection<? extends TimeSeries> getTypeTimeSeries() {
		return sort(AveragingTimeSeries.unwrap(type.values()));
	}

}
