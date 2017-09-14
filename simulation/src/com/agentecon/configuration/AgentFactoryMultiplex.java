/**
 * Created by Luzius Meisser on Jun 18, 2017
 * Copyright: Meisser Economics AG, Zurich
 * Contact: luzius@meissereconomics.com
 *
 * Feel free to reuse this code under the MIT License
 * https://opensource.org/licenses/MIT
 */
package com.agentecon.configuration;

import java.io.IOException;

import com.agentecon.IAgentFactory;
import com.agentecon.agent.Endowment;
import com.agentecon.agent.IAgentIdGenerator;
import com.agentecon.consumer.IConsumer;
import com.agentecon.consumer.IUtility;

public class AgentFactoryMultiplex implements IAgentFactory {

	private int current;
	private IAgentFactory[] factories;

	public AgentFactoryMultiplex(IAgentFactory... factories) throws IOException {
		this.factories = factories;
		this.current = 1;
		assert factories.length >= 1;
	}

	private IAgentFactory getCurrent() {
		return factories[current++ % factories.length];
	}

	@Override
	public IConsumer createConsumer(IAgentIdGenerator id, Endowment endowment, IUtility utilityFunction) {
		IAgentFactory current = getCurrent();
		IConsumer consumer = current.createConsumer(id, endowment, utilityFunction);
		return consumer == null ? createDefault(id, endowment, utilityFunction) : consumer;
	}

	protected IConsumer createDefault(IAgentIdGenerator id, Endowment endowment, IUtility utilityFunction) {
		return factories[0].createConsumer(id, endowment, utilityFunction);
	}

}
