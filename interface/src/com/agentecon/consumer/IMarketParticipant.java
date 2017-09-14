package com.agentecon.consumer;

import com.agentecon.market.IPriceTakerMarket;

public interface IMarketParticipant {
	
	/**
	 * Buy and sell goods on the market in a hopefully optimal way given the
	 * offers provided by the market makers of the goods market.
	 * This method is invoked on all IConsumers before consumption.
	 */
	public void tradeGoods(IPriceTakerMarket market);

}
