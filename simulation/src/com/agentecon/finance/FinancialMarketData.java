package com.agentecon.finance;

import com.agentecon.firm.FirmFinancials;
import com.agentecon.firm.IFinancialMarketData;
import com.agentecon.firm.Ticker;
import com.agentecon.market.MarketStatistics;
import com.agentecon.world.Agents;

public class FinancialMarketData implements IFinancialMarketData {
	
	private Agents ags;
	private MarketStatistics stockStats;

	public FinancialMarketData(Agents ags, MarketStatistics stockStats) {
		this.ags = ags;
		this.stockStats = stockStats;
	}
	
	@Override
	public FirmFinancials getFirmData(Ticker ticker) {
		Firm firm = (Firm) ags.getAgent(ticker.getNumer());
		return new FirmFinancials(firm, stockStats.getStats(ticker));
	}

}
