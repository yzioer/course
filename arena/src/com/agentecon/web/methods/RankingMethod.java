/**
 * Created by Luzius Meisser on Jun 15, 2017
 * Copyright: Meisser Economics AG, Zurich
 * Contact: luzius@meissereconomics.com
 *
 * Feel free to reuse this code under the MIT License
 * https://opensource.org/licenses/MIT
 */
package com.agentecon.web.methods;

import java.io.IOException;
import java.util.Collection;

import com.agentecon.runner.SimulationStepper;
import com.agentecon.web.data.JsonData;

public class RankingMethod extends SimSpecificMethod {

	public RankingMethod(ListMethod listing) {
		super(listing);
	}

	@Override
	public JsonData getJsonAnswer(Parameters params) throws IOException {
		SimulationStepper simulation = getSimulation(params);
		return new Ranking(simulation.getRanking());
	}
	
	class Ranking extends JsonData {
		
		Collection<Rank> list;
		
		public Ranking(Collection<Rank> children) {
			this.list = children;
		}

	}

}
