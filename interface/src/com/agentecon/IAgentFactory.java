package com.agentecon;

import com.agentecon.agent.Endowment;
import com.agentecon.agent.IAgentIdGenerator;
import com.agentecon.consumer.IConsumer;
import com.agentecon.consumer.IUtility;

public interface IAgentFactory {
	
	public IConsumer createConsumer(IAgentIdGenerator id, Endowment endowment, IUtility utilityFunction);
	
	public default IConsumer createConsumer(IAgentIdGenerator id, int maxAge, Endowment endowment, IUtility utilityFunction) {
		return createConsumer(id, endowment, utilityFunction);
	}
	
}
