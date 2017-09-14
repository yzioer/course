package com.agentecon.finance;

import com.agentecon.agent.IAgent;
import com.agentecon.firm.Factor;
import com.agentecon.goods.IStock;
import com.agentecon.learning.IBelief;
import com.agentecon.market.AbstractOffer;
import com.agentecon.market.Ask;
import com.agentecon.market.Price;

public class CeilingFactor extends Factor {

	public CeilingFactor(IStock stock, IBelief price) {
		super(stock, price);
	}

	public void adapt(double min) {
		if (prevOffer != null) {
			price.adaptWithFloor(shouldIncrease(), min);
		}
	}

	@Override
	protected AbstractOffer newOffer(IAgent owner, IStock money, double price, double amount) {
		return new Ask(owner, money, stock, new Price(getGood(), price), amount);
	}

}
