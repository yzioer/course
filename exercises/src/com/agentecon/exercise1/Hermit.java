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
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.agentecon.agent.Endowment;
import com.agentecon.agent.IAgentIdGenerator;
import com.agentecon.consumer.Consumer;
import com.agentecon.consumer.IUtility;
import com.agentecon.exercises.HermitConfiguration;
import com.agentecon.firm.IFirm;
import com.agentecon.goods.IStock;
import com.agentecon.goods.Inventory;
import com.agentecon.goods.Quantity;
import com.agentecon.market.IPriceTakerMarket;
import com.agentecon.market.IStatistics;
import com.agentecon.production.IProductionFunction;
import com.agentecon.research.IFounder;
import com.agentecon.research.IInnovation;

/**
 * An autarkic consumer that produces its own food and does not interact with others.
 */
public class Hermit extends Consumer implements IFounder {

	private IProductionFunction prodFun;
	private double workFraction = 0.2;
	private double fraction;

	public Hermit(IAgentIdGenerator id, Endowment end, IUtility utility) {
		super(id, end, utility);
	}

	@Override
	public IFirm considerCreatingFirm(IStatistics statistics, IInnovation research, IAgentIdGenerator id) {
		if (this.prodFun == null) {
			// instead of creating a firm, the hermit will create a production function for himself
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
		IStock currentManhours = inventory.getStock(HermitConfiguration.MAN_HOUR);

		// Play here. Maybe you find a better fraction than 60%?
		// getUtilityFunction().getWeights() might help you finding out
		// how the consumer weighs the utility of potatoes and of leisure
		// time (man-hours) relative to each other.
		/*
		double[] weights = getUtilityFunction().getWeights();
		for (double weight : weights) {
			System.out.println("in produce: " + weight);
		}
		// weights = {1.0,1.0}
		*/

		// double plannedLeisureTime = currentManhours.getAmount() * 0.6;
		double plannedLeisureTime = currentManhours.getAmount() * fraction;
		workFraction = workFraction + 0.005;

		// The hide function creates allows to hide parts of the inventory from the
		// production function, preserving it for later consumption.
		Inventory productionInventory = inventory.hide(HermitConfiguration.MAN_HOUR, plannedLeisureTime);
		prodFun.produce(productionInventory);
	}

	@Override
	// this method is only here for better explanation what is going on
	public double consume() {
		// super class already knows how to consume, let it do the work
		// System.out.println("Eating from " + getInventory());
		double utility = super.consume();
		return utility;
	}

	public void setFraction(double new_fraction){
		fraction = new_fraction;
	}

	public double getFraction(){
		return fraction;
	}

	// The "static void main" method is executed when running a class
	public static void main(String[] args) throws SocketTimeoutException, IOException {
		HermitConfiguration config = new HermitConfiguration(null, 0);
		Endowment endowment = config.createEndowment();
		IUtility utilityFunction = config.create(0);
		
		Hermit bob = new Hermit(new SimpleAgentIdGenerator(), endowment, utilityFunction);
		int endOfTime = 100; // let world end after 100 days


		/*
		// grid search (yes, this is brute force), trying different fractions
		double utility = 0.0;
		double old_fraction = 0.0;
		double step = 0.000001;
		int num_elements = (int) ((1.0 - old_fraction)/step);
		//ArrayList<Double> arrList = new ArrayList<Double>(num_elements);
		List<Map.Entry<Double,Double>> pairList= new ArrayList<>(num_elements);
		bob.setFraction(old_fraction);
		while (bob.getFraction()<1.0) {
			for (int t=0; t<endOfTime; t++) {
				bob.collectDailyEndowment();
				bob.considerCreatingFirm(null, config, null);
				bob.tradeGoods(null);
				List<Quantity> inventoryBeforeConsumption = bob.getInventory().getQuantities();
				// double utility = bob.consume();
				utility = bob.consume();
				//System.out.println("Bob achieved a utility of " + utility + " on day " + t + ". Inventory before consumption was: " + inventoryBeforeConsumption);
			}
			AbstractMap.SimpleEntry<Double, Double> current_result = new AbstractMap.SimpleEntry<>(bob.getFraction(), utility);
			pairList.add(current_result);
			bob.setFraction(bob.getFraction()+step);
		}
		// find max utility
		double max_fraction = pairList.get(0).getKey(), max = pairList.get(0).getValue();
		for (AbstractMap.Entry<Double, Double> pair: pairList) {
			if (max<pair.getValue()) {
				max = pair.getValue();
				max_fraction = pair.getKey();
			}
			//System.out.println("(fraction, utility) = (" + pair.getKey() + "," + pair.getValue() + ")");
		}
		// print found max
		System.out.println("MAX: (fraction, utility) = (" + max_fraction + "," + max + ")"); // result: (fraction, utility) = (0.46874999999437655,4.487127668778711)
		*/


		// first submission
		/*
		double hardcoded_fraction = 0.53125;
		bob.setFraction(hardcoded_fraction);
		*/
		// omit next two lines for the first submission
		double maximising_fraction = 0.46875;
		bob.setFraction(maximising_fraction);

		for (int t=0; t<endOfTime; t++) {
			bob.collectDailyEndowment();
			bob.considerCreatingFirm(null, config, null);
			bob.tradeGoods(null);
			List<Quantity> inventoryBeforeConsumption = bob.getInventory().getQuantities();
			double utility = bob.consume();
			System.out.println("Bob achieved a utility of " + utility + " on day " + t + ". Inventory before consumption was: " + inventoryBeforeConsumption);
		}
	}

	/*
	@Override
	public void addListener(IConsumerListener listener) {
		this.listeners.add(listener);
	}
	*/

	/*
	@Override
	public void addListener(Obj ect) {
	}
	*/
}
