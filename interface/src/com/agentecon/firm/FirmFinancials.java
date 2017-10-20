package com.agentecon.firm;

import com.agentecon.market.GoodStats;
import com.agentecon.util.IAverage;

public class FirmFinancials {

	private IFirm firm;
	private GoodStats stats;

	public FirmFinancials(IFirm firm, GoodStats stats) {
		this.firm = firm;
		this.stats = stats;
	}

	/**
	 * A long term exponentially weighted average of the share price.
	 * Also allows to find out volatility.
	 */
	public IAverage getPriceLongtermAverage() {
		return stats.getMovingAverage();
	}

	/**
	 * The volume-weighted average price at which its shares traded yesterday.
	 */
	public double getSharePrice() {
		return stats.getYesterday().getAverage();
	}

	/**
	 * Yesterday's trading volume (number of shares).
	 */
	public double getTradingVolume() {
		return stats.getYesterday().getTotWeight();
	}

	/**
	 * The average daily dividend paid per share.
	 */
	public double getDailyDividendPerShare() {
		IRegister register = firm.getShareRegister();
		return register.getAverageDividend() / register.getTotalShareCount();
	}

	/**
	 * Calculates free-float market capitalization (ignoring the shares held by the company itself).
	 */
	public double getMarketCapitalization() {
		return getPriceLongtermAverage().getAverage() * firm.getShareRegister().getFreeFloatShares();
	}

}
