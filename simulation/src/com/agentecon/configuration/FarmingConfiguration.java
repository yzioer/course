/**
 * Created by Luzius Meisser on Jun 19, 2017
 * Copyright: Meisser Economics AG, Zurich
 * Contact: luzius@meissereconomics.com
 *
 * Feel free to reuse this code under the MIT License
 * https://opensource.org/licenses/MIT
 */
package com.agentecon.configuration;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.SocketTimeoutException;

import com.agentecon.IAgentFactory;
import com.agentecon.ISimulation;
import com.agentecon.Simulation;
import com.agentecon.agent.Endowment;
import com.agentecon.agent.IAgentIdGenerator;
import com.agentecon.consumer.Consumer;
import com.agentecon.consumer.IConsumer;
import com.agentecon.consumer.IUtility;
import com.agentecon.consumer.LogUtilWithFloor;
import com.agentecon.consumer.Weight;
import com.agentecon.events.ConsumerEvent;
import com.agentecon.events.IUtilityFactory;
import com.agentecon.exercises.HermitConfiguration;
import com.agentecon.firm.production.CobbDouglasProductionWithFixedCost;
import com.agentecon.goods.Good;
import com.agentecon.goods.IStock;
import com.agentecon.goods.Inventory;
import com.agentecon.goods.Quantity;
import com.agentecon.goods.Stock;
import com.agentecon.market.GoodStats;
import com.agentecon.market.IStatistics;
import com.agentecon.production.IProductionFunction;
import com.agentecon.production.PriceUnknownException;
import com.agentecon.ranking.ConsumerRanking;
import com.agentecon.research.IInnovation;
import com.agentecon.research.IResearchProject;
import com.agentecon.sim.SimulationConfig;

public class FarmingConfiguration extends SimulationConfig implements IInnovation, IUtilityFactory {

	public static final String FARMER = "com.agentecon.exercise2.Farmer";
	public static final String EXPERIMENTAL_FARMER = "com.agentecon.exercise2.ExperimentalFarmer";

	public static final Good GOLD = new Good("Gold", 1.0);
	public static final Good LAND = HermitConfiguration.LAND;
	public static final Good POTATOE = HermitConfiguration.POTATOE;
	public static final Good MAN_HOUR = HermitConfiguration.MAN_HOUR;

	private static final int ROUNDS = 2000;

	public static final Quantity FIXED_COSTS = HermitConfiguration.FIXED_COSTS;

	public FarmingConfiguration() throws IOException {
		this(new AgentFactoryMultiplex(new IAgentFactory() {

			@Override
			public IConsumer createConsumer(IAgentIdGenerator id, Endowment endowment, IUtility utilityFunction) {
				return new Consumer(id, endowment, utilityFunction);
			}

		}, new LimitingAgentFactory(1, new CompilingAgentFactory(EXPERIMENTAL_FARMER, new File("../exercises/src"))),
				new LimitingAgentFactory(1, new CompilingAgentFactory(HermitConfiguration.AGENT_CLASS_NAME, new File("../exercises/src"))),
				new LimitingAgentFactory(30, new CompilingAgentFactory(FARMER, new File("../exercises/src")))), 60);
	}

	public FarmingConfiguration(IAgentFactory factory, int agents) {
		super(ROUNDS);
		IStock[] initialEndowment = new IStock[] { new Stock(LAND, 100), new Stock(GOLD, 1000) };
		IStock[] dailyEndowment = new IStock[] { new Stock(MAN_HOUR, HermitConfiguration.DAILY_ENDOWMENT) };
		Endowment end = new Endowment(GOLD, initialEndowment, dailyEndowment);
		addEvent(new ConsumerEvent(agents, end, this) {
			@Override
			protected IConsumer createConsumer(IAgentIdGenerator id, Endowment end, IUtility util) {
				return factory.createConsumer(id, end, util);
			}
		});
	}

	@Override
	public IUtility create(int number) {
		return new LogUtilWithFloor(new Weight(POTATOE, 1.0), new Weight(MAN_HOUR, 1.0));
	}

	// Use gold as money
	@Override
	public Good getMoney() {
		return GOLD;
	}

	@Override
	public IInnovation getInnovation() {
		return this;
	}

	@Override
	public CobbDouglasProductionWithFixedCost createProductionFunction(Good desiredOutput) {
		assert desiredOutput.equals(POTATOE);
		return new CobbDouglasProductionWithFixedCost(POTATOE, 1.0, FIXED_COSTS, new Weight(LAND, 0.25, true), new Weight(MAN_HOUR, 0.75));
	}

	@Override
	public IResearchProject createResearchProject(Good desiredOutput) {
		return null;
	}

	public void diagnoseResult(PrintStream out, ISimulation sim) {
		try {
			IStatistics stats = sim.getStatistics();
			CobbDouglasProductionWithFixedCost prodFun = createProductionFunction(POTATOE);
			GoodStats manhours = stats.getGoodsMarketStats().getStats(MAN_HOUR);
			double laborShare = prodFun.getWeight(MAN_HOUR).weight;
			double optimalNumberOfFirms = manhours.getYesterday().getTotWeight() / FIXED_COSTS.getAmount() * (1 - laborShare);
			int numberOfFirms = sim.getAgents().getFirms().size();
			System.out.println(manhours + " implies optimal number of firms k=" + optimalNumberOfFirms + ", actual number of firms is " + numberOfFirms);
			System.out.println(stats.getGoodsMarketStats());

			Inventory inv = new Inventory(GOLD, new Stock(LAND, 100));
			double optimalCost = prodFun.getCostOfMaximumProfit(inv, stats.getGoodsMarketStats());
			double optimalManhours = optimalCost / stats.getGoodsMarketStats().getPriceBelief(MAN_HOUR);
			double fixedCosts = prodFun.getFixedCost(MAN_HOUR) * stats.getGoodsMarketStats().getPriceBelief(MAN_HOUR);
			inv.getStock(MAN_HOUR).add(optimalManhours);
			Quantity prod = prodFun.produce(inv);
			double profits = prod.getAmount() * stats.getGoodsMarketStats().getPriceBelief(POTATOE) - optimalCost;

			double profitShare = 1.0 - laborShare;
			double profits2 = (optimalCost - fixedCosts) / laborShare * profitShare - fixedCosts;
			System.out.println("Firm should use " + optimalManhours + " " + MAN_HOUR + " to produce " + prod + " and yield a profit of " + profits + " (" + profits2 + ")");

			double totalInput = manhours.getYesterday().getTotWeight();
			double perFirm = totalInput / optimalNumberOfFirms;
			if (perFirm > 0.0) {
				inv.getStock(MAN_HOUR).add(perFirm);
				double output = prodFun.produce(inv).getAmount() * optimalNumberOfFirms;
				System.out.println("With " + optimalNumberOfFirms + " firms the " + totalInput + " manhours could have produced " + output + " instead of "
						+ stats.getGoodsMarketStats().getStats(POTATOE).getYesterday().getTotWeight());
			}
			double altInput = 12;
			System.out.println("Using only " + altInput + " man-hours would yield a profit of " + getProfits(prodFun, stats, altInput));
		} catch (PriceUnknownException e) {
			e.printStackTrace(out);
		}
	}

	private static double getProfits(IProductionFunction prodFun, IStatistics sim, double inputAmount) throws PriceUnknownException {
		Inventory inv = new Inventory(GOLD, new Stock(LAND, 100));
		double costs = inputAmount * sim.getGoodsMarketStats().getPriceBelief(MAN_HOUR);
		inv.getStock(MAN_HOUR).add(inputAmount);
		Quantity prod = prodFun.produce(inv);
		return prod.getAmount() * sim.getGoodsMarketStats().getPriceBelief(POTATOE) - costs;
	}

	public static void main(String[] args) throws SocketTimeoutException, IOException {
		IAgentFactory defaultFactory = new CompilingAgentFactory(FARMER, new File("../exercises/src")); // this factory loads your Farmer
		IAgentFactory normalConsumerFactory = new IAgentFactory() {

			@Override
			public IConsumer createConsumer(IAgentIdGenerator id, Endowment endowment, IUtility utilityFunction) {
				return new Consumer(id, endowment, utilityFunction);
			}
		};
		// IAgentFactory meisserFactory = new RemoteAgentFactory("meisserecon", "agentecon"); // loads the Hermit implementation from the meisserecon repository
		// IAgentFactory other = new RemoteAgentFactory("user", "repo"); // maybe you want to load agents from someone else's repository for comparison?

		// Create a multiplex factory that alternates between different factories when instantiating agents
		IAgentFactory factory = new AgentFactoryMultiplex(defaultFactory, normalConsumerFactory);

		FarmingConfiguration config = new FarmingConfiguration(factory, 10); // Create the configuration
		Simulation sim = new Simulation(config); // Create the simulation
		ConsumerRanking ranking = new ConsumerRanking(); // Create a ranking
		sim.addListener(ranking); // register the ranking as a listener interested in what is going on
		sim.run(); // run the simulation
		ranking.print(System.out); // print the resulting ranking
	}

}
