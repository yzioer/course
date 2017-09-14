package com.agentecon.firm;

import java.util.List;

import com.agentecon.agent.IAgent;
import com.agentecon.goods.IStock;
import com.agentecon.market.Ask;
import com.agentecon.market.Bid;
import com.agentecon.market.IPriceMakerMarket;

public interface IStockMarket extends IPriceMakerMarket {

	public Ticker findAnyAsk(List<Ticker> preferred, boolean marketCapWeight);
	
	public Position buy(IAgent buyer, Ticker ticker, Position existing, IStock wallet, double budget);

	public double sell(IAgent seller, Position pos, IStock wallet, double maxAmount);

	public Ask getAsk(Ticker ticker);
	
	public Bid getBid(Ticker ticker);

	public boolean hasBid(Ticker ticker);

	public boolean hasAsk(Ticker ticker);	

}
