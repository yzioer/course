// Created on Jun 23, 2015 by Luzius Meisser

package com.agentecon.market;

import com.agentecon.agent.IAgent;
import com.agentecon.goods.Good;

public interface IMarketListener {
	
	public void notifyTraded(IAgent seller, IAgent buyer, Good good, double quantity, double payment);
	
	public void notifyTradesCancelled();
	
	public void notifyMarketClosed(int day);

}
