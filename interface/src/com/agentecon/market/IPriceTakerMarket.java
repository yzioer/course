// Created by Luzius on Apr 22, 2014

package com.agentecon.market;

import java.util.Collection;

import com.agentecon.agent.IAgent;
import com.agentecon.goods.Good;
import com.agentecon.goods.IStock;
import com.agentecon.goods.Quantity;

public interface IPriceTakerMarket extends IMarket {

	// public double buy(Wallet wallet, Stock stock, double amount);
	//
	// public double sell(Wallet wallet, Stock stock, double amount);
	//
	// /**
	// * Convenience method to either buy (if amount positive) or sell (if amount
	// negative)
	// * at the current best market price
	// */
	// public double trade(Wallet wallet, Stock stock, double amount);

	/**
	 * Convenience method for getPrices
	 */
	public Collection<IOffer> getBids();

	/**
	 * Convenience method for getPrices
	 */
	public Collection<IOffer> getAsks();

	public Collection<IOffer> getOffers(IPriceFilter bidAskFilter);

	public IOffer getOffer(Good good, boolean bid);
	
	public default void sellSome(IAgent who, IStock wallet, IStock good) {
		sellSome(who, wallet, good, 1.0);
	}

	/**
	 * Convenience method to sell some of the good if possible
	 */
	public default void sellSome(IAgent who, IStock wallet, IStock good, double fraction) {
		if (good.hasSome()) {
			IOffer offer = getOffer(good.getGood(), true);
			if (offer != null) {
				offer.accept(who, wallet, good, new Quantity(good.getGood(), good.getAmount() * fraction));
			}
		}
	}

	/**
	 * Convenience method to buy some of the good if possible
	 */
	public default void buySome(IAgent who, IStock wallet, IStock good) {
		if (good.hasSome()) {
			IOffer offer = getOffer(good.getGood(), false);
			if (offer != null) {
				offer.accept(who, wallet, good, good.getQuantity());
			}
		}
	}

}
