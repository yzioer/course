package com.agentecon.firm.decisions;

public class ExpectedRevenueBasedStrategy implements IFirmDecisions {

	private double laborshare;
	private double profitshare;

	public ExpectedRevenueBasedStrategy(double laborshare) {
		this.laborshare = laborshare;
		this.profitshare = 1.0 - laborshare;
	}
	
	protected double getLaborShare(){
		return laborshare;
	}

	@Override
	public IFirmDecisions duplicate() {
		return new ExpectedRevenueBasedStrategy(laborshare);
	}

	public double calcCogs(IFinancials financials) {
		return financials.getCash() / 5.0;
	}

	@Override
	public double calcDividend(IFinancials metrics) {
		return metrics.getExpectedRevenue() * profitshare - metrics.getFixedCosts();
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " with profitshare " + profitshare;
	}

}
