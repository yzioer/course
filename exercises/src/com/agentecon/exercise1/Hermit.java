/**
 * Created by Luzius Meisser on Jun 18, 2017
 * Copyright: Meisser Economics AG, Zurich
 * Contact: luzius@meissereconomics.com
 *
 * Feel free to reuse this code under the MIT License
 * https://opensource.org/licenses/MIT
 */
package com.agentecon.exercise1;

import java.io.IOException;
import java.net.SocketTimeoutException;

import com.agentecon.IAgentFactory;
import com.agentecon.Simulation;
import com.agentecon.agent.Endowment;
import com.agentecon.agent.IAgentIdGenerator;
import com.agentecon.consumer.Consumer;
import com.agentecon.consumer.IConsumer;
import com.agentecon.consumer.IUtility;
import com.agentecon.exercises.HermitConfiguration;
import com.agentecon.firm.IFirm;
import com.agentecon.goods.Good;
import com.agentecon.goods.IStock;
import com.agentecon.goods.Inventory;
import com.agentecon.market.IPriceTakerMarket;
import com.agentecon.market.IStatistics;
import com.agentecon.production.IProductionFunction;
import com.agentecon.research.IFounder;
import com.agentecon.research.IInnovation;

/**
 * An autarkic consumer that produces its own food and does not interact with others.
 */
public class Hermit extends Consumer implements IFounder {

	private Good manhours;
	private IProductionFunction prodFun;

	public Hermit(IAgentIdGenerator id, Endowment end, IUtility utility) {
		super(id, end, utility);
		this.manhours = end.getDaily()[0].getGood();
		assert this.manhours.equals(HermitConfiguration.MAN_HOUR);
	}

	@Override
	public IFirm considerCreatingFirm(IStatistics statistics, IInnovation research, IAgentIdGenerator id) {
		if (this.prodFun == null) {
			// instead of creating a firm, the hermit will create a production function for
			// himself
			this.prodFun = research.createProductionFunction(HermitConfiguration.POTATOE);
		}
		return null;
	}

	@Override
	public void tradeGoods(IPriceTakerMarket market) {
		// Hermit does not trade, produces instead for himself
		produce(getInventory());
	}

	private void produce(Inventory inventory) {
		IStock currentManhours = inventory.getStock(manhours);

		// Play here. Maybe you find a better fraction than 60%?
		// getUtilityFunction().getWeights() might help you finding out
		// how the consumer weighs the utility of potatoes and of leisure
		// time (man-hours) relative to each other.
		double plannedLeisureTime = currentManhours.getAmount() * 0.40;

		// The hide function creates allows to hide parts of the inventory from the
		// production function, preserving it for later consumption.
		Inventory productionInventory = inventory.hide(manhours, plannedLeisureTime);
		prodFun.produce(productionInventory);
	}

	protected double calculateWorkAmount(IStock currentManhours) {
		double weight = prodFun.getWeight(manhours).weight;
		double fixedCost = prodFun.getFixedCost(manhours);
		return (currentManhours.getAmount() * weight + fixedCost) / (1 + weight);
	}

	@Override
	// this method is only here for better explanation what is going on
	public double consume() {
		// super class already knows how to consume, let it do the work
		// System.out.println("Eating from " + getInventory());
		double utility = super.consume();
		return utility;
	}

	// The "static void main" method is executed when running a class
	public static void main(String[] args) throws SocketTimeoutException, IOException {
		HermitConfiguration config = new HermitConfiguration(new IAgentFactory() {

			@Override
			public IConsumer createConsumer(IAgentIdGenerator id, Endowment endowment, IUtility utilityFunction) {
				return new Hermit(id, endowment, utilityFunction) {
					
					// We override the consume method of the hermit implementation to add some system output
					@Override
					public double consume() {
						double utility = super.consume();
						System.out.println("Day " + getAge() + ": achieved utility " + utility);
						return utility;
					}
				};
			}
		}, 1); // Create the configuration
		Simulation sim = new Simulation(config); // Create the simulation
		sim.run(); // run the simulation
	}

}
