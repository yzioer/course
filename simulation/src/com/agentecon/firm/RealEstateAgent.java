/**
 * Created by Luzius Meisser on Jun 18, 2017
 * Copyright: Meisser Economics AG, Zurich
 * Contact: luzius@meissereconomics.com
 *
 * Feel free to reuse this code under the MIT License
 * https://opensource.org/licenses/MIT
 */
package com.agentecon.firm;

import com.agentecon.agent.Endowment;
import com.agentecon.agent.IAgentIdGenerator;
import com.agentecon.finance.Firm;
import com.agentecon.finance.MarketMakerPrice;
import com.agentecon.goods.Good;
import com.agentecon.goods.IStock;
import com.agentecon.learning.QuadraticMaximizer;
import com.agentecon.market.IPriceMakerMarket;
import com.agentecon.production.IGoodsTrader;
import com.agentecon.production.IPriceProvider;
import com.agentecon.production.PriceUnknownException;

public class RealEstateAgent extends Firm implements IGoodsTrader {
	
	private static final double DISTRIBUTION_RATIO = 0.02;
	
	private Good land;
	private double capital;
	private double minCashLevel;
	private MarketMakerPrice priceBelief;
	private QuadraticMaximizer profitModel; 

	public RealEstateAgent(IAgentIdGenerator id, IShareholder owner, IStock initialMoney, IStock initialLand) {
		super(id, owner, new Endowment(initialMoney.getGood()));
		getMoney().absorb(initialMoney);
		
		this.land = initialLand.getGood();
		this.minCashLevel = getMoney().getAmount();
		IStock ownedLand = getStock(this.land);
		ownedLand.absorb(initialLand);
		this.priceBelief = new MarketMakerPrice(ownedLand);
		this.profitModel = new QuadraticMaximizer(0.98, id.getRand().nextLong(), initialMoney.getAmount(), initialMoney.getAmount() * 1000);
		this.capital = calculateCapital();
	}

	private double calculateCapital() {
		return getInventory().calculateValue(new IPriceProvider() {
			
			@Override
			public double getPriceBelief(Good good) throws PriceUnknownException {
				return priceBelief.getPrice();
			}
		});
	}

	@Override
	public void offer(IPriceMakerMarket market) {
		this.priceBelief.trade(market, this, getMoney(), getMoney().getAmount() / 10);
	}
	
	@Override
	public void adaptPrices() {
		// done during offer phase
	}
	
	@Override
	protected double calculateDividends(int day) {
		double profits = calculateProfits();
		return getMoney().getAmount() * DISTRIBUTION_RATIO - minCashLevel;
	}

	private double calculateProfits() {
		double currentCapital = calculateCapital();
		double prevCapital = this.capital;
		double profits = currentCapital - prevCapital;
//		this.profitModel.update(prevCapital, profits);
		this.capital = currentCapital;
//		System.out.println(prevCapital + "\t" + profits);
		return profits;
	}

}
