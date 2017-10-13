package com.agentecon.metric.variants;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

import com.agentecon.ISimulation;
import com.agentecon.agent.IAgent;
import com.agentecon.consumer.Inheritance;
import com.agentecon.goods.Good;
import com.agentecon.market.IMarketStatistics;
import com.agentecon.market.IStatistics;
import com.agentecon.metric.SimStats;
import com.agentecon.metric.series.Chart;
import com.agentecon.metric.series.TimeSeries;
import com.agentecon.util.Average;
import com.agentecon.util.InstantiatingHashMap;

public class MonetaryStats extends SimStats {

	private TimeSeries velocity;
	private TimeSeries moneySupply;
	private HashMap<Good, TimeSeries> prices;
	private HashMap<Good, TimeSeries> volumes;

	public MonetaryStats(ISimulation agents) {
		super(agents);
		this.moneySupply = new TimeSeries("Money Supply");
		this.prices = new InstantiatingHashMap<Good, TimeSeries>() {

			@Override
			protected TimeSeries create(Good key) {
				return new TimeSeries(key.getName() + " price");
			}
		};
		this.volumes = new InstantiatingHashMap<Good, TimeSeries>() {

			@Override
			protected TimeSeries create(Good key) {
				return new TimeSeries(key.getName() + " volume");
			}
		};
		this.velocity = new TimeSeries("Velocity of Money");
	}

	@Override
	public void notifyDayEnded(IStatistics stats) {
		int day = stats.getDay();

		double moneySupply = 0.0;
		for (IAgent a : getAgents().getAgents()) {
			moneySupply += a.getMoney().getAmount();
		}
		for (Inheritance pending: getAgents().getPendingInheritances()) {
			moneySupply += pending.getMoney().getAmount();
		}
		this.moneySupply.set(day, moneySupply);

		double transactionVolume = 0.0;
		transactionVolume += record(day, stats.getGoodsMarketStats());
		transactionVolume += record(day, stats.getStockMarketStats());
		this.velocity.set(day, transactionVolume / moneySupply); // Fisher equation
	}

	private double record(int day, IMarketStatistics stats) {
		double transactionVolume = 0.0;
		for (Good good : stats.getTradedGoods()) {
			Average priceData = stats.getStats(good).getYesterday();
			if (priceData.getTotWeight() > 0) {
				transactionVolume += priceData.getTotal();
				prices.get(good).set(day, priceData.getAverage());
				volumes.get(good).set(day, priceData.getTotWeight());
			}
		}
		return transactionVolume;
	}

	@Override
	public Collection<? extends Chart> getCharts() {
		Chart ch = new Chart("Monetary Statistics", "All relevant data for calculating monetary velocity according to the Fisher equation.", getTimeSeries());
		ch.setStacking("normal");
		return Collections.singleton(ch);
	}

	@Override
	public Collection<TimeSeries> getTimeSeries() {
		ArrayList<TimeSeries> list = new ArrayList<>();
		list.add(moneySupply);
		list.add(velocity);
		list.addAll(prices.values());
		list.addAll(volumes.values());
		return list;
	}

}
