package com.agentecon.metric.variants;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.agentecon.ISimulation;
import com.agentecon.agent.IAgent;
import com.agentecon.agent.IAgents;
import com.agentecon.goods.Good;
import com.agentecon.goods.IStock;
import com.agentecon.goods.Inventory;
import com.agentecon.market.IMarket;
import com.agentecon.market.IMarketListener;
import com.agentecon.metric.SimStats;
import com.agentecon.metric.series.Chart;
import com.agentecon.metric.series.MinMaxTimeSeries;
import com.agentecon.metric.series.TimeSeries;
import com.agentecon.util.Average;
import com.agentecon.util.InstantiatingHashMap;

public class InventoryStats extends SimStats {

	private HashMap<Good, MinMaxTimeSeries> consumerInv;
	private HashMap<Good, MinMaxTimeSeries> firmInv;

	public InventoryStats(ISimulation agents) {
		super(agents);
		this.firmInv = new InstantiatingHashMap<Good, MinMaxTimeSeries>() {

			@Override
			protected MinMaxTimeSeries create(Good key) {
				return new MinMaxTimeSeries(key.getName() + " inventory");
			}
		};
		this.consumerInv = new InstantiatingHashMap<Good, MinMaxTimeSeries>() {

			@Override
			protected MinMaxTimeSeries create(Good key) {
				return new MinMaxTimeSeries(key.getName() + " inventory");
			}
		};
	}

	@Override
	public void notifyGoodsMarketOpened(IMarket market) {
		market.addMarketListener(new IMarketListener() {

			@Override
			public void notifyTradesCancelled() {
			}

			@Override
			public void notifyTraded(IAgent seller, IAgent buyer, Good good, double quantity, double payment) {
			}

			@Override
			public void notifyMarketClosed(int day) {
				InventoryStats.this.notifyMarketClosed(day);
			}
		});
	}

	public void notifyMarketClosed(int day) {
		IAgents agents = getAgents();
		checkInventories(day, agents.getFirms(), firmInv);
		checkInventories(day, agents.getConsumers(), consumerInv);
	}

	private void checkInventories(int day, Collection<? extends IAgent> agents, HashMap<Good, MinMaxTimeSeries> inventories) {
		HashMap<Good, Average> all = new InstantiatingHashMap<Good, Average>() {

			@Override
			protected Average create(Good key) {
				return new Average();
			}

		};
		for (IAgent ag : agents) {
			Inventory inv = ag.getInventory();
			for (IStock stock : inv.getAll()) {
				if (!stock.isEmpty() && stock.getGood().getPersistence() > 0.0) {
					all.get(stock.getGood()).add(1.0, stock.getAmount());
				}
			}
		}
		for (Map.Entry<Good, Average> entry : all.entrySet()) {
			inventories.get(entry.getKey()).set(day, entry.getValue());
		}
	}

	public Collection<Chart> getCharts() {
		ArrayList<Chart> charts = new ArrayList<>();
		charts.add(new Chart("Firm Inventory", "Average firm inventory after trading, but before production and consumption", firmInv.values()));
		charts.add(new Chart("Consumer Inventory", "Average consumer inventory after trading, but before production and consumption", consumerInv.values()));
		return charts;
	}

	@Override
	public ArrayList<TimeSeries> getTimeSeries() {
		ArrayList<TimeSeries> list = new ArrayList<>();
		list.addAll(TimeSeries.prefix("Firms' ", firmInv.values()));
		list.addAll(TimeSeries.prefix("Consumers' ", consumerInv.values()));
		return list;
	}

}
