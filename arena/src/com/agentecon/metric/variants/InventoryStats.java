package com.agentecon.metric.variants;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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
import com.agentecon.sim.Event;
import com.agentecon.util.Average;
import com.agentecon.util.InstantiatingHashMap;
import com.agentecon.util.Numbers;

public class InventoryStats extends SimStats {

	private double money;
	private boolean allowMoneySupplyChange;

	private HashMap<Good, MinMaxTimeSeries> consumerInv;
	private HashMap<Good, MinMaxTimeSeries> firmInv;
	private HashMap<Good, MinMaxTimeSeries> traderInv;
	// private HashMap<IFirm, HashMap<Good, TimeSeries>> individualInventories;
	private TimeSeries moneySupply;

	public InventoryStats(IAgents agents) {
		super(agents);
		this.money = 0.0;
		this.moneySupply = new TimeSeries("Money Supply");
		this.allowMoneySupplyChange = true;
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
		this.traderInv = new InstantiatingHashMap<Good, MinMaxTimeSeries>() {

			@Override
			protected MinMaxTimeSeries create(Good key) {
				return new MinMaxTimeSeries(key.getName() + " inventory");
			}
		};
	}

	@Override
	public void notifyEvent(Event e) {
		// event might change money supply
		allowMoneySupplyChange = true;
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
		checkInventories(day, agents.getFirms(), firmInv);
		checkInventories(day, agents.getConsumers(), consumerInv);
		checkMoneySupply(day);
	}

	protected void checkMoneySupply(int day) {
		double money = 0.0;
		for (IAgent a : agents.getAgents()) {
			money += a.getMoney().getAmount();
		}
		if (this.money != money && allowMoneySupplyChange) {
			this.allowMoneySupplyChange = false;
			this.money = money;
		} else {
			assert Numbers.equals(this.money, money);
		}
		moneySupply.set(day, money);
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

	public Collection<Chart> getCharts(String parentId) {
		ArrayList<Chart> charts = new ArrayList<>();
		charts.add(new Chart(parentId, "Firm Inventory", "Average firm inventory after trading, but before production and consumption", firmInv.values()));
		charts.add(new Chart(parentId, "Consumer Inventory", "Average consumer inventory after trading, but before production and consumption", consumerInv.values()));
		if (!traderInv.isEmpty()) {
			charts.add(new Chart(parentId, "Trader Inventory", "Average trader inventory after trading, but before production and consumption", traderInv.values()));
		}
		if (moneySupply.isInteresting()) {
			charts.add(new Chart(parentId, "Money Supply", "Total money supply in the economy", Collections.singleton(moneySupply)));
		}
		// for (Map.Entry<IFirm, HashMap<Good, TimeSeries>> e : individualInventories.entrySet()) {
		// charts.add(new Chart(parentId, "Inventory of " + e.getKey().getName(), "Inventory of " + e.getKey().getName() + " after trading, before production", e.getValue().values()));
		// }
		return charts;
	}

	@Override
	public ArrayList<TimeSeries> getTimeSeries() {
		ArrayList<TimeSeries> list = new ArrayList<>();
		list.addAll(TimeSeries.prefix("Inventory", firmInv.values()));
		list.addAll(TimeSeries.prefix("Inventory", consumerInv.values()));
		list.add(moneySupply);
		return list;
	}

}
