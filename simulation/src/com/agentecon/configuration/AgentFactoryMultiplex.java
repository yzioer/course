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
	
	public AgentFactoryMultiplex(IAgentFactory... factories) throws IOException{
		this.factories = factories;
		this.current = 0;
	}
	
	private IAgentFactory getCurrent(){
		return factories[current++ % factories.length];
	}

//	public static final AgentFactoryMultiplex createDefault() throws SocketTimeoutException, IOException{
//		return new AgentFactoryMultiplex(
//				new CompilingAgentFactory("meisserecon", "agentecon", "master")
//				);
//	}

	@Override
	public IConsumer createConsumer(IAgentIdGenerator id, Endowment endowment, IUtility utilityFunction) {
		IConsumer consumer =  getCurrent().createConsumer(id, endowment, utilityFunction);
		return consumer == null ? createConsumer(id, endowment, utilityFunction) : consumer;
	}

}
