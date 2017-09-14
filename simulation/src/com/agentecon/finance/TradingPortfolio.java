package com.agentecon.finance;

import java.util.ArrayList;
import java.util.Collections;

import com.agentecon.agent.IAgent;
import com.agentecon.firm.IStockMarket;
import com.agentecon.firm.Portfolio;
import com.agentecon.firm.Position;
import com.agentecon.firm.Ticker;
import com.agentecon.goods.IStock;
import com.agentecon.production.IPriceProvider;
import com.agentecon.production.PriceUnknownException;
import com.agentecon.util.Numbers;

public class TradingPortfolio extends Portfolio {

	public TradingPortfolio(IStock money) {
		super(money);
	}

	public double getCombinedValue(IPriceProvider prices, int timeHorizon) throws PriceUnknownException {
		return getSubstanceValue(prices) + getEarningsValue(timeHorizon);
	}

	public double getEarningsValue(int timeHorizon) {
		return getLatestDividendIncome() * timeHorizon;
	}

	public double getSubstanceValue(IPriceProvider prices) throws PriceUnknownException {
		double value = wallet.getAmount();
		for (Position p : inv.values()) {
			value += p.getAmount() * prices.getPriceBelief(p.getTicker());
		}
		return value;
	}

	public double sell(IStockMarket stocks, IAgent owner, double fraction) {
		double moneyBefore = wallet.getAmount();
		for (Ticker ticker : new ArrayList<>(inv.keySet())) {
			Position pos = inv.get(ticker);
			stocks.sell(owner, pos, wallet, pos.getAmount() * fraction);
			if (pos.isEmpty()) {
				disposePosition(ticker);
			}
		}
		return wallet.getAmount() - moneyBefore;
	}

	public double invest(IStockMarket stocks, IAgent owner, double budget) {
		double moneyBefore = wallet.getAmount();
		if (Numbers.isBigger(budget, 0.0)) {
			assert wallet.getAmount() >= budget;
			Ticker any = findStockToBuy(stocks);
			if (any != null) {
				double before = wallet.getAmount();
				Position pos = getPosition(any);
				addPosition(stocks.buy(owner, any, pos, wallet, budget));
				double spent = before - wallet.getAmount();
				invest(stocks, owner, budget - spent);
			}
		}
		return moneyBefore - wallet.getAmount();
	}

	@SuppressWarnings("unchecked")
	private Ticker findStockToBuy(IStockMarket stocks) {
		return stocks.findAnyAsk(Collections.EMPTY_LIST, false);
	}

	@Override
	public TradingPortfolio clone(IStock money) {
		return (TradingPortfolio) super.clone(money);
	}

}
