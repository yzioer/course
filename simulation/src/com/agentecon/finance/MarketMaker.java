package com.agentecon.finance;

import java.util.Collection;
import java.util.HashMap;

import com.agentecon.agent.Endowment;
import com.agentecon.agent.IAgentIdGenerator;
import com.agentecon.firm.IFirm;
import com.agentecon.firm.IMarketMaker;
import com.agentecon.firm.IStockMarket;
import com.agentecon.firm.Portfolio;
import com.agentecon.firm.Position;
import com.agentecon.firm.Ticker;
import com.agentecon.goods.Good;
import com.agentecon.goods.IStock;
import com.agentecon.goods.Stock;
import com.agentecon.market.IPriceMakerMarket;
import com.agentecon.util.Average;

public class MarketMaker extends Firm implements IMarketMaker {

	private static final int MARKET_MAKER_CASH = 1000;

	private double reserve;
	private Portfolio portfolio;
	private HashMap<Ticker, MarketMakerPrice> priceBeliefs;

	public MarketMaker(IAgentIdGenerator id,Good money, Collection<IFirm> firms) {
		super(id, new Endowment(money, new IStock[] { new Stock(money, MARKET_MAKER_CASH) }, new IStock[] {}));
		this.portfolio = new Portfolio(getMoney());
		this.reserve = 0.0;
		this.priceBeliefs = new HashMap<Ticker, MarketMakerPrice>();
		for (IFirm firm : firms) {
			notifyFirmCreated(firm);
		}
	}

	@Override
	public void managePortfolio(IStockMarket dsm) {
	}

	public void postOffers(IPriceMakerMarket dsm) {
		IStock money = getMoney().hide(reserve);
		double budgetPerPosition = money.getAmount() / priceBeliefs.size();
		for (MarketMakerPrice e : priceBeliefs.values()) {
			e.trade(dsm, this, money, budgetPerPosition);
		}
	}

	public void notifyFirmCreated(IFirm firm){
		if (firm.getTicker().equals(getTicker())) {
			// do not trade own shares
		} else {
			Position pos = firm.getShareRegister().createPosition();
			portfolio.addPosition(pos);
			MarketMakerPrice prev = priceBeliefs.put(pos.getTicker(), new MarketMakerPrice(pos));
			assert prev == null;
		}
	}

	public double getPrice(Good output) {
		return priceBeliefs.get(output).getPrice();
	}

	public Average getAvgHoldings() {
		Average avg = new Average();
		for (Ticker t : priceBeliefs.keySet()) {
			avg.add(portfolio.getPosition(t).getAmount());
		}
		return avg;
	}

	private Average getIndex() {
		Average avg = new Average();
		for (MarketMakerPrice mmp : priceBeliefs.values()) {
			avg.add(mmp.getPrice());
		}
		return avg;
	}

	@Override
	protected double calculateDividends(int day) {
		double excessCash = getMoney().getAmount() - MARKET_MAKER_CASH;
		if (excessCash > 0) {
			double dividend = excessCash / 3;
			this.reserve = excessCash - dividend;
			return dividend;
		} else {
			this.reserve = 0.0;
			return 0.0;
		}
		// return excessCash; // excessCash / 5 would lead to market makers eventually owning everything...
	}

	@Override
	public MarketMaker clone() {
		return this; // TEMP todo
	}

	@Override
	public Portfolio getPortfolio() {
		return portfolio;
	}

	@Override
	public String toString() {
		return getType() + " with " + getMoney() + ", holding " + getAvgHoldings() + ", price index: " + getIndex().toFullString() + ", dividend " + getShareRegister().getAverageDividend();
	}
}
