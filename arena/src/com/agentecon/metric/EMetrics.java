/**
 * Created by Luzius Meisser on Jun 18, 2017
 * Copyright: Meisser Economics AG, Zurich
 * Contact: luzius@meissereconomics.com
 *
 * Feel free to reuse this code under the MIT License
 * https://opensource.org/licenses/MIT
 */
package com.agentecon.metric;

import java.util.ArrayList;
import java.util.List;

import com.agentecon.ISimulation;
import com.agentecon.agent.IAgents;
import com.agentecon.metric.variants.Demographics;
import com.agentecon.metric.variants.DividendStats;
import com.agentecon.metric.variants.FirmStats;
import com.agentecon.metric.variants.InventoryStats;
import com.agentecon.metric.variants.MarketStats;
import com.agentecon.metric.variants.MonetaryStats;
import com.agentecon.metric.variants.OwnershipStats;
import com.agentecon.metric.variants.ProductionStats;
import com.agentecon.metric.variants.StockMarketStats;
import com.agentecon.metric.variants.UtilityStats;
import com.agentecon.metric.variants.ValuationStats;
import com.agentecon.web.query.AgentQuery;

public enum EMetrics {
	
	DEMOGRAPHICS, DIVIDENDS, INVENTORY, MARKET, MONETARY, OWNERSHIP, FIRM, STOCKMARKET, PRODUCTION, UTILITY, VALUATION;
	
	public SimStats createAndRegister(ISimulation sim, List<String> list){
		ArrayList<AgentQuery> queries = new ArrayList<>();
		for (String query: list) {
			queries.add(new AgentQuery(query));
		}
		SimStats stats = instantiate(sim, queries);
		sim.addListener(stats);
		return stats;
	}
	
	private SimStats instantiate(ISimulation sim, ArrayList<AgentQuery> agents){
		switch (this){
		case DEMOGRAPHICS:
			return new Demographics(sim);
		case DIVIDENDS:
			return new DividendStats(sim, agents);
		case FIRM:
			return new FirmStats();
		case INVENTORY:
			return new InventoryStats(sim);
		case MARKET:
			return new MarketStats(true);
		case MONETARY:
			return new MonetaryStats(sim);
		case OWNERSHIP:
			return new OwnershipStats(sim);
		case PRODUCTION:
			return new ProductionStats();
		case STOCKMARKET:
			return new StockMarketStats(sim);
		case UTILITY:
			return new UtilityStats();
		case VALUATION:
			return new ValuationStats(sim);
		default:
			return null;
		}
	}

	public static EMetrics parse(String metric) {
		for (EMetrics candidate: EMetrics.values()){
			if (candidate.getName().equals(metric)){
				return candidate;
			}
		}
		return null;
	}

	public String getName() {
		return this.name().toLowerCase();
	}

}