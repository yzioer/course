package com.agentecon.market;

import java.io.PrintStream;
import java.util.Collection;

import com.agentecon.goods.Good;
import com.agentecon.production.IPriceProvider;

public interface IMarketStatistics extends IPriceProvider {
	
	public Collection<Good> getTradedGoods();
	
	public GoodStats getStats(Good good);

	public void print(PrintStream out);
	
}
