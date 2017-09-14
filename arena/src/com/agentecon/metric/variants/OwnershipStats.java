package com.agentecon.metric.variants;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.agentecon.agent.IAgents;
import com.agentecon.consumer.IConsumer;
import com.agentecon.firm.IShareholder;
import com.agentecon.firm.Portfolio;
import com.agentecon.firm.Position;
import com.agentecon.metric.SimStats;
import com.agentecon.metric.series.Chart;
import com.agentecon.metric.series.Line;
import com.agentecon.metric.series.TimeSeries;
import com.agentecon.util.InstantiatingHashMap;

public class OwnershipStats extends SimStats {

	private HashMap<String, HashMap<String, TimeSeries>> structure;

	public OwnershipStats(IAgents agents) {
		super(agents);
		this.structure = new InstantiatingHashMap<String, HashMap<String, TimeSeries>>() {

			@Override
			protected HashMap<String, TimeSeries> create(String key) {
				return new InstantiatingHashMap<String, TimeSeries>() {

					@Override
					protected TimeSeries create(String key) {
						return new TimeSeries(key, new Line());
					}
				};
			}
		};
	}

	@Override
	public void notifyDayEnded(int day) {
		if (day % 10 == 0) {
			HashMap<String, OwnershipStructure> owners = new InstantiatingHashMap<String, OwnershipStats.OwnershipStructure>() {

				@Override
				protected OwnershipStructure create(String key) {
					return new OwnershipStructure(key);
				}
			};
			for (IShareholder pc : agents.getShareholders()) {
				String ownerType = pc.getType();
				if (pc instanceof IConsumer && ((IConsumer) pc).isRetired()) {
					ownerType = "Retiree";
				}
				Portfolio pf = pc.getPortfolio();
				for (Position pos : pf.getPositions()) {
					String ownedType = pos.getTicker().getType();
					owners.get(ownedType).include(ownerType, pos.getAmount());
				}
			}
			for (OwnershipStructure os : owners.values()) {
				os.push(day, structure.get(os.type));
			}
		}
	}

	public class OwnershipStructure {

		private String type;
		private HashMap<String, Double> owners;

		public OwnershipStructure(String type) {
			this.type = type;
			this.owners = new InstantiatingHashMap<String, Double>() {

				@Override
				protected Double create(String key) {
					return 0.0d;
				}
			};
		}

		public void push(int day, HashMap<String, TimeSeries> hashMap) {
			for (Map.Entry<String, Double> e : owners.entrySet()) {
				hashMap.get(e.getKey()).set(day, e.getValue());
				;
			}
		}

		public void include(String ownerType, double amount) {
			owners.put(ownerType, owners.get(ownerType) + amount);
		}

	}

	@Override
	public Collection<? extends Chart> getCharts(String simId) {
		ArrayList<Chart> charts = new ArrayList<>();
		for (Map.Entry<String, HashMap<String, TimeSeries>> owned : structure.entrySet()) {
			Collection<TimeSeries> ts = owned.getValue().values();
			Chart ch = new Chart(simId, owned.getKey() + " Owners", "Owners of an average firm of type " + owned.getKey(), ts);
			ch.setStacking("percent");
			charts.add(ch);
		}
		return charts;
	}

	@Override
	public Collection<TimeSeries> getTimeSeries() {
		ArrayList<TimeSeries> list = new ArrayList<>();
		for (Map.Entry<String, HashMap<String, TimeSeries>> owned : structure.entrySet()) {
			Collection<TimeSeries> ts = owned.getValue().values();
			list.addAll(TimeSeries.prefix(owned.getKey() + " owner", ts));
		}
		return list;
	}

}
