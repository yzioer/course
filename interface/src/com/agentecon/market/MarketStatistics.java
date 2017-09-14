package com.agentecon.market;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.function.BiConsumer;

import com.agentecon.agent.IAgent;
import com.agentecon.goods.Good;
import com.agentecon.production.PriceUnknownException;
import com.agentecon.util.InstantiatingHashMap;
import com.agentecon.util.MovingAverage;

public class MarketStatistics implements IMarketStatistics, IMarketListener {

	private HashMap<Good, GoodStats> prices;

	public MarketStatistics() {
		this.prices = new InstantiatingHashMap<Good, GoodStats>() {

			@Override
			protected GoodStats create(Good key) {
				return new GoodStats();
			}
		};
	}

	@Override
	public Collection<Good> getTradedGoods() {
		return prices.keySet();
	}

	@Override
	public void notifyTraded(IAgent seller, IAgent buyer, Good good, double quantity, double payment) {
		assert quantity > 0.0;
		prices.get(good).notifyTraded(quantity, payment / quantity);
	}

	@Override
	public void notifyTradesCancelled() {
		for (GoodStats good : prices.values()) {
			good.resetCurrent();
		}
	}

	@Override
	public void notifyMarketClosed(int day) {
		for (GoodStats good : prices.values()) {
			good.commitCurrent();
		}
	}

	@Override
	public GoodStats getStats(Good good) {
		return prices.get(good);
	}

	@Override
	public void print(PrintStream out) {
		out.println("Good\tPrice\tVolume");
		prices.forEach(new BiConsumer<Good, GoodStats>() {

			@Override
			public void accept(Good t, GoodStats u) {
				out.println(t + "\t" + u.toTabString());
			}
		});
	}

	@Override
	public String toString() {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		PrintStream stream = new PrintStream(out);
		print(stream);
		return new String(out.toByteArray());
	}

	@Override
	public double getPriceBelief(Good good) throws PriceUnknownException {
		MovingAverage avg = getStats(good).getMovingAverage();
		if (avg == null){
			throw new PriceUnknownException();
		} else {
			return avg.getAverage();
		}
	}

}
