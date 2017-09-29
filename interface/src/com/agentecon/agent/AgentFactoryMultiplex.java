/**
 * Created by Luzius Meisser on Jun 18, 2017
 * Copyright: Meisser Economics AG, Zurich
 * Contact: luzius@meissereconomics.com
 *
 * Feel free to reuse this code under the MIT License
 * https://opensource.org/licenses/MIT
 */
package com.agentecon.agent;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import com.agentecon.IAgentFactory;
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

	public AgentFactoryMultiplex(Class<? extends IConsumer>[] agents) {
		this.factories = new IAgentFactory[agents.length];
		for (int i=0; i<agents.length; i++) {
			final Class<? extends IConsumer> current = agents[i];
			this.factories[i] = new IAgentFactory() {
				
				@Override
				public IConsumer createConsumer(IAgentIdGenerator id, Endowment endowment, IUtility utilityFunction) {
					try {
						return current.getConstructor(IAgentIdGenerator.class, Endowment.class, IUtility.class).newInstance(id, endowment, utilityFunction);
					} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
						System.err.println("Could not instantiate agent " + current + " due to " + e);
						return null;
					}
				}
			};
		}
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