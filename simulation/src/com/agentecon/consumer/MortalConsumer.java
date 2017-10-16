// Created by Luzius on Apr 22, 2014

package com.agentecon.consumer;

import com.agentecon.agent.Endowment;
import com.agentecon.agent.IAgentIdGenerator;
import com.agentecon.firm.Portfolio;
import com.agentecon.goods.Inventory;
import com.agentecon.util.MovingAverage;

public class MortalConsumer extends Consumer {

	private int maxAge;
	private boolean receivedInheritance;
	private MovingAverage dailySpendings;

	public MortalConsumer(IAgentIdGenerator id, int maxAge, Endowment end, IUtility utility) {
		super(id, end, utility);
		this.maxAge = maxAge;
		this.dailySpendings = new MovingAverage(0.9);
		this.receivedInheritance = end.getInitialInventory().getQuantities().size() > 1;
	}
	
	@Override
	public void inherit(Inheritance inheritance) {
		this.receivedInheritance = inheritance.getPortfolio().hasPositions();
		super.inherit(inheritance);
	}
	
	// special getType for the growth configuration
	@Override
	public String getType() {
		String basic = super.getType();
		if (receivedInheritance) {
			return basic + " (capitalist)";
		} else {
			return basic + " (worker)";
		}
	}

//	@Override
//	public void managePortfolio(IStockMarket stocks) {
//		if (isRetired()) {
//			int daysLeft = maxAge - getAge() + 1;
//			double amount = portfolio.sell(stocks, this, 1.0 / daysLeft);
//			listeners.notifyDivested(this, amount);
//		} else {
//			double shareOfLiveSpentInRetirement = (maxAge - getRetirementAge()) / maxAge;
//			double invest = dailySpendings.getAverage() * shareOfLiveSpentInRetirement;
//			invest(stocks, invest);
//		}
//	}

//	private void invest(IStockMarket stocks, double invest) {
//		double dividendIncome = portfolio.getLatestDividendIncome();
//		if (dividendIncome < invest) {
//			savingsTarget = invest - dividendIncome;
//			invest = Math.min(getMoney().getAmount(), invest);
//		} else {
//			savingsTarget = 0.0;
//		}
//		double amount = portfolio.invest(stocks, this, invest);
//		listeners.notifyInvested(this, amount);
//	}
//
	public double getDailySpendings() {
		return dailySpendings.getAverage();
	}
	
	@Override
	protected void notifySpent(double spendings) {
		dailySpendings.add(spendings);
	}

	@Override
	public boolean isMortal() {
		return true;
	}

	@Override
	public Inheritance considerDeath() {
		Inheritance inh = super.considerDeath();
		assert inh == null; // super is immortal and should never return an inheritance
		int age = getAge();
		if (age == getRetirementAge()) {
			listeners.notifyRetiring(this, age);
		}
		if (age > maxAge) {
			Inventory inv = super.dispose();
			Portfolio portfolio = new Portfolio(inv.getMoney());
			portfolio.absorb(getPortfolio());
			return new Inheritance(inv, portfolio);
		} else {
			return null;
		}
	}

	@Override
	public boolean isRetired() {
		return getAge() > getRetirementAge();
	}
	
	public int getMaxAge() {
		return maxAge;
	}

	public int getRetirementAge() {
		return maxAge / 5 * 4;
	}

	@Override
	public MortalConsumer clone() {
		MortalConsumer klon = (MortalConsumer) super.clone();
		klon.dailySpendings = dailySpendings.clone();
		return klon;
	}

	@Override
	public String toString() {
		return super.toString();
	}

}
