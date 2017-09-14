package com.agentecon.market;

import com.agentecon.agent.IAgent;
import com.agentecon.goods.Good;
import com.agentecon.util.AbstractListenerList;

public class MarketListeners extends AbstractListenerList<IMarketListener> implements IMarketListener {

	@Override
	public void notifyTraded(IAgent seller, IAgent buyer, Good good, double quantity, double payment) {
		for (IMarketListener l: list){
			l.notifyTraded(seller, buyer, good, quantity, payment);
		}
	}

	@Override
	public void notifyTradesCancelled() {
		for (IMarketListener l: list){
			l.notifyTradesCancelled();
		}
	}

	@Override
	public void notifyMarketClosed(int day) {
		for (IMarketListener l: list){
			l.notifyMarketClosed(day);
		}
	}

}
