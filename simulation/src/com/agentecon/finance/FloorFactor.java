package com.agentecon.finance;

import com.agentecon.agent.IAgent;
import com.agentecon.firm.Factor;
import com.agentecon.goods.IStock;
import com.agentecon.learning.IBelief;
import com.agentecon.market.AbstractOffer;
import com.agentecon.market.Bid;
import com.agentecon.market.Price;

public class FloorFactor extends Factor {

	public FloorFactor(IStock stock, IBelief price) {
		super(stock, price);
	}

	public void adapt(double max) {
		if (prevOffer != null) {
			price.adaptWithCeiling(shouldIncrease(), max);
		}
	}

	@Override
	protected AbstractOffer newOffer(IAgent owner, IStock money, double p, double planned) {
		return new Bid(owner, money, stock, new Price(getGood(), p), planned);
	}

}
