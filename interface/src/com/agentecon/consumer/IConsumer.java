// Created on Jun 1, 2015 by Luzius Meisser

package com.agentecon.consumer;

import com.agentecon.agent.IAgent;
import com.agentecon.firm.Portfolio;
import com.agentecon.goods.Inventory;

public interface IConsumer extends IAgent, IMarketParticipant {
	
	/**
	 * Called every morning.
	 */
	public void collectDailyEndowment();

	/**
	 * Time to consume, called once per day in the evening after trading goods.
	 * @return the utility gained from consumption.
	 */
	public double consume();

	/**
	 * Get one day older and die if the maximum age is reached.
	 * In case of death, the inventory must be returned and the remaining
	 * portfolio transferred to the 'inheritance' portfolio.
	 */
	public Inventory considerDeath(Portfolio inheritance);
	
	public boolean isRetired();
	
	public IUtility getUtilityFunction();
	
	public void addListener(IConsumerListener listener);
	
}
