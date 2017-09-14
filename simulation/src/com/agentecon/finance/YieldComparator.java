package com.agentecon.finance;

import java.util.Comparator;

import com.agentecon.firm.IFirm;
import com.agentecon.firm.IStockMarket;
import com.agentecon.firm.Ticker;
import com.agentecon.market.AbstractOffer;

public class YieldComparator implements Comparator<IFirm> {

	private boolean buying;
	private IStockMarket dsm;

	public YieldComparator(IStockMarket dsm, boolean buying) {
		this.dsm = dsm;
		this.buying = buying;
	}

	@Override
	public int compare(IFirm o1, IFirm o2) {
		double yield1 = getYield(o1);
		double yield2 = getYield(o2);
		return Double.compare(yield1, yield2);
	}

	public double getYield(IFirm o1) {
		double dividend = o1.getShareRegister().getAverageDividend();
		double price = getPrice(o1.getTicker());
		return dividend / price;
	}

	public double getPrice(Ticker ticker) {
		AbstractOffer offer = buying ? dsm.getAsk(ticker) : dsm.getBid(ticker);
		return offer.getPrice().getPrice();
	}

}
