// Created by Luzius on Apr 22, 2014

package com.agentecon.consumer;

import com.agentecon.agent.Endowment;
import com.agentecon.agent.IAgentIdGenerator;
import com.agentecon.firm.IStockMarket;
import com.agentecon.firm.Portfolio;
import com.agentecon.goods.Inventory;
import com.agentecon.market.IPriceTakerMarket;
import com.agentecon.util.MovingAverage;

public class MortalConsumer extends Consumer {

	private int maxAge;
	private double savingsTarget;
	private MovingAverage dailySpendings;

	public MortalConsumer(IAgentIdGenerator id, int maxAge, Endowment end, IUtility utility) {
		super(id, end, utility);
		this.maxAge = maxAge;
		this.dailySpendings = new MovingAverage(0.95);
	}

	@Override
	public void managePortfolio(IStockMarket stocks) {
		if (isRetired()) {
			int daysLeft = maxAge - getAge() + 1;
			double amount = portfolio.sell(stocks, this, 1.0 / daysLeft);
			listeners.notifyDivested(this, amount);
		} else {
			double shareOfLiveSpentInRetirement = (maxAge - getRetirementAge()) / maxAge;
			double invest = dailySpendings.getAverage() * shareOfLiveSpentInRetirement;
			invest(stocks, invest);
		}
	}

	private void invest(IStockMarket stocks, double invest) {
		double dividendIncome = portfolio.getLatestDividendIncome();
		if (dividendIncome < invest) {
			savingsTarget = invest - dividendIncome;
			invest = Math.min(getMoney().getAmount(), invest);
		} else {
			savingsTarget = 0.0;
		}
		double amount = portfolio.invest(stocks, this, invest);
		listeners.notifyInvested(this, amount);
	}

	@Override
	protected void trade(Inventory inv, IPriceTakerMarket market) {
		if (savingsTarget > 0.0) {
			inv = inv.hide(getMoney().getGood(), Math.min(savingsTarget, dailySpendings.getAverage() / 2));
		}
		super.trade(inv, market);
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
	public Inventory considerDeath(Portfolio inheritance) {
		Inventory inv = super.considerDeath(inheritance);
		assert inv == null; // super is immortal and should never return an inheritance
		int age = getAge();
		if (age == getRetirementAge()) {
			listeners.notifyRetiring(this, age);
		}
		if (age > maxAge) {
			inheritance.absorb(portfolio);
			return super.dispose();
		} else {
			return null;
		}
	}

	@Override
	public boolean isRetired() {
		return getAge() > getRetirementAge();
	}

	private int getRetirementAge() {
		return maxAge / 5 * 3;
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
