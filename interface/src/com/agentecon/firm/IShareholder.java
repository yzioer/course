package com.agentecon.firm;

import com.agentecon.agent.IAgent;

public interface IShareholder extends IAgent {
	
	public Portfolio getPortfolio();
	
	/**
	 * This is the time to trade stocks on the stock market.
	 * The stock market opens before the goods market, but
	 * after new firms have been founded.
	 */
	public void managePortfolio(IStockMarket dsm);
	
}
