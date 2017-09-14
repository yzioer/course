// Created on Jun 23, 2015 by Luzius Meisser

package com.agentecon.metric.variants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.agentecon.agent.IAgent;
import com.agentecon.agent.IAgents;
import com.agentecon.consumer.IConsumer;
import com.agentecon.consumer.IConsumerListener;
import com.agentecon.firm.Ticker;
import com.agentecon.goods.Good;
import com.agentecon.goods.Inventory;
import com.agentecon.market.IMarket;
import com.agentecon.market.IMarketListener;
import com.agentecon.metric.SimStats;
import com.agentecon.metric.series.AveragingTimeSeries;
import com.agentecon.metric.series.Chart;
import com.agentecon.metric.series.TimeSeries;
import com.agentecon.util.Average;
import com.agentecon.util.InstantiatingHashMap;

public class StockMarketStats extends SimStats implements IMarketListener, IConsumerListener {

	public static boolean PRINT_TICKER = false;

	private Good index = new Good("Index");
	private HashMap<Ticker, Average> averages;
	private HashMap<Good, TimeSeries> prices;
	private HashMap<Good, TimeSeries> volumes;
	private HashMap<Good, TimeSeries> peratio;
	private AveragingTimeSeries investments, divestments, difference;

	public StockMarketStats(IAgents agents) {
		super(agents);
		this.investments = new AveragingTimeSeries("Inflows");
		this.divestments = new AveragingTimeSeries("Outflows");
		this.difference = new AveragingTimeSeries("Inflows - Outflows");
		this.averages = new InstantiatingHashMap<Ticker, Average>() {

			@Override
			protected Average create(Ticker key) {
				return new Average();
			}
		};
		this.prices = new InstantiatingHashMap<Good, TimeSeries>() {

			@Override
			protected TimeSeries create(Good key) {
				return new TimeSeries(key.getName());
			}

		};
		this.volumes = new InstantiatingHashMap<Good, TimeSeries>() {

			@Override
			protected TimeSeries create(Good key) {
				return new TimeSeries(key.getName());
			}

		};
		this.peratio = new InstantiatingHashMap<Good, TimeSeries>() {

			@Override
			protected TimeSeries create(Good key) {
				return new TimeSeries(key.getName());
			}

		};
	}

	public double getPrice(Ticker ticker) {
		return averages.get(ticker).getAverage();
	}

	@Override
	public void notifyDayStarted(int day) {
		averages.clear();
	}

	@Override
	public void notifyStockMarketOpened(IMarket market) {
		market.addMarketListener(this);
	}

	@Override
	public void notifyConsumerCreated(IConsumer consumer) {
		consumer.addListener(this);
	}

	@Override
	public void notifyTraded(IAgent seller, IAgent buyer, Good good, double quantity, double payment) {
		averages.get(good).add(quantity, payment / quantity);
	}

	@Override
	public void notifyTradesCancelled() {
		averages.clear();
	}
	
	@Override
	public void notifyMarketClosed(int day) {
		investments.pushSum(day);
		divestments.pushSum(day);
		difference.pushSum(day);
		Average indexPoints = new Average();
		HashMap<Good, Average> sectorIndices = new InstantiatingHashMap<Good, Average>() {

			@Override
			protected Average create(Good key) {
				return new Average();
			}
		};
		Average indexRatio = new Average();
		HashMap<Good, Average> sectorRatios = new InstantiatingHashMap<Good, Average>() {

			@Override
			protected Average create(Good key) {
				return new Average();
			}
		};
		for (Entry<Ticker, Average> e : averages.entrySet()) {
			Ticker firm = e.getKey();
			Good sector = new Good(firm.getType());
			Average avgPrice = e.getValue();
			indexPoints.add(avgPrice);
			sectorIndices.get(sector).add(avgPrice);

			double dividends = agents.getFirm(firm).getShareRegister().getAverageDividend();
			if (dividends > 1) {
				double peratio = avgPrice.getAverage() / dividends;
				indexRatio.add(peratio);
				sectorRatios.get(sector).add(peratio);
			}
		}
		printTicker(day);
		sectorIndices.put(index, indexPoints);
		for (Map.Entry<Good, Average> e : sectorIndices.entrySet()) {
			Average ind = e.getValue();
			if (ind.hasValue()) {
				Good sector = e.getKey();
				prices.get(sector).set(day, ind.getAverage());
				volumes.get(sector).set(day, ind.getTotWeight());
			}
		}
		for (Map.Entry<Good, Average> e : sectorRatios.entrySet()) {
			peratio.get(e.getKey()).set(day, e.getValue().getAverage());
		}
		if (indexRatio.hasValue()) {
			peratio.get(index).set(day, indexRatio.getAverage());
		}
	}

	private ArrayList<Ticker> toPrint = new ArrayList<>();

	protected void printTicker(int day) {
		if (PRINT_TICKER) {
			if (day == 1000) {
				toPrint.addAll(averages.keySet());
				Collections.sort(toPrint);
				printLabels();
			} else if (day > 1000 && toPrint.size() < averages.size()) {
				for (Ticker t : averages.keySet()) {
					if (!toPrint.contains(t)) {
						toPrint.add(t);
					}
				}
				printLabels();
			}
			if (toPrint.size() > 0) {
				String line = Integer.toString(day);
				for (Good g : toPrint) {
					Average avg = averages.get(g);
					if (avg == null) {
						line += "\t";
					} else {
						line += "\t" + avg.getAverage();
					}
				}
				System.out.println(line);
			}
		}
	}

	protected void printLabels() {
		String labels = "";
		for (Good g : toPrint) {
			labels += "\t" + g;
		}
		System.out.println(labels);
	}

	@Override
	public Collection<? extends Chart> getCharts(String simId) {
		Chart ch1 = new Chart(simId, "Stock Market Prices", "Volume-weighted stock prices for each sector", prices.values());
		Chart ch2 = new Chart(simId, "Stock Market Volumes", "Stock trading volumes of each sector", volumes.values());
		Chart ch3 = new Chart(simId, "Price/Earning Ratios", "P/E ratios by sector", peratio.values());
		Chart ch4 = new Chart(simId, "Investment Flows", "Worker investments versus retiree divestments", investments.getTimeSeries(), divestments.getTimeSeries());
		return Arrays.asList(ch1, ch2, ch3, ch4);
	}

	@Override
	public String toString() {
		return "Sales stats on " + prices.size() + " stocks";
	}

	@Override
	public Collection<TimeSeries> getTimeSeries() {
		ArrayList<TimeSeries> list = new ArrayList<>();
		list.addAll(TimeSeries.prefix("Price", prices.values()));
		ArrayList<TimeSeries> logReturns = TimeSeries.logReturns(list);
		list.addAll(logReturns);
		list.addAll(TimeSeries.absolute(logReturns));
		list.addAll(TimeSeries.prefix("Volume", volumes.values()));
		list.addAll(TimeSeries.prefix("P/E Ratio", peratio.values()));
		if (investments.getTimeSeries().compact().isInteresting()) {
			list.add(investments.getTimeSeries());
			list.add(divestments.getTimeSeries());
			list.add(difference.getTimeSeries());
		}
		return list;
	}

	@Override
	public void notifyConsuming(IConsumer inst, int age, Inventory inv, double utility) {
	}

	@Override
	public void notifyRetiring(IConsumer inst, int age) {
	}

	@Override
	public void notifyInvested(IConsumer inst, double amount) {
		investments.add(amount);
		difference.add(amount);
	}

	@Override
	public void notifyDivested(IConsumer inst, double amount) {
		divestments.add(amount);
		difference.add(-amount);
	}

}
