package com.agentecon.exercise5;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

import com.agentecon.finance.IStockPickingStrategy;
import com.agentecon.firm.IStockMarket;
import com.agentecon.firm.Portfolio;
import com.agentecon.firm.Ticker;

public class StockPickingStrategy implements IStockPickingStrategy {

	private Random random; // a random number generator, useful to make a random choice
	private Portfolio portfolio;

	public StockPickingStrategy(Random random, Portfolio portfolio) {
		this.portfolio = portfolio;
		this.random = random;
	}

	@Override
	public Ticker findStockToBuy(IStockMarket stocks) {
		return selectRandomFarm(stocks);
	}

	protected Ticker selectRandomFarm(IStockMarket stocks) {
		Collection<Ticker> listedStocks = stocks.getTradedStocks(); // a list of traded stocks
		ArrayList<Ticker> farms = new ArrayList<>();
		for (Ticker t: listedStocks) {
			if (t.getType().endsWith("Maker") && stocks.hasAsk(t)){
				farms.add(t);
			}
		}
		if (farms.isEmpty()) {
			return null;
		} else {
			return farms.get(random.nextInt(farms.size()));
		}
	}

}
